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
            .group("通用")
            .pathsToMatch("/common/**")
            .build();
    }

    @Bean
    public GroupedOpenApi jobApi() {
        return GroupedOpenApi.builder()
            .group("任务")
            .pathsToMatch("/job/**")
            .build();
    }

    @Bean
    public GroupedOpenApi jobExpandApi() {
        return GroupedOpenApi.builder()
            .group("任务拓展")
            .pathsToMatch("/job/expand/**")
            .build();
    }

    @Bean
    public GroupedOpenApi watchmanApi() {
        return GroupedOpenApi.builder()
            .group("调度器")
            .pathsToMatch("/watchman/**")
            .build();
    }

    @Bean
    public GroupedOpenApi notityApi() {
        return GroupedOpenApi.builder()
            .group("调度器上报数据")
            .pathsToMatch("/notify/**")
            .build();
    }
}
