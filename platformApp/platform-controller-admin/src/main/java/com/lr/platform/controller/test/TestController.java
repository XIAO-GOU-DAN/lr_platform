package com.lr.platform.controller.test;

import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping
    @ApiOperation(value = "test")
    public String test(@RequestParam(value = "update_offset",required = false) Long offsetId){
        System.out.println(offsetId);
        return "ok";
    }

    @GetMapping("testSleep")
    public String test1() throws InterruptedException {
        Thread.sleep(5000);
        return "okk";
    }
}
