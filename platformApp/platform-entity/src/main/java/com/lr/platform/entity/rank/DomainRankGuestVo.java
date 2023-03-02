package com.lr.platform.entity.rank;

import com.lr.platform.entity.domains.DomainsOpVo;

public class DomainRankGuestVo {

    private Integer id;

    private DomainsOpVo domain;

    private Double solveNum;

    private Double score;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public DomainsOpVo getDomain() {
        return domain;
    }

    public void setDomain(DomainsOpVo domain) {
        this.domain = domain;
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
