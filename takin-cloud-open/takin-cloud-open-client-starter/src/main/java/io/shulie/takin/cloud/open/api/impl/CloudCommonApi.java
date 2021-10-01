package io.shulie.takin.cloud.open.api.impl;

import java.util.Map;
import java.util.HashMap;

import cn.hutool.core.bean.BeanUtil;
import org.springframework.stereotype.Component;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;

/**
 * @author qianshui
 * @date 2020/11/16 下午4:21
 */
@Component
public class CloudCommonApi {
    /**
     * 转化 header
     *
     * @return -
     */
    protected Map<String, String> getHeaders() {
        // 格式转换
        Map<String, Object> header = BeanUtil.beanToMap(CloudPluginUtils.getContext());
        Map<String, String> headerMap = new HashMap<>(header.size());
        header.forEach((k, v) -> headerMap.put(k, v.toString()));
        // 返回数据
        return headerMap;
    }
}
