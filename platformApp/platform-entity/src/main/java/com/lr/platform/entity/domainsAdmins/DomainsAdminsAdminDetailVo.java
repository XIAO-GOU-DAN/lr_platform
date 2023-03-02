package com.lr.platform.entity.domainsAdmins;


import com.lr.platform.entity.admins.AdminsAdminVo;
import com.lr.platform.entity.domains.DomainsVo;

import java.util.Date;

public class DomainsAdminsAdminDetailVo {
    private Integer id;

    private Date createdAt;

    private Date updatedAt;

    private Integer domainId;

    private Integer adminId;

    private AdminsAdminVo admin;

    private DomainsVo domain;

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

    public Integer getDomainId() {
        return domainId;
    }

    public void setDomainId(Integer domainId) {
        this.domainId = domainId;
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public AdminsAdminVo getAdmin() {
        return admin;
    }

    public void setAdmin(AdminsAdminVo admin) {
        this.admin = admin;
    }

    public DomainsVo getDomain() {
        return domain;
    }

    public void setDomain(DomainsVo domain) {
        this.domain = domain;
    }
}
