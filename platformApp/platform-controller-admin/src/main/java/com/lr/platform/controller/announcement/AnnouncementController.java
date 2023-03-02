package com.lr.platform.controller.announcement;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lr.platform.annotations.ManagerLog;
import com.lr.platform.entity.JWTClaims;
import com.lr.platform.entity.admins.Admins;
import com.lr.platform.entity.admins.AdminsAdminVo;
import com.lr.platform.entity.announcement.Announcement;
import com.lr.platform.entity.announcement.AnnouncementAdminVo;
import com.lr.platform.entity.announcement.AnnouncementEditVo;
import com.lr.platform.entity.announcement.AnnouncementUpdate;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.enums.WarningLevel;
import com.lr.platform.exception.LoginException;
import com.lr.platform.response.Response;
import com.lr.platform.service.AdminsService;
import com.lr.platform.service.AnnouncementService;
import com.lr.platform.utils.Dozer;
import com.lr.platform.utils.JWTAuth;
import com.lr.platform.utils.Snowflake;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Api(tags = "公告模块")
@RestController
@RequestMapping("announcement")
public class AnnouncementController {

    @Resource
    private AnnouncementService announcementService;

    @Resource
    private AdminsService adminsService;

    @Resource
    private HttpServletRequest httpServletRequest;

    @GetMapping
    @ApiOperation(value = "id获取公告")
    public Response getAnnouncement(@RequestParam("id") Long id){
        Announcement announcement=announcementService.queryById(id);
        if (announcement==null){
            return Response.builder()
                            .code(ResponseCode.ANNOUNCEMENT_NOT_FOUNDED)
                            .build();
        }
        AnnouncementAdminVo announcementAdminVo= Dozer.convert(announcement,AnnouncementAdminVo.class);
        if (announcementAdminVo.getFirstAdminId()!=null){
            Admins admins=adminsService.queryById(announcementAdminVo.getFirstAdminId());
            announcementAdminVo.setFirstAdmin(Dozer.convert(admins, AdminsAdminVo.class));
        }
        if (announcementAdminVo.getLatestAdminId()!=null){
            Admins admins=adminsService.queryById(announcementAdminVo.getLatestAdminId());
            announcementAdminVo.setLatestAdmin(Dozer.convert(admins,AdminsAdminVo.class));
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(announcementAdminVo)
                .build();
    }

    @ApiOperation(value = "分页获取公告",notes = "")
    @GetMapping("/page")
    public Response selectProblemsPage(@RequestParam("pageSize") Integer pageSize,
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
        Announcement announcement=new Announcement();
        PageHelper.startPage(current,pageSize);
        List<Announcement> announcementList=announcementService.queryAll(announcement);
        PageInfo<Announcement> pageInfo=new PageInfo<>(announcementList);
        List<AnnouncementAdminVo> announcementAdminVoList=Dozer.convert(pageInfo.getList(),AnnouncementAdminVo.class);
        Admins admins;
        for (AnnouncementAdminVo a:announcementAdminVoList){
            if (a.getFirstAdminId()!=null){
                admins=adminsService.queryById(a.getFirstAdminId());
                a.setFirstAdmin(Dozer.convert(admins,AdminsAdminVo.class));
            }
            if (a.getLatestAdminId()!=null){
                admins=adminsService.queryById(a.getLatestAdminId());
                a.setLatestAdmin(Dozer.convert(admins,AdminsAdminVo.class));
            }
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(announcementAdminVoList)
                .len((int)pageInfo.getTotal())
                .build();
    }

    @PostMapping("/create")
    @ApiOperation(value = "发布新公告",notes = "announcementType : dynamic 非弹窗公告 dialog 弹窗公告")
    @ManagerLog(tag = "发布公告",level = WarningLevel.COMMON_CREATE)
    public Response insertAnnouncement(@RequestBody AnnouncementEditVo announcementEditVo){
        announcementEditVo.setId(null);
        if (announcementEditVo.getTitle().length()>50){
            return Response
                    .builder()
                    .code(ResponseCode.ANNOUNCEMENT_TITLE_TOO_LONG)
                    .build();
        }
        if (!Objects.equals(announcementEditVo.getAnnouncementType(),"dynamic") &&
                !Objects.equals(announcementEditVo.getAnnouncementType(),"dialog")){
            return Response
                    .builder()
                    .code(ResponseCode.ANNOUNCEMENT_ILLEGAL_TYPE)
                    .build();
        }
        Announcement announcement=Dozer.convert(announcementEditVo,Announcement.class);
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        announcement.setFirstAdminId(jwtClaims.getUid());
        announcement.setLatestAdminId(jwtClaims.getUid());
        announcement.setId(Snowflake.uniqueLong());
        announcementService.insert(announcement);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改公告",notes = "isReset 对弹窗 0:修改前已阅读的新生在修改后将不在弹窗, 1:修改前已阅读的新生将在修改后重新弹窗,默认为1, 对非弹窗: 0:修改后不改变显示顺序, 1:修改后将排到非弹窗公告的第一位")
    @ManagerLog(tag = "修改公告",level = WarningLevel.COMMON_UPDATE)
    public Response updateAnnouncement(@RequestBody AnnouncementEditVo announcementEditVo,
                                       @RequestParam(value = "isReset",required = false) Integer isReset){
        if (announcementEditVo.getTitle().length()>50){
            return Response
                    .builder()
                    .code(ResponseCode.ANNOUNCEMENT_TITLE_TOO_LONG)
                    .build();
        }
        if (!Objects.equals(announcementEditVo.getAnnouncementType(),"dynamic") &&
                !Objects.equals(announcementEditVo.getAnnouncementType(),"dialog")){
            return Response
                    .builder()
                    .code(ResponseCode.ANNOUNCEMENT_ILLEGAL_TYPE)
                    .build();
        }
        Announcement announcement=announcementService.queryById(announcementEditVo.getId());
        if (announcement==null){
            return Response
                    .builder()
                    .code(ResponseCode.ANNOUNCEMENT_NOT_FOUNDED)
                    .build();
        }
        AnnouncementUpdate announcementUpdate=Dozer.convert(announcementEditVo,AnnouncementUpdate.class);
        announcementUpdate.setRawId(announcementEditVo.getId());
        String token=httpServletRequest.getHeader("token");
        JWTClaims jwtClaims= JWTAuth.parseToken(token);
        if (jwtClaims==null) throw new LoginException("请先登录");
        announcementUpdate.setLatestAdminId(jwtClaims.getUid());
        if (Objects.equals(isReset,0)){
            announcementUpdate.setSetId(null);
        }else {
            announcementUpdate.setSetId(Snowflake.uniqueLong());
        }
        announcementService.update(announcementUpdate);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }

    @ApiOperation(value = "删除公告")
    @ManagerLog(tag = "删除公告",level = WarningLevel.COMMON_DELETE)
    @PostMapping("/delete")
    public Response deleteAnnouncement(@RequestParam("id")Long id){
        Announcement announcement=announcementService.queryById(id);
        if (announcement==null){
            return Response
                    .builder()
                    .code(ResponseCode.ANNOUNCEMENT_NOT_FOUNDED)
                    .build();
        }
        AnnouncementUpdate announcementUpdate=new AnnouncementUpdate();
        announcementUpdate.setRawId(id);
        announcementUpdate.setDeletedAt(new Date());
        announcementService.update(announcementUpdate);
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .build();
    }
}
