package com.pamirs.takin.entity.domain.dto.report;

import java.util.List;

import io.shulie.takin.cloud.common.pojo.AbstractEntry;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author moriarty
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(value = "压测报告节点树")
public class ScriptNodeTree extends AbstractEntry {

    @ApiModelProperty(value = "业务活动ID")
    private Long businessActivityId;

    @ApiModelProperty(value = "节点类型名称")
    private String name;

    @ApiModelProperty(value = "节点名称")
    private String testName;

    @ApiModelProperty(value = "节点MD5值")
    private String md5;

    @ApiModelProperty(value = "节点绝对路径")
    private String xpath;

    @ApiModelProperty(value = "节点路径MD5值")
    private String xpathMd5;

    @ApiModelProperty(value = "节点定义")
    private String identification;

    @ApiModelProperty(value = "子节点")
    private List<ScriptNodeTree> children;
}
