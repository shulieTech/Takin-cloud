package io.shulie.takin.ext.content.enginecall;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 莫问
 * @date 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleStartRequestExt extends ScheduleEventRequestExt implements Serializable {

    /**
     * 脚本引擎
     */
    private String engineType;

    /**
     * 施压模式
     */
//    private String pressureMode;

    /**
     * 脚本文件路径
     */
    private String scriptPath;

    /**
     * IP数量
     */
    private Integer totalIp;

    /**
     * 从上次位点继续读取文件,默认false
     */
    private Boolean fileContinueRead = false;

    /**
     * 数据文件
     */
    private List<DataFile> dataFile;

    /**
     * 压测时长
     */
    private Long continuedTime;

    /**
     * 施压类型,0:并发,1:tps,2:自定义;不填默认为0
     */
//    private Integer pressureType;

    /**
     * 最大并发
     */
    private Integer expectThroughput;

//    /**
//     * 递增时长
//     */
//    private Long rampUp;
//
//    /**
//     * 阶梯层数
//     */
//    private Integer steps;

    /**
     * 控制台地址
     */
    private String console;

    /**
     * 目标tps
     */
    private Integer tps;

    /**
     * 业务指标，目标rt
     */
    private Map<String, String> businessData;

    /**
     * 业务指标，目标tps
     */
    private Map<String, Integer> businessTpsData;

    /**
     * 压测引擎插件文件位置  一个压测场景可能有多个插件 一个插件也有可能有多个文件
     */
    private List<String> enginePluginsFilePath;

    /**
     * 循环次数
     */
    private Integer loopsNum;

    /**
     * 固定定时器配置的周期
     */
    private Long fixedTimer;

    /**
     * 是否为巡检任务
     */
    private boolean isInspect;

    /**
     * 是否为流量试跑
     */
    private boolean isTryRun;

    /**
     * 施压配置
     */
    private Map<String, PressureConfigExt> threadGroupConfig;

    /**
     * 添加引擎插件路径
     *
     * @param enginePluginsFilePath 引擎插件路径
     * @return -
     * @author lipeng
     */
    public List<String> addEnginePluginsFilePath(String enginePluginsFilePath) {
        this.enginePluginsFilePath.add(enginePluginsFilePath);
        return this.enginePluginsFilePath;
    }

    @Data
    public static class DataFile implements Serializable {

        /**
         * 文件名称
         */
        private String name;

        /**
         * 文件路径
         */
        private String path;

        /**
         * 文件类型
         */
        private Integer fileType;

        /**
         * 是否分割文件
         */
        private boolean split;

        /**
         * 是否有序
         */
        private boolean ordered;

        /**
         * refId
         */
        private Long refId;


        /**
         * 是否大文件
         */
        private boolean isBigFile;

        /**
         * 文件分片信息,key-排序，引擎会用到；value-需要读取的分区数据
         */
        Map<Integer, List<StartEndPosition>> startEndPositions;

    }

    @Data
    public static class StartEndPosition implements Serializable {

        /**
         * 分区
         */
        private String partition;

        /**
         * pod读取文件开始位置
         */
        private String start = "-1";

        /**
         * pod读取文件结束位置
         */
        private String end = "-1";
    }
}
