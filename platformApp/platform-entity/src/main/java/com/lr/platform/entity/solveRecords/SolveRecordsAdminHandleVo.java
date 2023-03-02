package com.lr.platform.entity.solveRecords;

import com.lr.platform.entity.admins.AdminsAdminVo;
import com.lr.platform.entity.problems.ProblemsAdminHandlerVo;
import com.lr.platform.entity.users.UsersAdminVo;

import java.util.Date;

public class SolveRecordsAdminHandleVo {
    private Integer id;

    private Date createdAt;

    private Date updatedAt;

    private Integer problemId;

    private ProblemsAdminHandlerVo problem;

    private Integer userId;

    private UsersAdminVo user;

    private Float degree;

    private Integer score;

    private Integer status; //1 未读 2已读 3已评阅

    private Integer handlerId;

    private AdminsAdminVo handler;

    private Integer submitTimes;

    private Date latestSubmissionTime;

    private Integer hasPdf;

    private Integer hasOtherFile;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getHasPdf() {
        return hasPdf;
    }

    public void setHasPdf(Integer hasPdf) {
        this.hasPdf = hasPdf;
    }

    public Integer getHasOtherFile() {
        return hasOtherFile;
    }

    public void setHasOtherFile(Integer hasOtherFile) {
        this.hasOtherFile = hasOtherFile;
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

    public ProblemsAdminHandlerVo getProblem() {
        return problem;
    }

    public void setProblem(ProblemsAdminHandlerVo problem) {
        this.problem = problem;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public UsersAdminVo getUser() {
        return user;
    }

    public void setUser(UsersAdminVo user) {
        this.user = user;
    }

    public Float getDegree() {
        return degree;
    }

    public void setDegree(Float degree) {
        this.degree = degree;
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

    public AdminsAdminVo getHandler() {
        return handler;
    }

    public void setHandler(AdminsAdminVo handler) {
        this.handler = handler;
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
