package com.lr.platform.entity.announcement;

import java.util.Date;
import java.io.Serializable;

/**
 * (Announcement)实体类
 *
 * @author makejava
 * @since 2022-08-24 19:32:49
 */
public class Announcement implements Serializable {
    private static final long serialVersionUID = -24275780438334161L;
    
    private Long id;
    
    private Date createdAt;
    
    private Date updatedAt;
    
    private Date deletedAt;
    
    private String title;
    
    private String content;
    /**
     * 发布人id
     */
    private Integer firstAdminId;
    /**
     * 最后修改人id
     */
    private Integer latestAdminId;

    private String announcementType;

    public String getAnnouncementType() {
        return announcementType;
    }

    public void setAnnouncementType(String announcementType) {
        this.announcementType = announcementType;
    }

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

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
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

    public Integer getLatestAdminId() {
        return latestAdminId;
    }

    public void setLatestAdminId(Integer latestAdminId) {
        this.latestAdminId = latestAdminId;
    }

}

