package io.shulie.takin.cloud.app.conf;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger配置
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Configuration
public class Swagger {
    @Bean
    public GroupedOpenApi commonApi() {
        return GroupedOpenApi.builder()
            .group("通用模块")
            .pathsToMatch("/common/**")
            .build();
    }

    @Bean
    public GroupedOpenApi resourceApi() {
        return GroupedOpenApi.builder()
            .group("资源模块")
            .pathsToMatch("/resource/**")
            .build();
    }

    @Bean
    public GroupedOpenApi jobApi() {
        return GroupedOpenApi.builder()
            .group("任务模块")
            .pathsToMatch("/job/**")
            .build();
    }

    @Bean
    public GroupedOpenApi watchmanApi() {
        return GroupedOpenApi.builder()
            .group("调度器模块")
            .pathsToMatch("/watchman/**")
            .build();
    }

    @Bean
    public GroupedOpenApi notityApi() {
        return GroupedOpenApi.builder()
            .group("上报模块")
            .pathsToMatch("/notify/**")
            .build();
    }

    @Bean
    public GroupedOpenApi excessJobApi() {
        return GroupedOpenApi.builder()
            .group("额外的任务")
            .pathsToMatch("/excess/job/**")
            .build();
    }

}
