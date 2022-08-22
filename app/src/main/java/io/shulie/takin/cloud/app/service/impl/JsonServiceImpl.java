package io.shulie.takin.cloud.app.service.impl;

import cn.hutool.json.JSONException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;

import io.shulie.takin.cloud.app.service.JsonService;

/**
 * Json服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class JsonServiceImpl implements JsonService {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonServiceImpl() {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public String writeValueAsString(Object obj) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("JSON序列化失败");
        }
    }

    @Override
    public <T> T readValue(String jsonString, Class<T> valueType) {
        try {
            return objectMapper.readValue(jsonString, valueType);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public <T> T readValue(String jsonString, TypeReference<T> valueTypeRef) {
        try {
            return objectMapper.readValue(jsonString, valueTypeRef);
        } catch (JsonProcessingException e) {
            throw new JSONException("解析metrics数据异常，不是标准JSON字符串");
        }
    }
}
