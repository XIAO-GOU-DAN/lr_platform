package com.lr.platform.entity.domains;

public class DomainsAdminOpVo {
    private Integer id;

    private String name;

    private String description;

    private Long newCommit;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Long getNewCommit() {
        return newCommit;
    }

    public void setNewCommit(Long newCommit) {
        this.newCommit = newCommit;
    }
}
