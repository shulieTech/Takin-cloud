package io.shulie.takin.cloud.biz.service.schedule;

import java.io.IOException;
import java.util.Collections;

import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.utils.file.FileManagerHelper;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mubai
 * @date 2020-10-29 17:15
 */
@Slf4j
public class CopyFileTask implements Runnable {
    private String source;
    private String dest;

    public CopyFileTask(String source, String dest) {
        this.source = source;
        this.dest = dest;
    }

    @Override
    public void run() {
        try {
            FileManagerHelper.copyFiles(Collections.singletonList(source), dest);
        } catch (IOException e) {
            log.error("异常代码【{}】,异常内容：文件复制失败 --> 异常信息: {}",
                    TakinCloudExceptionEnum.FILE_COPY_ERROR, e);
        }
    }
}
