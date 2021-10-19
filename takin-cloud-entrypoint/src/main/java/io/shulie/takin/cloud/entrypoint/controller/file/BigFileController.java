package io.shulie.takin.cloud.entrypoint.controller.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;

import com.pamirs.takin.entity.domain.vo.file.Part;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.shulie.takin.cloud.biz.service.cloud.server.BigFileService;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author mubai
 * @date 2020-05-12 14:47
 */
@RestController
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_FILE_BIG)
public class BigFileController {

    public static Logger logger = LoggerFactory.getLogger(BigFileController.class);

    @Autowired
    private BigFileService bigFileService;

    private static void writeFile(HttpServletResponse response, File file) {
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[64];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
    }

    @PostMapping(EntrypointUrl.METHOD_BIG_FILE_UPLOAD)
    public ResponseResult<?> upload(HttpServletRequest request, String param, @RequestBody List<MultipartFile> file) {

        Part uploadVO = JSON.parseObject(param, Part.class);

        if (uploadVO.getUserAppKey() == null || uploadVO.getSceneId() == null || uploadVO.getFileName() == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR, "license | sceneId | fileName can not be null");
        }

        if (file.size() == 0) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR, "upload file can not be null");
        }

        try {
            MultipartFile multipartFile = file.get(0);
            byte[] bytes = multipartFile.getBytes();
            uploadVO.setByteData(bytes);
            return bigFileService.upload(uploadVO);
        } catch (IOException e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_ERROR, "文件上传失败!", e);
        }

    }

    private Part getBigFileUploadVO(HttpServletRequest request) {
        //字段放在header 中，body 放字节数组
        String userAppKey = request.getHeader("userAppKey");
        String sceneId = request.getHeader("sceneId");
        String fileName = request.getHeader("fileName");
        String start = request.getHeader("start");
        String end = request.getHeader("end");
        String md5 = request.getHeader("md5");
        String originalName = request.getHeader("originalName");

        Part param = new Part();
        param.setStart(Long.valueOf(start));
        param.setFileName(fileName);
        param.setUserAppKey(userAppKey);
        param.setSceneId(Long.valueOf(sceneId));
        param.setOriginalName(originalName);
        param.setEnd(Long.valueOf(end));
        param.setMd5(md5);
        return param;
    }

    @PostMapping(EntrypointUrl.METHOD_BIG_FILE_COMPACT)
    public ResponseResult<Map<String, Object>> compact(HttpServletRequest request, @RequestBody Part param) {
        //BigFileUploadVO param = JSON.parseObject(json,BigFileUploadVO.class);
        if (param.getUserAppKey() == null || param.getSceneId() == null || param.getOriginalName() == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.BIGFILE_UPLOAD_VERIFY_ERROR, "license | sceneId | fileName can not be null");

        }
        return bigFileService.compact(param);
    }

    @ApiOperation("客户端下载")
    @GetMapping(value = EntrypointUrl.METHOD_BIG_FILE_DOWNLOAD, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public void downloadFile(HttpServletResponse response) {
        logger.info("上传客户端下载...");
        File pradarUploadFile = bigFileService.getPradarUploadFile();
        if (pradarUploadFile != null) {
            writeFile(response, pradarUploadFile);
        }

    }

}
