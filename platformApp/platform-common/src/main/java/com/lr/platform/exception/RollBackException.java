package com.lr.platform.exception;

import lombok.Getter;

@Getter
public class RollBackException extends RuntimeException{
    private final String msg;

    private final String resMsg;

    public RollBackException(String msg){
        //super(msg);
        this.msg=msg;
        this.resMsg=null;
    }

    public RollBackException(String msg,String resMsg){
        this.msg=msg;
        this.resMsg=resMsg;
    }

    public RollBackException(){
        this.msg=null;
        this.resMsg=null;
    }
}
