package com.lr.platform.controller.guestLog;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lr.platform.entity.JWTClaims;
import com.lr.platform.entity.guestLog.GuestLog;
import com.lr.platform.entity.guestLog.GuestLogSuperAdminVo;
import com.lr.platform.entity.guestLog.GuestLogVo;
import com.lr.platform.entity.users.Users;
import com.lr.platform.entity.users.UsersAdminVo;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.exception.LoginException;
import com.lr.platform.response.Response;
import com.lr.platform.service.GuestLogService;
import com.lr.platform.service.UsersService;
import com.lr.platform.utils.Dozer;
import com.lr.platform.utils.JWTAuth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("guestLog")
@Api(tags = "用户日志模块")
public class GuestLogController {

    @Resource
    private HttpServletRequest httpServletRequest;

    @Resource
    private UsersService usersService;

    @Resource
    private GuestLogService guestLogService;

    @ApiOperation(value = "分页查询日志",notes = "pageSize 页面大小不能超过50, current 选择页从1开始")
    @GetMapping("/page")
    public Response getGuestLogPage(@RequestParam("pageSize") Integer pageSize,
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
        GuestLog guestLog=new GuestLog();
        PageHelper.startPage(current,pageSize);
        List<GuestLog> guestLogList=guestLogService.queryAll(guestLog);
        PageInfo<GuestLog> pageInfo=new PageInfo<>(guestLogList);
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        HashMap<Integer, UsersAdminVo> usersAdminVoHashMap=new HashMap<>();
        if (jwtClaims.getUid()==1){
            List<GuestLogSuperAdminVo> managerLogVos = Dozer.convert(pageInfo.getList(), GuestLogSuperAdminVo.class);
            for (GuestLogSuperAdminVo e:managerLogVos){
                if (e.getUserId() != null){
                    UsersAdminVo adminsAdminVo=usersAdminVoHashMap.get(e.getId());
                    if (adminsAdminVo==null){
                        Users admins=usersService.queryById(e.getUserId());
                        adminsAdminVo=Dozer.convert(admins, UsersAdminVo.class);
                        usersAdminVoHashMap.put(e.getUserId(),adminsAdminVo);
                    }
                    e.setUser(adminsAdminVo);
                }
            }
            return Response
                    .builder()
                    .code(ResponseCode.COMMON_SUCCESS)
                    .data(managerLogVos)
                    .len((int)pageInfo.getTotal())
                    .build();
        }
        List<GuestLogVo> managerLogVos = Dozer.convert(pageInfo.getList(), GuestLogVo.class);
        for (GuestLogVo e:managerLogVos){
            if (e.getUserId() != null){
                UsersAdminVo adminsAdminVo=usersAdminVoHashMap.get(e.getId());
                if (adminsAdminVo==null){
                    Users admins=usersService.queryById(e.getUserId());
                    adminsAdminVo=Dozer.convert(admins, UsersAdminVo.class);
                    usersAdminVoHashMap.put(e.getUserId(),adminsAdminVo);
                }
                e.setUser(adminsAdminVo);
            }
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(managerLogVos)
                .len((int)pageInfo.getTotal())
                .build();
    }
}
