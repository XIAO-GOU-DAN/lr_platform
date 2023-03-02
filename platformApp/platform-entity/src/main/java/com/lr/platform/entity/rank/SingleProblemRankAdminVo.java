package com.lr.platform.entity.rank;

import com.lr.platform.entity.users.UsersAdminVo;
import com.lr.platform.entity.users.UsersRankGuestVo;

import java.util.Date;

public class SingleProblemRankAdminVo {

    private Integer id;

    private UsersAdminVo user;

    private Double score;

    private Date solveTime;

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
