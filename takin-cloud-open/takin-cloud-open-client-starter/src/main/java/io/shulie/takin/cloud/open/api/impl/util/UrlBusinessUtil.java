package io.shulie.takin.cloud.open.api.impl.util;

import javax.annotation.PostConstruct;

import io.shulie.takin.cloud.open.constant.CloudApiConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.tro.properties.TroCloudClientProperties;
import org.springframework.stereotype.Component;

/**
 * url 业务工具类
 * 提供各个模块的 url 前缀
 *
 * @author liuchuan
 * @date 2021/4/25 10:22 上午
 */
@Component
public class UrlBusinessUtil {

    @Autowired
    private TroCloudClientProperties troCloudClientProperties;

    /**
     * troCloud 属性配置, 静态属性
     */
    private static TroCloudClientProperties tccp;


    @PostConstruct
    public void init() {
        tccp = troCloudClientProperties;
    }

    /**
     * 场景管理更新脚本文件 url
     *
     * @return 场景管理更新脚本文件 url
     */
    public static String getSceneMangeUpdateFileUrl() {
        return tccp.getUrl() + CloudApiConstant.SCENE_MANAGE_UPDATE_FILE_URL;
    }

}
