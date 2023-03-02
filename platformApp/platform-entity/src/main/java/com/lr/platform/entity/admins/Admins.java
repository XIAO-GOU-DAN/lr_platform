package com.lr.platform.entity.admins;

import java.io.Serializable;
import java.util.Date;

/**
 * (Admins)实体类
 *
 * @author makejava
 * @since 2022-07-18 15:15:48
 */
public class Admins implements Serializable {
    private static final long serialVersionUID = -88369683183379971L;
    
    private Integer id;
    
    private Date createdAt;
    
    private Date updatedAt;
    
    private Date deletedAt;
    
    private String username;
    
    private String password;
    
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

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

