package com.lr.platform.entity.solveRecords;

import com.lr.platform.entity.admins.AdminsAdminVo;
import com.lr.platform.entity.domains.DomainsVo;
import com.lr.platform.entity.problems.ProblemsAdminHandlerVo;
import com.lr.platform.entity.users.UsersAdminVo;

import java.util.Date;

public class SolveRecordsAdminDetailVo {
    private Integer id;

    private Date createdAt;

    private Date updatedAt;

    private Integer problemId;

    private ProblemsAdminHandlerVo problem;
    //private String problemTitle;

    //private Integer domainId;

    //private DomainsVo domain;

    private Integer userId;

    private UsersAdminVo user;

    private Float degree;

    private Integer currentScore;

    private Integer status;

    private Integer handlerId;

    private AdminsAdminVo handler;

    private Integer submitTimes;

    private Date latestSubmissionTime;

    public ProblemsAdminHandlerVo getProblem() {
        return problem;
    }

    public void setProblem(ProblemsAdminHandlerVo problem) {
        this.problem = problem;
    }

    public UsersAdminVo getUser() {
        return user;
    }

    public void setUser(UsersAdminVo user) {
        this.user = user;
    }

    public AdminsAdminVo getHandler() {
        return handler;
    }

    public void setHandler(AdminsAdminVo handler) {
        this.handler = handler;
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

    public Integer getProblemId() {
        return problemId;
    }

    public void setProblemId(Integer problemId) {
        this.problemId = problemId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Float getDegree() {
        return degree;
    }

    public void setDegree(Float degree) {
        this.degree = degree;
    }

    public Integer getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Integer currentScore) {
        this.currentScore = currentScore;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getHandlerId() {
        return handlerId;
    }

    public void setHandlerId(Integer handlerId) {
        this.handlerId = handlerId;
    }

    public Integer getSubmitTimes() {
        return submitTimes;
    }

    public void setSubmitTimes(Integer submitTimes) {
        this.submitTimes = submitTimes;
    }

    public Date getLatestSubmissionTime() {
        return latestSubmissionTime;
    }

    public void setLatestSubmissionTime(Date latestSubmissionTime) {
        this.latestSubmissionTime = latestSubmissionTime;
    }
}
