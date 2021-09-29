package io.shulie.takin.cloud.open.api.impl;

import java.util.Map;

import com.google.common.collect.Maps;
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
     * @param ext -
     * @return -
     */
    protected Map<String, String> getHeaders(CloudUserCommonRequestExt ext) {
        Map<String, String> map = Maps.newHashMap();
        map.put(CloudApiConstant.LICENSE_REQUIRED, "true");
        //map.put(CloudApiConstant.LICENSE_KEY, this.getDevLicense(ext.getLicense()));
        if (StringUtils.isNotBlank(ext.getFilterSql())) {
            map.put(CloudApiConstant.FILTER_SQL, ext.getFilterSql());
        }
        map.put(CloudApiConstant.USER_ID, String.valueOf(ext.getUserId()));
        return map;
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
