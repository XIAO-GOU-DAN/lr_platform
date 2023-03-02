package com.lr.platform.log;

import com.lr.platform.entity.JWTClaims;
import com.lr.platform.entity.admins.Admins;
import com.lr.platform.entity.admins.LoginFormVo;
import com.lr.platform.entity.admins.RegisterForm;
import com.lr.platform.entity.managerLog.ManagerLog;
import com.lr.platform.entity.users.UserUpdateAdminVo;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.exception.RollBackException;
import com.lr.platform.response.Response;
import com.lr.platform.service.AdminsService;
import com.lr.platform.service.ManagerLogService;
import com.lr.platform.utils.Dozer;
import com.lr.platform.utils.GeoIP;
import com.lr.platform.utils.JWTAuth;
import com.lr.platform.utils.Transaction;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

@Aspect
@Component
@Slf4j
public class LogExporter {
    @Resource
    private ManagerLogService managerLogService;

    //@Resource
    //private HttpServletRequest httpServletRequest;

    @Resource
    private Transaction transaction;

    @Resource
    private AdminsService adminsService;


    @Around("@annotation(com.lr.platform.annotations.ManagerLog)")
    public Object methodExporter(ProceedingJoinPoint joinPoint) throws Throwable{
        long st=new Date().getTime();
        TransactionStatus begin=transaction.begin();//开启事物
        Object res;
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip=request.getHeader("X-real-ip");
        String organization=request.getHeader("system-organization");
        try {
            res=joinPoint.proceed();//controller接口方法
        }catch (RollBackException e) {
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
            com.lr.platform.annotations.ManagerLog managerLog=method.getAnnotation(com.lr.platform.annotations.ManagerLog.class);
            ManagerLog newManagerLog =new ManagerLog();
            newManagerLog.setOp(managerLog.tag());
            newManagerLog.setLevel(managerLog.level().getCOMMON_LEVEL());
            newManagerLog.setCommonOption(managerLog.level().getCOMMON_OP());
            newManagerLog.setTimeConsuming((int) (et-st));
            //String uri=request.getRequestURI();
            Object[] body= joinPoint.getArgs();
            HashMap<String,Object> hashMap=new HashMap<>();
            String[] parameterNames = ((CodeSignature)joinPoint.getSignature()).getParameterNames();
            for (int i=0;i< parameterNames.length;i++){
                if (body[i]==null){
                    hashMap.put(parameterNames[i],body[i]);
                    continue;
                }
                if (body[i].getClass() == Long.class){
                    body[i] = body[i].toString();
                }
                if (body[i].getClass()== LoginFormVo.class){
                    ((LoginFormVo)body[i]).setPassword(null);
                    ((LoginFormVo)body[i]).setToken(null);
                    Admins admins=new Admins();
                    admins.setUsername(((LoginFormVo)body[i]).getUsername());
                    admins=adminsService.queryByAdmins(admins);
                    if (admins!=null){
                        newManagerLog.setUserId(admins.getId());
                    }
                }
                if (body[i].getClass()== RegisterForm.class){
                    ((RegisterForm)body[i]).setPassword(null);
                    ((RegisterForm)body[i]).setRecaptcha(null);
                    ((RegisterForm)body[i]).setToken(null);
                }
                if (body[i].getClass()== UserUpdateAdminVo.class){
                    ((UserUpdateAdminVo)body[i]).setPassword(null);
                }
                hashMap.put(parameterNames[i],body[i]);
            }
            String token=request.getHeader("token");
            if (token != null){
                JWTClaims jwtClaims= JWTAuth.parseToken(token);
                if (jwtClaims != null){
                    newManagerLog.setUserId(jwtClaims.getUid());
                }
            }
            if (organization==null){
                ip = ip+" unknown";
            }else {
                ip=ip+" "+ GeoIP.getCNASNName(organization);
            }
            newManagerLog.setIp(ip);
            newManagerLog.setParameter(Dozer.toJsonString(hashMap));
            managerLogService.insert(newManagerLog);
            transaction.commit(begin);
            return res;
        }catch (Exception e){
            transaction.rollBack(begin);
            throw e;
            //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res1);
        }

    }
}
