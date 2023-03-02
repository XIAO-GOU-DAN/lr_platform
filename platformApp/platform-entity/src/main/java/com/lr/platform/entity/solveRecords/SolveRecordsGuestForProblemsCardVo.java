package com.lr.platform.entity.solveRecords;

import java.util.Date;

public class SolveRecordsGuestForProblemsCardVo {
    private Integer id;

    private Date createdAt;

    private Date updatedAt;

    private Integer problemId;

    private Integer userId;

    private Float degree;

    private Integer status; //1 未读 2已读 3已评阅

    private Integer handlerId;

    private Integer submitTimes;

    private Date latestSubmissionTime;

    private Integer score;

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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
