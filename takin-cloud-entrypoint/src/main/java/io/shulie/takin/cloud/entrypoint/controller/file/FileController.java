package io.shulie.takin.cloud.entrypoint.controller.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.pamirs.takin.entity.domain.dto.file.FileDTO;
import com.pamirs.takin.entity.domain.vo.file.FileDeleteVO;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.LinuxUtil;
import io.shulie.takin.cloud.common.utils.Md5Util;
import io.shulie.takin.cloud.entrypoint.controller.strategy.LocalFileStrategy;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.shulie.takin.cloud.sdk.model.request.filemanage.FileCopyParamRequest;
import io.shulie.takin.cloud.sdk.model.request.filemanage.FileCreateByStringParamRequest;
import io.shulie.takin.cloud.sdk.model.request.filemanage.FileDeleteParamRequest;
import io.shulie.takin.cloud.sdk.model.request.filemanage.FileZipParamRequest;
import io.shulie.takin.cloud.sdk.model.request.filemanager.FileContentParamReq;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.file.FileManagerHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author qianshui
 * @date 2020/4/17 下午5:50
 */
@Slf4j
@RestController
@Api(tags = "文件管理")
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_FILE)
public class FileController {

    @Value("${script.temp.path}")
    private String tempPath;

    @Value("${script.path}")
    private String scriptPath;

    @Resource
    private LocalFileStrategy fileStrategy;

    @PostMapping(EntrypointUrl.METHOD_FILE_UPLOAD)
    @ApiOperation(value = "文件上传")
    public ResponseResult<List<FileDTO>> upload(@RequestBody List<MultipartFile> file) {
        List<FileDTO> result = file.stream().map(t -> {
            String uploadId = UUID.randomUUID().toString();
            File targetDir = new File(tempPath + SceneManageConstant.FILE_SPLIT + uploadId);
            if (!targetDir.exists()) {
                boolean mkdirResult = targetDir.mkdirs();
                log.debug("io.shulie.takin.cloud.web.entrypoint.controller.file.NewFileController#upload-mkdirResult:{}", mkdirResult);
            }
            File targetFile = new File(tempPath + SceneManageConstant.FILE_SPLIT
                + uploadId + SceneManageConstant.FILE_SPLIT + t.getOriginalFilename());
            FileDTO dto = new FileDTO();
            try {
                if (StrUtil.isBlank(t.getOriginalFilename())) {
                    throw new FileAlreadyExistsException("不允许空名文件");
                }
                // 保存文件
                t.transferTo(targetFile);

                dto.setUploadId(uploadId);
                setDataCount(targetFile, dto);
                dto.setMd5(Md5Util.md5File(targetFile));
                dto.setFileName(t.getOriginalFilename());
                dto.setFileType(t.getOriginalFilename().endsWith("jmx") ? 0 : 1);
                dto.setDownloadUrl(targetDir + SceneManageConstant.FILE_SPLIT + t.getOriginalFilename());
                // 默认数据
                dto.setIsSplit(0);
                dto.setIsDeleted(0);
                dto.setUploadResult(true);
                dto.setUploadTime(DateUtil.formatDateTime(new Date()));
            } catch (IOException e) {
                log.error("文件处理异常:【{}】\n", TakinCloudExceptionEnum.FILE_CMD_EXECUTE_ERROR, e);
                dto.setUploadResult(false);
                dto.setErrorMsg(e.getMessage());
            }
            return dto;
        }).collect(Collectors.toList());
        return ResponseResult.success(result);
    }

    @DeleteMapping(EntrypointUrl.METHOD_FILE_DELETE_TEMP)
    @ApiOperation(value = "临时文件删除")
    public ResponseResult<?> delete(@RequestBody FileDeleteVO vo) {
        String tempPath = scriptPath;
        if (vo.getUploadId() != null) {
            String targetDir = tempPath + SceneManageConstant.FILE_SPLIT + vo.getUploadId();
            LinuxUtil.executeLinuxCmd("rm -rf " + targetDir);
        }
        //根据文件： 删除大文件行数，删除大文件起始位置
        return ResponseResult.success();
    }

    @ApiOperation("脚本文件下载")
    @GetMapping(value = "/download", produces = MediaType.APPLICATION_JSON_VALUE)
    public void downloadFile(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        try {
            String filePath = scriptPath + SceneManageConstant.FILE_SPLIT + fileName;

            if (new File(filePath).exists()) {
                ServletOutputStream outputStream = response.getOutputStream();
                Files.copy(Paths.get(filePath), outputStream);
                response.setContentType("application/octet-stream");
                String saveName = StringUtils.substring(fileName, StringUtils.indexOf(fileName, "/") + "/".length());
                response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(saveName.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
            }
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：文件命令执行异常 --> 脚本文件下载异常，异常信息: {}",
                TakinCloudExceptionEnum.FILE_CMD_EXECUTE_ERROR, e);
        }
    }

    @ApiOperation("文件下载")
    @GetMapping(value = EntrypointUrl.METHOD_FILE_DOWNLOAD)
    public void downloadFileByPath(@RequestParam("filePath") String filePath, HttpServletResponse response) {
        try {
            //反编码
            filePath = URLDecoder.decode(filePath, "utf-8");
            boolean permit = fileStrategy.filePathValidate(filePath);

            if (!permit) {
                log.warn("非法下载路径文件，禁止下载：{}", filePath);
                return;
            }

            if (new File(filePath).exists()) {
                ServletOutputStream outputStream = response.getOutputStream();
                Files.copy(Paths.get(filePath), outputStream);
                response.setContentType("application/octet-stream");
                String saveName = filePath.substring(filePath.lastIndexOf("/") + 1);
                response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(saveName.getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.ISO_8859_1));
            }
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：文件命令执行异常 --> 脚本文件下载异常，异常信息: {}",
                TakinCloudExceptionEnum.FILE_CMD_EXECUTE_ERROR, e);
        }
    }

    @PostMapping(EntrypointUrl.METHOD_FILE_DELETE)
    @ApiOperation(value = "文件删除")
    public ResponseResult<Boolean> deleteFile(@RequestBody FileDeleteParamRequest fileDeleteParamDTO) {
        return ResponseResult.success(FileManagerHelper.deleteFiles(fileDeleteParamDTO.getPaths()));
    }

    @PostMapping(EntrypointUrl.METHOD_FILE_COPY)
    @ApiOperation(value = "复制文件")
    public ResponseResult<Boolean> copyFile(@RequestBody FileCopyParamRequest fileCopyParamDTO) {
        try {
            FileManagerHelper.copyFiles(fileCopyParamDTO.getSourcePaths(), fileCopyParamDTO.getTargetPath());
        } catch (IOException e) {
            log.error("异常代码【{}】,异常内容：文件复制异常 --> 异常信息: {}",
                TakinCloudExceptionEnum.FILE_COPY_ERROR, e);
            return ResponseResult.success(Boolean.FALSE);
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    @PostMapping(EntrypointUrl.METHOD_FILE_ZIP)
    @ApiOperation(value = "打包文件")
    public ResponseResult<Boolean> zipFile(@RequestBody FileZipParamRequest fileZipParamDTO) {
        try {
            FileManagerHelper.zipFiles(fileZipParamDTO.getSourcePaths(), fileZipParamDTO.getTargetPath()
                , fileZipParamDTO.getZipFileName(), fileZipParamDTO.getIsCovered());
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：文件打包失败 --> 异常信息: {}",
                TakinCloudExceptionEnum.FILE_ZIP_ERROR, e);
            return ResponseResult.success(Boolean.FALSE);
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    @PostMapping(EntrypointUrl.METHOD_FILE_CREATE_BY_STRING)
    @ApiOperation(value = "根据字符串创建文件")
    public ResponseResult<String> createFileByPathAndString(@RequestBody FileCreateByStringParamRequest fileContent) {
        Boolean fileCreateResult = FileManagerHelper.createFileByPathAndString(fileContent.getFilePath(), fileContent.getFileContent());
        if (!fileCreateResult) {return ResponseResult.success();}
        // 返回文件的MD5值
        else {
            String fileMd5 = Md5Util.md5File(fileContent.getFilePath());
            return ResponseResult.success(fileMd5);
        }
    }

    private void setDataCount(File file, FileDTO dto) {
        if (!file.getName().endsWith(".csv")) {
            return;
        }
        String topic = SceneManageConstant.SCENE_TOPIC_PREFIX + System.currentTimeMillis();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            Long length = 0L;
            while (br.readLine() != null) {length++;}
            dto.setUploadedData(length);
            dto.setTopic(topic);
        } catch (FileNotFoundException e) {
            log.error("异常代码【{}】,异常内容：找不到对应的文件 --> 异常信息: {}",
                TakinCloudExceptionEnum.FILE_NOT_FOUND_ERROR, e);
        } catch (IOException e) {
            log.error("异常代码【{}】,异常内容：文件处理异常 --> 异常信息: {}",
                TakinCloudExceptionEnum.FILE_CMD_EXECUTE_ERROR, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("异常代码【{}】,异常内容：文件流关闭异常 --> 异常信息: {}",
                        TakinCloudExceptionEnum.FILE_CLOSE_ERROR, e);
                }
            }
        }
    }

    @ApiOperation("文件内容获取")
    @PostMapping(value = EntrypointUrl.METHOD_FILE_CONTENT)
    public ResponseResult<Map<String, Object>> getFileContentByPaths(@RequestBody FileContentParamReq req) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            for (String filePath : req.getPaths()) {
                if (new File(filePath).exists()) {
                    result.put(filePath, FileManagerHelper.readFileToString(new File(filePath), "UTF-8"));
                }
            }
        } catch (IOException e) {
            log.error("异常代码【{}】,异常内容：文件内容获取异常 --> 异常信息: {}",
                TakinCloudExceptionEnum.FILE_READ_ERROR, e);
        }
        return ResponseResult.success(result);
    }
}
