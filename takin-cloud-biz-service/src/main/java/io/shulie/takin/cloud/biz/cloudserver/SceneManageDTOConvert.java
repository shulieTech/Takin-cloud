package io.shulie.takin.cloud.biz.cloudserver;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.pamirs.takin.entity.domain.entity.scene.manage.SceneBusinessActivityRef;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneManage;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneScriptRef;
import com.pamirs.takin.entity.domain.entity.scene.manage.SceneSlaRef;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageListOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.common.bean.RuleBean;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;
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

    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "customerId", target = "tenantId"),
        @Mapping(source = "sceneName", target = "sceneName"),
        @Mapping(source = "lastPtTime", target = "lastPtTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
        @Mapping(source = "status", target = "status")
    })
    SceneManageListOutput of(SceneManage source);

    List<SceneManageListOutput> ofs(List<SceneManage> sources);

    @AfterMapping
    default void fillSceneManageListDTO(SceneManage source, @MappingTarget SceneManageListOutput dto) {
        String ptConfig = source.getPtConfig();
        if (ptConfig == null) {
            return;
        }
        JSONObject jsonObject = JSON.parseObject(ptConfig);
        BigDecimal flow = jsonObject.getBigDecimal(SceneManageConstant.ESTIMATE_FLOW);
        dto.setEstimateFlow(flow != null ? flow.setScale(2, RoundingMode.HALF_UP) : null);
    }

    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "bindRef", target = "bindRef"),
        @Mapping(source = "businessActivityId", target = "businessActivityId"),
        @Mapping(source = "businessActivityName", target = "businessActivityName")
    })
    SceneManageWrapperOutput.SceneBusinessActivityRefOutput of(SceneBusinessActivityRef source);

    @AfterMapping
    default void fillGoalValue(SceneBusinessActivityRef source, @MappingTarget SceneManageWrapperOutput.SceneBusinessActivityRefOutput dto) {
        String goalValue = source.getGoalValue();
        if (StringUtils.isBlank(goalValue)) {
            return;
        }
        JSONObject jsonObject = JSON.parseObject(goalValue);
        dto.setTargetTPS(jsonObject.getInteger(SceneManageConstant.TPS));
        dto.setTargetRT(jsonObject.getInteger(SceneManageConstant.RT));
        dto.setTargetSuccessRate(jsonObject.getBigDecimal(SceneManageConstant.SUCCESS_RATE));
        dto.setTargetSA(jsonObject.getBigDecimal(SceneManageConstant.SA));
    }

    List<SceneManageWrapperOutput.SceneBusinessActivityRefOutput> ofBusinessActivityList(List<SceneBusinessActivityRef> sources);


    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "fileName", target = "fileName"),
        @Mapping(source = "fileSize", target = "fileSize"),
        @Mapping(source = "fileType", target = "fileType"),
        @Mapping(source = "uploadPath", target = "uploadPath"),
        @Mapping(source = "uploadTime", target = "uploadTime", dateFormat = "yyyy-MM-dd HH:mm:ss"),
        @Mapping(source = "isDeleted", target = "isDeleted"),
    })
    SceneManageWrapperOutput.SceneScriptRefOutput of(SceneScriptRef source);

    List<SceneManageWrapperOutput.SceneScriptRefOutput> ofScriptList(List<SceneScriptRef> sources);

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
    }

    @Mappings({
        @Mapping(source = "id", target = "id"),
        @Mapping(source = "slaName", target = "ruleName"),
        @Mapping(source = "status", target = "status"),
        @Mapping(source = "businessActivityIds", target = "businessActivity", ignore = true)
    })
    SceneManageWrapperOutput.SceneSlaRefOutput of(SceneSlaRef source);

    @AfterMapping
    default void fillSlaRule(SceneSlaRef source, @MappingTarget SceneManageWrapperOutput.SceneSlaRefOutput dto) {
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

    List<SceneManageWrapperOutput.SceneSlaRefOutput> ofSlaList(List<SceneSlaRef> sources);
}
