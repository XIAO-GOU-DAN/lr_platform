package com.lr.platform.entity.guestLog;

import com.lr.platform.entity.admins.AdminsAdminVo;
import com.lr.platform.entity.users.UsersAdminVo;

import java.util.Date;

public class GuestLogSuperAdminVo {
    private Integer id;
    //操作日期
    private Date createdAt;
    //操作用户
    private Integer userId;

    private UsersAdminVo user;
    //1删除 2新增,修改 3登录
    private Integer level;
    //新增,修改,删除,登录
    private String commonOption;
    //新增xx,修改xx,删除xx,登录
    private String op;
    //耗时 ms
    private Integer timeConsuming;
    //url参数
    //@PrivacyEncrypt(type = PrivacyType.IP)
    private String ip;
    //body参数
    private String parameter;

    public UsersAdminVo getUser() {
        return user;
    }

    public void setUser(UsersAdminVo user) {
        this.user = user;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

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


    public String getParameter() {
        return parameter;
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }
}
