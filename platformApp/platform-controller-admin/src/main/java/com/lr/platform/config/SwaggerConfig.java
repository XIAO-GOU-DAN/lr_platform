package com.lr.platform.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.HashSet;
import java.util.Set;

//@Configuration
//@EnableKnife4j
public class SwaggerConfig {

    private static final String split = ";";
    @Bean
    public Docket createRestApi() {
        /*
        return new Docket(DocumentationType.OAS_30)
//                .apiInfo(apiInfo())
//                .select()
//                .apis(basePackage(userControllerPath + split
//                                + equipmentControllerPath + split
//                                + algorithmControllerPath))
//                .build();
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .produces(newHashSet("https","http"))
                .build();

         */
        return new Docket(DocumentationType.OAS_30).pathMapping("/")


                // 定义是否开启swagger，false为关闭，可以通过变量控制
                .enable(true)


                // 将api的元信息设置为包含在json ResourceListing响应中。
                .apiInfo(apiInfo())


                // 接口调试地址
                // 选择哪些接口作为swagger的doc发布
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex("/platform_backend/api/.*"))
                .paths(PathSelectors.regex("/platform_backend/api/error.*").negate())
                .build()
                // 支持的通讯协议集合
                .protocols(newHashSet("https", "http"));
    }

    private Set<String> newHashSet(String type1, String type2){
        Set<String> set = new HashSet<>();
        set.add(type1);
        set.add(type2);
        return set;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("招新平台管理端")
                .description("招新平台接口文档")
                .version("1.0.0")
                .build();
    }
    /*
    private static Predicate<RequestHandler> basePackage(final String basePackage) {
        return input -> declaringClass(input).map(handlerPackage(basePackage)).orElse(true);
    }

    private static Function<Class<?>, Boolean> handlerPackage(final String basePackage) {
        return input -> {
            for (String strPackage : basePackage.split(split)) {
                boolean isMatch = input.getPackage().getName().startsWith(strPackage);
                if (isMatch) {
                    return true;
                }
            }
            return false;
        };
    }

    private static Optional<? extends Class<?>> declaringClass(RequestHandler input) {
        return Optional.ofNullable(input.declaringClass());
    }*/
}
