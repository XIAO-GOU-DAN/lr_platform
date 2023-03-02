package com.lr.platform.controller.users;


import com.lr.platform.annotations.GuestLog;
import com.lr.platform.entity.JWTClaims;
import com.lr.platform.entity.users.*;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.enums.WarningLevel;
import com.lr.platform.exception.LoginException;
import com.lr.platform.exception.RollBackException;
import com.lr.platform.response.Response;
import com.lr.platform.service.UsersService;
import com.lr.platform.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * (Users)表控制层
 *
 * @author makejava
 * @since 2022-07-07 13:12:00
 */
@Slf4j
@Api(tags = "用户模块")
@RestController
@RequestMapping("users")
public class UsersController {
    /**
     * 服务对象
     */
    @Resource
    private UsersService usersService;

    @Resource
    private JavaMailSenderImpl javaMailSender;

    @Resource
    private Email email;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${recaptcha.key}")
    private String accessKey;

    @Value("${recaptcha-v2.key}")
    private String accessKeyV2;

    //@Resource(name = "restTemplateIgnoreSSL")
    //private RestTemplate restTemplateIgnoreSSL;

    @Resource
    private HttpServletRequest httpServletRequest;

    @GuestLog(tag = "登录",level = WarningLevel.COMMON_LOGIN)
    @ApiOperation(value = "登录",notes = "谷歌验证码填 login")
    @PostMapping("/login")
    public Response login(@RequestBody LoginFormVo loginFormVo){
        String ip=httpServletRequest.getHeader("X-real-ip");
        String loginToken=loginFormVo.getRecaptcha();
        String ua=httpServletRequest.getHeader("User-Agent");
        if (!GoogleCaptcha.isValidGoogleCaptchaV2WithV3(loginToken,"login",accessKeyV2,accessKey)){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_RECAPTCHA_ERROR)
                    .build();
        }
        if ((Validator.isValidUsername(loginFormVo.getUsername()) || Validator.isValidEmail(loginFormVo.getUsername()))
                && Validator.isValidPassword(loginFormVo.getPassword())
                && !Objects.equals(null,ip)){
            Users users=new Users();
            if (Validator.isValidUsername(loginFormVo.getUsername())){
                users.setUsername(loginFormVo.getUsername());
            }else {
                users.setEmail(loginFormVo.getUsername());
            }
            // usersStr = Redis.get("guest:Users:");
            Users query= usersService.queryByUsers(users);
            if (query != null && Security.bCryptMatchPassword(loginFormVo.getPassword(),query.getPassword())){
                String token= JWTAuth.releaseToken(query.getUsername(),3,"login",query.getId());
                LoginData loginData=new LoginData();
                loginData.setIp(ip);
                loginData.setId(query.getId());
                loginData.setUsername(query.getUsername());
                loginData.setPassword(query.getPassword());
                loginData.setToken(token);
                loginData.setTtl(3);
                String redisRes;
                if (Validator.isMobile(ua)){
                    redisRes = Redis.set("guest:LoginData-Mobile:"+query.getId(), Dozer.toJsonString(loginData),10800);
                }else {
                    redisRes = Redis.set("guest:LoginData-Web:"+query.getId(), Dozer.toJsonString(loginData),10800);
                }
                if (Objects.equals(redisRes, "OK")){
                    return Response
                            .builder()
                            .code(ResponseCode.COMMON_SUCCESS)
                            .data(token)
                            .build();
                }
                return Response
                        .builder()
                        .code(ResponseCode.COMMON_REDIS_ERROR)
                        .build();
            }
        }
        return Response
                .builder()
                .code(ResponseCode.USERS_WRONG_USERNAME_OR_PASSWORD)
                .build();
    }

    @ApiOperation(value = "发送验证码",notes = "可能是手机,可能是邮箱,也可能两个分开,成功返回凭据,注册时需带上,谷歌验证action填 send_code,url action注册填register,修改密码填 changePassword, 找回密码填 retrievePassword")
    @PostMapping("/sendCode")
    public Response sendEmail(@RequestParam("action") String action,
                              @RequestBody RegisterForm registerForm) throws Exception{
        String ip=httpServletRequest.getHeader("X-real-ip");
        String loginToken=registerForm.getRecaptcha();
        if (!GoogleCaptcha.isValidGoogleCaptchaV2WithV3(loginToken,"send_code",accessKeyV2,accessKey)){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_RECAPTCHA_ERROR)
                    .build();
        }
        if (!Validator.isValidEmail(registerForm.getEmail())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_EMAIL)
                    .build();
        }
        if (!Objects.equals(action, "register") && !Objects.equals(action, "changePassword") && !Objects.equals(action,"retrievePassword")){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_PARAMETER_MISSING)
                    .msg("错误的操作")
                    .build();
        }
        Users users=new Users();
        users.setEmail(registerForm.getEmail());
        users=usersService.queryByUsers(users);
        if (users!=null && Objects.equals(action, "register")){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_EMAIL_ALREADY_REGISTER)
                    .build();
        }
        if (users == null && (Objects.equals(action,"changePassword") || Objects.equals(action,"retrievePassword"))){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_WRONG_EMAIL)
                    .build();
        }
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper helper=new MimeMessageHelper(mimeMessage,true);
        String str="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random=new Random();
        StringBuilder buffer=new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int number=random.nextInt(str.length());
            char charAt = str.charAt(number);
            buffer.append(charAt);
        }
        String code= String.valueOf(buffer);
        helper.setFrom("凌睿招新平台<"+username+">");
        helper.setTo(registerForm.getEmail());
        helper.setSubject("验证码");
        Map<String,Object> map=new HashMap<>();
        map.put("email",registerForm.getEmail());
        map.put("code",code);
        helper.setText(email.builderContent(Email.codeTemplate,map),true);
        javaMailSender.send(mimeMessage);
        String uuid=Security.encodeMD5Hex(registerForm.getEmail()+ip+action);
        String token =JWTAuth.releaseToken(300, uuid, 1,action,0);
        String redisRes=Redis.set("guest:SendCode:"+uuid,code,300);
        if (!Objects.equals(redisRes, "OK")){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_REDIS_ERROR)
                    .build();
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .msg("发送成功")
                .data(token)
                .build();
    }

    @ApiOperation(value = "注册",notes = "目前不支持手机验证码,谷歌填 register")
    @PostMapping("/register")
    public Response register(@RequestBody RegisterForm registerForm) throws Exception {
        String ip=httpServletRequest.getHeader("X-real-ip");
        if (!GoogleCaptcha.isValidGoogleCaptchaV2WithV3(registerForm.getRecaptcha(),"register",accessKeyV2,accessKey)){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_RECAPTCHA_ERROR)
                    .build();
        }
        if (!Validator.isValidEmail(registerForm.getEmail())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_EMAIL)
                    .build();
        }
        if (!Validator.isValidUsername(registerForm.getUsername())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_USERNAME)
                    .build();
        }
        if (!Validator.isValidPassword(registerForm.getPassword())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_PASSWORD)
                    .build();
        }
        if (!Validator.isValidCode(registerForm.getCode())){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_CODE_ERROR)
                    .build();
        }
        String token=registerForm.getToken();
        String code=registerForm.getCode();
        JWTClaims jwtClaims=JWTAuth.parseToken(token);
        String uuid=Security.encodeMD5Hex(registerForm.getEmail()+ip+"register");
        if (jwtClaims==null){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_TOKEN)
                    .build();
        }
        if (!Objects.equals(uuid, jwtClaims.getUsername()) || !Objects.equals(jwtClaims.getOption(), "register")){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_TOKEN)
                    .build();
        }
        Users users=new Users();
        users.setUsername(registerForm.getUsername());
        if (usersService.queryByUsers(users)!=null){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_USERNAME_ALREADY_REGISTER)
                    .build();
        }
        Long res=(Long) Redis.evalSHA(Redis.isValidCodeSHA, Collections.singletonList("guest:SendCode:" + uuid), Collections.singletonList(code));
        if (res==null || res.intValue()!=1){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_CODE_ERROR)
                    .build();
        }
        users.setEmail(registerForm.getEmail());
        users.setPassword(Security.bCryptEncodePassword(registerForm.getPassword()));
        int res1=usersService.register(users);
        if (res1!=1){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_DATA_ALREADY_REGISTER)
                    .build();
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

    @ApiOperation(value = "获取个人信息")
    @GetMapping("/userInfo")
    public Response selectUserInfo(){
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims=JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        String usersStr = Redis.get("guest:Users:"+jwtClaims.getUid());
        Users users;
        if (usersStr == null){
            users = usersService.queryById(jwtClaims.getUid());
        }else {
            users = Dozer.toEntity(usersStr,Users.class);
        }
        if (users==null){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_NOT_FOUND)
                    .build();
        }
        UserInfo userInfo= Dozer.convert(users,UserInfo.class);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(userInfo)
                .build();
    }

    @ApiOperation(value = "修改个人信息",notes = "")
    @GuestLog(tag = "修改个人信息",level = WarningLevel.COMMON_UPDATE)
    @PostMapping("/update")
    public Response updateUsers(@RequestBody UpdateForm updateForm){
        if (!Validator.isValidName(updateForm.getName())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_NAME)
                    .build();
        }
        if (!Validator.isValidQQ(updateForm.getQq())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_QQ)
                    .build();
        }
        if (!Validator.isValidPhone(updateForm.getPhone())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_PHONE)
                    .build();
        }
        if (!Validator.isValidStudentNumber(updateForm.getStudentNum())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_STUDENT_NUMBER)
                    .build();
        }
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims=JWTAuth.parseToken(token);
        if (jwtClaims == null){
            throw new LoginException("请先登录");
        }
        Users users=Dozer.convert(updateForm,Users.class);
        users.setGrade(users.getStudentNum().substring(0, 4));
        users.setId(jwtClaims.getUid());
        int res = usersService.update(users);
        if (res > 1 ){
            throw new RollBackException(String.format("更新用户信息发现重复项 : userId %s ,重复 :%s",users.getId(),res));
        }
        Redis.del("guest:Users:"+jwtClaims.getUid());
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                //.msg("更新成功,更新结果显示可能会有延迟,请耐心等待")
                .build();
    }

    @ApiOperation(value = "修改密码",notes = "谷歌填 change_password,此接口需要登入 不检查用户名")
    @PostMapping("/changePassword")
    @GuestLog(tag = "修改密码",level = WarningLevel.COMMON_UPDATE)
    public Response changePassword(@RequestBody RegisterForm registerForm){
        if (!GoogleCaptcha.isValidGoogleCaptchaV2WithV3(registerForm.getRecaptcha(),"change_password",accessKeyV2,accessKey)){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_RECAPTCHA_ERROR)
                    .build();
        }
        String ip=httpServletRequest.getHeader("X-real-ip");
        String userToken=httpServletRequest.getHeader("token");
        JWTClaims userJwtClaims=JWTAuth.parseToken(userToken);
        if (userJwtClaims==null) throw new LoginException("请先登录");
        if (!Validator.isValidEmail(registerForm.getEmail())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_EMAIL)
                    .build();
        }
        if (!Validator.isValidPassword(registerForm.getPassword())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_PASSWORD)
                    .build();
        }
        if (!Validator.isValidCode(registerForm.getCode())){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_CODE_ERROR)
                    .build();
        }
        String token=registerForm.getToken();
        String code=registerForm.getCode();
        JWTClaims jwtClaims=JWTAuth.parseToken(token);
        String uuid=Security.encodeMD5Hex(registerForm.getEmail()+ip+"changePassword");
        if (jwtClaims==null){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_TOKEN)
                    .build();
        }
        if (!Objects.equals(uuid, jwtClaims.getUsername()) || !Objects.equals(jwtClaims.getOption(), "changePassword")){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_TOKEN)
                    .build();
        }
        Users users=usersService.queryById(userJwtClaims.getUid());
        if (users==null) throw new LoginException("请先登录");
        if (!Objects.equals(users.getEmail(),registerForm.getEmail())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_WRONG_EMAIL)
                    .msg("仅能输入当前账号的注册邮箱")
                    .build();
        }
        Long res=(Long) Redis.evalSHA(Redis.isValidCodeSHA, Collections.singletonList("guest:SendCode:" + uuid), Collections.singletonList(code));
        if (res==null || res.intValue()!=1){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_CODE_ERROR)
                    .build();
        }
        users.setPassword(Security.bCryptEncodePassword(registerForm.getPassword()));
        int res1=usersService.update(users);
        if (res1 > 1){
            throw new RollBackException("ERROR: 发现: "+registerForm.getEmail()+" 存在重复项: "+res1);
        }
        /*
        if (res1 < 1){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_NOT_FOUND)
                    .build();
        }*/
        Long redisRes=null;
        for (int i = 0;i<3;i++){
            redisRes =(Long) Redis.eval("local res = redis.call('del',KEYS[1]) " +
                    "if res == 1 then return 1 end " +
                    "res = redis.call('exists',KEYS[1]) " +
                    "if res == 0 then return 1 end " +
                    "return 0",
                    Collections.singletonList("guest:Users:"+users.getId()),
                    Collections.emptyList());
            if (redisRes != null && redisRes.intValue()==1) break;
            try {
                Thread.sleep(1000);
            }catch (Exception ignored){}
        }
        if (redisRes != null && redisRes.intValue() == 1){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_SUCCESS)
                    .build();
        }
        throw new RollBackException("REDIS 删除异常:"+"guest:Users:"+jwtClaims.getUid(),"修改失败,请稍微再试");
    }

    @ApiOperation(value = "找回密码",notes = "谷歌填 retrieve_password ,此接口需要填入用户名, 会检查邮箱与用户名是否对应")
    @PostMapping("/retrievePassword")
    @GuestLog(tag = "找回密码",level = WarningLevel.COMMON_UPDATE)
    public Response retrievePassword(@RequestBody RegisterForm registerForm){
        String ip=httpServletRequest.getHeader("X-real-ip");
        if (!GoogleCaptcha.isValidGoogleCaptchaV2WithV3(registerForm.getRecaptcha(),"retrieve_password",accessKeyV2,accessKey)){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_RECAPTCHA_ERROR)
                    .build();
        }
        if (!Validator.isValidUsername(registerForm.getUsername())){
            return Response
                    .builder()
                    .code(ResponseCode.ADMINS_ILLEGAL_USERNAME)
                    .build();
        }
        if (!Validator.isValidEmail(registerForm.getEmail())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_EMAIL)
                    .build();
        }
        if (!Validator.isValidPassword(registerForm.getPassword())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_PASSWORD)
                    .build();
        }
        if (!Validator.isValidCode(registerForm.getCode())){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_CODE_ERROR)
                    .build();
        }
        String token=registerForm.getToken();
        String code=registerForm.getCode();
        JWTClaims jwtClaims=JWTAuth.parseToken(token);
        String uuid=Security.encodeMD5Hex(registerForm.getEmail()+ip+"retrievePassword");
        if (jwtClaims==null){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_TOKEN)
                    .build();
        }
        if (!Objects.equals(uuid, jwtClaims.getUsername()) || !Objects.equals(jwtClaims.getOption(), "retrievePassword")){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_TOKEN)
                    .build();
        }
        Long res=(Long) Redis.evalSHA(Redis.isValidCodeSHA, Collections.singletonList("guest:SendCode:" + uuid), Collections.singletonList(code));
        if (res==null || res.intValue()!=1){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_CODE_ERROR)
                    .build();
        }
        Users users=new Users();
        users.setEmail(registerForm.getEmail());
        users.setUsername(registerForm.getUsername());
        users=usersService.queryByUsers(users);
        if (users==null){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_NOT_FOUND)
                    .build();
        }
        users.setPassword(Security.bCryptEncodePassword(registerForm.getPassword()));
        int res1=usersService.update(users);
        if (res1 > 1){
            throw new RollBackException("ERROR: 发现: "+registerForm.getEmail()+" 存在重复项: "+res1);
        }
        /*
        if (res1 < 1){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_NOT_FOUND)
                    .build();
        }

         */
        Long redisRes=null;
        for (int i = 0;i<3;i++){
            redisRes =(Long) Redis.eval("local res = redis.call('del',KEYS[1]) " +
                            "if res == 1 then return 1 end " +
                            "res = redis.call('exists',KEYS[1]) " +
                            "if res == 0 then return 1 end " +
                            "return 0",
                    Collections.singletonList("guest:Users:"+users.getId()),
                    Collections.emptyList());
            if (redisRes != null && redisRes.intValue()==1) break;
            try {
                Thread.sleep(1000);
            }catch (Exception ignored){}
        }
        if (redisRes != null && redisRes.intValue() == 1){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_SUCCESS)
                    .build();
        }
        throw new RollBackException("REDIS 删除异常:"+"guest:Users:"+jwtClaims.getUid(),"修改失败,请稍微再试");
    }

    @ApiOperation(value = "获取所有年级",notes = "返回数组, 年级的所有可能取值")
    @GetMapping("/grade")
    public Response getGrade(){
        List<Users> usersList=usersService.queryAllGrade(new Users());
        List<String> grade=new LinkedList<>();
        for (Users u:usersList){
            if (u!=null) {
                grade.add(u.getGrade());
            }else {
                grade.add(null);
            }
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(grade)
                .build();
    }
}

