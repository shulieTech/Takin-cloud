package io.shulie.takin.cloud.biz.service.middleware.impl;

import java.util.Map;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.ImportParams;

import io.shulie.takin.cloud.biz.service.DistributedLock;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import io.shulie.takin.cloud.common.constants.LockKeyConstants;
import io.shulie.takin.cloud.common.constants.CloudAppConstants;
import io.shulie.takin.cloud.data.dao.middleware.MiddlewareJarDAO;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.data.result.middleware.MiddlewareJarResult;
import io.shulie.takin.cloud.biz.service.middleware.MiddlewareJarService;
import io.shulie.takin.cloud.data.param.middleware.CreateMiddleWareJarParam;
import io.shulie.takin.cloud.common.pojo.vo.middleware.ImportMiddlewareJarVO;
import io.shulie.takin.cloud.common.enums.middleware.MiddlewareJarStatusEnum;
import io.shulie.takin.cloud.common.pojo.vo.middleware.CompareMiddlewareJarVO;
import io.shulie.takin.cloud.common.pojo.vo.middleware.RemarksAndStatusDescVO;
import io.shulie.takin.cloud.common.pojo.vo.middleware.ImportMiddlewareJarResultVO;
import io.shulie.takin.cloud.common.pojo.vo.middleware.CompareMiddlewareJarResultVO;
import io.shulie.takin.cloud.common.enums.middleware.CompareMiddlewareJarStatusEnum;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

/**
 * ???????????????(MiddlewareJar)??????????????????
 *
 * @author liuchuan
 * @since 2021-06-01 11:07:09
 */
@Slf4j
@Service
public class MiddlewareJarServiceImpl implements MiddlewareJarService, CloudAppConstants, LockKeyConstants {

    @Resource
    private DistributedLock distributedLock;
    @Resource
    private MiddlewareJarDAO middlewareJarDAO;

    @Transactional(rollbackFor = Throwable.class)
    @Override
    public Workbook importMiddlewareJar(MultipartFile file) {
        // ????????????, ???????????????, ??????????????????
        String lockKey = String.format(LOCK_IMPORT_MIDDLEWARE_JAR, CloudPluginUtils.getTenantId());
        this.isImportException(!distributedLock.tryLock(lockKey, 0L, -1L, TimeUnit.SECONDS), TOO_FREQUENTLY);

        try {
            // ??????????????????
            List<ImportMiddlewareJarVO> importList = ExcelImportUtil.importExcel(file.getInputStream(),
                ImportMiddlewareJarVO.class, new ImportParams());
            this.isImportException(CollectionUtil.isEmpty(importList), "?????????????????????!");

            // ???????????????
            this.enduranceData(importList);

            // ?????????????????????????????????
            List<ImportMiddlewareJarResultVO> importResultList = this.listExportImportMiddlewareJarResult(importList);
            return ExcelExportUtil.exportExcel(new ExportParams(), ImportMiddlewareJarResultVO.class, importResultList);
        } catch (Exception e) {
            throw getImportException(e.getMessage());
        } finally {
            distributedLock.unLockSafely(lockKey);
        }
    }

    @Override
    public Workbook compareMiddlewareJar(List<MultipartFile> files) {
        this.isCompareException(CollectionUtil.isEmpty(files), "???????????????!");
        this.isCompareException(files.size() > 12, "??????????????????, ?????????12?????????????????????!");

        // ????????????, ???????????????, ??????????????????
        String lockKey = String.format(LOCK_COMPARE_MIDDLEWARE_JAR, CloudPluginUtils.getTenantId());
        this.isCompareException(!distributedLock.tryLock(lockKey, 0L, -1L, TimeUnit.SECONDS), TOO_FREQUENTLY);

        try {
            // ????????????
            List<CompareMiddlewareJarVO> importList = new ArrayList<>();
            for (MultipartFile file : files) {
                importList.addAll(ExcelImportUtil.importExcel(file.getInputStream(),
                    CompareMiddlewareJarVO.class, new ImportParams()));
            }

            this.isCompareException(CollectionUtil.isEmpty(importList), "?????????????????????!");

            // ????????????????????????, ???????????????
            List<CompareMiddlewareJarResultVO> incorrectList = this.listIncorrectResult(importList);

            // ?????????????????????, ??????
            List<CompareMiddlewareJarResultVO> correctList = this.listCompareResult(importList);

            incorrectList.addAll(correctList);
            return ExcelExportUtil.exportExcel(new ExportParams(), CompareMiddlewareJarResultVO.class, incorrectList);
        } catch (Exception e) {
            throw this.getCompareException(e.getMessage());
        } finally {
            distributedLock.unLockSafely(lockKey);
        }
    }

    /**
     * ????????????
     *
     * @param importList ????????????
     * @return ????????????
     */
    private List<CompareMiddlewareJarResultVO> listCompareResult(List<CompareMiddlewareJarVO> importList) {
        // ??????????????????????????????
        List<CompareMiddlewareJarResultVO> correctList = importList.stream().map(importVO -> {
            // version, artifactId, ??????
            String artifactId = importVO.getArtifactId();
            if (StrUtil.isBlank(artifactId)) {
                return null;
            }
            return BeanUtil.copyProperties(importVO, CompareMiddlewareJarResultVO.class);
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (correctList.isEmpty()) {
            return Collections.emptyList();
        }

        // ?????? artifactIds
        List<String> artifactIds = correctList.stream()
            .map(CompareMiddlewareJarVO::getArtifactId).collect(Collectors.toList());

        // ???????????????
        List<MiddlewareJarResult> middlewareJarResults = middlewareJarDAO.listByArtifactIds(artifactIds);
        if (middlewareJarResults.isEmpty()) {
            correctList.forEach(result -> result.setStatusDesc(CompareMiddlewareJarStatusEnum.NO.getDesc()));
            return correctList;
        }

        // ???????????????, map ??????
        Map<String, List<MiddlewareJarResult>> artifactIdAboutResultListMap = middlewareJarResults.stream()
            .collect(Collectors.groupingBy(MiddlewareJarResult::getArtifactId));

        Map<String, List<MiddlewareJarResult>> artifactIdAndGroupIdAboutResultListMap = middlewareJarResults.stream()
            .collect(Collectors.groupingBy(middlewareJarResult ->
                String.format("%s_%s", middlewareJarResult.getArtifactId(), middlewareJarResult.getGroupId())));

        correctList.parallelStream().filter(compareResult -> StrUtil.isBlank(compareResult.getGroupId()))
            .forEach(this::fillGroupId);

        // ????????????
        correctList.forEach(compareResult -> {
            // ??????????????????
            List<MiddlewareJarResult> localResults;
            String groupId = compareResult.getGroupId();
            if (StrUtil.isBlank(groupId)) {
                localResults = artifactIdAboutResultListMap.get(compareResult.getArtifactId());
            } else {
                localResults = artifactIdAndGroupIdAboutResultListMap
                    .get(String.format("%s_%s", compareResult.getArtifactId(), groupId));
            }

            // ??????????????????
            RemarksAndStatusDescVO remarksAndStatusDescVO = this.getRemarksAndStatusDesc(compareResult, localResults);
            compareResult.setStatusDesc(remarksAndStatusDescVO.getStatusDesc());
            List<String> remarks = remarksAndStatusDescVO.getRemarks();
            if (!remarks.isEmpty()) {
                compareResult.setRemark(String.join(COMMA_SPACE, remarks));
            }
        });

        return correctList;
    }

    private static final String SEARCH_MAVEN_URL_ALIYUN
        = "https://maven.aliyun.com/artifact/aliyunMaven/searchArtifactByGav?groupId=&artifactId=%s&version=%s&repoId"
        + "=all&_input_charset=utf-8";
    private static final String[] BAD_PREFIX_TOKEN_ALIYUN = new String[] {"...", ";.", "."};

    /**
     * @param middlewareJarResult ??????????????????????????????
     */
    private void fillGroupId(CompareMiddlewareJarResultVO middlewareJarResult) {
        String groupId = searchGroupIdFromAliYun(middlewareJarResult.getArtifactId(),
            middlewareJarResult.getVersion());
        if (StrUtil.isBlank(groupId)) {
            groupId = searchGroupIdFromOrg(middlewareJarResult.getArtifactId(), middlewareJarResult.getVersion());
        }
        middlewareJarResult.setGroupId(groupId);
    }

    /**
     * @param artifactId artifactId
     * @param version    version
     * @return ??????????????????????????????????????????groupId
     */
    private String searchGroupIdFromAliYun(String artifactId, String version) {
        String getGroupId = null;
        try {
            String responseString;
            try {
                responseString = HttpUtil.get(String.format(SEARCH_MAVEN_URL_ALIYUN, artifactId, version));
            } catch (Exception e) {
                //Avoid network instability???try it again
                responseString = HttpUtil.get(String.format(SEARCH_MAVEN_URL_ALIYUN, artifactId, version));
            }
            final JSONObject jsonObject = JSONUtil.parseObj(responseString);
            if (jsonObject.getBool("successful")) {
                final JSONArray object = jsonObject.getJSONArray("object");
                if (object == null || object.size() == 0) {
                    log.info("searchGroupIdFromAliYun: search empty result,artifactId:{},response:{}", artifactId, responseString);
                    return null;
                }
                for (int j = 0; j < object.size(); j++) {
                    getGroupId = object.get(j, JSONObject.class).getStr("groupId");
                    // drop bad groupId
                    if (getGroupId == null || getGroupId.contains("#") || getGroupId.contains("%")) {
                        continue;
                    }
                    // fix bad groupId
                    for (String badPrefixToken : BAD_PREFIX_TOKEN_ALIYUN) {
                        if (getGroupId.startsWith(badPrefixToken)) {
                            getGroupId = getGroupId.substring(badPrefixToken.length());
                            break;
                        }
                    }
                    break;
                }
            } else {
                log.info("searchGroupIdFromAliYun: search empty fail,artifactId:{},response:{}", artifactId, responseString);
            }
        } catch (Exception e) {
            log.error("???????????????{}???,????????????:????????????????????? --> searchGroupIdFromAliYun: artifactId:{},fillGroupId exception: {}",
                TakinCloudExceptionEnum.MIDDLEWARE_JAR_COMPARE_ERROR, artifactId, e);
        }
        return getGroupId;
    }

    private static final String SEARCH_MAVEN_URL_ORG
        = "https://search.maven.org/solrsearch/select?q=a:%s%%20AND%%20v:%s&start=0&rows=20";

    /**
     * @param artifactId artifactId
     * @param version    version
     * @return ????????????groupId
     */
    private String searchGroupIdFromOrg(String artifactId, String version) {
        String getGroupId = null;
        try {
            String responseString;
            try {
                responseString = HttpUtil.get(String.format(SEARCH_MAVEN_URL_ORG, artifactId, version));
            } catch (Exception e) {
                //Avoid network instability???try it again
                responseString = HttpUtil.get(String.format(SEARCH_MAVEN_URL_ORG, artifactId, version));
            }
            final JSONObject jsonObject = JSONUtil.parseObj(responseString);
            final JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("docs");
            if (jsonArray == null || jsonArray.size() == 0) {
                log.info("searchGroupIdFromOrg: search empty result,artifactId:{},response:{}", artifactId, responseString);
                return null;
            }
            getGroupId = jsonArray.getJSONObject(0).getStr("g");
        } catch (Exception e) {
            log.error("???????????????{}???,????????????:????????????????????? --> searchGroupIdFromOrg: artifactId:{},fillGroupId exception: {}",
                TakinCloudExceptionEnum.MIDDLEWARE_JAR_COMPARE_ERROR, artifactId, e);
        }
        return getGroupId;
    }

    /**
     * ??????????????????, ??????
     *
     * @param compareResult ??????????????????
     * @param localResults  ?????????????????????
     * @return ????????????, ??????
     */
    private RemarksAndStatusDescVO getRemarksAndStatusDesc(CompareMiddlewareJarResultVO compareResult,
        List<MiddlewareJarResult> localResults) {
        RemarksAndStatusDescVO vo = new RemarksAndStatusDescVO();

        // ???????????????????????????, ?????????
        if (CollectionUtil.isEmpty(localResults)) {
            vo.setStatusDesc(CompareMiddlewareJarStatusEnum.NO.getDesc());
            vo.setRemarks(Collections.emptyList());
            return vo;
        }

        // ????????????
        List<String> remarks = new ArrayList<>(3);
        if (StrUtil.isBlank(compareResult.getGroupId())) {
            remarks.add("???groupId");

            if (localResults.size() > 1) {
                remarks.add("???????????????artifactId");
            } else {
                remarks.add("?????????1???artifactId");
            }
        }

        vo.setRemarks(remarks);

        // ????????????
        MiddlewareJarResult noRequiredMatch = localResults.stream().filter(result ->
            this.isNoRequired(result.getStatus())).findFirst().orElse(null);
        if (noRequiredMatch != null) {
            vo.setStatusDesc(CompareMiddlewareJarStatusEnum.NO_REQUIRED.getDesc());
            return vo;
        }

        // 100%?????????
        String version = compareResult.getVersion();
        MiddlewareJarResult allMatch = localResults.stream().filter(result -> {
            String resultVersion = result.getVersion();
            return Objects.equals(version, resultVersion);
        }).findFirst().orElse(null);
        if (allMatch != null) {
            MiddlewareJarStatusEnum middlewareJarStatusEnum =
                MiddlewareJarStatusEnum.getByCode(allMatch.getStatus());
            vo.setStatusDesc(middlewareJarStatusEnum == null ? "" : middlewareJarStatusEnum.getDesc());
            remarks.add("100%??????");
            return vo;
        }

        // ??????????????????
        MiddlewareJarResult firstTwoMatch = localResults.stream().filter(result -> {
            String resultVersion = result.getVersion();
            if (StrUtil.isBlank(resultVersion) || StrUtil.isBlank(version)) {
                return false;
            }

            // ?????????
            String firstTwo = this.getFirstTwoByFlag(version, ENGLISH_PERIOD);
            String resultVersionFirstTwo = getFirstTwoByFlag(resultVersion, ENGLISH_PERIOD);
            return resultVersionFirstTwo.equals(firstTwo);
        }).findFirst().orElse(null);
        if (firstTwoMatch != null) {
            MiddlewareJarStatusEnum middlewareJarStatusEnum = MiddlewareJarStatusEnum.getByCode(
                firstTwoMatch.getStatus());
            vo.setStatusDesc(middlewareJarStatusEnum == null ? "" : middlewareJarStatusEnum.getDesc());
            remarks.add("???2?????????");
            return vo;
        }

        vo.setRemarks(Collections.singletonList("?????????????????????"));
        vo.setStatusDesc(CompareMiddlewareJarStatusEnum.NO.getDesc());
        return vo;
    }

    /**
     * ?????????????????????, ?????????????????????
     *
     * @param version ??????
     * @param flag    ??????
     * @return ???????????????
     */
    private String getFirstTwoByFlag(String version, String flag) {
        int endIndex = version.indexOf(flag, 2);
        return endIndex == -1 ? version : version.substring(0, endIndex);
    }

    /**
     * ?????????, ??????????????????
     * artifactId ??????
     *
     * @param importList ???????????????
     * @return ??????????????????
     */
    private List<CompareMiddlewareJarResultVO> listIncorrectResult(List<CompareMiddlewareJarVO> importList) {
        return importList.stream().map(importVO -> {
            // artifactId ??????
            if (StrUtil.isNotBlank(importVO.getArtifactId())) {
                return null;
            }

            CompareMiddlewareJarResultVO compareResultVO = BeanUtil.copyProperties(importVO, CompareMiddlewareJarResultVO.class);
            compareResultVO.setRemark("artifactId ?????????");
            return compareResultVO;

        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * ?????????????????????
     *
     * @param importList ???????????????
     * @return ???????????????
     */
    private List<ImportMiddlewareJarResultVO> listExportImportMiddlewareJarResult(
        List<ImportMiddlewareJarVO> importList) {
        return importList.stream().map(importVO -> {
            // ????????????
            List<String> messages = new ArrayList<>(6);

            // groupId, artifactId, ??????
            if (StrUtil.isBlank(importVO.getGroupId())) {
                messages.add("groupId ?????????");
            }

            if (StrUtil.isBlank(importVO.getArtifactId())) {
                messages.add("artifactId ?????????");
            }

            if (StrUtil.isBlank(importVO.getName())) {
                messages.add("??????????????? ?????????");
            }

            if (StrUtil.isBlank(importVO.getType())) {
                messages.add("??????????????? ?????????");
            }

            String statusDesc = importVO.getStatusDesc();
            if (StrUtil.isBlank(statusDesc)) {
                messages.add("?????? ?????????");
            }

            if (!isNoRequired(statusDesc) && StrUtil.isBlank(importVO.getVersion())) {
                messages.add("?????? ?????????");
            }

            if (messages.isEmpty()) {
                messages.add("????????????");
            }

            ImportMiddlewareJarResultVO exportVO = BeanUtil.copyProperties(importVO, ImportMiddlewareJarResultVO.class);
            String remark = String.join(COMMA_SPACE, messages);
            exportVO.setRemark(remark);
            return exportVO;
        }).collect(Collectors.toList());
    }

    /**
     * ????????????????????????
     *
     * @param importList ???????????????
     */
    private void enduranceData(List<ImportMiddlewareJarVO> importList) {
        // ???????????????
        List<CreateMiddleWareJarParam> createParams = importList.stream().map(importVO -> {
                String groupId = importVO.getGroupId();
                String artifactId = importVO.getArtifactId();
                String statusDesc = importVO.getStatusDesc();

                // groupId, artifactId ??????
                if (StrUtil.isBlank(groupId) || StrUtil.isBlank(artifactId) ||
                    StrUtil.isBlank(statusDesc) || StrUtil.isBlank(importVO.getName()) ||
                    StrUtil.isBlank(importVO.getType())) {
                    return null;
                }

                // ????????????version???null?????????????????????
                if (importVO.getVersion() == null) {
                    importVO.setVersion("");
                }

                // ?????????????????????, version ??????
                String version = importVO.getVersion();
                if (!isNoRequired(statusDesc) && StrUtil.isBlank(version)) {
                    return null;
                }

                // ?????????????????????????????????
                MiddlewareJarStatusEnum middlewareJarStatusEnum = MiddlewareJarStatusEnum.getByDesc(statusDesc);
                if (middlewareJarStatusEnum == null) {
                    return null;
                }

                CreateMiddleWareJarParam param = BeanUtil.copyProperties(importVO, CreateMiddleWareJarParam.class);
                param.setStatus(middlewareJarStatusEnum.getCode());
                param.setAgv(String.format("%s_%s_%s", artifactId, groupId, version));
                return param;
            }).filter(Objects::nonNull)
            .collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                new TreeSet<>(Comparator.comparing(CreateMiddleWareJarParam::getAgv))), ArrayList::new));

        if (createParams.isEmpty()) {
            return;
        }

        List<String> agvList = createParams.stream()
            .map(CreateMiddleWareJarParam::getAgv).collect(Collectors.toList());
        // ?????????, ???????????????
        middlewareJarDAO.removeByAgvList(agvList);
        this.isImportException(!middlewareJarDAO.saveBatch(createParams), "?????????????????????!");
    }

    /**
     * ????????????
     *
     * @param statusDesc ??????
     * @return ????????? ????????????
     */
    private boolean isNoRequired(String statusDesc) {
        return MiddlewareJarStatusEnum.NO_REQUIRED.getDesc().equals(statusDesc);
    }

    /**
     * ????????????
     *
     * @param status ??????
     * @return ????????? ????????????
     */
    private boolean isNoRequired(Integer status) {
        return MiddlewareJarStatusEnum.NO_REQUIRED.getCode().equals(status);
    }

    /**
     * ????????????????????????
     *
     * @param message ????????????
     * @return ??????
     */
    private TakinCloudException getImportException(String message) {
        return new TakinCloudException(TakinCloudExceptionEnum.MIDDLEWARE_JAR_IMPORT_ERROR, message);
    }

    /**
     * ??? ??????????????????
     *
     * @param condition ??????, true???, ????????????
     * @param message   ????????????
     */
    private void isImportException(boolean condition, String message) {
        if (condition) {
            throw getImportException(message);
        }
    }

    /**
     * ????????????????????????
     *
     * @param message ????????????
     * @return ??????
     */
    private TakinCloudException getCompareException(String message) {
        return new TakinCloudException(TakinCloudExceptionEnum.MIDDLEWARE_JAR_COMPARE_ERROR, message);
    }

    /**
     * ??? ??????????????????
     *
     * @param condition ??????, true???, ????????????
     * @param message   ????????????
     */
    private void isCompareException(boolean condition, String message) {
        if (condition) {
            throw getCompareException(message);
        }
    }

}
