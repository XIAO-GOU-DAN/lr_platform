package com.lr.platform.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.stereotype.Component;
import springfox.documentation.oas.web.OpenApiTransformationContext;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.spi.DocumentationType;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

//@Component
public class SpringfoxSwaggerHostResolver implements WebMvcOpenApiTransformationFilter {

    @Override
    public OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
        OpenAPI swagger = context.getSpecification();
        Server server = new Server();
        server.setUrl("http://124.223.199.137:8888/");
        swagger.setServers(Arrays.asList(server));
        return swagger;
    }

    @Override
    public boolean supports(DocumentationType delimiter) {
        return DocumentationType.OAS_30.equals(delimiter);
    }
}
