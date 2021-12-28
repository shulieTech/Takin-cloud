package io.shulie.takin.cloud.biz.output.scene.manage;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.common.constants.SceneManageConstant;

/**
 * @author qianshui
 * @date 2020/4/17 下午2:45
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel(description = "列表查询出参")
public class SceneManageListOutput extends ContextExt {
    /**
     * 从原来的Convert来的，梳理后再删掉
     *
     * @param entity 实体类
     */
    public SceneManageListOutput(SceneManageEntity entity) {
        this.setId(entity.getId());
        this.setStatus(entity.getStatus());
        this.setTenantId(entity.getTenantId());
        this.setSceneName(entity.getSceneName());
        this.setLastPtTime(DateUtil.formatDateTime(entity.getLastPtTime()));
        // 填充预估流量信息
        {
            String ptConfig = entity.getPtConfig();
            if (ptConfig == null) {return;}
            JSONObject jsonObject = JSON.parseObject(ptConfig);
            BigDecimal flow = jsonObject.getBigDecimal(SceneManageConstant.ESTIMATE_FLOW);
            this.setEstimateFlow(flow != null ? flow.setScale(2, RoundingMode.HALF_UP) : null);
        }
    }

    @ApiModelProperty(name = "id", value = "ID")
    private Long id;

    @ApiModelProperty(value = "场景名称")
    private String sceneName;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "压测场景类型:0普通场景，1流量调试")
    private Integer type;

    @ApiModelProperty(value = "最新压测时间")
    private String lastPtTime;

    @ApiModelProperty(value = "是否有报告")
    private Boolean hasReport;

    @ApiModelProperty(value = "预计消耗流量")
    private BigDecimal estimateFlow;

    @ApiModelProperty(value = "最大并发")
    private Integer threadNum;

    @ApiModelProperty(value = "拓展字段")
    private String features;

    @ApiModelProperty(value = "脚本解析结果")
    private String scriptAnalysisResult;
}
