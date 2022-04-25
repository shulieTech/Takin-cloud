package io.shulie.takin.cloud.app.conf;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static springfox.documentation.schema.AlternateTypeRules.newRule;

/**
 * Swagger配置
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Configuration
@EnableOpenApi
public class Swagger {

    @Bean
    public Docket commonApi() {
        return new Docket(DocumentationType.OAS_30)
            .apiInfo(emptyInfo("通用模块"))
            .groupName("通用")
            .select()
            .apis(RequestHandlerSelectors.basePackage("io.shulie.takin.cloud.app.controller"))
            .paths(PathSelectors.ant("/common/**"))
            .build();
    }

    @Bean
    public Docket resourceApi() {
        return new Docket(DocumentationType.OAS_30)
            .apiInfo(emptyInfo("资源模块"))
            .groupName("资源")
            .select()
            .apis(RequestHandlerSelectors.basePackage("io.shulie.takin.cloud.app.controller"))
            .paths(PathSelectors.ant("/resource/**"))
            .build();
    }

    @Bean
    public Docket jobApi() {
        return new Docket(DocumentationType.OAS_30)
            .apiInfo(emptyInfo("任务模块"))
            .groupName("任务")
            .select()
            .apis(RequestHandlerSelectors.basePackage("io.shulie.takin.cloud.app.controller"))
            .paths(PathSelectors.ant("/job/**"))
            .build();
    }

    public ApiInfo emptyInfo(String title) {
        return new ApiInfo(title, "", "", "",
            new Contact("", "", ""), "", "",
            new ArrayList<>(0)
        );
    }
}
