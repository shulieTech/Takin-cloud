package io.shulie.takin.app.conf;

import java.time.LocalDate;
import java.util.function.Predicate;

import io.swagger.annotations.Api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;

import springfox.documentation.PathProvider;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.Paths;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spring.web.paths.DefaultPathProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;

/**
 * The type Swagger config.
 */
@Configuration
@EnableKnife4j
@EnableSwagger2WebMvc
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

    /**
     * Api docket.
     *
     * @return the docket
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("默认模块")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(PathSelectors.any())
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo())
            ;
    }

    @Bean
    public Docket renshou() {
        return new Docket(DocumentationType.SWAGGER_2)
            .pathProvider(this.pathProvider())
            .groupName("人寿")
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(getRegex("/api(/v2/scene).*"))
            .build()
            .directModelSubstitute(LocalDate.class, String.class)
            .useDefaultResponseMessages(false)
            .apiInfo(apiInfo())
            ;
    }

    /**
     * api info
     *
     * @return ApiInfo
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Takin Cloud 接口文档")
            .build();
    }

    private Predicate<String> getRegex(String regex) {
        return PathSelectors.regex(servletContextPath + regex);
    }

    @Value("${server.servlet.context-path:}")
    private String servletContextPath;

    /**
     * 重写 PathProvider ,解决 context-path 重复问题
     *
     * @return
     */
    private PathProvider pathProvider() {
        return new DefaultPathProvider() {
            @Override
            public String getOperationPath(String operationPath) {
                operationPath = operationPath.replaceFirst(servletContextPath, "/");
                UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromPath("/");
                return Paths.removeAdjacentForwardSlashes(uriComponentsBuilder.path(operationPath).build().toString());
            }

            @Override
            public String getResourceListingPath(String groupName, String apiDeclaration) {
                apiDeclaration = super.getResourceListingPath(groupName, apiDeclaration);
                return apiDeclaration;
            }
        };
    }
}
