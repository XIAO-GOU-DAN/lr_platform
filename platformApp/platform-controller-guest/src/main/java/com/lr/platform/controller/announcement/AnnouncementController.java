package com.lr.platform.controller.announcement;

import com.lr.platform.entity.admins.Admins;
import com.lr.platform.entity.admins.AdminsGuestVo;
import com.lr.platform.entity.announcement.Announcement;
import com.lr.platform.entity.announcement.AnnouncementGuestVo;
import com.lr.platform.entity.announcement.AnnouncementOffsetVo;
import com.lr.platform.enums.ResponseCode;
import com.lr.platform.response.Response;
import com.lr.platform.service.AdminsService;
import com.lr.platform.service.AnnouncementService;
import com.lr.platform.utils.Dozer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@RestController
@Api(tags = "公告模块")
@RequestMapping("announcement")
public class AnnouncementController {
    @Resource
    private AnnouncementService announcementService;

    @Resource
    private AdminsService adminsService;

    @ApiOperation(value = "获取公告",notes = "next_offset: 返回时数据会附带next_offset的值,更新到下一页时传入该值,返回比该id小的内容并按时间排序,不传offset则从最新的公告开始返回,type:dynamic 普通公告, dialog 弹窗公告, all 全部")
    @GetMapping("/page")
    public Response getDynamic(@RequestParam(value = "next_offset",required = false) Long offsetId,
                               @RequestParam(value = "end_offset",required = false) Long endOffset,
                               @RequestParam(value = "type",required = false) String type){
        AnnouncementOffsetVo announcement=new AnnouncementOffsetVo();
        announcement.setNextOffset(offsetId);
        announcement.setEndId(endOffset);
        if ("dynamic".equalsIgnoreCase(type) || "dialog".equalsIgnoreCase(type)){
            announcement.setAnnouncementType(type.toLowerCase());
        }
        List<Announcement> announcementList=announcementService.queryByOffsetId(announcement);
        List<AnnouncementGuestVo> announcementGuestVoList= Dozer.convert(announcementList,AnnouncementGuestVo.class);
        for (AnnouncementGuestVo a:announcementGuestVoList){
            Admins admins;
            if (a.getFirstAdminId()!=null){
                admins=adminsService.queryById(a.getFirstAdminId());
                a.setFirstAdmin(Dozer.convert(admins, AdminsGuestVo.class));
            }
            if (a.getLatestAdminId()!=null){
                admins=adminsService.queryById(a.getLatestAdminId());
                a.setLatestAdmin(Dozer.convert(admins, AdminsGuestVo.class));
            }
        }
        HashMap<String,Object> data = new HashMap<>();
        data.put("items",announcementGuestVoList);
        if (!announcementGuestVoList.isEmpty()){
            AnnouncementGuestVo announcementGuestVo=announcementGuestVoList.get(announcementGuestVoList.size()-1);
            data.put("next_offset",announcementGuestVo.getId().toString());
            announcementGuestVo=announcementGuestVoList.get(0);
            data.put("update_offset",announcementGuestVo.getId().toString());
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(data)
                .build();
    }

    @ApiOperation(value = "获取公告",notes = "update_offset: 返回时数据会附带update_offset的值,查询时传入该值,返回比该id大的内容并按时间排序,不传offset则从最新的公告开始返回,type:dynamic 普通公告, dialog 弹窗公告, all 全部")
    @GetMapping("/update")
    public Response getUpdateDynamic(@RequestParam(value = "update_offset",required = false) Long offsetId,
                               @RequestParam(value = "end_offset",required = false) Long endOffset,
                               @RequestParam(value = "type",required = false) String type){
        AnnouncementOffsetVo announcement=new AnnouncementOffsetVo();
        announcement.setUpdateOffset(offsetId);
        announcement.setEndId(endOffset);
        if ("dynamic".equalsIgnoreCase(type) || "dialog".equalsIgnoreCase(type)){
            announcement.setAnnouncementType(type.toLowerCase());
        }
        List<Announcement> announcementList=announcementService.queryByUpdateOffset(announcement);
        List<AnnouncementGuestVo> announcementGuestVoList= Dozer.convert(announcementList,AnnouncementGuestVo.class);
        for (AnnouncementGuestVo a:announcementGuestVoList){
            Admins admins;
            if (a.getFirstAdminId()!=null){
                admins=adminsService.queryById(a.getFirstAdminId());
                a.setFirstAdmin(Dozer.convert(admins, AdminsGuestVo.class));
            }
            if (a.getLatestAdminId()!=null){
                admins=adminsService.queryById(a.getLatestAdminId());
                a.setLatestAdmin(Dozer.convert(admins, AdminsGuestVo.class));
            }
        }
        HashMap<String,Object> data = new HashMap<>();
        data.put("items",announcementGuestVoList);
        if (!announcementGuestVoList.isEmpty()){
            AnnouncementGuestVo announcementGuestVo=announcementGuestVoList.get(announcementGuestVoList.size()-1);
            data.put("next_offset",announcementGuestVo.getId().toString());
            announcementGuestVo=announcementGuestVoList.get(0);
            data.put("update_offset",announcementGuestVo.getId().toString());
        }
        return Response
                .builder()
                .code(ResponseCode.COMMON_SUCCESS)
                .data(data)
                .build();
    }
}
