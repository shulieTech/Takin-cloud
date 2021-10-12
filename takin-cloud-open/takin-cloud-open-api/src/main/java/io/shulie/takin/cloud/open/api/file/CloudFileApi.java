package io.shulie.takin.cloud.open.api.file;

import java.util.Map;

import io.shulie.takin.cloud.open.req.filemanager.FileZipParamReq;
import io.shulie.takin.cloud.open.req.filemanager.FileCopyParamReq;
import io.shulie.takin.cloud.open.req.filemanager.FileDeleteParamReq;
import io.shulie.takin.cloud.open.req.filemanager.FileContentParamReq;
import io.shulie.takin.cloud.open.req.filemanager.FileCreateByStringParamReq;

/**
 * @author shiyajian
 * create: 2020-10-19
 */
public interface CloudFileApi {

    /**
     * 获取文件内容
     *
     * @param fileContentParamReq -
     * @return -
     */
    Map<String, Object> getFileContent(FileContentParamReq fileContentParamReq);

    /**
     * 删除文件
     *
     * @param fileDeleteParamReq -
     * @return -
     */
    Boolean deleteFile(FileDeleteParamReq fileDeleteParamReq);

    /**
     * 复制文件到指定目录
     *
     * @param fileCopyParamReq -
     * @return -
     */
    Boolean copyFile(FileCopyParamReq fileCopyParamReq);

    /**
     * 将指定文件打包到指定目录
     *
     * @param fileZipParamReq -
     * @return -
     */
    Boolean zipFile(FileZipParamReq fileZipParamReq);

    /**
     * 将字符串转为指定文件
     *
     * @param fileCreateByStringParamReq -
     * @return -
     */
    Boolean createFileByPathAndString(FileCreateByStringParamReq fileCreateByStringParamReq);
}
