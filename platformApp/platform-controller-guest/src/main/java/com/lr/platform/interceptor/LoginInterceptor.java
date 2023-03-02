package com.lr.platform.interceptor;

import com.lr.platform.entity.JWTClaims;
import com.lr.platform.entity.users.LoginData;
import com.lr.platform.entity.users.Users;
import com.lr.platform.exception.LoginException;
import com.lr.platform.service.UsersService;
import com.lr.platform.utils.Dozer;
import com.lr.platform.utils.JWTAuth;
import com.lr.platform.utils.Redis;
import com.lr.platform.utils.Validator;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Objects;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private UsersService usersService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;// 把handler强转为HandlerMethod
            // 从handlerMethod中获取本次请求的接口方法对象然后判断该方法上是否标有我们自定义的注解@LoginRequired
            ApiOperation loginRequired = handlerMethod.getMethod().getAnnotation(ApiOperation.class);
            if (loginRequired==null) return true;
            //进行鉴权
            String ip=request.getHeader("X-real-ip");
            String token=request.getHeader("token");
            String ua=request.getHeader("User-Agent");
            String prefix="guest:LoginData-Web:";
            if (Validator.isMobile(ua)){
                prefix="guest:LoginData-Mobile:";
            }
            JWTClaims jwtClaims= JWTAuth.parseToken(token);
            if (jwtClaims!=null){
                String jsonString= Redis.get(prefix+jwtClaims.getUid());
                String usersStr = Redis.get("guest:Users:"+jwtClaims.getUid());
                if (jsonString!=null){
                    LoginData loginData= Dozer.toEntity(jsonString,LoginData.class);
                    if (loginData!=null && Objects.equals(loginData.getIp(), ip) && Objects.equals(loginData.getToken(),token)){
                        Users users;
                        if (usersStr != null){
                           users = Dozer.toEntity(usersStr,Users.class);
                        }else {
                           users = usersService.queryById(loginData.getId());
                            Redis.eval("local res = redis.call('exists',KEYS[1]) " +
                                            "if res == 0 then " +
                                            "redis.call('set',KEYS[1],ARGV[1],'EX',60) " +
                                            "end return 0",
                                    Collections.singletonList("guest:Users:"+jwtClaims.getUid()),
                                    Collections.singletonList(Dozer.toJsonString(users)));
                        }
                        if (users!=null && Objects.equals(users.getPassword(), loginData.getPassword())){
                            Long exp=Redis.ttl(prefix+jwtClaims.getUid());
                            if (exp != null && exp <1800 && loginData.getTtl()>0){
                                loginData.setTtl(loginData.getTtl()-1);
                                jsonString=Dozer.toJsonString(loginData);
                                String res=Redis.set(prefix+jwtClaims.getUid(),jsonString,exp+10800);
                            }
                            return true;
                        }
                    }
                }
            }
        }
        throw new LoginException("请先登录");
    }
}
