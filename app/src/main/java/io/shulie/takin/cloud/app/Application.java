package io.shulie.takin.cloud.app;

import lombok.extern.slf4j.Slf4j;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * SpringBoot启动类
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Slf4j
@EnableScheduling
@SpringBootApplication
@MapperScan("io.shulie.takin.cloud.app.mapper")
public class Application implements DisposableBean {
    public static void main(String[] args) {
        System.setProperty("pagehelper.banner", Boolean.FALSE.toString());
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void destroy() {
        log.warn("程序停止.");
        Runtime.getRuntime().halt(0);
    }
}
