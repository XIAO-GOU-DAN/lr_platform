package com.lr.platform.log;

import com.lr.platform.annotations.GuestLog;
import com.lr.platform.entity.JWTClaims;
import com.lr.platform.entity.users.LoginFormVo;
import com.lr.platform.entity.users.RegisterForm;
import com.lr.platform.entity.users.Users;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.exception.RollBackException;
import com.lr.platform.response.Response;
import com.lr.platform.service.GuestLogService;
import com.lr.platform.service.UsersService;
import com.lr.platform.util.Geoip2;
import com.lr.platform.utils.*;
import com.maxmind.geoip2.model.AsnResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

@Aspect
@Component
@Slf4j
public class LogExporter {
    @Resource
    private GuestLogService guestLogService;

    //@Resource
    //private HttpServletRequest httpServletRequest;

    @Resource
    private Transaction transaction;

    @Resource
    private UsersService usersService;


    @Around("@annotation(com.lr.platform.annotations.GuestLog)")
    public Object methodExporter(ProceedingJoinPoint joinPoint) throws Throwable{
        long st=new Date().getTime();
        TransactionStatus begin=transaction.begin();//开启事物
        Object res;
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip=request.getHeader("X-real-ip");
        String organization=request.getHeader("system-organization");
        try {
            res=joinPoint.proceed();//controller接口方法
        }catch (RollBackException e){
            log.error(String.format("系统异常: IP:%s",ip), e);
            transaction.rollBack(begin);
            if (e.getResMsg()!=null){
                return Response
                        .builder()
                        .code(ResponseCode.COMMON_DATA_ERROR)
                        .msg(e.getResMsg())
                        .build();
            }
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_DATA_ERROR)
                    .build();
        } catch (Exception e){
            log.error(String.format("系统异常: IP:%s",ip), e);
            transaction.rollBack(begin);
            throw e;
        }
        long et=new Date().getTime();
        Response response=(Response)res;
        if (response==null){//返回空,传回测试接口
            transaction.commit(begin);//提交
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_TEST)
                    .build();
        }
        if (response.getCode()!=0){//自定义状态码不为0,不记录日志,直接返回
            transaction.commit(begin);
            return res;
        }
        try {//记录日志
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method= methodSignature.getMethod();
            //String id=method.getDeclaringClass().getName()+"."+method.getName();
            GuestLog guestLog =method.getAnnotation(GuestLog.class);
            com.lr.platform.entity.guestLog.GuestLog newGuestLog =new com.lr.platform.entity.guestLog.GuestLog();
            newGuestLog.setOp(guestLog.tag());
            newGuestLog.setLevel(guestLog.level().getCOMMON_LEVEL());
            newGuestLog.setCommonOption(guestLog.level().getCOMMON_OP());
            newGuestLog.setTimeConsuming((int) (et-st));
            //HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            //String uri=request.getRequestURI();
            Object[] body= joinPoint.getArgs();
            HashMap<String,Object> hashMap=new HashMap<>();
            String[] parameterNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
            for (int i=0;i< parameterNames.length;i++){
                //不存入文件
                //System.out.println(MultipartFile.class.isAssignableFrom(body[i].getClass()));
                if (body[i] instanceof MultipartFile){
                    continue;
                }
                if (body[i]==null){
                    hashMap.put(parameterNames[i],body[i]);
                    continue;
                }
                //隐藏密码
                if (body[i].getClass()== LoginFormVo.class){
                    ((LoginFormVo)body[i]).setPassword(null);
                    ((LoginFormVo)body[i]).setRecaptcha(null);
                    Users users=new Users();
                    String username=((LoginFormVo)body[i]).getUsername();
                    if (Validator.isValidUsername(username)){
                        users.setUsername(username);
                    }else {
                        users.setEmail(username);
                    }
                    users=usersService.queryByUsers(users);
                    if (users!=null){
                        newGuestLog.setUserId(users.getId());
                    }
                }
                if (body[i].getClass()== RegisterForm.class){
                    ((RegisterForm)body[i]).setPassword(null);
                    ((RegisterForm)body[i]).setRecaptcha(null);
                    ((RegisterForm)body[i]).setToken(null);
                }
                hashMap.put(parameterNames[i],body[i]);
            }
            String token=request.getHeader("token");
            if (token != null){
                JWTClaims jwtClaims= JWTAuth.parseToken(token);
                if (jwtClaims != null){
                    newGuestLog.setUserId(jwtClaims.getUid());
                }
            }
            /*
            try {
                AsnResponse asnResponse = Geoip2.getInstance().getAsn(ip);
                ip=ip+" "+ GeoIP.getCNASNName(asnResponse.getAutonomousSystemOrganization());
            }catch (Exception e){
                log.error("ERROR:",e);
            }

             */
            if (organization==null){
                ip = ip+" unknown";
            }else {
                ip=ip+" "+ GeoIP.getCNASNName(organization);
            }
            newGuestLog.setIp(ip);
            newGuestLog.setParameter(Dozer.toJsonString(hashMap));
            guestLogService.insert(newGuestLog);
            transaction.commit(begin);
            return res;
        }catch (Exception e){
            transaction.rollBack(begin);
            throw e;
            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res1);
        }

    }
}
