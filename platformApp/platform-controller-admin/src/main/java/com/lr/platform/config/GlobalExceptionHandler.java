package com.lr.platform.config;

import com.lr.platform.enums.ResponseCode;
import com.lr.platform.exception.LoginException;
import com.lr.platform.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public @ResponseBody
    ResponseEntity<Response> ExceptionHandler(Exception e){
        //e.printStackTrace();
        //log.error("ERROR:",e);
        Response res=Response
                .builder()
                .code(ResponseCode.COMMON_SERVER_ERROR)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public @ResponseBody
    ResponseEntity<Response> MethodArgumentNotValidExceptionHandler(MethodArgumentTypeMismatchException e){
        //e.printStackTrace();
        //log.error("ERROR:",e);
        Response res=Response
                .builder()
                .code(ResponseCode.COMMON_SERVER_ERROR)
                .msg("参数异常")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public @ResponseBody
    ResponseEntity<Response> HttpMessageNotReadableExceptionHandler(HttpMessageNotReadableException e){
        Response res=Response
                .builder()
                .code(ResponseCode.COMMON_SERVER_ERROR)
                .msg("参数异常")
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public @ResponseBody
    ResponseEntity<Response> NoHandlerFoundExceptionHandler(NoHandlerFoundException e){
        Response res=Response
                .builder()
                .code(ResponseCode.COMMON_NOT_FOUND)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public @ResponseBody
    ResponseEntity<Response> HttpRequestMethodNotSupportedExceptionHandler(HttpRequestMethodNotSupportedException e) {
        Response res=Response
                .builder()
                .code(ResponseCode.COMMON_METHODS_NOT_ALLOWED)
                .build();
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(res);
    }

    @ExceptionHandler(LoginException.class)
    public @ResponseBody
    ResponseEntity<Response> LoginExceptionHandler(LoginException e) {
        Response res=Response
                .builder()
                .code(ResponseCode.COMMON_AUTH_ERROR)
                .msg(e.getMsg())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public @ResponseBody
    ResponseEntity<Response> MissingServletRequestPartExceptionHandler(MissingServletRequestParameterException e){
        Response res=Response
                .builder()
                .code(ResponseCode.COMMON_PARAMETER_MISSING)
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
}
