package com.lr.platform.entity.problems;

import java.util.Date;

public class ProblemsAdminVo {

    private Integer id;

    private Date createdAt;

    private Date updatedAt;

    private Integer domainId;

    private String content;

    private Integer originalScore;

    private Integer currentScore;

    private Integer solverNum;

    private Integer firstSolverId;

    private Date firstSolveTime;

    private Integer isShow;

    private String submitType;

    private String flag;

    private String title;

    private Date revisedAt;

    private Date deadline;

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getRevisedAt() {
        return revisedAt;
    }

    public void setRevisedAt(Date revisedAt) {
        this.revisedAt = revisedAt;
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

    public Integer getDomainId() {
        return domainId;
    }

    public void setDomainId(Integer domainId) {
        this.domainId = domainId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getOriginalScore() {
        return originalScore;
    }

    public void setOriginalScore(Integer originalScore) {
        this.originalScore = originalScore;
    }

    public Integer getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Integer currentScore) {
        this.currentScore = currentScore;
    }

    public Integer getSolverNum() {
        return solverNum;
    }

    public void setSolverNum(Integer solverNum) {
        this.solverNum = solverNum;
    }

    public Integer getFirstSolverId() {
        return firstSolverId;
    }

    public void setFirstSolverId(Integer firstSolverId) {
        this.firstSolverId = firstSolverId;
    }

    public Date getFirstSolveTime() {
        return firstSolveTime;
    }

    public void setFirstSolveTime(Date firstSolveTime) {
        this.firstSolveTime = firstSolveTime;
    }

    public Integer getIsShow() {
        return isShow;
    }

    public void setIsShow(Integer isShow) {
        this.isShow = isShow;
    }

    public String getSubmitType() {
        return submitType;
    }

    public void setSubmitType(String submitType) {
        this.submitType = submitType;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
