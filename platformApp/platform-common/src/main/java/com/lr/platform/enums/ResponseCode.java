package com.lr.platform.enums;

import lombok.Getter;

@Getter
public enum ResponseCode {
    /*调用成功返回码*/
    COMMON_SUCCESS(0,"完成",true),
    /*通用错误返回码*/
    COMMON_FAIL(1,"错误"),
    COMMON_TEST(2,"测试接口,未完成"),
    /*10001~19999 通用业务错误返回码*/
    COMMON_ILLEGAL_PAGESIZE(10001,"不合法的页大小"),
    COMMON_ILLEGAL_CURRENT(10002,"不合法的选择页"),
    COMMON_REDIS_ERROR(10003,"redis服务异常"),
    COMMON_RECAPTCHA_ERROR(10004,"请重试"),
    COMMON_CODE_ERROR(10005,"验证码错误"),
    COMMON_DATA_ERROR(10006,"数据异常"),
    COMMON_DATA_ALREADY_REGISTER(10007,"信息已被注册"),
    /*服务器通用异常返回码*/
    COMMON_SERVER_ERROR(500,"服务器异常"),
    COMMON_AUTH_ERROR(401,"需要登入"),
    COMMON_NOT_FOUND(404,"接口不存在"),
    COMMON_PARAMETER_MISSING(400,"参数异常"),
    COMMON_METHODS_NOT_ALLOWED(405,"方法不被允许"),
    COMMON_PERMISSION_DENIED(403,"权限不足"),
    /*20001~29999 方向模块错误码*/
    DOMAINS_ILLEGAL_NAME(20001,"方向名称不合法"),
    DOMAINS_DATA_NOT_FOUND(20002,"方向不存在"),
    DOMAINS_NAME_ALREADY_REGISTERED(20003,"名称已被注册"),
    /*30001~39999 日志模块错误码*/

    /*40001~49999 管理员模块错误码*/
    ADMINS_ILLEGAL_NAME(40001,"名字不合法"),
    ADMINS_ILLEGAL_QQ(40002,"不合法的QQ"),
    ADMINS_DATA_NOT_FOUND(40003,"管理员不存在"),
    ADMINS_ILLEGAL_USERNAME(40004,"用户名不合法"),
    ADMINS_USERNAME_ALREADY_REGISTERED(40005,"用户名已被注册"),
    ADMINS_WRONG_USERNAME_OR_PASSWORD(40006,"账号或密码错误"),
    ADMINS_ILLEGAL_PASSWORD(40007,"密码不合法,请重新设置"),
    ADMINS_REDIS_ERROR(40008,"登录失败,服务器异常"),
    ADMINS_ILLEGAL_TOKEN(40009,"请重试"),
    ADMINS_WRONG_EMAIL(40010,"邮箱错误"),
    /*50001~59999 负责人模块错误码*/
    DOMAIN_ADMIN_ILLEGAL_ADMIN_ID(50001,"不合法的Admin ID"),
    DOMAIN_ADMIN_ILLEGAL_DOMAIN_ID(50002,"不合法的Domain ID"),
    DOMAIN_ADMIN_ILLEGAL_DOMAIN_ADMIN_ID(50003,"不合法的Domain Admin ID"),
    DOMAIN_ADMIN_EXISTS_DOMAIN_ADMIN_ID(50004, "该管理员已为所选方向的负责人"),
    /*60001~69999 题目模块错误码*/
    PROBLEMS_ILLEGAL_DOMAIN_ID(60001, "不合法的Domain ID"),
    PROBLEMS_ILLEGAL_TITLE(60002, "题目标题不合法"),
    PROBLEMS_ILLEGAL_CONTENT(60003, "题目内容不合法"),
    PROBLEMS_ILLEGAL_SUBMIT_TYPE(60004, "提交方式不合法"),
    PROBLEMS_ILLEGAL_FLAG(60005, "Flag不合法"),
    PROBLEMS_ILLEGAL_SCORE(60006, "分数设置不合法"),
    PROBLEMS_NOT_FOUND(60006, "题目不存在"),
    PROBLEMS_ILLEGAL_SHOW_OPTION(60007,"选项不合法"),
    PROBLEMS_WRONG_FLAG(60008,"flag错误"),
    PROBLEMS_WRONG_FILE_TYPE(60009,"错误的文件类型"),
    PROBLEMS_ALREADY_FINISH_FLAG(60010,"您已完成该题,请勿重复提交"),
    PROBLEMS_SUBMIT_OVER_TIME(60011,"已超过截止时间,无法进行提交"),
    /*70001~79999 用户模块错误码*/
    USERS_NOT_FOUND(70001, "用户不存在"),
    USERS_ILLEGAL_NAME(70002,"名字不合法"),
    USERS_ILLEGAL_QQ(70003,"不合法的QQ"),
    USERS_ILLEGAL_USERNAME(70004,"用户名不合法"),
    USERS_ILLEGAL_PASSWORD(70005,"密码不合法"),
    USERS_ILLEGAL_EMAIL(70006,"邮箱不合法"),
    USERS_ILLEGAL_TOKEN(70007,"请重试"),
    USERS_WRONG_USERNAME_OR_PASSWORD(70008,"账号或密码错误"),
    USERS_EMAIL_ALREADY_REGISTER(70009,"邮箱已被注册"),
    USERS_USERNAME_ALREADY_REGISTER(70010,"用户名已被注册"),
    USERS_WRONG_EMAIL(70011,"邮箱错误或不存在"),
    USERS_ILLEGAL_PHONE(70012,"手机不合法"),
    USERS_ILLEGAL_STUDENT_NUMBER(70013,"学号不合法"),
    /*80001~89999 解题记录错误码*/
    SOLVE_RECORDS_NOT_FOUND(80001,"解题记录不存在"),
    SOLVE_RECORDS_FILE_NOT_FOUND(80002,"文件不存在"),
    SOLVE_RECORDS_FILE_ERROR(80003,"文件异常"),
    SOLVE_RECORDS_ILLEGAL_DEGREE(80004,"分数超出范围"),
    SOLVE_RECORDS_FILE_SIZE_EXCEEDS_LIMIT(80005,"文件大小超出限制"),
    /*90001~99999 公告模块错误码*/
    ANNOUNCEMENT_NOT_FOUNDED(90001,"公告不存在"),
    ANNOUNCEMENT_TITLE_TOO_LONG(90002,"公告名过长"),
    ANNOUNCEMENT_ILLEGAL_TYPE(90003,"错误的公告类型"),
    /*100001~109999 排行榜模块错误码*/
    RANK_NOT_DATA(100001,"数据不存在"),
    ;
    private Integer code;
    private String message;
    private Boolean status;
    ResponseCode(int code, String message){
        this.code=code;
        this.message=message;
        this.status=false;
    }
    ResponseCode(int code, String message, boolean b) {
        this.code=code;
        this.message=message;
        this.status=b;
    }

    public ResponseCode setCode(Integer code) {
        this.code = code;
        return this;
    }
    public ResponseCode setMessage(String message) {
        this.message = message;
        return this;
    }
    public ResponseCode setStatus(Boolean status) {
        this.status = status;
        return this;
    }
}
