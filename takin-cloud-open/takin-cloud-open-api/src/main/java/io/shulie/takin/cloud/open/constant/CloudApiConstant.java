package io.shulie.takin.cloud.open.constant;

/**
 * @author zhaoyong
 * cloud api接口路径
 */
public class CloudApiConstant {

    public static final String LICENSE_REQUIRED = "licenseRequired";

    public static final String LICENSE_KEY = "licenseKey";

    public static final String FILTER_SQL = "filterSql";

    public static final String USER_ID = "userId";

    public static final String FILE_DELETE_URL = "/api/file/deleteFile";
    public static final String FILE_COPY_URL = "/api/file/copyFile";
    public static final String FILE_ZIP_URL = "/api/file/zipFile";
    public static final String FILE_CREATE_BY_STRING = "/api/file/createFileByPathAndString";
    public static final String FILE_CONTENT_BY_PATHS = "/open-api/file/getFileContentByPaths";

    public static final String SCENE_MANAGE_UPDATE_FILE_URL = "/open-api/scenemanage/updateFile";
    public static final String SCENE_MANAGE_URL = "/open-api/scenemanage";
    public static final String SCENE_MANAGE_LIST_URL = "/open-api/scenemanage/list";
    public static final String SCENE_MANAGE_All_LIST_URL = "/open-api/scenemanage/listSceneManage";
    public static final String SCENE_MANAGE_DETAIL_URL = "/api/scenemanage/detail";
    public static final String SCENE_MANAGE_IPNUM_URL = "/open-api/scenemanage/ipnum";

    public static final String SCENE_MANAGE_FLOWCALC_URL = "/open-api/scenemanage/flow/calc";
    public static final String SCENE_MANAGE_CHECK_AND_UPDATE_URL = "/open-api/scenemanage/checkAndUpdate/script";

    // 混合场景压测
    public static final String MULTIPLE_SCENE_CREATE = "/api/v2/scene/create";
    public static final String MULTIPLE_SCENE_UPDATE = "/api/v2/scene/update";
    public static final String MULTIPLE_SCENE_DETAIL = "/api/v2/scene/detail";
    public static final String MULTIPLE_SCENE_SYNCHRONIZE = "/api/v2/scene/synchronize";
    //task
    public static final String SCENE_TASK_UPDATE_TPS = "/open-api/scene/task/updateSceneTaskTps";

    public static final String SCENE_TASK_QUERY_ADJUST_TPS = "/open-api/scene/task/queryAdjustTaskTps";

    public static final String SCENE_MANAGE_BY_SCENE_IDS = "/open-api/scenemanage/query/ids" ;

    public static final String SCENE_MANAGE_BY_STATUS = "/open-api/scenemanage/getByStatus";


    public static final String SCENE_START_TRIAL_RUN_URL = "/open-api/scene/task/startTrialRun";
    public static final String SCENE_SCRIPT_FILE_ANALYZE_URL = "/open-api/scene/scriptAnalyze";

    /**
     * 压测任务
     */
    public static final String SCENE_TASK_START= "/open-api/scene/task/start";
    public static final String SCENE_TASK_STOP= "/open-api/scene/task/stop";
    public static final String SCENE_TASK_CHECK= "/open-api/scene/task/checkStartStatus";
    public static final String START_FLOW_DEBUG_TASK = "/open-api/scene/task/startFlowDebugTask";
    public static final String START_INSPECT_TASK = "/open-api/scene/task/startInspectTask";
    public static final String STOP_INSPECT_TASK = "/open-api/scene/task/stopInspectTask";
    public static final String START_TRY_RUN_TASK = "/open-api/scene/task/startTryRunTask";
    public static final String CHECK_TRY_RUN_TASK_STATUS = "/open-api/scene/task/checkTaskStatus";
    public static final String CHECK_SCENE_JOB_STATUS = "/open-api/scene/task/checkJobStatus";
    public static final String SCENE_START_PRE_CHECK = "/open-api/scene/task/preCheck";
    public static final String SCENE_TASK_WRITE_BALANCE = "/open-api/scene/task/writeBalance";
    public static final String REPORT_WARN_URL = "/open-api/report/warn";

    /**
     * 任务报告更新状态，目前漏数用
     */
    public static final String REPORT_UPDATE_STATE_URL = "/open-api/report/updateReportConclusion";
    public static final String REPORT_DETAIL_GET_URL = "/open-api/report/getReportByReportId";
    public static final String REPORT_TEMP_DETAIL_GET_URL = "/open-api/report/tempReportDetail";
    public static final String SCRIPT_NODE_TREE = "/open-api/report/nodeTree";
    public static final String REPORT_TEMP_TREND = "/open-api/report/tempReportTrend";
    public static final String REPORT_TREND = "/open-api/report/reportTrend";
    public static final String REPORT_SUMMARY_LIST = "/open-api/report/summary/list";
    public static final String INSPECT_REPORT_DETAIL_GET_URL = "/open-api/report/inspect";
    public static final String REPORT_ACTIVITIES = "/open-api/report/activities";

    /**
     * 统计相关接口
     */
    public static final String STATISTIC_PRESSUREPIE_URL = "/open-api/statistic/getPressurePieTotal";
    public static final String STATISTIC_REPORT_URL = "/open-api/statistic/getReportTotal";
    public static final String STATISTIC_PRESSURELIST_URL = "/open-api/statistic/getPressureListTotal";
    //engine
    //获取引擎支持的插件信息
    public static final String ENGINE_FETCH_PLUGINS_URI = "/open-api/engine/fetchAvailableEnginePlugins";
    //获取引擎插件详情
    public static final String ENGINE_FETCH_PLUGIN_DETAILS_URI = "/open-api/engine/fetchEnginePluginDetails";
    //公共信息接口
    public static final String TROCLOUD_COMMON_INFOS_URI = "/open-api/common/info/getCloudConfigurationInfos";


}
