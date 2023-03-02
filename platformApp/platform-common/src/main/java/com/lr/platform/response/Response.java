package com.lr.platform.response;

import com.lr.platform.enums.ResponseCode;
import lombok.Data;

import java.text.SimpleDateFormat;
import java.util.Date;
@Data
public class Response {
    Integer code;
    String message;
    Object data;
    Boolean status;
    String dt;
    Long ts;
    Integer len;

    Response(){}
    Response(Builder builder){
        this.code=builder.code.getCode();
        this.data=builder.data;
        if (builder.msg!=null){
            this.message=builder.msg;
        }else {
            this.message=builder.code.getMessage();
        }
        this.status=builder.code.getStatus();
        Date nowTime = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.dt=sdf.format(nowTime);
        this.ts= nowTime.getTime();
        this.len=builder.len;
    }
    public static Builder builder(){return new Builder();}
    public static class Builder {
        ResponseCode code;
        String msg;
        Object data;
        Integer len;
        public Builder code(ResponseCode code) {
            this.code = code;
            return this;
        }

        public Builder msg(String msg) {
            this.msg = msg;
            return this;
        }
        public Builder len(Integer len){
            this.len=len;
            return this;
        }
        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Response build() {
            return new Response(this);
        }
    }
}
