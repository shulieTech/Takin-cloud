package io.shulie.takin.cloud.biz.service.schedule.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;

import com.pamirs.takin.entity.dao.scene.manage.TSceneManageMapper;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneScriptRef;
import com.pamirs.takin.entity.domain.vo.file.FileSliceRequest;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskService;
import io.shulie.takin.cloud.biz.service.schedule.FileSliceService;
import io.shulie.takin.cloud.common.enums.FileSliceStatusEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.utils.FileSliceByLine;
import io.shulie.takin.cloud.common.utils.FileSliceByLine.FileSliceInfo;
import io.shulie.takin.cloud.common.utils.FileSliceByPodNum;
import io.shulie.takin.cloud.data.dao.scenemanage.SceneManageDAO;
import io.shulie.takin.cloud.common.utils.FileSliceByPodNum.Builder;
import io.shulie.takin.cloud.common.utils.FileSliceByPodNum.StartEndPair;
import io.shulie.takin.cloud.data.dao.scenemanage.SceneBigFileSliceDAO;
import io.shulie.takin.cloud.data.model.mysql.SceneBigFileSliceEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneScriptRefEntity;
import io.shulie.takin.cloud.data.result.scenemanage.SceneManageResult;
import io.shulie.takin.cloud.data.param.scenemanage.SceneBigFileSliceParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author moriarty
 */
@Service
@Slf4j
public class FileSliceServiceImpl implements FileSliceService {
    @Resource
    SceneBigFileSliceDAO fileSliceDAO;
    @Value("${script.path}")
    private String nfsDir;
    @Resource
    SceneManageDAO sceneManageDao;

    @Autowired
    SceneTaskService sceneTaskService;

    private static final String DEFAULT_PATH_SEPARATOR = "/";

    private static final String DEFAULT_FILE_COLUMN_SEPARATOR = ",";

    @Override
    public boolean fileSlice(FileSliceRequest request) {
        //1.是否分片/是否已分片
        Integer fileSliceStatusCode = this.isFileSliced(request);
        FileSliceStatusEnum fileSliceStatus = FileSliceStatusEnum.getFileSliceStatusEnumByCode(
            fileSliceStatusCode);
        if (Objects.nonNull(request.getForceSplit()) && request.getForceSplit()) {
            if (fileSliceStatus == FileSliceStatusEnum.SLICED) {
                fileSliceStatus = FileSliceStatusEnum.FILE_CHANGED;
            }
        }
        //文件已拆分
        if (fileSliceStatus == FileSliceStatusEnum.SLICED) {
            log.info("【文件分片】--场景ID：【{}】，文件名：【{}】 文件已经分片完成.", request.getSceneId(), request.getFileName());
            return true;
        }
        //文件拆分中
        if (fileSliceStatus == FileSliceStatusEnum.SLICING) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_CSV_FILE_SPLIT_ERROR, "文件分片任务执行中，请勿重复发起" +
                ":场景ID【{" + request.getSceneId() + "}】,文件名【{" + request.getFileName() + "}】");
        }
        //根据请求，更新关联的scriptRef
        if (request.getSplit()) {
            updateFileRefExtend(request);
        }
        //填充request
        fillRequest(request);

        SceneBigFileSliceParam param = new SceneBigFileSliceParam() {{
            setSceneId(request.getSceneId());
            setFileRefId(request.getRefId());
            setFileName(request.getFileName());
            setStatus(FileSliceStatusEnum.SLICING.getCode());
        }};

        SceneScriptRefEntity entity = fileSliceDAO.selectRef(param);
        if (Objects.nonNull(entity)){
            param.setFileUploadTime(entity.getUploadTime());
        }
        if (fileSliceStatus == FileSliceStatusEnum.FILE_CHANGED) {
            if (request.isBigFile() && request.getOrderSplit()){
                return false;
            }
            fileSliceDAO.update(param);
        } else if (fileSliceStatus == FileSliceStatusEnum.UNSLICED) {
            if (request.isBigFile() && request.getOrderSplit()){
                return false;
            }
            fileSliceDAO.create(param);
        }
        if (request.getOrderSplit() != null && request.getOrderSplit()) {
            sliceFileByOrder(request, param);
        } else {
            sliceFileWithoutOrder(request, param);
        }
        return fileSliceDAO.update(param) == 1;
    }

    @Override
    public SceneBigFileSliceEntity getOneByParam(FileSliceRequest request) {
        return fileSliceDAO.selectOne(new SceneBigFileSliceParam() {{
            setSceneId(request.getSceneId());
            setFileName(request.getFileName());
        }});
    }

    @Override
    public Integer isFileSliced(FileSliceRequest request) {
        return fileSliceDAO.isFileSliced(new SceneBigFileSliceParam() {{
            setSceneId(request.getSceneId());
            //setFileRefId(request.getRefId());
            setFileName(request.getFileName());
        }});
    }

    @Override
    public Boolean updateFileRefExtend(FileSliceRequest request) {
        boolean hasChange = false;
        SceneScriptRefEntity entity = fileSliceDAO.selectRef(new SceneBigFileSliceParam() {{
            setSceneId(request.getSceneId());
            setFileName(request.getFileName());
        }});
        JSONObject jsonObject;
        if (entity == null || entity.getId() == null) {
            SceneScriptRef insertParam = new SceneScriptRef();
            insertParam.setFileName(request.getFileName());
            insertParam.setSceneId(request.getSceneId());
            insertParam.setScriptType(0);
            insertParam.setFileType(1);
            insertParam.setUploadTime(new Date());
            insertParam.setUploadPath(request.getSceneId() + DEFAULT_PATH_SEPARATOR + request.getFileName());
            jsonObject = new JSONObject();
            if (request.getSplit()) {
                jsonObject.put(FileSliceService.IS_SPLIT, 1);
            }
            if (request.getOrderSplit()) {
                jsonObject.put(FileSliceService.IS_ORDER_SPLIT, 1);
            }
            insertParam.setFileExtend(jsonObject.toJSONString());
            Long refId = fileSliceDAO.createRef(insertParam);
            return refId > 0;
        }
        jsonObject = JSONObject.parseObject(entity.getFileExtend());
        if (request.getSplit() && !jsonObject.containsKey(FileSliceService.IS_SPLIT)) {
            jsonObject.put(FileSliceService.IS_SPLIT, 1);
            hasChange = true;
        }
        if (request.getOrderSplit() && !jsonObject.containsKey(FileSliceService.IS_ORDER_SPLIT)) {
            jsonObject.put(FileSliceService.IS_ORDER_SPLIT, 1);
            hasChange = true;
        }
        entity.setFileExtend(jsonObject.toJSONString());
        if (hasChange) {
            return fileSliceDAO.updateRef(entity) == 1;
        }
        return true;
    }


    @Override
    public void preSlice(SceneBigFileSliceParam param) {
        //todo 时间改为一致
        Date currentDate = new Date();
        SceneScriptRefEntity entity = fileSliceDAO.selectRef(param);
        if (Objects.isNull(entity)) {
            Date finalCurrentDate = currentDate;
            SceneScriptRef sceneScriptRef = new SceneScriptRef() {{
                setFileName(param.getFileName());
                setSceneId(param.getSceneId());
                setUploadPath(param.getSceneId() + DEFAULT_PATH_SEPARATOR + param.getFileName());
                JSONObject extJson = new JSONObject();
                extJson.put("isSplit", param.getIsSplit());
                extJson.put("isOrderSplit", 1);
                extJson.put("isBigFile",1);
                setFileExtend(extJson.toJSONString());
                setUploadTime(finalCurrentDate);
                setFileType(1);
                setScriptType(0);
                setIsDeleted(0);
            }};
            fileSliceDAO.createRef(sceneScriptRef);
            entity = fileSliceDAO.selectRef(param);
        } else {
            JSONObject extJson = JSONObject.parseObject(entity.getFileExtend());
            extJson.put("isSplit", param.getIsSplit());
            extJson.put("isOrderSplit", 1);
            extJson.put("isBigFile",1);
            currentDate = entity.getUploadTime();
            entity.setFileExtend(extJson.toJSONString());
            fileSliceDAO.updateRef(entity);
        }
        param.setFileRefId(entity.getId());
        param.setFileUploadTime(currentDate);
        param.setStatus(FileSliceStatusEnum.SLICED.getCode());
        param.setFilePath(param.getSceneId() + DEFAULT_PATH_SEPARATOR + param.getFileName());
        SceneBigFileSliceEntity sliceEntity = fileSliceDAO.selectOne(param);
        if (sliceEntity == null) {
            fileSliceDAO.create(param);
        } else {
            fileSliceDAO.update(param);
        }
        //加文件分片清除位点缓存的逻辑
        sceneTaskService.cleanCachedPosition(param.getSceneId());
    }

    /**
     * 文件顺序拆分，包含排序字段，排序列号如果没有传，默认按最后一列
     *
     * @param request -
     * @param param   -
     */
    private void sliceFileByOrder(FileSliceRequest request, SceneBigFileSliceParam param) {
        try {
            log.info("【文件分片】--场景ID：【{}】，文件名：【{}】 文件【顺序分片】任务执开始.", request.getSceneId(), request.getFileName());
            FileSliceByLine fileSliceUtil =
                new FileSliceByLine.Builder(nfsDir + DEFAULT_PATH_SEPARATOR + request.getFilePath())
                    .withSeparator(StringUtils.isNotBlank(request.getColumnSeparator()) ? request.getColumnSeparator()
                        : DEFAULT_FILE_COLUMN_SEPARATOR)
                    .withOrderColumnNum(request.getOrderColumnNum())
                    .build();
            Map<Integer, FileSliceInfo> resultMap = fileSliceUtil.sliceFile();
            log.info("【文件分片】--场景ID：【{}】，文件名：【{}】 文件【顺序分片】任务执结束.", request.getSceneId(), request.getFileName());
            if (resultMap.size() > 0) {
                //不需要记录计算出来的partition的hash值  modified by xr.l @20210804
                List<FileSliceInfo> resultList = new ArrayList<>();
                for (Map.Entry<Integer, FileSliceInfo> entry : resultMap.entrySet()) {
                    resultList.add(entry.getValue());
                }
                String sliceInfo = JSONObject.toJSONString(resultList);
                param.setStatus(FileSliceStatusEnum.SLICED.getCode());
                param.setSliceInfo(sliceInfo);
                param.setSliceCount(resultMap.size());
            }
        } catch (Exception e) {
            log.error("【文件分片】--场景ID：【{}】，文件名：【{}】 文件【顺序分片】任务异常,异常信息：【{}】", request.getSceneId(), request.getFileName(),
                e.getMessage());
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_CSV_FILE_SPLIT_ERROR, e);
        }
    }

    /**
     * 根据pod数量拆分文件
     *
     * @param request -
     * @param param   -
     */
    private void sliceFileWithoutOrder(FileSliceRequest request, SceneBigFileSliceParam param) {
        try {
            log.info("【文件分片】--场景ID：【{}】，文件名：【{}】，传入pod数量：【{}】 文件分片任务执开始.", request.getSceneId(), request.getFileName(),
                request.getPodNum());
            FileSliceByPodNum build = new Builder(nfsDir + DEFAULT_PATH_SEPARATOR + request.getFilePath())
                .withPartSize(request.getPodNum())
                .build();
            ArrayList<StartEndPair> startEndPairs = build.getStartEndPairs();
            log.info("【文件分片】--场景ID：【{}】，文件名：【{}】，实际分片数量：【{}】 文件分片任务执结束.", request.getSceneId(), request.getFileName(),
                startEndPairs.size());
            if (!startEndPairs.isEmpty()) {
                String sliceInfo = JSONObject.toJSONString(startEndPairs);
                param.setStatus(FileSliceStatusEnum.SLICED.getCode());
                param.setSliceInfo(sliceInfo);
                param.setSliceCount(startEndPairs.size());
            }
        } catch (Exception e) {
            log.error("【文件分片】--场景ID：【{}】，文件名：【{}】， 文件分片任务异常，异常信息【{}】.", request.getSceneId(), request.getFileName(),
                e);
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_CSV_FILE_SPLIT_ERROR, e);
        }
    }

    /**
     * 填充 podNum,refId,filePath
     *
     * @param request -
     */
    private void fillRequest(FileSliceRequest request) {
        //refId,filePath
        if (request.getRefId() == null || StringUtils.isBlank(request.getFilePath())) {
            SceneScriptRefEntity entity = fileSliceDAO.selectRef(new SceneBigFileSliceParam() {{
                setSceneId(request.getSceneId());
                setFileName(request.getFileName());
            }});
            request.setRefId(entity.getId());
            request.setFilePath(entity.getUploadPath());
        }
        //podNum
        if (request.getForceSplit() && request.getPodNum() > 0) {
            request.setPodNum(request.getPodNum());
        }
    }

}
