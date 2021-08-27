package io.shulie.takin.cloud.biz.service.log;

public interface PushLogService {
    void pushLogToAmdb(byte[] data, String version);

    void pushLogToAmdb(String context, String version);

}
