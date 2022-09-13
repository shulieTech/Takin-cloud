package io.shulie.takin.cloud.sdk.constant;

/**
 * 入口配置
 *
 * @author 张天赐
 */
@SuppressWarnings("SpellCheckingInspection")
public class EntrypointUrl {
    private EntrypointUrl() {}

    public static String join(String... path) {
        return BASIC + "/"
            + String.join("/", path);
    }

    /**
     * 基础路径
     */
    public static final String BASIC = "/api";

    /**
     * 模块 - 通用接口
     */
    public static final String MODULE_COMMON = "common";
    public static final String METHOD_COMMON_CONFIG = "getCloudConfigurationInfos";
    public static final String METHOD_COMMON_DICTIONARY = "link/dictionary";

    /**
     * 模块 - 文件管理
     */
    public static final String MODULE_FILE = "file";
    public static final String METHOD_FILE_UPLOAD = "upload";
    public static final String METHOD_FILE_DOWNLOAD_BY_PATH = "downloadFileByPath";
    public static final String METHOD_FILE_DOWNLOAD = "download";
    public static final String METHOD_FILE_CONTENT = "getFileContentByPaths";
    public static final String METHOD_FILE_DELETE = "deleteFile";
    public static final String METHOD_FILE_DELETE_TEMP = "";
    public static final String METHOD_FILE_COPY = "copyFile";
    public static final String METHOD_FILE_ZIP = "zipFile";
    public static final String METHOD_FILE_CREATE_BY_STRING = "createFileByPathAndString";

    /**
     * 模块 - 大文件管理
     */
    public static final String MODULE_FILE_BIG = MODULE_FILE + "/big";
    public static final String METHOD_BIG_FILE_UPLOAD = "upload";
    public static final String METHOD_BIG_FILE_COMPACT = "compact";
    public static final String METHOD_BIG_FILE_DOWNLOAD = "download";

    /**
     * 模块 - 引擎
     */
    private static final String MODULE_ENGINE = "engine";

    /**
     * 模块 - 引擎插件
     */
    public static final String MODULE_ENGINE_PLUGIN = MODULE_ENGINE + "";
    public static final String METHOD_ENGINE_PLUGIN_LIST = "fetchAvailableEnginePlugins";
    public static final String METHOD_ENGINE_PLUGIN_DETAILS = "fetchEnginePluginDetails";
    public static final String METHOD_ENGINE_PLUGIN_SAVE = "saveEnginePlugin";
    public static final String METHOD_ENGINE_PLUGIN_ENABLE = "enableEnginePlugin";
    public static final String METHOD_ENGINE_PLUGIN_DISABLE = "disableEnginePlugin";

    /**
     * 模块 - 引擎回调
     */
    public static final String MODULE_ENGINE_CALLBACK = MODULE_ENGINE + "/callback";
    public static final String METHOD_ENGINE_CALLBACK_TASK_RESULT_NOTIFY = "";
    /**
     * 模块 - 中间件JAR包
     */
    public static final String MODULE_MIDDLEWARE_JAR = "middlewareJar";
    public static final String METHOD_MODULE_MIDDLEWARE_JAR_IMPORT = "import";
    public static final String METHOD_MODULE_MIDDLEWARE_JAR_COMPARE = "compare";

    /**
     * 新业务流程
     */
    public static final String MODULE_PROCESS = "process";
    public static final String METHOD_PROCESS_SCRIPT_ANALYZE = "scriptAnalyze";
    /**
     * 模块 - 压测报告
     */
    public static final String MODULE_REPORT = "report";
    public static final String METHOD_REPORT_LIST = "listReport";
    public static final String METHOD_REPORT_LOCK = "lock";
    public static final String METHOD_REPORT_UNLOCK = "unlock";
    public static final String METHOD_REPORT_FINISH = "finish";
    public static final String METHOD_REPORT_WARN_COUNT = "warn/count";
    public static final String METHOD_REPORT_METRICS = "metrices";
    public static final String METHOD_REPORT_SUMMARY = "businessActivity/summary/list";
    public static final String METHOD_REPORT_ACTIVITY_REPORT_ID = "queryReportActivityByReportId";
    public static final String METHOD_REPORT_ACTIVITY_REPORT_IDS = "queryReportActivityByReportIds";
    public static final String METHOD_REPORT_ACTIVITY_SCENE_ID = "queryReportActivityBySceneId";
    public static final String METHOD_REPORT_WARN_ADD = "warn";
    public static final String METHOD_REPORT_WARN_LIST = "listWarn";
    public static final String METHOD_REPORT_UPDATE_CONCLUSION = "updateReportConclusion";
    public static final String METHOD_REPORT_DETAIL = "getReportByReportId";
    public static final String METHOD_REPORT_STATUS_BY_ID = "getReportStatusById";
    public static final String METHOD_REPORT_DETAIL_TEMP = "tempReportDetail";
    public static final String METHOD_REPORT_ONE_RUNNING = "running";
    public static final String METHOD_REPORT_LIST_RUNNING = "running/list";
    public static final String METHOD_REPORT_LIST_PRESSURING = "pressuring/list";
    public static final String METHOD_REPORT_SCRIPT_NODE_TREE = "nodeTree";
    public static final String METHOD_REPORT_GET_JTL_DOWNLOAD_URL = "getJtlDownLoadUrl";

    public static final String METHOD_REPORT_TREND = "queryReportTrend";
    public static final String METHOD_REPORT_TREND_TEMP = "queryTempReportTrend";

    /**
     * 模块 - 场景
     */
    private static final String MODULE_SCENE = "scene";
    /**
     * 模块 - 场景管理 (v2-混合压测)
     */
    public static final String MODULE_SCENE_MIX = MODULE_SCENE + "/mix";
    public static final String METHOD_SCENE_MIX_CREATE = "create";
    public static final String METHOD_SCENE_MIX_UPDATE = "update";
    public static final String METHOD_SCENE_MIX_DETAIL = "detail";
    public static final String METHOD_SCENE_MIX_SYNCHRONIZE = "synchronize";
    /**
     * 模块 - 场景管理
     */
    public static final String MODULE_SCENE_MANAGE = MODULE_SCENE + "/manage";
    public static final String METHOD_SCENE_MANAGE_UPDATE_FILE = "updateFile";
    public static final String METHOD_SCENE_MANAGE_CONTENT = "content";
    public static final String METHOD_SCENE_MANAGE_SAVE = "";
    public static final String METHOD_SCENE_MANAGE_UPDATE = "";
    public static final String METHOD_SCENE_MANAGE_DELETE = "";
    public static final String METHOD_SCENE_MANAGE_DETAIL = "detail";
    public static final String METHOD_SCENE_MANAGE_DETAIL_NO_AUTH = "detailNoAuth";
    public static final String METHOD_SCENE_MANAGE_LIST = "listSceneManage";
    public static final String METHOD_SCENE_MANAGE_SEARCH = "list";
    public static final String METHOD_SCENE_MANAGE_CALC_FLOW = "flow/calc";
    public static final String METHOD_SCENE_MANAGE_GET_IP_NUMBER = "ip_num";
    public static final String METHOD_SCENE_MANAGE_CHECK_AND_UPDATE_SCRIPT = "checkAndUpdate/script";
    public static final String METHOD_SCENE_MANAGE_QUERY_BY_IDS = "query/ids";
    public static final String METHOD_SCENE_MANAGE_RECOVERY = "recovery";
    public static final String METHOD_SCENE_MANAGE_QUERY_BY_STATUS = "query/status";

    /**
     * 模块 - 场景任务
     */
    public static final String MODULE_SCENE_TASK = MODULE_SCENE + "/task";
    public static final String METHOD_SCENE_TASK_START = "start";
    public static final String METHOD_SCENE_TASK_STOP = "stop";
    public static final String METHOD_SCENE_TASK_BOLT_STOP = "bolt/stop";
    public static final String METHOD_SCENE_TASK_CHECK_TASK = "checkStartStatus";
    public static final String METHOD_SCENE_TASK_UPDATE_TPS = "updateSceneTaskTps";
    public static final String METHOD_SCENE_TASK_ADJUST_TPS = "queryAdjustTaskTps";
    public static final String METHOD_SCENE_TASK_RESUME = "resume/scenetask";
    public static final String METHOD_SCENE_TASK_START_FLOW_DEBUG = "startFlowDebugTask";
    public static final String METHOD_SCENE_TASK_START_INSPECT = "startInspectTask";
    public static final String METHOD_SCENE_TASK_STOP_INSPECT = "stopInspectTask";
    public static final String METHOD_SCENE_TASK_FORCE_STOP_INSPECT = "forceStopTask";
    public static final String METHOD_SCENE_TASK_START_TRY_RUN = "startTryRunTask";
    public static final String METHOD_SCENE_TASK_CHECK_STATUS = "checkTaskStatus";
    public static final String METHOD_SCENE_TASK_CHECK_JOB_STATUS = "checkJobStatus";
    public static final String METHOD_SCENE_TASK_START_PRE_CHECK = "preCheck";
    public static final String METHOD_SCENE_TASK_CALL_BACK_TO_WRITE_BALANCE = "writeBalance";
    public static final String METHOD_SCENE_TASK_INIT_CALL_BACK = "initCallback";
    public static final String METHOD_SCENE_TASK_TASK_RESULT_NOTIFY = "taskResultNotify";
    public static final String METHOD_SCENE_TASK_FILE_CONTACT = "script/contactScene";

    /**
     * 模块 - 调度记录
     */
    public static final String MODULE_SCHEDULE = "schedule";
    public static final String METHOD_SCHEDULE_LIST = "list";

    /**
     * 模块 - 统计信息
     */
    public static final String MODULE_STATISTICS = "statistic";
    public static final String METHOD_STATISTICS_PRESSURE_PIE_TOTAL = "getPressurePieTotal";
    public static final String METHOD_STATISTICS_REPORT_TOTAL = "getReportTotal";
    public static final String METHOD_STATISTICS_PRESSURE_LIST_TOTAL = "getPressureListTotal";
    public static final String METHOD_STATISTICS_PRESSURE_FULL = "full";

    /**
     * 模块 - 策略
     */
    public static final String MODULE_STRATEGY = "strategy";
    public static final String METHOD_STRATEGY_LIST = "list";
    public static final String METHOD_STRATEGY_ADD = "";
    public static final String METHOD_STRATEGY_UPDATE = "";
    public static final String METHOD_STRATEGY_DETAIL = "";
    public static final String METHOD_STRATEGY_DELETE = "";

    /**
     * 模块 - 接受压测引擎数据
     */
    public static final String MODULE_COLLECTOR = "collector";
    public static final String METHOD_COLLECTOR_RECEIVE = "receive";

    /**
     * 健康检测
     */
    public static final String MODULE_HEALTH = "health";
    public static final String METHOD_HEALTH_CHECK = "";

    /**
     * 机器 - 压测机器
     */
    public static final String MODULE_MACHINE = "pressureTestMachine";
    public static final String METHOD_MACHINE_LIST = "list";
    public static final String METHOD_MACHINE_ADD = "add";
    public static final String METHOD_MACHINE_UPDATE = "update";
    public static final String METHOD_MACHINE_DELETE = "delete";
    public static final String METHOD_MACHINE_ENABLE = "enable";
    public static final String METHOD_MACHINE_DISABLE = "disable";

}
