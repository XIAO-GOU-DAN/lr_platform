package com.lr.platform.interceptor;

import com.lr.platform.annotations.ManagerLog;
import com.lr.platform.entity.JWTClaims;
import com.lr.platform.entity.admins.Admins;
import com.lr.platform.entity.users.LoginData;
import com.lr.platform.exception.LoginException;
import com.lr.platform.service.AdminsService;
import com.lr.platform.utils.Dozer;
import com.lr.platform.utils.JWTAuth;
import com.lr.platform.utils.Redis;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private AdminsService adminsService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;// 把handler强转为HandlerMethod
            // 从handlerMethod中获取本次请求的接口方法对象然后判断该方法上是否标有我们自定义的注解@LoginRequired
            ApiOperation loginRequired = handlerMethod.getMethod().getAnnotation(ApiOperation.class);
            ManagerLog managerLog=handlerMethod.getMethod().getAnnotation(ManagerLog.class);
            if (loginRequired==null) return true;
            //进行鉴权
            String ip=request.getHeader("X-real-ip");
            String token=request.getHeader("token");
            JWTClaims jwtClaims= JWTAuth.parseToken(token);
            if (jwtClaims!=null){
                String jsonString= Redis.get("manager:LoginData:"+jwtClaims.getUsername());
                if (jsonString!=null){
                    LoginData loginData= Dozer.toEntity(jsonString,LoginData.class);
                    if (loginData!=null && Objects.equals(loginData.getIp(), ip) && Objects.equals(loginData.getToken(),token)){
                        Admins admins=adminsService.queryById(loginData.getId());
                        if (admins!=null && Objects.equals(admins.getPassword(), loginData.getPassword())){
                            if (admins.getChangePasswordAt() ==null && (managerLog==null || !Objects.equals(managerLog.tag(), "修改密码"))){
                                throw new LoginException("请先修改密码");
                            }
                            Long exp=Redis.ttl("manager:LoginData:"+jwtClaims.getUsername());
                            if (exp != null && exp <1800 && loginData.getTtl()>0){
                                loginData.setTtl(loginData.getTtl()-1);
                                jsonString=Dozer.toJsonString(loginData);
                                String res=Redis.set("manager:LoginData:"+jwtClaims.getUsername(),jsonString,exp+10800);
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
