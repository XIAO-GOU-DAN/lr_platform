package com.lr.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AdminApp {
    public static void main(String[] args){
        SpringApplication.run(AdminApp.class,args);
    }
}
