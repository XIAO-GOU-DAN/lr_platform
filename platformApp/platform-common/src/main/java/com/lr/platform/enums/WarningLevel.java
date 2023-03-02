package com.lr.platform.enums;

import lombok.Getter;

@Getter
public enum WarningLevel {
    COMMON_DELETE("删除",1),
    COMMON_UPDATE("修改",2),
    COMMON_CREATE("新增",2),
    COMMON_LOGIN("登录",3),
    COMMON_SELECT("查询",3)
    ;

    public void setCOMMON_OP(String COMMON_OP) {
        this.COMMON_OP = COMMON_OP;
    }

    public void setCOMMON_LEVEL(Integer COMMON_LEVEL) {
        this.COMMON_LEVEL = COMMON_LEVEL;
    }

    private String COMMON_OP;

    private Integer COMMON_LEVEL;

    WarningLevel(String OP, int LEVEL) {
        this.COMMON_OP=OP;
        this.COMMON_LEVEL=LEVEL;
    }

}
