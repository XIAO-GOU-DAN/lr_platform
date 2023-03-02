package com.lr.platform.entity.admins;


import java.util.Date;

public class AdminsAdminVo {
    private Integer id;

    private Date createdAt;

    private Date updatedAt;

    private String username;

    private String name;

    private String qq;

    private String email;

    private Date changePasswordAt;

    public Date getChangePasswordAt() {
        return changePasswordAt;
    }

    public void setChangePasswordAt(Date changePasswordAt) {
        this.changePasswordAt = changePasswordAt;
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

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
