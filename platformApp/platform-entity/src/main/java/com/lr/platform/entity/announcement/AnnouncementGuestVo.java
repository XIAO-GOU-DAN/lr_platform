package com.lr.platform.entity.announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lr.platform.entity.admins.AdminsAdminVo;
import com.lr.platform.entity.admins.AdminsGuestVo;

import java.util.Date;

public class AnnouncementGuestVo {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private Date createdAt;

    private Date updatedAt;

    private String title;

    private String content;
    /**
     * 发布人id
     */
    private Integer firstAdminId;

    private AdminsGuestVo firstAdmin;
    /**
     * 最后修改人id
     */
    private Integer latestAdminId;

    private AdminsGuestVo latestAdmin;

    private String announcementType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getFirstAdminId() {
        return firstAdminId;
    }

    public void setFirstAdminId(Integer firstAdminId) {
        this.firstAdminId = firstAdminId;
    }

    public AdminsGuestVo getFirstAdmin() {
        return firstAdmin;
    }

    public void setFirstAdmin(AdminsGuestVo firstAdmin) {
        this.firstAdmin = firstAdmin;
    }

    public Integer getLatestAdminId() {
        return latestAdminId;
    }

    public void setLatestAdminId(Integer latestAdminId) {
        this.latestAdminId = latestAdminId;
    }

    public AdminsGuestVo getLatestAdmin() {
        return latestAdmin;
    }

    public void setLatestAdmin(AdminsGuestVo latestAdmin) {
        this.latestAdmin = latestAdmin;
    }

    public String getAnnouncementType() {
        return announcementType;
    }

    public void setAnnouncementType(String announcementType) {
        this.announcementType = announcementType;
    }
}
