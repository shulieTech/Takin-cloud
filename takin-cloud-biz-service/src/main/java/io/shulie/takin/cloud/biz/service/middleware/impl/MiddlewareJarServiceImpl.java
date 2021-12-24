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
import io.shulie.takin.cloud.data.model.mysql.MiddlewareJarEntity;
import io.shulie.takin.cloud.biz.service.middleware.MiddlewareJarService;
import io.shulie.takin.cloud.common.pojo.vo.middleware.ImportMiddlewareJarVO;
import io.shulie.takin.cloud.common.enums.middleware.MiddlewareJarStatusEnum;
import io.shulie.takin.cloud.common.pojo.vo.middleware.CompareMiddlewareJarVO;
import io.shulie.takin.cloud.common.pojo.vo.middleware.RemarksAndStatusDescVO;
import io.shulie.takin.cloud.common.pojo.vo.middleware.ImportMiddlewareJarResultVO;
import io.shulie.takin.cloud.common.pojo.vo.middleware.CompareMiddlewareJarResultVO;
import io.shulie.takin.cloud.common.enums.middleware.CompareMiddlewareJarStatusEnum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

/**
 * 中间件包表(MiddlewareJar)表服务实现类
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
        // 分布式锁, 同一个文件, 防止重复点击
        String lockKey = String.format(LOCK_IMPORT_MIDDLEWARE_JAR, CloudPluginUtils.getTenantId());
        this.isImportException(!distributedLock.tryLock(lockKey, 0L, -1L, TimeUnit.SECONDS), TOO_FREQUENTLY);

        try {
            // 解析文件数据
            List<ImportMiddlewareJarVO> importList = ExcelImportUtil.importExcel(file.getInputStream(),
                ImportMiddlewareJarVO.class, new ImportParams());
            this.isImportException(CollectionUtil.isEmpty(importList), "文件内没有数据!");

            // 持久化数据
            this.enduranceData(importList);

            // 数据整理导入的结果数据
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
        this.isCompareException(CollectionUtil.isEmpty(files), "文件不存在!");
        this.isCompareException(files.size() > 12, "文件个数太多, 请选择12个以下进行比对!");

        // 分布式锁, 同一个文件, 防止重复点击
        String lockKey = String.format(LOCK_COMPARE_MIDDLEWARE_JAR, CloudPluginUtils.getTenantId());
        this.isCompareException(!distributedLock.tryLock(lockKey, 0L, -1L, TimeUnit.SECONDS), TOO_FREQUENTLY);

        try {
            // 解析数据
            List<CompareMiddlewareJarVO> importList = new ArrayList<>();
            for (MultipartFile file : files) {
                importList.addAll(ExcelImportUtil.importExcel(file.getInputStream(),
                    CompareMiddlewareJarVO.class, new ImportParams()));
            }

            this.isCompareException(CollectionUtil.isEmpty(importList), "文件内没有数据!");

            // 数据不完整的数据, 先整理一遍
            List<CompareMiddlewareJarResultVO> incorrectList = this.listIncorrectResult(importList);

            // 收集完整的数据, 比对
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
     * 比对结果
     *
     * @param importList 导入数据
     * @return 比对结果
     */
    private List<CompareMiddlewareJarResultVO> listCompareResult(List<CompareMiddlewareJarVO> importList) {
        // 获得有正确数据的数据
        List<CompareMiddlewareJarResultVO> correctList = importList.stream().map(importVO -> {
            // version, artifactId, 必填
            String artifactId = importVO.getArtifactId();
            if (StrUtil.isBlank(artifactId)) {
                return null;
            }
            return new CompareMiddlewareJarResultVO(importVO);
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if (correctList.isEmpty()) {
            return Collections.emptyList();
        }

        // 收集 artifactIds
        List<String> artifactIds = correctList.stream()
            .map(CompareMiddlewareJarVO::getArtifactId).collect(Collectors.toList());

        // 查询数据库
        List<MiddlewareJarEntity> middlewareJarResults = middlewareJarDAO.listByArtifactIds(artifactIds);
        if (middlewareJarResults.isEmpty()) {
            correctList.forEach(result -> result.setStatusDesc(CompareMiddlewareJarStatusEnum.NO.getDesc()));
            return correctList;
        }

        // 查出的数据, map 形式
        Map<String, List<MiddlewareJarEntity>> artifactIdAboutResultListMap = middlewareJarResults.stream()
            .collect(Collectors.groupingBy(MiddlewareJarEntity::getArtifactId));

        Map<String, List<MiddlewareJarEntity>> artifactIdAndGroupIdAboutResultListMap = middlewareJarResults.stream()
            .collect(Collectors.groupingBy(middlewareJarResult ->
                String.format("%s_%s", middlewareJarResult.getArtifactId(), middlewareJarResult.getGroupId())));

        correctList.parallelStream().filter(compareResult -> StrUtil.isBlank(compareResult.getGroupId()))
            .forEach(this::fillGroupId);

        // 遍历比对
        correctList.forEach(compareResult -> {
            // 获得本地记录
            List<MiddlewareJarEntity> localResults;
            String groupId = compareResult.getGroupId();
            if (StrUtil.isBlank(groupId)) {
                localResults = artifactIdAboutResultListMap.get(compareResult.getArtifactId());
            } else {
                localResults = artifactIdAndGroupIdAboutResultListMap
                    .get(String.format("%s_%s", compareResult.getArtifactId(), groupId));
            }

            // 获得比对结果
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
     * @param middlewareJarResult 需要填充的中间件信息
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
     * @return 经过查找并去除部分错误信息的groupId
     */
    private String searchGroupIdFromAliYun(String artifactId, String version) {
        String getGroupId = null;
        try {
            String responseString;
            try {
                responseString = HttpUtil.get(String.format(SEARCH_MAVEN_URL_ALIYUN, artifactId, version));
            } catch (Exception e) {
                //Avoid network instability，try it again
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
            log.error("异常代码【{}】,异常内容:中间件对比异常 --> searchGroupIdFromAliYun: artifactId:{},fillGroupId exception: {}",
                TakinCloudExceptionEnum.MIDDLEWARE_JAR_COMPARE_ERROR, artifactId, e);
        }
        return getGroupId;
    }

    private static final String SEARCH_MAVEN_URL_ORG
        = "https://search.maven.org/solrsearch/select?q=a:%s%%20AND%%20v:%s&start=0&rows=20";

    /**
     * @param artifactId artifactId
     * @param version    version
     * @return 查找到的groupId
     */
    private String searchGroupIdFromOrg(String artifactId, String version) {
        String getGroupId = null;
        try {
            String responseString;
            try {
                responseString = HttpUtil.get(String.format(SEARCH_MAVEN_URL_ORG, artifactId, version));
            } catch (Exception e) {
                //Avoid network instability，try it again
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
            log.error("异常代码【{}】,异常内容:中间件对比异常 --> searchGroupIdFromOrg: artifactId:{},fillGroupId exception: {}",
                TakinCloudExceptionEnum.MIDDLEWARE_JAR_COMPARE_ERROR, artifactId, e);
        }
        return getGroupId;
    }

    /**
     * 获得比对结果, 备注
     *
     * @param compareResult 比对结果数据
     * @param localResults  对应的本地数据
     * @return 比对结果, 备注
     */
    private RemarksAndStatusDescVO getRemarksAndStatusDesc(CompareMiddlewareJarResultVO compareResult,
        List<MiddlewareJarEntity> localResults) {
        RemarksAndStatusDescVO vo = new RemarksAndStatusDescVO();

        // 没有匹配到本地记录, 未录入
        if (CollectionUtil.isEmpty(localResults)) {
            vo.setStatusDesc(CompareMiddlewareJarStatusEnum.NO.getDesc());
            vo.setRemarks(Collections.emptyList());
            return vo;
        }

        // 比对备注
        List<String> remarks = new ArrayList<>(3);
        if (StrUtil.isBlank(compareResult.getGroupId())) {
            remarks.add("无groupId");

            if (localResults.size() > 1) {
                remarks.add("匹配到多个artifactId");
            } else {
                remarks.add("匹配到1个artifactId");
            }
        }

        vo.setRemarks(remarks);

        // 无需支持
        MiddlewareJarEntity noRequiredMatch = localResults.stream().filter(result ->
            this.isNoRequired(result.getStatus())).findFirst().orElse(null);
        if (noRequiredMatch != null) {
            vo.setStatusDesc(CompareMiddlewareJarStatusEnum.NO_REQUIRED.getDesc());
            return vo;
        }

        // 100%匹配的
        String version = compareResult.getVersion();
        MiddlewareJarEntity allMatch = localResults.stream().filter(result -> {
            String resultVersion = result.getVersion();
            return Objects.equals(version, resultVersion);
        }).findFirst().orElse(null);
        if (allMatch != null) {
            MiddlewareJarStatusEnum middlewareJarStatusEnum =
                MiddlewareJarStatusEnum.getByCode(allMatch.getStatus());
            vo.setStatusDesc(middlewareJarStatusEnum == null ? "" : middlewareJarStatusEnum.getDesc());
            remarks.add("100%匹配");
            return vo;
        }

        // 前两位匹配的
        MiddlewareJarEntity firstTwoMatch = localResults.stream().filter(result -> {
            String resultVersion = result.getVersion();
            if (StrUtil.isBlank(resultVersion) || StrUtil.isBlank(version)) {
                return false;
            }

            // 前两位
            String firstTwo = this.getFirstTwoByFlag(version, ENGLISH_PERIOD);
            String resultVersionFirstTwo = getFirstTwoByFlag(resultVersion, ENGLISH_PERIOD);
            return resultVersionFirstTwo.equals(firstTwo);
        }).findFirst().orElse(null);
        if (firstTwoMatch != null) {
            MiddlewareJarStatusEnum middlewareJarStatusEnum = MiddlewareJarStatusEnum.getByCode(
                firstTwoMatch.getStatus());
            vo.setStatusDesc(middlewareJarStatusEnum == null ? "" : middlewareJarStatusEnum.getDesc());
            remarks.add("前2位匹配");
            return vo;
        }

        vo.setRemarks(Collections.singletonList("未匹配到版本号"));
        vo.setStatusDesc(CompareMiddlewareJarStatusEnum.NO.getDesc());
        return vo;
    }

    /**
     * 根据传入的标识, 获取前两位字符
     *
     * @param version 版本
     * @param flag    标识
     * @return 前两位字符
     */
    private String getFirstTwoByFlag(String version, String flag) {
        int endIndex = version.indexOf(flag, 2);
        return endIndex == -1 ? version : version.substring(0, endIndex);
    }

    /**
     * 比对时, 不正确的数据
     * artifactId 必填
     *
     * @param importList 导入的数据
     * @return 不正确的数据
     */
    private List<CompareMiddlewareJarResultVO> listIncorrectResult(List<CompareMiddlewareJarVO> importList) {
        return importList.stream().map(importVO -> {
            // artifactId 必填
            if (StrUtil.isNotBlank(importVO.getArtifactId())) {
                return null;
            }
            CompareMiddlewareJarResultVO compareResultVO = new CompareMiddlewareJarResultVO(importVO);
            compareResultVO.setRemark("artifactId 未填写");
            return compareResultVO;

        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 获得导入的结果
     *
     * @param importList 导入的数据
     * @return 导入的结果
     */
    private List<ImportMiddlewareJarResultVO> listExportImportMiddlewareJarResult(
        List<ImportMiddlewareJarVO> importList) {
        return importList.stream().map(importVO -> {
            // 备注信息
            List<String> messages = new ArrayList<>(6);

            // groupId, artifactId, 必填
            if (StrUtil.isBlank(importVO.getGroupId())) {
                messages.add("groupId 未填写");
            }

            if (StrUtil.isBlank(importVO.getArtifactId())) {
                messages.add("artifactId 未填写");
            }

            if (StrUtil.isBlank(importVO.getName())) {
                messages.add("中间件名称 未填写");
            }

            if (StrUtil.isBlank(importVO.getType())) {
                messages.add("中间件类型 未填写");
            }

            String statusDesc = importVO.getStatusDesc();
            if (StrUtil.isBlank(statusDesc)) {
                messages.add("状态 未填写");
            }

            if (!isNoRequired(statusDesc) && StrUtil.isBlank(importVO.getVersion())) {
                messages.add("版本 未填写");
            }

            if (messages.isEmpty()) {
                messages.add("导入成功");
            }

            ImportMiddlewareJarResultVO exportVO = new ImportMiddlewareJarResultVO(importVO);
            String remark = String.join(COMMA_SPACE, messages);
            exportVO.setRemark(remark);
            return exportVO;
        }).collect(Collectors.toList());
    }

    /**
     * 导入的数据持久化
     *
     * @param importList 导入的数据
     */
    private void enduranceData(List<ImportMiddlewareJarVO> importList) {
        // 导入数据库
        List<MiddlewareJarEntity> createParams = importList.stream().map(importVO -> {
                String groupId = importVO.getGroupId();
                String artifactId = importVO.getArtifactId();
                String statusDesc = importVO.getStatusDesc();

                // groupId, artifactId 必填
                if (StrUtil.isBlank(groupId) || StrUtil.isBlank(artifactId) ||
                    StrUtil.isBlank(statusDesc) || StrUtil.isBlank(importVO.getName()) ||
                    StrUtil.isBlank(importVO.getType())) {
                    return null;
                }

                // 导入数据version为null的默认空字符串
                if (importVO.getVersion() == null) {
                    importVO.setVersion("");
                }

                // 不是无需支持的, version 必填
                String version = importVO.getVersion();
                if (!isNoRequired(statusDesc) && StrUtil.isBlank(version)) {
                    return null;
                }

                // 根据状态字符串获得枚举
                MiddlewareJarStatusEnum middlewareJarStatusEnum = MiddlewareJarStatusEnum.getByDesc(statusDesc);
                if (middlewareJarStatusEnum == null) {
                    return null;
                }

                MiddlewareJarEntity param = new MiddlewareJarEntity();
                BeanUtils.copyProperties(importVO, param);
                param.setStatus(middlewareJarStatusEnum.getCode());
                param.setAgv(String.format("%s_%s_%s", artifactId, groupId, version));
                return param;
            }).filter(Objects::nonNull)
            .collect(Collectors.collectingAndThen(Collectors.toCollection(() ->
                new TreeSet<>(Comparator.comparing(MiddlewareJarEntity::getAgv))), ArrayList::new));

        if (createParams.isEmpty()) {
            return;
        }

        List<String> agvList = createParams.stream()
            .map(MiddlewareJarEntity::getAgv).collect(Collectors.toList());
        // 先删除, 然后再创建
        boolean result = middlewareJarDAO.removeByAgvList(agvList);
        log.debug("io.shulie.takin.cloud.biz.service.middleware.impl.MiddlewareJarServiceImpl.enduranceData\n"
            + "middlewareJarDAO.removeByAgvList(agvList)\n"
            + "result:{}", result);
        this.isImportException(!middlewareJarDAO.saveBatch(createParams), "插入数据库错误!");
    }

    /**
     * 无需支持
     *
     * @param statusDesc 状态
     * @return 是否是 无需支持
     */
    private boolean isNoRequired(String statusDesc) {
        return MiddlewareJarStatusEnum.NO_REQUIRED.getDesc().equals(statusDesc);
    }

    /**
     * 无需支持
     *
     * @param status 状态
     * @return 是否是 无需支持
     */
    private boolean isNoRequired(Integer status) {
        return MiddlewareJarStatusEnum.NO_REQUIRED.getCode().equals(status);
    }

    /**
     * 获得导入相关异常
     *
     * @param message 错误信息
     * @return 异常
     */
    private TakinCloudException getImportException(String message) {
        return new TakinCloudException(TakinCloudExceptionEnum.MIDDLEWARE_JAR_IMPORT_ERROR, message);
    }

    /**
     * 是 导入相关异常
     *
     * @param condition 条件, true时, 抛出异常
     * @param message   错误信息
     */
    private void isImportException(boolean condition, String message) {
        if (condition) {
            throw getImportException(message);
        }
    }

    /**
     * 获得比对相关异常
     *
     * @param message 错误信息
     * @return 异常
     */
    private TakinCloudException getCompareException(String message) {
        return new TakinCloudException(TakinCloudExceptionEnum.MIDDLEWARE_JAR_COMPARE_ERROR, message);
    }

    /**
     * 是 比对相关异常
     *
     * @param condition 条件, true时, 抛出异常
     * @param message   错误信息
     */
    private void isCompareException(boolean condition, String message) {
        if (condition) {
            throw getCompareException(message);
        }
    }

}
