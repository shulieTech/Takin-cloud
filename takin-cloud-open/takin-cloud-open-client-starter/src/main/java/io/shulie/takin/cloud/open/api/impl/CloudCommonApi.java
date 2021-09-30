package io.shulie.takin.cloud.open.api.impl;

import java.util.HashMap;
import java.util.Map;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Maps;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import io.shulie.takin.cloud.common.utils.AppBusinessUtil;
import io.shulie.takin.cloud.open.constant.CloudApiConstant;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;

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

    /**
     * 本地环境 license, 供测试用
     *
     * @return license
     */
    private String getDevLicense(String license) {
        return AppBusinessUtil.isLocal() ? "5b06060a-17cb-4588-bb71-edd7f65035af" : license;
    }
}
