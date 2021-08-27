package io.shulie.takin.cloud.open.entrypoint.controller.file;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;
import io.shulie.takin.cloud.common.constants.APIUrls;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.open.req.filemanager.FileContentParamReq;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.file.FileManagerHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName FileController
 * @Description
 * @Author qianshui
 * @Date 2020/4/17 下午5:50
 */
@RestController
@RequestMapping(APIUrls.TRO_OPEN_API_URL + "file")
@Api(tags = "文件管理")
@Slf4j
public class FileOpenController {


    @Value("${script.path}")
    private String scriptPath;

    @ApiOperation("文件内容获取")
    @PostMapping(value = "/getFileContentByPaths")
    public ResponseResult<Map<String, Object>> getFileContentByPaths(@RequestBody FileContentParamReq req) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            for(String filePath :req.getPaths()) {
                if (new File(filePath).exists()) {
                    result.put(filePath, FileManagerHelper.readFileToString(new File(filePath),"UTF-8"));
                }
            }
        } catch (IOException e) {
            log.error("异常代码【{}】,异常内容：文件内容获取异常 --> 异常信息: {}",
                    TakinCloudExceptionEnum.FILE_READ_ERROR,e);
        }
        return ResponseResult.success(result);
    }

}
