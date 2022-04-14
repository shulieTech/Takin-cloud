package io.shulie.takin.cloud.sdk.impl.file;

import java.util.Map;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.TypeReference;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollUtil;

import org.springframework.stereotype.Service;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.entrypoint.file.CloudFileApi;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.sdk.service.CloudApiSenderService;
import io.shulie.takin.cloud.sdk.model.request.file.UploadRequest;
import io.shulie.takin.cloud.sdk.model.response.file.UploadResponse;
import io.shulie.takin.cloud.sdk.model.request.file.DeleteTempRequest;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileZipParamReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileCopyParamReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileDeleteParamReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileContentParamReq;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileCreateByStringParamReq;

/**
 * @author shiyajian
 * @author 张天赐
 */
@Service
public class CloudFileApiImpl implements CloudFileApi {

    @Resource
    CloudApiSenderService cloudApiSenderService;

    @Override
    public Map<String, Object> getFileContent(FileContentParamReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_FILE, EntrypointUrl.METHOD_FILE_CONTENT),
            req, new TypeReference<ResponseResult<Map<String, Object>>>() {}).getData();
    }

    @Override
    public Boolean deleteFile(FileDeleteParamReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_FILE, EntrypointUrl.METHOD_FILE_DELETE),
            req, new TypeReference<ResponseResult<Boolean>>() {}).getData();
    }

    @Override
    public void deleteTempFile(DeleteTempRequest req) {
        cloudApiSenderService.delete(EntrypointUrl.join(EntrypointUrl.MODULE_FILE, EntrypointUrl.METHOD_FILE_DELETE_TEMP),
            req, new TypeReference<ResponseResult<?>>() {});
    }

    @Override
    public Boolean copyFile(FileCopyParamReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_FILE, EntrypointUrl.METHOD_FILE_COPY),
            req, new TypeReference<ResponseResult<Boolean>>() {}).getData();

    }

    @Override
    public Boolean zipFile(FileZipParamReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_FILE, EntrypointUrl.METHOD_FILE_ZIP),
            req, new TypeReference<ResponseResult<Boolean>>() {}).getData();
    }

    @Override
    public String createFileByPathAndString(FileCreateByStringParamReq req) {
        return cloudApiSenderService.post(EntrypointUrl.join(EntrypointUrl.MODULE_FILE, EntrypointUrl.METHOD_FILE_CREATE_BY_STRING),
            req, new TypeReference<ResponseResult<String>>() {}).getData();
    }

    /**
     * 上传文件
     *
     * @param req 请求
     * @return 上传结果
     */
    @Override
    public List<UploadResponse> upload(UploadRequest req) {
        if (req == null) {
            throw new RuntimeException("调用SDK进行文件上传时,请求不能为空");
        }
        if (StrUtil.isBlank(req.getFieldName())) {
            throw new RuntimeException("调用SDK进行文件上传时,form表单名称不能为空");
        }
        if (CollUtil.isEmpty(req.getFileList())) {
            throw new RuntimeException("调用SDK进行文件上传时,文件不能为空");
        }
        return cloudApiSenderService.uploadFile(EntrypointUrl.join(EntrypointUrl.MODULE_FILE, EntrypointUrl.METHOD_FILE_UPLOAD),
            req, req.getFieldName(), req.getFileList(), new TypeReference<ResponseResult<List<UploadResponse>>>() {}).getData();
    }

}
