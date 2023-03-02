package com.lr.platform.entity.rank;

import com.lr.platform.entity.users.UsersRankGuestVo;

import java.util.Date;

public class SingleProblemRankGuestVo {

    private Integer id;

    private UsersRankGuestVo user;

    private Double score;

    private Date solveTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public UsersRankGuestVo getUser() {
        return user;
    }

    public void setUser(UsersRankGuestVo user) {
        this.user = user;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Date getSolveTime() {
        return solveTime;
    }

    public void setSolveTime(Date solveTime) {
        this.solveTime = solveTime;
    }
}
