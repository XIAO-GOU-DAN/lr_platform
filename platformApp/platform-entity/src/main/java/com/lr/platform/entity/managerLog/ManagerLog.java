package com.lr.platform.entity.managerLog;

import com.lr.platform.annotations.PrivacyEncrypt;
import com.lr.platform.enums.PrivacyType;

import java.io.Serializable;
import java.util.Date;

/**
 * (Log)实体类
 *
 * @author makejava
 * @since 2022-06-28 21:38:36
 */
public class ManagerLog implements Serializable {
    private static final long serialVersionUID = 107120172054742929L;
    
    private Integer id;
    
    private Date createdAt;
    
    private Date updatedAt;
    
    private Date deletedAt;
    
    private Integer userId;
    
    private Integer level;
    
    private String commonOption;
    
    private String op;
    
    private Integer timeConsuming;

    private String ip;
    
    private String parameter;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getCommonOption() {
        return commonOption;
    }

    public void setCommonOption(String commonOption) {
        this.commonOption = commonOption;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public Integer getTimeConsuming() {
        return timeConsuming;
    }

    public void setTimeConsuming(Integer timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

}

