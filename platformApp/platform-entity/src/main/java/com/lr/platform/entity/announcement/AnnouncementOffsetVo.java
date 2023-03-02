package com.lr.platform.entity.announcement;

import java.util.Date;

public class AnnouncementOffsetVo {
    private Long id;

    private Long nextOffset;

    private Long updateOffset;

    private Long endId;

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

    public Long getEndId() {
        return endId;
    }

    public void setEndId(Long endId) {
        this.endId = endId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(Long nextOffset) {
        this.nextOffset = nextOffset;
    }

    public Long getUpdateOffset() {
        return updateOffset;
    }

    public void setUpdateOffset(Long updateOffset) {
        this.updateOffset = updateOffset;
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

    public String getAnnouncementType() {
        return announcementType;
    }

    public void setAnnouncementType(String announcementType) {
        this.announcementType = announcementType;
    }
}
