package com.lr.platform.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lr.platform.entity.JWTClaims;

import java.util.Date;

public class JWTAuth {

    private static final long EXPIRE_TIME = 540 * 60 * 1000; //ms
    private static final String SECRET_KEY = "34fcjhs-*2d=.df3";

    public static void main(String[] args){
       System.out.println(releaseToken("timefile",3,"login",1));
       System.out.println(releaseFileToken(300,"test",-1,"download",1,"/download/test"));

    }

    public static String releaseFileToken(long expire_time_second,String username, int ttl,String option,int uid,String filePath){
        Date expire_at = new Date(System.currentTimeMillis() + expire_time_second * 1000);
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withClaim("username", username)
                .withClaim("ttl",ttl)
                .withClaim("option",option)
                .withClaim("uid",uid)
                .withClaim("file_path",filePath)
                .withExpiresAt(expire_at)
                .sign(algorithm);
    }

    public static String releaseToken(String username,int ttl,String option,int uid) {
        Date expire_at = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withClaim("username", username)
                .withClaim("ttl",ttl)
                .withClaim("option",option)
                .withClaim("uid",uid)
                .withExpiresAt(expire_at)
                .sign(algorithm);
    }

    public static String releaseToken(long expire_time_second,String username, int ttl,String option,int uid) {
        Date expire_at = new Date(System.currentTimeMillis() + expire_time_second * 1000);
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withClaim("username", username)
                .withClaim("ttl",ttl)
                .withClaim("option",option)
                .withClaim("uid",uid)
                .withExpiresAt(expire_at)
                .sign(algorithm);
    }

    public static String renewToken(String username,int ttl,String option,int uid,Long startTime){
        Date expire_at = new Date(startTime+ EXPIRE_TIME);
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        return JWT.create()
                .withClaim("username", username)
                .withClaim("ttl",ttl)
                .withClaim("option",option)
                .withClaim("uid",uid)
                .withExpiresAt(expire_at)
                .sign(algorithm);
    }

    public static JWTClaims parseToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            JWTClaims jwtClaims=Dozer.convert(jwt.getClaims(),JWTClaims.class);
            jwtClaims.setOption(jwt.getClaim("option").asString());
            jwtClaims.setUsername(jwt.getClaim("username").asString());
            return jwtClaims;
        } catch (Exception e) {
            return null;
        }
    }
}
