package io.shulie.takin.cloud.biz.service.log;

/**
 * 日志推送 service
 *
 * @author -
 */
public interface PushLogService {
    /**
     * 向AMDB推送数据
     *
     * @param data    数据
     * @param version 版本
     */
    void pushLogToAmdb(byte[] data, String version);

    /**
     * 向AMDB推送数据
     *
     * @param context 数据
     * @param version 版本
     */
    void pushLogToAmdb(String context, String version);

}
