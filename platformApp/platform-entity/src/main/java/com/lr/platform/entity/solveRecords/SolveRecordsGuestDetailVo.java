package com.lr.platform.entity.solveRecords;

import com.lr.platform.entity.admins.AdminsGuestVo;
import com.lr.platform.entity.domains.DomainsOpVo;
import com.lr.platform.entity.problems.ProblemsGuestCardsVo;

import java.util.Date;

public class SolveRecordsGuestDetailVo {
    private Integer id;

    private Date createdAt;

    private Date updatedAt;

    private Integer problemId;

    private ProblemsGuestCardsVo problem;

    //private String problemTitle;

    private Integer domainId;

    private DomainsOpVo domain;

    private Integer userId;

    //private UsersAdminVo user;

    private Float degree;

    private Integer currentScore;

    private Integer status;

    private Integer handlerId;

    private AdminsGuestVo handler;

    private Integer submitTimes;

    private Date latestSubmissionTime;

    private Integer hasPdf;

    private Integer hasOtherFile;

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

    public ProblemsGuestCardsVo getProblem() {
        return problem;
    }

    public void setProblem(ProblemsGuestCardsVo problem) {
        this.problem = problem;
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

    public Integer getDomainId() {
        return domainId;
    }

    public void setDomainId(Integer domainId) {
        this.domainId = domainId;
    }

    public DomainsOpVo getDomain() {
        return domain;
    }

    public void setDomain(DomainsOpVo domain) {
        this.domain = domain;
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

    public AdminsGuestVo getHandler() {
        return handler;
    }

    public void setHandler(AdminsGuestVo handler) {
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
