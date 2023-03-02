package com.lr.platform.controller.domainsAdmins;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lr.platform.annotations.ManagerLog;
import com.lr.platform.entity.admins.Admins;
import com.lr.platform.entity.admins.AdminsAdminVo;
import com.lr.platform.entity.domains.Domains;
import com.lr.platform.entity.domains.DomainsVo;
import com.lr.platform.entity.domainsAdmins.DomainsAdmins;
import com.lr.platform.entity.domainsAdmins.DomainsAdminsAdminDetailVo;
import com.lr.platform.entity.domainsAdmins.DomainsAdminsVo;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.enums.WarningLevel;
import com.lr.platform.response.Response;
import com.lr.platform.service.AdminsService;
import com.lr.platform.service.DomainsAdminsService;
import com.lr.platform.service.DomainsService;
import com.lr.platform.utils.Dozer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * (DomainsAdmins)表控制层
 *
 * @author makejava
 * @since 2022-06-29 15:37:21
 */
@Api(tags = "负责人模块")
@RestController
@RequestMapping("domainsAdmin")
public class DomainsAdminsController {
    /**
     * 服务对象
     */
    @Resource
    private DomainsAdminsService domainsAdminsService;

    @Resource
    private AdminsService adminsService;

    @Resource
    private DomainsService domainsService;

    @ApiOperation(value = "分页查询负责人",notes = "pageSize 页面大小不能超过50 从1开始, current 选择页从1开始, 方向id留空返回所有负责人")
    @GetMapping("/page")
    public Response selectDomainsAdminsPage(@RequestParam("pageSize") Integer pageSize,
                                            @RequestParam("current") Integer current,
                                            @RequestParam(value = "domainId",required = false) Integer domainId){
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
        DomainsAdmins domainsAdmins=new DomainsAdmins();
        domainsAdmins.setDomainId(domainId);
        PageHelper.startPage(current,pageSize);
        List<DomainsAdmins> domainsAdminsList=domainsAdminsService.queryAll(domainsAdmins);
        PageInfo<DomainsAdmins> pageInfo=new PageInfo<>(domainsAdminsList);
        List<DomainsAdminsAdminDetailVo> domainsAdminsAdminDetailVos = Dozer.convert(pageInfo.getList(), DomainsAdminsAdminDetailVo.class);
        for (DomainsAdminsAdminDetailVo e: domainsAdminsAdminDetailVos){
            Admins admins=adminsService.queryById(e.getAdminId());
            Domains domains=domainsService.queryById(e.getDomainId());//?cache
            e.setAdmin(Dozer.convert(admins, AdminsAdminVo.class));
            e.setDomain(Dozer.convert(domains,DomainsVo.class));
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .len((int) pageInfo.getTotal())
                .data(domainsAdminsAdminDetailVos)
                .build();
    }

    @ApiOperation(value = "新增负责人",notes = "")
    @ManagerLog(tag = "新增负责人",level = WarningLevel.COMMON_CREATE)
    @PostMapping("/create")
    public Response createDomainsAdmins(@RequestBody DomainsAdminsVo domainsAdminsVo){
        if (!adminsService.exists(domainsAdminsVo.getAdminId())) {
            return Response
                    .builder()
                    .code(ResponseCode.DOMAIN_ADMIN_ILLEGAL_ADMIN_ID)
                    .build();
        }
        if (!domainsService.exists(domainsAdminsVo.getDomainId())) {
            return Response
                    .builder()
                    .code(ResponseCode.DOMAIN_ADMIN_ILLEGAL_DOMAIN_ID)
                    .build();
        }
        DomainsAdmins domainsAdmins = Dozer.convert(domainsAdminsVo, DomainsAdmins.class);
        domainsAdmins.setId(null);
        if (domainsAdminsService.existsByDomainAdmin(domainsAdmins)) {
            return Response
                    .builder()
                    .code(ResponseCode.DOMAIN_ADMIN_EXISTS_DOMAIN_ADMIN_ID)
                    .build();
        }
        int res=domainsAdminsService.register(domainsAdmins);
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

    @ApiOperation(value = "修改负责人")
    @ManagerLog(tag = "修改负责人",level = WarningLevel.COMMON_UPDATE)
    @PostMapping("/update")
    public Response updateDomainsAdmins(@RequestBody DomainsAdminsVo domainsAdminsVo){
        if (!domainsAdminsService.exists(domainsAdminsVo.getId())){
            return Response
                    .builder()
                    .code(ResponseCode.DOMAIN_ADMIN_ILLEGAL_DOMAIN_ADMIN_ID)
                    .build();
        }
        if (!adminsService.exists(domainsAdminsVo.getAdminId())) {
            return Response
                    .builder()
                    .code(ResponseCode.DOMAIN_ADMIN_ILLEGAL_ADMIN_ID)
                    .build();
        }
        if (!domainsService.exists(domainsAdminsVo.getDomainId())) {
            return Response
                    .builder()
                    .code(ResponseCode.DOMAIN_ADMIN_ILLEGAL_DOMAIN_ID)
                    .build();
        }
        DomainsAdmins domainsAdmins = Dozer.convert(domainsAdminsVo, DomainsAdmins.class);
        domainsAdmins.setId(null);
        if (domainsAdminsService.existsByDomainAdmin(domainsAdmins)) {
            return Response
                    .builder()
                    .code(ResponseCode.DOMAIN_ADMIN_EXISTS_DOMAIN_ADMIN_ID)
                    .build();
        }
        domainsAdmins.setId(domainsAdminsVo.getId());
        domainsAdminsService.update(domainsAdmins);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

    @ApiOperation(value = "删除负责人")
    @ManagerLog(tag = "删除负责人",level = WarningLevel.COMMON_DELETE)
    @GetMapping("/delete")
    public Response deleteDomainsAdmins(@RequestParam("id") Integer id){
        DomainsAdmins domainsAdmins = domainsAdminsService.queryById(id);
        if (domainsAdmins == null) {
            return Response
                    .builder()
                    .code(ResponseCode.DOMAIN_ADMIN_ILLEGAL_DOMAIN_ADMIN_ID)
                    .build();
        }
        domainsAdmins.setDeletedAt(new Date());
        domainsAdminsService.update(domainsAdmins);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

}

