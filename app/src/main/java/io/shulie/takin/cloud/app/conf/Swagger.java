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
    public Docket jobApi() {
        return new Docket(DocumentationType.OAS_30)
            .apiInfo(jobApiInfo())
            .groupName("资源")
            .select()
            .apis(RequestHandlerSelectors.basePackage("io.shulie.takin.cloud.app.controller"))
            .paths(PathSelectors.ant("/job/**"))
            .build();
    }

    public ApiInfo jobApiInfo() {
        return new ApiInfo(
            "Cloud-Api", "Cloud-新版-API", "5.6.0",
            "https://www.shulie.io/",
            new Contact("数列科技", "https://www.shulie.io/", "zhangtianci@shulie.io"),
            "null", "https://www.shulie.io/",
            new ArrayList<>(0)
        );
    }

    @Bean
    public Docket commonApi() {
        return new Docket(DocumentationType.OAS_30)
            .apiInfo(commonApiInfo())
            .groupName("通用")
            .select()
            .apis(RequestHandlerSelectors.basePackage("io.shulie.takin.cloud.app.controller"))
            .paths(PathSelectors.ant("/common/**"))
            .build();
    }

    public ApiInfo commonApiInfo() {
        return new ApiInfo(
            "Cloud-Api", "Cloud-新版-API", "5.6.0",
            "https://www.shulie.io/",
            new Contact("数列科技", "https://www.shulie.io/", "zhangtianci@shulie.io"),
            "null", "https://www.shulie.io/",
            new ArrayList<>(0)
        );
    }
}
