package com.lr.platform.entity.problems;

import com.lr.platform.entity.domains.DomainsOpVo;
import com.lr.platform.entity.solveRecords.SolveRecordsGuestForProblemsCardVo;

import java.util.Date;

public class ProblemsGuestCardsVo {
    private Integer id;


    //private Date createdAt;

    //private Date updatedAt;

    //private Date deletedAt;

    private Integer domainId;

    private DomainsOpVo domain;

    //private String content;

    private Integer originalScore;

    private Integer currentScore;

    private Integer solverNum;

    private Date revisedAt;

    //private Integer firstSolverId;

    //private Date firstSolveTime;

    //private Integer isShow;

    private String submitType;

    //private String flag;

    private String title;

    private Date deadline;

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public DomainsOpVo getDomain() {
        return domain;
    }

    public void setDomain(DomainsOpVo domain) {
        this.domain = domain;
    }

    public String getSubmitType() {
        return submitType;
    }

    public void setSubmitType(String submitType) {
        this.submitType = submitType;
    }

    private SolveRecordsGuestForProblemsCardVo solveRecord;

    public SolveRecordsGuestForProblemsCardVo getSolveRecord() {
        return solveRecord;
    }

    public void setSolveRecord(SolveRecordsGuestForProblemsCardVo solveRecord) {
        this.solveRecord = solveRecord;
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

    public Integer getDomainId() {
        return domainId;
    }

    public void setDomainId(Integer domainId) {
        this.domainId = domainId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
