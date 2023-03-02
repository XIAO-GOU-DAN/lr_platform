package com.lr.platform.controller.managerLog;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lr.platform.entity.JWTClaims;
import com.lr.platform.entity.admins.Admins;
import com.lr.platform.entity.admins.AdminsAdminVo;
import com.lr.platform.entity.managerLog.ManagerLog;
import com.lr.platform.entity.managerLog.ManagerLogSuperAdminVo;
import com.lr.platform.entity.managerLog.ManagerLogVo;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.exception.LoginException;
import com.lr.platform.response.Response;
import com.lr.platform.service.AdminsService;
import com.lr.platform.service.ManagerLogService;
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

/**
 * (Log)表控制层
 *
 * @author makejava
 * @since 2022-06-28 17:12:05
 */
@RestController
@Api(tags = "管理员日志模块")
@RequestMapping("managerLog")
public class ManagerLogController {
    /**
     * 服务对象
     */
    @Resource
    private ManagerLogService managerLogService;

    @Resource
    private HttpServletRequest httpServletRequest;

    @Resource
    private AdminsService adminsService;

    @ApiOperation(value = "分页查询日志",notes = "pageSize 页面大小不能超过50, current 选择页从1开始")
    @GetMapping("/page")
    public Response getManagerLogPage(@RequestParam("pageSize") Integer pageSize,
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
        ManagerLog managerLog =new ManagerLog();
        PageHelper.startPage(current,pageSize);
        List<ManagerLog> managerLogs = managerLogService.getAll(managerLog);
        PageInfo<ManagerLog> pageInfo=new PageInfo<>(managerLogs);
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        HashMap<Integer,AdminsAdminVo> adminsAdminVoHashMap=new HashMap<>();
        if (jwtClaims.getUid()==1){
            List<ManagerLogSuperAdminVo> managerLogVos = Dozer.convert(pageInfo.getList(), ManagerLogSuperAdminVo.class);
            for (ManagerLogSuperAdminVo e:managerLogVos){
                if (e.getUserId() != null){
                    AdminsAdminVo adminsAdminVo=adminsAdminVoHashMap.get(e.getId());
                    if (adminsAdminVo==null){
                        Admins admins=adminsService.queryById(e.getUserId());
                        adminsAdminVo=Dozer.convert(admins, AdminsAdminVo.class);
                        adminsAdminVoHashMap.put(e.getUserId(),adminsAdminVo);
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
        List<ManagerLogVo> managerLogVos = Dozer.convert(pageInfo.getList(), ManagerLogVo.class);
        for (ManagerLogVo e:managerLogVos){
            if (e.getUserId() != null){
                AdminsAdminVo adminsAdminVo=adminsAdminVoHashMap.get(e.getId());
                if (adminsAdminVo==null){
                    Admins admins=adminsService.queryById(e.getUserId());
                    adminsAdminVo=Dozer.convert(admins, AdminsAdminVo.class);
                    adminsAdminVoHashMap.put(e.getUserId(),adminsAdminVo);
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

