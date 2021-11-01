package io.shulie.takin.cloud.open.request.scene.manage;

import java.util.Map;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;

/**
 * 创建/修改 场景  -  请求
 *
 * @author 张天赐
 */
@Data
@ApiModel(value = "创建/修改 场景")
@EqualsAndHashCode(callSuper = true)
public class WriteSceneRequest extends CloudUserCommonRequestExt {
    @ApiModelProperty(value = "基础信息")
    @NotBlank(message = "场景基础信息不能为空")
    private BasicInfo basicInfo;
    @ApiModelProperty(value = "脚本解析结果")
    @NotBlank(message = "脚本解析结果不能为空")
    private List<?> analysisResult;
    @ApiModelProperty(value = "压测内容")
    @NotNull(message = "压测内容不能为空")
    private List<Content> content;
    @ApiModelProperty(value = "施压配置")
    @NotNull(message = "施压配置不能为空")
    private Config config;
    @ApiModelProperty(value = "压测目标")
    @NotNull(message = "压测目标不能为空")
    private Map<String, Goal> goal;
    @ApiModelProperty(value = "SLA配置")
    @NotNull(message = "SLA配置不能为空")
    private List<MonitoringGoal> monitoringGoal;
    @ApiModelProperty(value = "数据验证配置")
    @NotNull(message = "数据验证配置不能为空")
    private DataValidation dataValidation;
    @ApiModelProperty(value = "压测文件")
    @NotNull(message = "压测文件不能为空")
    private List<File> file;

    /**
     * 基础信息
     */
    @Data
    public static class BasicInfo {
        @ApiModelProperty(value = "场景主键")
        private Long sceneId;
        @ApiModelProperty(value = "场景名称")
        @NotBlank(message = "场景名称不能为空")
        private String name;
        @ApiModelProperty(value = "场景类型")
        @NotBlank(message = "场景类型不能为空")
        private Integer type;
        @ApiModelProperty(value = "脚本实例主键")
        @NotNull(message = "脚本实例主键不能为空")
        private Long scriptId;
        @ApiModelProperty(value = "脚本类型")
        @NotNull(message = "脚本类型不能为空")
        private Integer scriptType;
        @ApiModelProperty(value = "业务流程主键")
        @NotBlank(message = "业务流程主键不能为空")
        private Long businessFlowId;
    }

    /**
     * 压测内容
     */
    @Data
    @ApiModel(value = "压测内容")
    public static class Content {
        @ApiModelProperty(value = "名称")
        @NotBlank(message = "名称不能为空")
        private String name;
        @ApiModelProperty(value = "脚本节点路径MD5")
        @NotBlank(message = "MD5不能为空")
        private String pathMd5;
        @ApiModelProperty(value = "业务活动主键")
        @NotNull(message = "业务活动主键不能为空")
        private Long businessActivityId;
        @ApiModelProperty(value = "关联应用的主键")
        private List<String> applicationId;
    }

    /**
     * 压测目标
     */
    @Data
    @ApiModel(value = "压测目标")
    public static class Goal {
        @ApiModelProperty(value = "目标TPS")
        private Integer tps;
        @ApiModelProperty(value = "目标RT(ms)")
        private Integer rt;
        @ApiModelProperty(value = "目标成功率(%)")
        private Double sr;
        @ApiModelProperty(value = "目标SA(%)")
        private Double sa;
    }

    /**
     * 施压配置
     */
    @Data
    @ApiModel(value = "线程组施压配置")
    public static class Config {
        Map<String, ConfigItem> items;

        /**
         * 施压配置 - 具体项
         */
        @Data
        @ApiModel(value = "配置项")
        public static class ConfigItem {
            @NotNull(message = "施压模式不能为空")
            @ApiModelProperty(value = "施压模式")
            private Integer mode;
            @ApiModelProperty(value = "指定Pod数")
            @NotNull(message = "指定Pod数不能为空")
            private Integer podNumber;
            @ApiModelProperty(value = "压测时长")
            @NotNull(message = "压测时长不能为空")
            private Integer time;
            @ApiModelProperty(value = "压测时长单位")
            @NotNull(message = "压测时长单位不能为空")
            private String timeUtil;
            @ApiModelProperty(value = "施压模式类型")
            @NotNull(message = "施压模式类型不能为空")
            private Integer modeType;
            @ApiModelProperty(value = "并发数")
            private Integer concurrency;
            @ApiModelProperty(value = "递增时长")
            private String increaseTime;
            @ApiModelProperty(value = "递增时长单位")
            private Integer increaseTimeUtil;
            @ApiModelProperty(value = "递增步骤")
            private Integer increaseStep;
        }
    }

    /**
     * 监控目标
     */
    @Data
    @ApiModel(value = "监控目标")
    public static class MonitoringGoal {
        @NotNull(message = "监控类型不能为空")
        @ApiModelProperty(value = "监控类型 终止/告警")
        private Integer type;
        @ApiModelProperty(value = "名称")
        @NotBlank(message = "名称不能为空")
        private String name;
        @ApiModelProperty(value = "对象(MD5值)")
        @NotBlank(message = "对象不能为空")
        private List<String> target;
        @ApiModelProperty(value = "算式目标")
        @NotBlank(message = "条件规则指标不能为空")
        private Integer formulaTarget;
        @ApiModelProperty(value = "算式符号")
        @NotBlank(message = "条件规则判断条件不能为空")
        private Integer formulaSymbol;
        @ApiModelProperty(value = "算式数值")
        @NotBlank(message = "条件规则判断数据不能为空")
        private Double formulaNumber;
        @ApiModelProperty(value = "忽略次数")
        @NotBlank(message = "连续出现次数不能为空")
        private Integer numberOfIgnore;

    }

    /**
     * 数据验证配置
     */
    @Data
    @ApiModel(value = "数据验证配置")
    public static class DataValidation {
        @ApiModelProperty(value = "时间间隔")
        @NotNull(message = "时间间隔不能为空")
        private Integer timeInterval;
        @ApiModelProperty(value = "内容-不明")
        private String content;
    }

    /**
     * 压测文件
     */
    @Data
    public static class File {
        @ApiModelProperty(value = "文件路径")
        @NotBlank(message = "文件路径不能为空")
        private String path;
        @ApiModelProperty(value = "文件名称")
        @NotBlank(message = "文件名称不能为空")
        private String name;
        @ApiModelProperty(value = "文件类型")
        @NotNull(message = "文件类型不能为空")
        private Integer type;
        @ApiModelProperty(value = "文件拓展信息")
        @NotNull(message = "文件拓展信息不能为空")
        Map<String, Object> extend;
    }
}
