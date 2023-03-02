package com.lr.platform.entity.rank;

import com.lr.platform.entity.users.UsersAdminVo;

import java.util.List;

public class TotalRankAdminVo {

    private Integer id;

    private UsersAdminVo user;

    private List<DomainRankGuestVo> domainRank;

    private Double solveNum;

    private Double score;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UsersAdminVo getUser() {
        return user;
    }

    public void setUser(UsersAdminVo user) {
        this.user = user;
    }

    public List<DomainRankGuestVo> getDomainRank() {
        return domainRank;
    }

    public void setDomainRank(List<DomainRankGuestVo> domainRank) {
        this.domainRank = domainRank;
    }

    public Double getSolveNum() {
        return solveNum;
    }

    public void setSolveNum(Double solveNum) {
        this.solveNum = solveNum;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
