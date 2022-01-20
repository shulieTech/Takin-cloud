package io.shulie.takin.cloud.sdk.constant;

/**
 * 入口配置
 *
 * @author 张天赐
 */
@SuppressWarnings("SpellCheckingInspection")
public class EntrypointUrl {
    public static String join(String... path) {
        return BASIC + "/"
            + String.join("/", path);
    }

    /**
     * 基础路径
     */
    public final static String BASIC = "/api";

    /**
     * 模块 - 通用接口
     */
    public final static String MODULE_COMMON = "common";
    public final static String METHOD_COMMON_CONFIG = "getCloudConfigurationInfos";
    public final static String METHOD_COMMON_DICTIONARY = "link/dictionary";

    /**
     * 模块 - 文件管理
     */
    public final static String MODULE_FILE = "file";
    public final static String METHOD_FILE_UPLOAD = "upload";
    public final static String METHOD_FILE_DOWNLOAD_BY_PATH = "downloadFileByPath";
    public final static String METHOD_FILE_DOWNLOAD = "download";
    public final static String METHOD_FILE_CONTENT = "getFileContentByPaths";
    public final static String METHOD_FILE_DELETE = "deleteFile";
    public final static String METHOD_FILE_DELETE_TEMP = "";
    public final static String METHOD_FILE_COPY = "copyFile";
    public final static String METHOD_FILE_ZIP = "zipFile";
    public final static String METHOD_FILE_CREATE_BY_STRING = "createFileByPathAndString";

    /**
     * 模块 - 大文件管理
     */
    public final static String MODULE_FILE_BIG = MODULE_FILE + "/big";
    public final static String METHOD_BIG_FILE_UPLOAD = "upload";
    public final static String METHOD_BIG_FILE_COMPACT = "compact";
    public final static String METHOD_BIG_FILE_DOWNLOAD = "download";

    /**
     * 模块 - 引擎
     */
    private final static String MODULE_ENGINE = "engine";

    /**
     * 模块 - 引擎插件
     */
    public final static String MODULE_ENGINE_PLUGIN = MODULE_ENGINE + "";
    public final static String METHOD_ENGINE_PLUGIN_LIST = "fetchAvailableEnginePlugins";
    public final static String METHOD_ENGINE_PLUGIN_DETAILS = "fetchEnginePluginDetails";
    public final static String METHOD_ENGINE_PLUGIN_SAVE = "saveEnginePlugin";
    public final static String METHOD_ENGINE_PLUGIN_ENABLE = "enableEnginePlugin";
    public final static String METHOD_ENGINE_PLUGIN_DISABLE = "disableEnginePlugin";

    /**
     * 模块 - 引擎回调
     */
    public final static String MODULE_ENGINE_CALLBACK = MODULE_ENGINE + "/callback";
    public final static String METHOD_ENGINE_CALLBACK_TASK_RESULT_NOTIFY = "";
    /**
     * 模块 - 中间件JAR包
     */
    public final static String MODULE_MIDDLEWARE_JAR = "middlewareJar";
    public final static String METHOD_MODULE_MIDDLEWARE_JAR_IMPORT = "import";
    public final static String METHOD_MODULE_MIDDLEWARE_JAR_COMPARE = "compare";

    /**
     * 新业务流程
     */
    public final static String MODULE_PROCESS = "process";
    public final static String METHOD_PROCESS_SCRIPT_ANALYZE = "scriptAnalyze";
    /**
     * 模块 - 压测报告
     */
    public final static String MODULE_REPORT = "report";
    public final static String METHOD_REPORT_LIST = "listReport";
    public final static String METHOD_REPORT_LOCK = "lock";
    public final static String METHOD_REPORT_UNLOCK = "unlock";
    public final static String METHOD_REPORT_FINISH = "finish";
    public final static String METHOD_REPORT_WARN_COUNT = "warn/count";
    public final static String METHOD_REPORT_METRICS = "metrices";
    public final static String METHOD_REPORT_SUMMARY = "businessActivity/summary/list";
    public final static String METHOD_REPORT_ACTIVITY_REPORT_ID = "queryReportActivityByReportId";
    public final static String METHOD_REPORT_ACTIVITY_SCENE_ID = "queryReportActivityBySceneId";
    public final static String METHOD_REPORT_WARN_ADD = "warn";
    public final static String METHOD_REPORT_WARN_LIST = "listWarn";
    public final static String METHOD_REPORT_UPDATE_CONCLUSION = "updateReportConclusion";
    public final static String METHOD_REPORT_DETAIL = "getReportByReportId";
    public final static String METHOD_REPORT_DETAIL_TEMP = "tempReportDetail";
    public final static String METHOD_REPORT_ONE_RUNNING = "running";
    public final static String METHOD_REPORT_LIST_RUNNING = "running/list";
    public final static String METHOD_REPORT_LIST_PRESSURING = "pressuring/list";
    public final static String METHOD_REPORT_SCRIPT_NODE_TREE = "nodeTree";
    public final static String METHOD_REPORT_GET_JTL_DOWNLOAD_URL= "getJtlDownLoadUrl";

    public final static String METHOD_REPORT_TREND = "queryReportTrend";
    public final static String METHOD_REPORT_TREND_TEMP = "queryTempReportTrend";

    /**
     * 模块 - 场景
     */
    private final static String MODULE_SCENE = "scene";
    /**
     * 模块 - 场景管理 (v2-混合压测)
     */
    public final static String MODULE_SCENE_MIX = MODULE_SCENE + "/mix";
    public final static String METHOD_SCENE_MIX_CREATE = "create";
    public final static String METHOD_SCENE_MIX_UPDATE = "update";
    public final static String METHOD_SCENE_MIX_DETAIL = "detail";
    public final static String METHOD_SCENE_MIX_SYNCHRONIZE = "synchronize";
    /**
     * 模块 - 场景管理
     */
    public final static String MODULE_SCENE_MANAGE = MODULE_SCENE + "/manage";
    public final static String METHOD_SCENE_MANAGE_UPDATE_FILE = "updateFile";
    public final static String METHOD_SCENE_MANAGE_CONTENT = "content";
    public final static String METHOD_SCENE_MANAGE_SAVE = "";
    public final static String METHOD_SCENE_MANAGE_UPDATE = "";
    public final static String METHOD_SCENE_MANAGE_DELETE = "";
    public final static String METHOD_SCENE_MANAGE_DETAIL = "detail";
    public final static String METHOD_SCENE_MANAGE_LIST = "listSceneManage";
    public final static String METHOD_SCENE_MANAGE_SEARCH = "list";
    public final static String METHOD_SCENE_MANAGE_CALC_FLOW = "flow/calc";
    public final static String METHOD_SCENE_MANAGE_GET_IP_NUMBER = "ip_num";
    public final static String METHOD_SCENE_MANAGE_CHECK_AND_UPDATE_SCRIPT = "checkAndUpdate/script";
    public final static String METHOD_SCENE_MANAGE_QUERY_BY_IDS = "query/ids";

    /**
     * 模块 - 场景任务
     */
    public final static String MODULE_SCENE_TASK = MODULE_SCENE + "/task";
    public final static String METHOD_SCENE_TASK_START = "start";
    public final static String METHOD_SCENE_TASK_STOP = "stop";
    public final static String METHOD_SCENE_TASK_BOLT_STOP = "bolt/stop";
    public final static String METHOD_SCENE_TASK_CHECK_TASK = "checkStartStatus";
    public final static String METHOD_SCENE_TASK_UPDATE_TPS = "updateSceneTaskTps";
    public final static String METHOD_SCENE_TASK_ADJUST_TPS = "queryAdjustTaskTps";
    public final static String METHOD_SCENE_TASK_RESUME = "resume/scenetask";
    public final static String METHOD_SCENE_TASK_START_FLOW_DEBUG = "startFlowDebugTask";
    public final static String METHOD_SCENE_TASK_START_INSPECT = "startInspectTask";
    public final static String METHOD_SCENE_TASK_STOP_INSPECT = "stopInspectTask";
    public final static String METHOD_SCENE_TASK_FORCE_STOP_INSPECT = "forceStopTask";
    public final static String METHOD_SCENE_TASK_START_TRY_RUN = "startTryRunTask";
    public final static String METHOD_SCENE_TASK_CHECK_STATUS = "checkTaskStatus";
    public final static String METHOD_SCENE_TASK_CHECK_JOB_STATUS = "checkJobStatus";
    public final static String METHOD_SCENE_TASK_START_PRE_CHECK = "preCheck";
    public final static String METHOD_SCENE_TASK_CALL_BACK_TO_WRITE_BALANCE = "writeBalance";
    public final static String METHOD_SCENE_TASK_INIT_CALL_BACK = "initCallback";
    public final static String METHOD_SCENE_TASK_TASK_RESULT_NOTIFY = "taskResultNotify";
    public final static String METHOD_SCENE_TASK_FILE_CONTACT = "script/contactScene";

    /**
     * 模块 - 调度记录
     */
    public final static String MODULE_SCHEDULE = "schedule";
    public final static String METHOD_SCHEDULE_LIST = "list";

    /**
     * 模块 - 统计信息
     */
    public final static String MODULE_STATISTICS = "statistic";
    public final static String METHOD_STATISTICS_PRESSURE_PIE_TOTAL = "getPressurePieTotal";
    public final static String METHOD_STATISTICS_REPORT_TOTAL = "getReportTotal";
    public final static String METHOD_STATISTICS_PRESSURE_LIST_TOTAL = "getPressureListTotal";

    /**
     * 模块 - 策略
     */
    public final static String MODULE_STRATEGY = "strategy";
    public final static String METHOD_STRATEGY_LIST = "list";
    public final static String METHOD_STRATEGY_ADD = "";
    public final static String METHOD_STRATEGY_UPDATE = "";
    public final static String METHOD_STRATEGY_DETAIL = "";
    public final static String METHOD_STRATEGY_DELETE = "";

    /**
     * 模块 - 接受压测引擎数据
     */
    public final static String MODULE_COLLECTOR = "collector";
    public final static String METHOD_COLLECTOR_RECEIVE = "receive";

    /**
     * 健康检测
     */
    public final static String MODULE_HEALTH = "health";
    public final static String METHOD_HEALTH_CHECK = "";

    /**
     * 压测
     */
    public final static String MODULE_PRESSURE = "pressure";
    public final static String METHOD_PRESSURE_START = "start";
    public final static String METHOD_PRESSURE_HEALTH = "health";
}
