package io.shulie.takin.app;

import io.shulie.takin.cloud.biz.service.report.ReportService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author shulie
 * @date 2019-07-23 18:30
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.pamirs.takin", "io.shulie.takin"})
@MapperScan({"com.pamirs.takin.*.dao", "io.shulie.takin.cloud.data.mapper.mysql", "io.shulie.takin.cloud.data.dao.statistics"})
public class Application {

    @Autowired
    private ReportService reportService;

    public static void main(String[] args) {
        SpringApplicationBuilder applicationBuilder = new SpringApplicationBuilder().sources(Application.class);
        applicationBuilder.run(args);
    }

    @Primary
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(50000);
        return new RestTemplate(requestFactory);
    }

    @Qualifier("consumingRestTemplate")
    @Bean
    public RestTemplate consumingRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(10000);
        requestFactory.setReadTimeout(60000);
        return new RestTemplate(requestFactory);
    }

    /**
     * 说明: 文件下载
     *
     * @author shulie
     * @date 2018/10/10 10:37
     */
    @Bean
    public HttpMessageConverters restFileDownloadSupport() {
        ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
        return new HttpMessageConverters(arrayHttpMessageConverter);
    }

}
