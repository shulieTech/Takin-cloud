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
 * @date 2020/4/17 ??????5:50
 */
@Slf4j
@RestController
@Api(tags = "????????????")
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
    @ApiOperation(value = "????????????")
    public ResponseResult<List<FileDTO>> upload(@RequestBody List<MultipartFile> file, HttpServletRequest request) {
        List<FileDTO> result = file.stream().map(t -> {
            //??????????????????????????????
            String key = MD5Utils.getInstance().getMD5(t.getOriginalFilename());
            String uploadId = request.getHeader(key);//?????????????????????

            File targetDir = new File(tempPath + SceneManageConstant.FILE_SPLIT + uploadId);
            if (!targetDir.exists()) {
                boolean mkdirResult = targetDir.mkdirs();
                log.debug("io.shulie.takin.cloud.web.entrypoint.controller.file.NewFileController#upload-mkdirResult:{}", mkdirResult);
            }
            File targetFile = new File(tempPath + SceneManageConstant.FILE_SPLIT
                + uploadId + SceneManageConstant.FILE_SPLIT + t.getOriginalFilename());

            //????????????????????????????????????????????????10??????
            //String path = targetFile.getAbsolutePath().replaceAll("[/]", "");
            //String pathMd5 = MD5Utils.getInstance().getMD5(path);
            //redisTemplate.opsForValue().set(CACHE_NAME+pathMd5, uploadId,10, TimeUnit.MINUTES); //????????????10??????
            FileDTO dto = new FileDTO();
            try {
                if (StrUtil.isBlank(t.getOriginalFilename())) {
                    throw new FileAlreadyExistsException("?????????????????????");
                }
                // ????????????
                t.transferTo(targetFile);

                dto.setUploadId(uploadId);
                setDataCount(targetFile, dto);
                dto.setMd5(uploadId);
                dto.setFileName(t.getOriginalFilename());
                dto.setFileType(t.getOriginalFilename().endsWith("jmx") ? 0 : 1);
                dto.setDownloadUrl(targetDir + SceneManageConstant.FILE_SPLIT + t.getOriginalFilename());
                // ????????????
                dto.setIsSplit(0);
                dto.setIsDeleted(0);
                dto.setUploadResult(true);
                dto.setUploadTime(DateUtil.formatDateTime(new Date()));
            } catch (IOException e) {
                log.error("??????????????????:???{}???\n", TakinCloudExceptionEnum.FILE_CMD_EXECUTE_ERROR, e);
                dto.setUploadResult(false);
                dto.setErrorMsg(e.getMessage());
            }
            return dto;
        }).collect(Collectors.toList());
        return ResponseResult.success(result);
    }

    @DeleteMapping(EntrypointUrl.METHOD_FILE_DELETE_TEMP)
    @ApiOperation(value = "??????????????????")
    public ResponseResult<?> delete(@RequestBody FileDeleteVO vo) {
        //String tempPath = scriptPath;
        if (vo.getUploadId() != null) {
            String targetDir = tempPath + SceneManageConstant.FILE_SPLIT + vo.getUploadId();
            LinuxUtil.executeLinuxCmd("rm -rf " + targetDir);
        }
        //??????????????? ???????????????????????????????????????????????????
        return ResponseResult.success();
    }

    @ApiOperation("??????????????????")
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
            log.error("???????????????{}???,??????????????????????????????????????? --> ???????????????????????????????????????: {}",
                TakinCloudExceptionEnum.FILE_CMD_EXECUTE_ERROR, e);
        }
    }

    @ApiOperation("????????????")
    @GetMapping(value = EntrypointUrl.METHOD_FILE_DOWNLOAD_BY_PATH)
    public void downloadFileByPath(@RequestParam("filePath") String filePath, HttpServletResponse response) {
        try (OutputStream outputStream = response.getOutputStream()) {
            //?????????
            filePath = URLDecoder.decode(filePath, "utf-8");
            boolean permit = fileStrategy.filePathValidate(filePath);

            if (!permit) {
                response.setContentType("text/plain; charset=utf-8");
                outputStream.write(("??????????????????????????????????????????" + filePath).getBytes(StandardCharsets.UTF_8));
                log.warn("??????????????????????????????????????????{}", filePath);
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
                outputStream.write(("??????????????????" + filePath).getBytes(StandardCharsets.UTF_8));
                log.warn("??????????????????{}", filePath);
            }
        } catch (Exception e) {
            log.error("???????????????{}???,??????????????????????????????????????? --> ???????????????????????????????????????: {}",
                TakinCloudExceptionEnum.FILE_CMD_EXECUTE_ERROR, e);
        }
    }

    @PostMapping(EntrypointUrl.METHOD_FILE_DELETE)
    @ApiOperation(value = "????????????")
    public ResponseResult<Boolean> deleteFile(@RequestBody FileDeleteParamRequest fileDeleteParamDTO) {
        return ResponseResult.success(FileManagerHelper.deleteFiles(fileDeleteParamDTO.getPaths()));
    }

    @PostMapping(EntrypointUrl.METHOD_FILE_COPY)
    @ApiOperation(value = "????????????")
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

                //????????????????????????md5???????????????????????????md5
                //String sourceP = sourceFilePath.replaceAll("[/]", "");
                //String sourcePMd5 = MD5Utils.getInstance().getMD5(sourceP);
                //String md5 = redisTemplate.opsForValue().get(CACHE_NAME+sourcePMd5);

                redisTemplate.opsForValue().set(CACHE_NAME+targetPMd5, MD5Utils.getInstance().getMD5(f));
            }
        } catch (IOException e) {
            log.error("???????????????{}???,????????????????????????????????? --> ????????????: {}",
                TakinCloudExceptionEnum.FILE_COPY_ERROR, e);
            return ResponseResult.success(Boolean.FALSE);
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    @PostMapping(EntrypointUrl.METHOD_FILE_ZIP)
    @ApiOperation(value = "????????????")
    public ResponseResult<Boolean> zipFile(@RequestBody FileZipParamRequest fileZipParamDTO) {
        try {
            FileManagerHelper.zipFiles(fileZipParamDTO.getSourcePaths(), fileZipParamDTO.getTargetPath()
                , fileZipParamDTO.getZipFileName(), fileZipParamDTO.getIsCovered());
        } catch (Exception e) {
            log.error("???????????????{}???,????????????????????????????????? --> ????????????: {}",
                TakinCloudExceptionEnum.FILE_ZIP_ERROR, e);
            return ResponseResult.success(Boolean.FALSE);
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    @PostMapping(EntrypointUrl.METHOD_FILE_CREATE_BY_STRING)
    @ApiOperation(value = "???????????????????????????")
    public ResponseResult<String> createFileByPathAndString(@RequestBody FileCreateByStringParamRequest fileContent) {
        Boolean fileCreateResult = FileManagerHelper.createFileByPathAndString(fileContent.getFilePath(), fileContent.getFileContent());
        if (!fileCreateResult) {return ResponseResult.success();}
        // ???????????????MD5???
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
            log.error("???????????????{}???,??????????????????????????????????????? --> ????????????: {}",
                TakinCloudExceptionEnum.FILE_NOT_FOUND_ERROR, e);
        } catch (IOException e) {
            log.error("???????????????{}???,????????????????????????????????? --> ????????????: {}",
                TakinCloudExceptionEnum.FILE_CMD_EXECUTE_ERROR, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    log.error("???????????????{}???,???????????????????????????????????? --> ????????????: {}",
                        TakinCloudExceptionEnum.FILE_CLOSE_ERROR, e);
                }
            }
        }
    }

    @ApiOperation("??????????????????")
    @PostMapping(value = EntrypointUrl.METHOD_FILE_CONTENT)
    public ResponseResult<Map<String, Object>> getFileContentByPaths(@RequestBody FileContentParamReq req) {
        Map<String, Object> result = Maps.newHashMap();
        try {
            for (String filePath : req.getPaths()) {
                if (new File(filePath).exists()) {
                    //????????????
                    String currentMd5 = MD5Utils.getInstance().getMD5(new File(filePath));
                    //????????????
                    String sourceP = filePath.replaceAll("[/]", "");
                    String sourcePMd5 = MD5Utils.getInstance().getMD5(sourceP);
                    String targetMd5 = redisTemplate.opsForValue().get(CACHE_NAME+sourcePMd5);
                    if(StringUtils.isBlank(targetMd5)||currentMd5.equals(targetMd5)){
                        result.put(filePath, FileManagerHelper.readFileToString(new File(filePath), "UTF-8"));
                    }else{
                        result.put(filePath, "??????????????????,????????????:"+targetMd5+";????????????:"+currentMd5);
                    }
                }
            }
        } catch (IOException e) {
            log.error("???????????????{}???,??????????????????????????????????????? --> ????????????: {}",
                TakinCloudExceptionEnum.FILE_READ_ERROR, e);
        }
        return ResponseResult.success(result);
    }
}
