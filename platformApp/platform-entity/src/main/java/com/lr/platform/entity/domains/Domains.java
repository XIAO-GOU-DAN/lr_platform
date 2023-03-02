package com.lr.platform.entity.domains;

import java.io.Serializable;
import java.util.Date;

/**
 * (Domains)实体类
 *
 * @author makejava
 * @since 2022-07-09 18:44:13
 */
public class Domains implements Serializable {
    private static final long serialVersionUID = 823786841198634302L;
    
    private Integer id;
    
    private Date createdAt;
    
    private Date updatedAt;
    
    private Date deletedAt;
    
    private String name;
    
    private String description;


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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

