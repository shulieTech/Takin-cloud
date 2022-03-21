package io.shulie.takin.cloud.entrypoint.controller.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import com.pamirs.takin.entity.domain.dto.file.FileDTO;
import com.pamirs.takin.entity.domain.vo.file.FileDeleteVO;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import io.shulie.takin.cloud.common.utils.FileUtils;
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
import io.shulie.takin.utils.PathFormatForTest;
import io.shulie.takin.utils.file.FileManagerHelper;
import io.shulie.takin.utils.security.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
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
    private RedisTemplate<String, String> redisTemplate;

    public static final String CACHE_NAME = "t:a:c:pressure:filemd5";

    @Resource
    private LocalFileStrategy fileStrategy;


    @PostMapping(EntrypointUrl.METHOD_FILE_UPLOAD)
    @ApiOperation(value = "文件上传")
    public ResponseResult<List<FileDTO>> upload(@RequestBody List<MultipartFile> file, HttpServletRequest request) {
        List<FileDTO> result = file.stream().map(t -> {
            //文件内容签名做父目录
            String key = MD5Utils.getInstance().getMD5(t.getOriginalFilename());
            String uploadId = request.getHeader(key);//文件内容签名值

            File targetDir = new File(tempPath + SceneManageConstant.FILE_SPLIT + uploadId);
            if (!targetDir.exists()) {
                boolean mkdirResult = targetDir.mkdirs();
                log.debug("io.shulie.takin.cloud.web.entrypoint.controller.file.NewFileController#upload-mkdirResult:{}", mkdirResult);
            }
            File targetFile = new File(tempPath + SceneManageConstant.FILE_SPLIT
                + uploadId + SceneManageConstant.FILE_SPLIT + t.getOriginalFilename());

            //临时文件签名，存储缓存，超时设置10分钟
            //String path = targetFile.getAbsolutePath().replaceAll("[/]", "");
            //String pathMd5 = MD5Utils.getInstance().getMD5(path);
            //redisTemplate.opsForValue().set(CACHE_NAME+pathMd5, uploadId,10, TimeUnit.MINUTES); //临时存储10分钟
            FileDTO dto = new FileDTO();
            try {
                if (StrUtil.isBlank(t.getOriginalFilename())) {
                    throw new FileAlreadyExistsException("不允许空名文件");
                }
                // 保存文件
                t.transferTo(targetFile);

                dto.setUploadId(uploadId);
                setDataCount(targetFile, dto);
                dto.setMd5(uploadId);
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
        //String tempPath = scriptPath;
        if (vo.getUploadId() != null) {
            String targetDir = tempPath + SceneManageConstant.FILE_SPLIT + vo.getUploadId();
            LinuxUtil.executeLinuxCmd("rm -rf " + targetDir);
        }
        //根据文件： 删除大文件行数，删除大文件起始位置
        return ResponseResult.success();
    }

    @ApiOperation("脚本文件下载")
    @GetMapping(value = EntrypointUrl.METHOD_FILE_DOWNLOAD, produces = MediaType.APPLICATION_JSON_VALUE)
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
    @GetMapping(value = EntrypointUrl.METHOD_FILE_DOWNLOAD_BY_PATH)
    public void downloadFileByPath(@RequestParam("filePath") String filePath, HttpServletResponse response) {
        try (OutputStream outputStream = response.getOutputStream()) {
            //反编码
            filePath = URLDecoder.decode(filePath, "utf-8");
            boolean permit = fileStrategy.filePathValidate(filePath);

            if (!permit) {
                response.setContentType("text/plain; charset=utf-8");
                outputStream.write(("非法下载路径文件，禁止下载：" + filePath).getBytes(StandardCharsets.UTF_8));
                log.warn("非法下载路径文件，禁止下载：{}", filePath);
                return;
            }
            if (new File(filePath).exists()) {
                Files.copy(Paths.get(filePath), outputStream);
                response.setContentType("application/octet-stream");
                String saveName = filePath.substring(filePath.lastIndexOf("/") + 1);
                response.setHeader("Content-Disposition",
                    "attachment;filename=" + new String(saveName.getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.ISO_8859_1));
            } else {
                response.setContentType("text/plain; charset=utf-8");
                outputStream.write(("文件不存在：" + filePath).getBytes(StandardCharsets.UTF_8));
                log.warn("文件不存在：{}", filePath);
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
            List<String> sourceList = fileCopyParamDTO.getSourcePaths();
            List<String> sourceList2 = new ArrayList<>();
            for(String temp : sourceList){
                sourceList2.add(PathFormatForTest.format(temp));
            }
            fileCopyParamDTO.setSourcePaths(sourceList2);
            String targetPath = fileCopyParamDTO.getTargetPath();
            targetPath = PathFormatForTest.format(targetPath);
            fileCopyParamDTO.setTargetPath(targetPath);
            FileManagerHelper.copyFiles(fileCopyParamDTO.getSourcePaths(), fileCopyParamDTO.getTargetPath());
            for(String sourceFilePath:fileCopyParamDTO.getSourcePaths()){
                File f = new File(sourceFilePath);
                String targetP = (fileCopyParamDTO.getTargetPath()+"/"+f.getName()).replaceAll("[/]", "");
                String targetPMd5 = MD5Utils.getInstance().getMD5(targetP);

                //把临时文件缓存的md5，复制至正式文件的md5
                //String sourceP = sourceFilePath.replaceAll("[/]", "");
                //String sourcePMd5 = MD5Utils.getInstance().getMD5(sourceP);
                //String md5 = redisTemplate.opsForValue().get(CACHE_NAME+sourcePMd5);

                redisTemplate.opsForValue().set(CACHE_NAME+targetPMd5, MD5Utils.getInstance().getMD5(f));
            }
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
                    //当前签名
                    String currentMd5 = MD5Utils.getInstance().getMD5(new File(filePath));
                    //期望签名
                    String sourceP = filePath.replaceAll("[/]", "");
                    String sourcePMd5 = MD5Utils.getInstance().getMD5(sourceP);
                    String targetMd5 = redisTemplate.opsForValue().get(CACHE_NAME+sourcePMd5);
                    if(StringUtils.isBlank(targetMd5)||currentMd5.equals(targetMd5)){
                        result.put(filePath, FileManagerHelper.readFileToString(new File(filePath), "UTF-8"));
                    }else{
                        result.put(filePath, "文件已被篡改,期望签名:"+targetMd5+";实际签名:"+currentMd5);
                    }
                }
            }
        } catch (IOException e) {
            log.error("异常代码【{}】,异常内容：文件内容获取异常 --> 异常信息: {}",
                TakinCloudExceptionEnum.FILE_READ_ERROR, e);
        }
        return ResponseResult.success(result);
    }
}
