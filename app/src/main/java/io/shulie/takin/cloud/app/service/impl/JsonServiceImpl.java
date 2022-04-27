package io.shulie.takin.cloud.app.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.shulie.takin.cloud.app.service.JsonService;
import org.springframework.stereotype.Service;

/**
 * Json服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class JsonServiceImpl implements JsonService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String formatString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败");
        }
    }
}
