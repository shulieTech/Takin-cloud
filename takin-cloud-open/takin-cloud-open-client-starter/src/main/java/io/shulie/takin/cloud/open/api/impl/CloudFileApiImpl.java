package io.shulie.takin.cloud.open.api.impl;

import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import io.shulie.takin.cloud.open.api.CloudFileApi;
import io.shulie.takin.cloud.open.constant.CloudApiConstant;
import io.shulie.takin.cloud.open.req.filemanager.FileContentParamReq;
import io.shulie.takin.cloud.open.req.filemanager.FileCopyParamReq;
import io.shulie.takin.cloud.open.req.filemanager.FileCreateByStringParamReq;
import io.shulie.takin.cloud.open.req.filemanager.FileDeleteParamReq;
import io.shulie.takin.cloud.open.req.filemanager.FileZipParamReq;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.http.HttpHelper;
import io.shulie.takin.utils.http.TakinResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.tro.properties.TroCloudClientProperties;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-10-19
 */
@Component
public class CloudFileApiImpl extends CloudCommonApi implements CloudFileApi {

    @Autowired
    private TroCloudClientProperties troCloudClientProperties;

    @Override
    public ResponseResult<Map<String, Object>> getFileContent(FileContentParamReq req) {
        TakinResponseEntity<ResponseResult<Map<String, Object>>> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.FILE_CONTENT_BY_PATHS,
                getHeaders(req), new TypeReference<ResponseResult<Map<String, Object>>>() {}, req);
        if(takinResponseEntity.getSuccess()) {
            return takinResponseEntity.getBody();
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(),"查看cloud日志");

    }

    @Override
    public ResponseResult<Boolean> deleteFile(FileDeleteParamReq req) {
        TakinResponseEntity<Boolean> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.FILE_DELETE_URL,
                getHeaders(req), Boolean.class,req);
        if(takinResponseEntity.getSuccess()) {
            return ResponseResult.success(takinResponseEntity.getBody());
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(),"查看cloud日志");
    }

    @Override
    public ResponseResult<Boolean> copyFile(FileCopyParamReq req) {
        TakinResponseEntity<Boolean> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.FILE_COPY_URL,
                getHeaders(req), Boolean.class,req);
        if(takinResponseEntity.getSuccess()) {
            return ResponseResult.success(takinResponseEntity.getBody());
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(),"查看cloud日志");

    }

    @Override
    public  ResponseResult<Boolean>  zipFile(FileZipParamReq req) {
        TakinResponseEntity<Boolean> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.FILE_ZIP_URL,
                getHeaders(req), Boolean.class,req);
        if(takinResponseEntity.getSuccess()) {
            return ResponseResult.success(takinResponseEntity.getBody());
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(),"查看cloud日志");

    }

    @Override
    public  ResponseResult<Boolean>  createFileByPathAndString(FileCreateByStringParamReq req) {
        TakinResponseEntity<Boolean> takinResponseEntity =
            HttpHelper.doPost(troCloudClientProperties.getUrl() + CloudApiConstant.FILE_CREATE_BY_STRING,
                getHeaders(req), Boolean.class,req);
        if(takinResponseEntity.getSuccess()) {
            return ResponseResult.success(takinResponseEntity.getBody());
        }
        return ResponseResult.fail(takinResponseEntity.getHttpStatus().toString(),
            takinResponseEntity.getErrorMsg(),"查看cloud日志");
    }



}
