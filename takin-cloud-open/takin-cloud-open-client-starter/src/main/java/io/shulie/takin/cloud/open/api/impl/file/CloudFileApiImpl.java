package io.shulie.takin.cloud.open.api.impl.file;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import io.shulie.takin.cloud.open.api.file.CloudFileApi;
import io.shulie.takin.cloud.open.constant.CloudApiConstant;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.open.req.filemanager.FileZipParamReq;
import io.shulie.takin.cloud.open.req.filemanager.FileCopyParamReq;
import io.shulie.takin.cloud.open.req.filemanager.FileDeleteParamReq;
import io.shulie.takin.cloud.open.req.filemanager.FileContentParamReq;
import io.shulie.takin.cloud.open.api.impl.sender.CloudApiSenderService;
import io.shulie.takin.cloud.open.req.filemanager.FileCreateByStringParamReq;

/**
 * @author shiyajian
 * @author 张天赐
 * create: 2020-10-19
 */
@Component
public class CloudFileApiImpl implements CloudFileApi {

    @Resource
    CloudApiSenderService cloudApiSenderService;

    @Override
    public Map<String, Object> getFileContent(FileContentParamReq req) {
        return cloudApiSenderService.post(CloudApiConstant.FILE_CONTENT_BY_PATHS, req,
                new com.alibaba.fastjson.TypeReference<ResponseResult<Map<String, Object>>>() {})
            .getData();
    }

    @Override
    public Boolean deleteFile(FileDeleteParamReq req) {
        return cloudApiSenderService.post(CloudApiConstant.FILE_DELETE_URL, req,
                new com.alibaba.fastjson.TypeReference<ResponseResult<Boolean>>() {})
            .getData();
    }

    @Override
    public Boolean copyFile(FileCopyParamReq req) {
        return cloudApiSenderService.post(CloudApiConstant.FILE_COPY_URL, req,
                new com.alibaba.fastjson.TypeReference<ResponseResult<Boolean>>() {})
            .getData();

    }

    @Override
    public Boolean zipFile(FileZipParamReq req) {
        return cloudApiSenderService.post(CloudApiConstant.FILE_ZIP_URL, req,
                new com.alibaba.fastjson.TypeReference<ResponseResult<Boolean>>() {})
            .getData();
    }

    @Override
    public Boolean createFileByPathAndString(FileCreateByStringParamReq req) {
        return cloudApiSenderService.post(CloudApiConstant.FILE_CREATE_BY_STRING, req,
                new com.alibaba.fastjson.TypeReference<ResponseResult<Boolean>>() {})
            .getData();
    }

}
