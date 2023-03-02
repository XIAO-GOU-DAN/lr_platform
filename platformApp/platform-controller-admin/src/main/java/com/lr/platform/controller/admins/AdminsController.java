package com.lr.platform.controller.admins;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lr.platform.annotations.ManagerLog;
import com.lr.platform.entity.JWTClaims;
import com.lr.platform.entity.admins.*;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.enums.WarningLevel;
import com.lr.platform.exception.LoginException;
import com.lr.platform.exception.RollBackException;
import com.lr.platform.response.Response;
import com.lr.platform.service.AdminsService;
import com.lr.platform.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * (Admins)表控制层
 *
 * @author makejava
 * @since 2022-06-29 15:30:20
 */
@Api(tags = "管理员模块")
@RestController
@RequestMapping("admin")
public class AdminsController {
    /**
     * 服务对象
     */
    @Resource
    private AdminsService adminsService;

    @Value("${recaptcha.key}")
    private String accessKey;

    @Value("${recaptcha-v2.key}")
    private String accessKeyV2;

    @Resource
    private JavaMailSenderImpl javaMailSender;

    @Resource
    private Email email;

    @Value("${spring.mail.username}")
    private String username;

    //@Resource(name = "restTemplateIgnoreSSL")
    //private RestTemplate restTemplate;

    @Resource
    private HttpServletRequest httpServletRequest;

    @ApiOperation(value = "分页查询管理员")
    @GetMapping("/page")
    public Response selectAdminPage(@RequestParam("pageSize") Integer pageSize,
                                    @RequestParam("current") Integer current){
        if (pageSize > 50 || pageSize <= 0){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_ILLEGAL_PAGESIZE)
                    .build();
        }
        if (current <= 0){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_ILLEGAL_CURRENT)
                    .build();
        }
        Admins admins=new Admins();
        PageHelper.startPage(current,pageSize);
        List<Admins> adminsList=adminsService.getAll(admins);
        PageInfo<Admins> pageInfo=new PageInfo<>(adminsList);
        List<AdminsAdminVo> adminsAdminVoList = Dozer.convert(pageInfo.getList(), AdminsAdminVo.class);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(adminsAdminVoList)
                .len((int)pageInfo.getTotal())
                .build();
    }

    @ApiOperation(value = "修改管理员",notes = "此接口密码,用户名(账号)部分无法修改")
    @ManagerLog(tag = "修改管理员",level = WarningLevel.COMMON_UPDATE)
    @PostMapping("/update")
    public Response updateAdmins(@RequestBody AdminsAdminVo adminsAdminVo){
        if (!Validator.isValidAlias(adminsAdminVo.getName())){
            return Response
                    .builder()
                    .code(ResponseCode.ADMINS_ILLEGAL_NAME)
                    .build();
        }
        if (!Validator.isValidQQ(adminsAdminVo.getQq())){
            return Response
                    .builder()
                    .code(ResponseCode.ADMINS_ILLEGAL_QQ)
                    .build();
        }
        if (!adminsService.exists(adminsAdminVo.getId())){
            return Response
                    .builder()
                    .code(ResponseCode.ADMINS_DATA_NOT_FOUND)
                    .build();
        }
        Admins admins=Dozer.convert(adminsAdminVo,Admins.class);
        admins.setUsername(null);
        admins.setPassword(null);
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        if (jwtClaims.getUid()!=1){
            admins.setEmail(null);
        }
        adminsService.update(admins);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

    @ApiOperation(value = "新增管理员",notes = "")
    @ManagerLog(level = WarningLevel.COMMON_CREATE,tag = "新增管理员")
    @PostMapping("/create")
    public Response createAdmins(@RequestBody RegisterForm registerForm){
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        if (jwtClaims.getUid()!=1){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_PERMISSION_DENIED)
                    .build();
        }
        if (!Validator.isValidQQ(registerForm.getQq())){
            return Response
                    .builder()
                    .code(ResponseCode.ADMINS_ILLEGAL_QQ)
                    .build();
        }
        if (!Validator.isValidAlias(registerForm.getName())){
            return Response
                    .builder()
                    .code(ResponseCode.ADMINS_ILLEGAL_NAME)
                    .build();
        }
        if (!Validator.isValidUsername(registerForm.getUsername())){
            return Response
                    .builder()
                    .code(ResponseCode.ADMINS_ILLEGAL_USERNAME)
                    .build();
        }
        if (!Validator.isValidPassword(registerForm.getPassword())){
            return Response
                    .builder()
                    .code(ResponseCode.ADMINS_ILLEGAL_PASSWORD)
                    .build();
        }
        if (!Validator.isValidEmail(registerForm.getEmail())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_EMAIL)
                    .build();
        }
        Admins admins=Dozer.convert(registerForm,Admins.class);
        if (adminsService.existsByAdmin(admins)){
            return Response
                    .builder()
                    .code(ResponseCode.ADMINS_USERNAME_ALREADY_REGISTERED)
                    .build();
        }
        admins.setPassword(Security.bCryptEncodePassword(registerForm.getPassword()));
        int res=adminsService.register(admins);
        if (res!=1){
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

    @ApiOperation(value = "删除管理员",notes = "后期可能会进行权限控制")
    @ManagerLog(level = WarningLevel.COMMON_DELETE,tag = "删除管理员")
    @GetMapping("/delete")
    public Response deleteAdmins(@RequestParam("id") Integer id){
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        if (jwtClaims.getUid()!=1){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_PERMISSION_DENIED)
                    .build();
        }
        if (id==1){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_PERMISSION_DENIED)
                    .build();
        }
        if (!adminsService.exists(id)){
            return Response
                    .builder()
                    .code(ResponseCode.ADMINS_DATA_NOT_FOUND)
                    .build();
        }
        Admins admins=new Admins();
        admins.setDeletedAt(new Date());
        admins.setId(id);
        adminsService.update(admins);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

    @ApiOperation(value = "登录",notes = "验证码action填 login")
    @ManagerLog(level = WarningLevel.COMMON_LOGIN,tag = "登录")
    @PostMapping("/login")
    public Response adminsLogin(@RequestBody LoginFormVo loginFormVo){
        String ip=httpServletRequest.getHeader("X-real-ip");
        String loginToken=loginFormVo.getToken();
        if (!GoogleCaptcha.isValidGoogleCaptchaV2WithV3(loginToken,"login",accessKeyV2,accessKey)){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_RECAPTCHA_ERROR)
                    .build();
        }
        if (Validator.isValidUsername(loginFormVo.getUsername())
                && Validator.isValidPassword(loginFormVo.getPassword())
                && !Objects.equals(null,ip)){
            Admins admins=new Admins();
            admins.setUsername(loginFormVo.getUsername());
            Admins query= adminsService.queryByAdmins(admins);
            if (query != null && Security.bCryptMatchPassword(loginFormVo.getPassword(),query.getPassword())){
                String token= JWTAuth.releaseToken(loginFormVo.getUsername(),3,"login",query.getId());
                LoginData loginData=new LoginData();
                loginData.setIp(ip);
                loginData.setId(query.getId());
                loginData.setUsername(query.getUsername());
                loginData.setPassword(query.getPassword());
                loginData.setToken(token);
                loginData.setTtl(3);
                String redisRes=Redis.set("manager:LoginData:"+query.getUsername(),Dozer.toJsonString(loginData),10800);
                if (Objects.equals(redisRes, "OK")){
                    HashMap<String,Object> res= new HashMap<>();
                    res.put("token",token);
                    if (query.getChangePasswordAt()!=null){
                        res.put("isChangePassword",true);
                    }else {
                        res.put("isChangePassword",false);
                    }
                    return Response
                            .builder()
                            .code(ResponseCode.COMMON_SUCCESS)
                            .data(res)
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
                .code(ResponseCode.ADMINS_WRONG_USERNAME_OR_PASSWORD)
                .build();
    }

    @ApiOperation(value = "发送验证码",notes = "action填updatePassword,谷歌填 send_code")
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
        if (!Objects.equals(action, "updatePassword")){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_PARAMETER_MISSING)
                    .build();
        }
        Admins admins= new Admins();
        admins.setEmail(registerForm.getEmail());
        admins=adminsService.queryByAdmins(admins);
        if (admins==null){
            return Response
                    .builder()
                    .code(ResponseCode.ADMINS_WRONG_EMAIL)
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
        String redisRes=Redis.set("manager:SendCode:"+uuid,code,300);
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


    @ApiOperation(value = "修改密码",notes = "仅能修改自己的密码一次,谷歌填 update_password")
    @ManagerLog(level = WarningLevel.COMMON_UPDATE,tag = "修改密码")
    @PostMapping("/updatePassword")
    public Response updatePassword(@RequestBody RegisterForm registerForm){
        String ip=httpServletRequest.getHeader("X-real-ip");
        String loginToken=registerForm.getRecaptcha();
        if (!GoogleCaptcha.isValidGoogleCaptchaV2WithV3(loginToken,"update_password",accessKeyV2,accessKey)){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_RECAPTCHA_ERROR)
                    .build();
        }
        String userToken=httpServletRequest.getHeader("token");
        JWTClaims userJwtClaims=JWTAuth.parseToken(userToken);
        if (userJwtClaims==null) throw new LoginException("请先登录");
        if (!Validator.isValidEmail(registerForm.getEmail())){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_EMAIL)
                    .build();
        }
        if (!Validator.isValidPassword(registerForm.getPassword()) ||
                Objects.equals(registerForm.getPassword().toLowerCase(),"lradmin010")){
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
        String uuid=Security.encodeMD5Hex(registerForm.getEmail()+ip+"updatePassword");
        if (jwtClaims==null){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_TOKEN)
                    .build();
        }
        if (!Objects.equals(uuid, jwtClaims.getUsername()) || !Objects.equals(jwtClaims.getOption(), "updatePassword")){
            return Response
                    .builder()
                    .code(ResponseCode.USERS_ILLEGAL_TOKEN)
                    .build();
        }
        Admins admins=adminsService.queryById(userJwtClaims.getUid());
        if (admins==null) throw new LoginException("请先登录");
        if (Security.bCryptMatchPassword(registerForm.getPassword(),admins.getPassword())){
            return Response
                    .builder()
                    .code(ResponseCode.ADMINS_ILLEGAL_PASSWORD)
                    .msg("禁止使用旧密码")
                    .build();
        }
        if (!Objects.equals(admins.getEmail(),registerForm.getEmail())){
            return Response
                    .builder()
                    .code(ResponseCode.ADMINS_WRONG_EMAIL)
                    .msg("仅能输入当前账号的注册邮箱")
                    .build();
        }
        if (admins.getChangePasswordAt()!=null){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_PERMISSION_DENIED)
                    .msg("当前账号无法再次修改密码 ")
                    .build();
        }
        Long res=(Long) Redis.evalSHA(Redis.isValidCodeSHA, Collections.singletonList("manager:SendCode:" + uuid), Collections.singletonList(code));
        if (res==null || res.intValue()!=1){
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_CODE_ERROR)
                    .build();
        }
        admins.setPassword(Security.bCryptEncodePassword(registerForm.getPassword()));
        admins.setChangePasswordAt(new Date());
        int res1=adminsService.update(admins);
        if (res1 > 1){
            throw new RollBackException("ERROR: 发现: manager:"+registerForm.getEmail()+" 存在重复项: "+res1);
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }
}

