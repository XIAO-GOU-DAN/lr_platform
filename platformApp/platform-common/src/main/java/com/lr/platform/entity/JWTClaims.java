package com.lr.platform.entity;

import lombok.Data;

@Data
public class JWTClaims {
    private Long exp;

    private Integer ttl;

    private String username;

    private Integer uid;

    private String option;

}
