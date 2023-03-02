package com.lr.platform.utils;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.annotation.Resource;

@Component
public class Transaction {
    @Resource
    private DataSourceTransactionManager dataSourceTransactionManager;

    /**
     * begin 事务
     * */
    public TransactionStatus begin(){
        return dataSourceTransactionManager.getTransaction(new DefaultTransactionAttribute());
    }

    /**
     * 提交事务
     * */
    public void commit(TransactionStatus transactionStatus){
        dataSourceTransactionManager.commit(transactionStatus);
    }

    /**
     * 回滚事务
     * */
    public void rollBack(TransactionStatus transactionStatus){
        dataSourceTransactionManager.rollback(transactionStatus);
    }
}
