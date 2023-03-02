package com.lr.platform.utils;


import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.util.Map;
@Component
public class Email {


    public static final String codeTemplate= "code1.html";
    @Resource
    private TemplateEngine templateEngine;

    public  String builderContent(String template, Map<String, Object> kv){
        Context context=new Context();
        context.setVariables(kv);
        return templateEngine.process(template,context);
    }
}
