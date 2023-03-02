package com.lr.platform.entity.solveRecords;

import java.util.List;

import com.lr.platform.entity.domains.DomainsOpVo;

public class SolveRecordsAdminUidVo {
    private DomainsOpVo domain;

    private List<SolveRecordsAdminHandleVo> records;

    public DomainsOpVo getDomain() {
        return domain;
    }

    public void setDomain(DomainsOpVo domain) {
        this.domain = domain;
    }

    public List<SolveRecordsAdminHandleVo> getRecords() {
        return records;
    }

    public void setRecords(List<SolveRecordsAdminHandleVo> records) {
        this.records = records;
    }
}
