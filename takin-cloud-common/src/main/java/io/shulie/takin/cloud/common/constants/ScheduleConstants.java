package io.shulie.takin.cloud.common.constants;

/**
 * @author 莫问
 * @date 2020-05-21
 */

public class ScheduleConstants {

    /**
     *
     */
    public static final String IMAGE_PULL_POLICY = "IfNotPresent";

    /**
     *
     */
    public static final String RESTART_POLICY_NEVER = "Never";

    /**
     * 引擎脚本文件名称
     */
    public static final String ENGINE_SCRIPT_FILE_NAME = "script-file";

    /**
     * 引擎脚本文件路径
     */
    public static final String ENGINE_SCRIPT_FILE_PATH = "/etc/engine/script/";

    /**
     * 引擎插件文件夹路径 add by lipeng
     */
    public static final String ENGINE_PLUGINS_FOLDER_PATH = "/etc/engine/plugins/";

    /**
     * 引擎配置文件名称
     */
    public static final String ENGINE_CONFIG_FILE_NAME = "engine-conf";

    /**
     * 引擎配置文件路径
     */
    public static final String ENGINE_CONFIG_FILE_PATH = "/etc/engine/config";


    /**
     * 引擎时区配置名称
     */
    public static final String ENGINE_TIMEZONE_CONFIG_NAME = "host-time";

    /**
     * 引擎时区配置文件路径
     */
    public static final String ENGINE_TIMEZONE_FILE_PATH = "/etc/localtime";

    /**
     * 调度状态: 失败
     */
    public static final int SCHEDULE_STATUS_0 = 0;

    /**
     * 调度状态：成功
     */
    public static final int SCHEDULE_STATUS_1 = 1;

    public static String TEMP_FAIL_SIGN = "temp-fail-";
    public static String FIRST_SIGN = "-first";
    public static String LAST_SIGN = "-last";
    public static String TEMP_TIMESTAMP_SIGN = "temp-timestamp-";
    public static String INTERRUPT_POD = "interrupt-pressure-node-";
    /**
     * 强制停止问题
     */
    public static String FORCE_STOP_POD = "force-stop-pressure-node-";
    public static String INTERRUPT_POD_NUM = "interrupt-pressure-node-num";
    public static String TEMP_LAST_SIGN = "temp-last-";

    public static String SCHEDULE_POD_NUM = "schedule-pressure-node-num";

    /**
     * 调度任务job
     */
    public static String SCENE_TASK = "scene-task-";

    /**
     * 文件分割调度名称
     */

    public static String getFileSplitScheduleName(Long sceneId, Long reportId, Long tenantId) {
        // 兼容原始redis key
        if (null == tenantId) {
            return String.format("file-split-%s-%s", sceneId, reportId);
        }
        return String.format("file-split-%s-%s-%s", sceneId, reportId, tenantId);
    }

    /**
     * 文件分割存储的队列
     */
    public static String getFileSplitQueue(Long scenId, Long reportId, Long tenantId) {
        // 兼容原始redis key
        if (null == tenantId) {
            return String.format("file-split-queue-%s-%s", scenId, reportId);
        }
        return String.format("file-split-queue-%s-%s-%s", scenId, reportId, tenantId);
    }


    /**
     * 调度名称
     *
     * @return -
     */
    public static String getScheduleName(Long sceneId, Long taskId, Long tenantId) {
        // 兼容原始redis key
        if (null == tenantId) {
            return String.format(SCENE_TASK + "%s-%s", sceneId, taskId);
        }
        return String.format(SCENE_TASK + "%s-%s-%s", sceneId, taskId, tenantId);
    }

    /**
     * ConfigMap名称
     *
     * @return -
     */
    public static String getConfigMapName(Long sceneId, Long taskId, Long tenantId) {
        // 兼容原始redis key
        if (null == tenantId) {
            return String.format("engine-config-%s-%s.json", sceneId, taskId);
        }
        return String.format("engine-config-%s-%s-%s.json", sceneId, taskId, tenantId);
    }

    /**
     * 获取url
     *
     * @return -
     */
    public static String getConsoleUrl(Long sceneId, Long taskId, Long tenantId) {
        // 兼容原始redis key
        if (null == tenantId) {
            return String.format("/api/collector/receive?sceneId=%s&reportId=%s", sceneId, taskId);
        }
        return String.format("/api/collector/receive?sceneId=%s&reportId=%s&tenantId=%s", sceneId, taskId,
            tenantId);
    }

    /**
     * 压力节点 引擎名
     *
     * @return -
     */
    public static String getEngineName(Long sceneId, Long reportId, Long tenantId) {
        // 兼容原始redis key
        if (null == tenantId) {
            return String.format("pressure-node-engine-%s-%s", sceneId, reportId);
        }
        return String.format("pressure-node-engine-%s-%s-%s", sceneId, reportId, tenantId);
    }

    /**
     * 压力节点名总数名称
     *
     * @return -
     */
    public static String getPressureNodeTotalKey(Long sceneId, Long reportId, Long tenantId) {
        // 兼容原始redis key
        if (null == tenantId) {
            return String.format("pressure-node-total-%s-%s", sceneId, reportId);
        }
        return String.format("pressure-node-total-%s-%s-%s", sceneId, reportId, tenantId);
    }

    /**
     * 压力节点 名
     *
     * @return -
     */
    public static String getPressureNodeName(Long sceneId, Long reportId, Long tenantId) {
        // 兼容原始redis key
        if (null == tenantId) {
            return String.format("pressure-node-%s-%s", sceneId, reportId);
        }
        return String.format("pressure-node-%s-%s-%s", sceneId, reportId, tenantId);
    }

}
