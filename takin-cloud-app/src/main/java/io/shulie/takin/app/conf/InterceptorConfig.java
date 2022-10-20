package io.shulie.takin.app.conf;

/**
 * @author pnz.zhao
 * @Description:
 * @date 2022/2/23 14:54
 */
import io.shulie.takin.app.interceptor.SecurityInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter{

    public void addInterceptors(InterceptorRegistry registry){
//        registry.addInterceptor(new SecurityInterceptor())
//                .addPathPatterns("/**");
    }
}