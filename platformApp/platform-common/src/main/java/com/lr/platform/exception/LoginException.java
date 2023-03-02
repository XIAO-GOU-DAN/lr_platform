package com.lr.platform.exception;

import lombok.Getter;

@Getter
public class LoginException extends RuntimeException{
    private final String msg;

    public LoginException(String msg){
        //super(msg);
        this.msg=msg;
    }

    public LoginException(){
        this.msg=null;
    }
}
