package io.shulie.takin.cloud.entrypoint.file;

import java.util.List;
import java.util.Map;

import io.shulie.takin.cloud.sdk.model.request.file.DeleteTempRequest;
import io.shulie.takin.cloud.sdk.model.request.file.UploadRequest;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileContentParamReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileCopyParamReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileCreateByStringParamReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileDeleteParamReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileZipParamReq;
import io.shulie.takin.cloud.sdk.model.response.file.UploadResponse;

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
     * 删除临时文件
     *
     * @param req -
     */
    void deleteTempFile(DeleteTempRequest req);

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

    /**
     * 上传文件
     *
     * @param req 请求
     * @return 上传结果
     */
    List<UploadResponse> upload(UploadRequest req);
}
