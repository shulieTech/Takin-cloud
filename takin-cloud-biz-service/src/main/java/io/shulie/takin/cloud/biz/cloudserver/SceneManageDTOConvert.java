package io.shulie.takin.cloud.biz.cloudserver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.pamirs.takin.entity.domain.entity.scene.manage.SceneBusinessActivityRef;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneScriptRef;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneSlaRef;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageListOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.sdk.model.common.RuleBean;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResponse.SceneSlaRefResponse;
import io.shulie.takin.cloud.ext.content.enginecall.BusinessActivityExt;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @author qianshui
 * @date 2020/4/17 下午4:26
 */
@Mapper
public interface SceneManageDTOConvert {

    SceneManageDTOConvert INSTANCE = Mappers.getMapper(SceneManageDTOConvert.class);

    /**
     * 数据转换
     *
     * @param sources 原数据
     * @return 转换后数据
     */
    List<SceneManageWrapperOutput.SceneBusinessActivityRefOutput> ofBusinessActivityList(List<SceneBusinessActivityRef> sources);

    /**
     * 数据转换
     *
     * @param source 原数据
     * @return 转换后数据
     */
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "fileName", target = "fileName"),
        @Mapping(source = "fileSize", target = "fileSize"),
        @Mapping(source = "fileType", target = "fileType"),
        @Mapping(source = "uploadPath", target = "uploadPath"),
        @Mapping(source = "uploadTime", target = "uploadTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
        @Mapping(source = "isDeleted", target = "isDeleted"),
        @Mapping(source = "fileMd5", target = "fileMd5"),
    })
    SceneManageWrapperOutput.SceneScriptRefOutput of(SceneScriptRef source);

    /**
     * 数据转换
     *
     * @param sources 原数据
     * @return 转换后数据
     */
    List<SceneManageWrapperOutput.SceneScriptRefOutput> ofScriptList(List<SceneScriptRef> sources);

    /**
     * 填充脚本信息
     *
     * @param source 原数据
     * @param dto    脚本信息
     */
    @AfterMapping
    default void fillScript(SceneScriptRef source, @MappingTarget SceneManageWrapperOutput.SceneScriptRefOutput dto) {
        if (StringUtils.isBlank(source.getFileExtend())) {
            return;
        }
        JSONObject jsonObject = JSON.parseObject(source.getFileExtend());
        dto.setUploadedData(jsonObject.getLong(SceneManageConstant.DATA_COUNT));
        dto.setIsSplit(jsonObject.getInteger(SceneManageConstant.IS_SPLIT));
        dto.setTopic(jsonObject.getString(SceneManageConstant.TOPIC));
        dto.setIsOrderSplit(jsonObject.getInteger(SceneManageConstant.IS_ORDERED_SPLIT));
        dto.setIsBigFile(jsonObject.getInteger(SceneManageConstant.IS_BIG_FILE));
    }

    /**
     * 数据转换
     *
     * @param source 原数据
     * @return 转换后数据
     */
    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "slaName", target = "ruleName"),
        @Mapping(source = "status", target = "status"),
        @Mapping(source = "businessActivityIds", target = "businessActivity", ignore = true)
    })
    SceneSlaRefResponse of(SceneSlaRef source);

    /**
     * 填充Sla规则数据
     *
     * @param source 原数据
     * @param dto    Sla规则
     */
    @AfterMapping
    default void fillSlaRule(SceneSlaRef source, @MappingTarget SceneSlaRefResponse dto) {
        String condition = source.getCondition();
        JSONObject jsonObject = JSON.parseObject(condition);
        Integer compareType = jsonObject.getInteger(SceneManageConstant.COMPARE_TYPE);
        BigDecimal compareValue = jsonObject.getBigDecimal(SceneManageConstant.COMPARE_VALUE);
        Integer achieveTimes = jsonObject.getInteger(SceneManageConstant.ACHIEVE_TIMES);
        String event = jsonObject.getString(SceneManageConstant.EVENT);
        dto.setEvent(event);
        RuleBean ruleResult = new RuleBean();
        ruleResult.setIndexInfo(source.getTargetType());
        ruleResult.setCondition(compareType);
        ruleResult.setDuring(compareValue);
        ruleResult.setTimes(achieveTimes);
        dto.setRule(ruleResult);
        dto.setBusinessActivity(StringUtils.split(source.getBusinessActivityIds(), ","));
    }

    /**
     * 数据转换
     *
     * @param sources 原数据
     * @return 转换后数据
     */
    List<SceneSlaRefResponse> ofSlaList(List<SceneSlaRef> sources);

    @Mappings({
        @Mapping(source = "bindRef", target = "bindRef"),
        @Mapping(source = "businessActivityName", target = "activityName"),
        @Mapping(source = "targetRT", target = "rt"),
        @Mapping(source = "targetTPS", target = "tps"),
    })
    BusinessActivityExt of(SceneManageWrapperOutput.SceneBusinessActivityRefOutput source);
}
