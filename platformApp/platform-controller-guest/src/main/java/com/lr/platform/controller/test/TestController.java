package com.lr.platform.controller.test;

import com.lr.platform.annotations.GuestLog;
import com.lr.platform.enums.WarningLevel;
import com.lr.platform.exception.RollBackException;
import com.lr.platform.response.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("test")
public class TestController {

    @GetMapping
    @GuestLog(tag = "test",level = WarningLevel.COMMON_CREATE)
    public Response test(@RequestParam(value = "action",required = false) Integer id) throws Exception {
        if (id==1){
            throw new RollBackException("RollBackException");
        }
        if (id==0){
            throw new Exception("Exception");
        }
        return null;
    }
}
