package io.shulie.takin.app.conf;

import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 自定义RedisTemplate配置
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Component("CustomRedisAutoConfiguration")
public class RedisAutoConfiguration {
    /**
     * RedisTemplate
     * <ul>
     *     <li>key  :String</li>
     *     <li>value:Object</li>
     * </ul>
     *
     * @param redisConnectionFactory redis连接工厂
     * @return RedisTemplate
     */
    @Bean(name = "StringWildcardRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 声明变量
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        com.alibaba.fastjson.support.spring.FastJsonRedisSerializer<Object> valueSerializer =
            new com.alibaba.fastjson.support.spring.FastJsonRedisSerializer<>(Object.class);
        // 配置属性
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(StringRedisSerializer.UTF_8);
        template.setHashKeySerializer(StringRedisSerializer.UTF_8);
        template.setValueSerializer(valueSerializer);
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        // 返回变量
        return template;
    }
}
