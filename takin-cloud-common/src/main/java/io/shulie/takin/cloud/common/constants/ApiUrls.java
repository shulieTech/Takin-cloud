package io.shulie.takin.cloud.common.constants;

/**
 * 说明: 接口定义类
 *
 * @author shulie
 * @version v1.0
 * @date 2018年4月13日
 */
public class ApiUrls {

    /***
     * root level URI **
     */
    public static final String TAKIN_API_URL = "/api/";

    /**
     * 开放平台统一入口
     */
    public static final String TAKIN_OPEN_API_URL = "/open-api/";

    /**
     * 配置中心模块
     */
    public static final String API_TAKIN_CONFCENTER_URI = "confcenter/";
    /**
     * 01 应用管理子模块
     */
    // API.01.01.001 添加应用接口
    public static final String API_TRO_CONFCENTER_ADD_APPLICATION_URI = API_TAKIN_CONFCENTER_URI
        + "applicationmnt/add/application";
    // API.01.01.002 查询应用信息列表接口
    public static final String API_TRO_CONFCENTER_QUERY_APPLICATIONINFO_URI = API_TAKIN_CONFCENTER_URI
        + "applicationmnt/query/applicationlist";
    // API.01.01.003 根据应用id查询应用信息详情接口
    public static final String API_TRO_CONFCENTER_MODIFY_APPLICATIONINFO_URI = API_TAKIN_CONFCENTER_URI
        + "applicationmnt/query/applicationinfo";
    // API.01.01.004 删除应用信息接口
    public static final String API_TRO_CONFCENTER_DELETE_APPLICATIONINFO_URI = API_TAKIN_CONFCENTER_URI
        + "applicationmnt/delete/applicationinfo";
    // API.01.01.005 查询应用下拉框数据接口
    public static final String API_TRO_CONFCENTER_QUERY_APPLICATIONDATA_URI = API_TAKIN_CONFCENTER_URI
        + "applicationmnt/dic/query/applicationdata";
    // API.01.01.006 根据应用id更新应用信息
    public static final String API_TRO_CONFCENTER_UPDATE_APPLICATIONINFO_URI = API_TAKIN_CONFCENTER_URI
        + "applicationmnt/update/applicationinfo";
    // API.01.01.007  查询应用信息列表(同步表查数据)
    public static final String API_TRO_CONFCENTER_QUERY_APPNAMELIST_URI = API_TAKIN_CONFCENTER_URI
        + "applicationmnt/query/appnamebyprada";
    // API.01.01.008  查询应用信息列表(同步表查数据)
    public static final String API_TRO_UPDATE_APP_AGENT_VERSION_URI = API_TAKIN_CONFCENTER_URI
        + "applicationmnt/update/applicationAgent";

    /**
     * 02 黑白名单管理子模块
     */
    // API.01.02.001 添加白名单接口
    public static final String API_TRO_CONFCENTER_ADD_WLIST_URI = API_TAKIN_CONFCENTER_URI + "wbmnt/add/wlist";
    //白名单上传接口
    public static final String API_TRO_CONFCENTER_UPLOAD_WLIST_URI = API_TAKIN_CONFCENTER_URI + "wbmnt/upload/wlist";
    //白名单导出接口
    public static final String API_TRO_CONFCENTER_EXCEL_DOWNLOAD_URI = API_TAKIN_CONFCENTER_URI + "wbmnt/download/wlist";
    // API.01.02.002 查询白名单列表
    public static final String API_TRO_CONFCENTER_QUERY_WLIST_URI = API_TAKIN_CONFCENTER_URI + "wbmnt/query/wlist";
    public static final String API_TRO_CONFCENTER_QUERY_WLIST_4AGENT_URI = API_TAKIN_CONFCENTER_URI
        + "wbmnt/query/wlist/agent";
    // API.01.02.003 根据id查询白名单详情接口
    public static final String API_TRO_CONFCENTER_QUERY_WLISTBYID_URI = API_TAKIN_CONFCENTER_URI
        + "wbmnt/query/wlistinfo";
    // API.01.02.004 根据id更新白名单接口
    public static final String API_TRO_CONFCENTER_UPDATE_WLIST_URI = API_TAKIN_CONFCENTER_URI + "wbmnt/update/wlist";
    // API.01.02.005 删除白名单接口
    public static final String API_TRO_CONFCENTER_DELETE_WLIST_URI = API_TAKIN_CONFCENTER_URI + "wbmnt/delete/wlist";
    // API.01.02.006 查询白名单字典列表接口
    public static final String API_TRO_CONFCENTER_DIC_QUERY_WLIST_URI = API_TAKIN_CONFCENTER_URI
        + "wbmnt/dic/query/wlist";
    // API.01.02.007 根据appname查询该应用下的白名单（查询pradar接口）
    public static final String API_TRO_CONFCENTER_QUERY_WLISTBYAPPNAME_URI = API_TAKIN_CONFCENTER_URI
        + "wbmnt/query/wlistbyappname";
    // API.01.02.008 根据applicationId查询该应用下的白名单列表
    public static final String API_TRO_CONFCENTER_QUERY_WLISTBYAPPID_URI = API_TAKIN_CONFCENTER_URI + "wbmnt/query/list";

    // API.01.02.007 添加黑名单接口
    public static final String API_TRO_CONFCENTER_ADD_BLIST_URI = API_TAKIN_CONFCENTER_URI + "wbmnt/add/blist";
    // API.01.02.008 查询黑名单列表
    public static final String API_TRO_CONFCENTER_QUERY_BLIST_URI = API_TAKIN_CONFCENTER_URI + "wbmnt/query/blist";
    // API.01.02.009 根据id查询黑名单详情接口
    public static final String API_TRO_CONFCENTER_QUERY_BLISTBYID_URI = API_TAKIN_CONFCENTER_URI
        + "wbmnt/query/blistbyid";
    // API.01.02.010 根据id更新黑名单接口
    public static final String API_TRO_CONFCENTER_UPDATE_BLIST_URI = API_TAKIN_CONFCENTER_URI + "wbmnt/update/blist";
    // API.01.02.011 删除黑名单接口
    public static final String API_TRO_CONFCENTER_DELETE_BLIST_URI = API_TAKIN_CONFCENTER_URI + "wbmnt/delete/blist";

    // API.01.02.012  查询黑白名单列表
    public static final String API_TRO_CONFCENTER_QUERY_BWLIST_URI = API_TAKIN_CONFCENTER_URI + "wbmnt/query/bwlist";

    // API.01.02.013  查询黑白名单列表 提供metrics特殊使用
    public static final String API_TRO_CONFCENTER_QUERY_BWLISTMETRIC_URI = API_TAKIN_CONFCENTER_URI
        + "wbmnt/query/bwlistmetric";

    /**
     * 03  链路管理子模块
     */
    // API.01.03.001 添加链路信息接口
    public static final String API_TRO_CONFCENTER_ADD_LINK_URI = API_TAKIN_CONFCENTER_URI + "linkmnt/add/link";
    // API.01.03.002 查询链路信息列表接口
    public static final String API_TRO_CONFCENTER_QUERY_LINKLIST_URI = API_TAKIN_CONFCENTER_URI
        + "linkmnt/query/linklist";
    //链路管理导出
    public static final String API_TRO_CONFCENTER_LINKLIST_DOWNLOAD = API_TAKIN_CONFCENTER_URI
        + "linkmnt/download/linklist";
    //链路管理上传
    public static final String API_TRO_CONFCENTER_LINKLIST_UPLOAD = API_TAKIN_CONFCENTER_URI + "linkmnt/upload/linklist";
    // API.01.03.003 根据链路id查询链路信息详情接口
    public static final String API_TRO_CONFCENTER_QUERY_LINKINFO_URI = API_TAKIN_CONFCENTER_URI
        + "linkmnt/query/linkinfo";
    // API.01.03.004 删除链路信息接口
    public static final String API_TRO_CONFCENTER_DELETE_LINKINFO_URI = API_TAKIN_CONFCENTER_URI + "linkmnt/delete/link";
    // API.01.03.005 查询链路等级字典列表
    public static final String API_TRO_CONFCENTER_QUERY_LINKRANK_URI = API_TAKIN_CONFCENTER_URI
        + "linkmnt/query/linkrank";
    // API.01.03.006 根据id更新链路信息接口
    public static final String API_TRO_CONFCENTER_UPDATE_LINKINFO_URI = API_TAKIN_CONFCENTER_URI
        + "linkmnt/update/linkinfo";
    // API.01.03.007 删除链路服务接口
    public static final String API_TRO_CONFCENTER_DELETE_LINKINTERFACE_URI = API_TAKIN_CONFCENTER_URI
        + "linkmnt/delete/linkinterface";
    // API.01.03.008 查询链路类型字典列表
    public static final String API_TRO_CONFCENTER_QUERY_LINKTYPE_URI = API_TAKIN_CONFCENTER_URI
        + "linkmnt/query/linktype";
    // API.01.03.009 通过链路类型查链路
    public static final String API_TRO_CONFCENTER_QUERY_LINKBYLINKTYPE_URI = API_TAKIN_CONFCENTER_URI
        + "linkmnt/query/linkByLinkType";
    // API.01.03.010 链路id和链路名称 下拉框使用
    public static final String API_TRO_CONFCENTER_QUERY_LINKIDNAME_URI = API_TAKIN_CONFCENTER_URI
        + "linkmnt/query/linkIdName";
    // API.01.03.011 查询链路模块字典列表
    public static final String API_TRO_CONFCENTER_QUERY_LINKMODULE_URI = API_TAKIN_CONFCENTER_URI
        + "linkmnt/query/linkmodule";
    // API.01.03.012 查询白名单列表
    public static final String API_TRO_CONFCENTER_QUERY_WHITELIST_URI = API_TAKIN_CONFCENTER_URI
        + "linkmnt/query/whiteListForLink";
    // API.01.03.013 查询计算单量方式字典列表
    public static final String API_TRO_CONFCENTER_QUERY_CALC_VOLUME_URI = API_TAKIN_CONFCENTER_URI
        + "linkmnt/query/calcVolumeList";
    // API.01.03.014 查询查询链路头信息, 包含：链路所属模块、数量、计算单量列表
    public static final String API_TRO_CONFCENTER_QUERY_LINK_HEADER_URI = API_TAKIN_CONFCENTER_URI
        + "linkmnt/query/linkHead";
    // API.01.03.015 查询链路模块下有哪些二级链路
    public static final String API_TRO_CONFCENTER_QUERY_SECONDLINK_BY_MODULE_URI = API_TAKIN_CONFCENTER_URI
        + "linkmnt/query/secondLinkByModule";
    /**
     * 04  二级链路管理子模块
     */
    // API.01.04.001 添加二级链路信息接口
    public static final String API_TRO_CONFCENTER_ADD_SECOND_LINK_URI = API_TAKIN_CONFCENTER_URI
        + "secondlinkmnt/add/link";
    // API.01.04.002 查询二级链路信息列表接口
    public static final String API_TRO_CONFCENTER_QUERY_SECOND_LINKLIST_URI = API_TAKIN_CONFCENTER_URI
        + "secondlinkmnt/query/linklist";
    // API.01.04.003 根据链路id查询链路信息详情接口
    public static final String API_TRO_CONFCENTER_QUERY_SECOND_LINKINFO_URI = API_TAKIN_CONFCENTER_URI
        + "secondlinkmnt/query/linkinfo";
    // API.01.04.004 删除链路信息接口
    public static final String API_TRO_CONFCENTER_DELETE_SECOND_LINKINFO_URI = API_TAKIN_CONFCENTER_URI
        + "secondlinkmnt/delete/link";
    // API.01.04.005 根据id更新链路信息接口
    public static final String API_TRO_CONFCENTER_UPDATE_SECOND_LINKINFO_URI = API_TAKIN_CONFCENTER_URI
        + "secondlinkmnt/update/linkinfo";

    /**
     * 05  一级链路管理子模块
     */
    // API.01.05.001 添加二级链路信息接口
    public static final String API_TRO_CONFCENTER_ADD_FIRST_LINK_URI = API_TAKIN_CONFCENTER_URI + "firstlinkmnt/add/link";
    // API.01.05.002 查询二级链路信息列表接口
    public static final String API_TRO_CONFCENTER_QUERY_FIRST_LINKLIST_URI = API_TAKIN_CONFCENTER_URI
        + "firstlinkmnt/query/linklist";
    // API.01.05.003 根据链路id查询链路信息详情接口
    public static final String API_TRO_CONFCENTER_QUERY_FIRST_LINKINFO_URI = API_TAKIN_CONFCENTER_URI
        + "firstlinkmnt/query/linkinfo";
    // API.01.05.004 删除链路信息接口
    public static final String API_TRO_CONFCENTER_DELETE_FIRST_LINKINFO_URI = API_TAKIN_CONFCENTER_URI
        + "firstlinkmnt/delete/link";
    // API.01.05.005 根据id更新链路信息接口
    public static final String API_TRO_CONFCENTER_UPDATE_FIRST_LINKINFO_URI = API_TAKIN_CONFCENTER_URI
        + "firstlinkmnt/update/linkinfo";
    // API.01.05.006 根据id查询链路拓扑
    public static final String API_TRO_CONFCENTER_QUERY_LINK_TOPOLOGY_URI = API_TAKIN_CONFCENTER_URI
        + "firstlinkmnt/query/linkTopology";
    // API.01.05.007 根据二级链路id查询是否存在一级链路
    public static final String API_TRO_CONFCENTER_EXIST_FIRST_LINK_URI = API_TAKIN_CONFCENTER_URI
        + "firstlinkmnt/query/existFirstLink";

    /**
     * 06 数据字典子模块
     */
    // API.01.06.001 保存数据字典
    public static final String API_TRO_CONFCENTER_SAVE_DICTIONARY_URL = API_TAKIN_CONFCENTER_URI + "dictionary/save";
    // API.01.06.002 修改数据字典
    public static final String API_TRO_CONFCENTER_UPDATE_DICTIONARY_URL = API_TAKIN_CONFCENTER_URI + "dictionary/update";
    // API.01.06.003 删除数据字典
    public static final String API_TRO_CONFCENTER_DELETE_DICTIONARY_URL = API_TAKIN_CONFCENTER_URI + "dictionary/delete";
    // API.01.06.004 查询数据字典详情
    public static final String API_TRO_CONFCENTER_QUERY_DICTIONARY_DETAIL_URL = API_TAKIN_CONFCENTER_URI
        + "dictionary/query/detail";
    // API.01.06.005 查询数据字典列表
    public static final String API_TRO_CONFCENTER_QUERY_DICTIONARY_LIST_URL = API_TAKIN_CONFCENTER_URI
        + "dictionary/query/list";

    // API.01.06.006 查询数据字典key_value值
    public static final String API_TRO_CONFCENTER_KV_DICTIONARY_URL = API_TAKIN_CONFCENTER_URI
        + "dictionary/queryDictValue";

    /**
     * 07 dubbo接口与job接口数据接口
     */
    // API.01.07.000 看是否需要上传 dubbo与job数据
    public static final String API_TRO_CONFCENTER_INTERFACE_NEED_UPLOAD_URL = API_TAKIN_CONFCENTER_URI
        + "interface/query/needUpload";
    // API.01.07.001 上传 dubbo与job数据
    public static final String API_TRO_CONFCENTER_INTERFACE_UPLOAD_URL = API_TAKIN_CONFCENTER_URI
        + "interface/add/interfaceData";

    /**
     * 08 影子表配置 接口
     */
    // API.01.08.000 java agent 获取 影子表配置
    public static final String API_TRO_CONFCENTER_SHADOWCONFIG_AGENT_GET_SHADOWCONFIG_URL = API_TAKIN_CONFCENTER_URI
        + "shadowTableConfig/queryAppShadowTableConfig";
    // API.01.08.001 从pradar获取 应用的影子表配置
    public static final String API_TRO_CONFCENTER_SHADOWCONFIG_GET_CONFIG_FROM_PRADAR_URL = API_TAKIN_CONFCENTER_URI
        + "shadowTableConfig/getTableConfigFromPradar";
    // API.01.08.002 获取影子表配置分页
    public static final String API_TRO_CONFCENTER_SHADOWCONFIG_GET_CONFIG_PAGE_URL = API_TAKIN_CONFCENTER_URI
        + "shadowTableConfig/queryConfigPage";
    // API.01.08.003 添加影子表配置分页
    public static final String API_TRO_CONFCENTER_SHADOWCONFIG_ADD_CONFIG_URL = API_TAKIN_CONFCENTER_URI
        + "shadowTableConfig/save";
    // API.01.08.004 更新影子表配置分页
    public static final String API_TRO_CONFCENTER_SHADOWCONFIG_UPDATE_CONFIG_URL = API_TAKIN_CONFCENTER_URI
        + "shadowTableConfig/update";
    // API.01.08.005 删除影子表配置分页
    public static final String API_TRO_CONFCENTER_SHADOWCONFIG_DELETE_CONFIG_URL = API_TAKIN_CONFCENTER_URI
        + "shadowTableConfig/delete";
    // API.01.08.006 通过应用id 获取 该应用对应的 数据库ip端口与库名
    public static final String API_TRO_CONFCENTER_SHADOWCONFIG_QUERYIPPORTNAME_CONFIG_URL = API_TAKIN_CONFCENTER_URI
        + "shadowTableConfig/queryIpPortNameByApplictionId";
    // API.01.08.007 通过应用id 获取 该应用对应的数据库ip端口列表
    public static final String API_TRO_CONFCENTER_SHADOWCONFIG_QUERY_IPPORT_CONFIG_URL = API_TAKIN_CONFCENTER_URI
        + "shadowTableConfig/queryIpPortList";
    // API.01.08.008 通过应用id 获取 该应用对应的数据库名称列表
    public static final String API_TRO_CONFCENTER_SHADOWCONFIG_QUERY_DBNAME_CONFIG_URL = API_TAKIN_CONFCENTER_URI
        + "shadowTableConfig/queryDbNameList";
    // API.01.08.009 使用 获取影子库的应用列表氪模糊搜索
    public static final String API_TRO_CONFCENTER_SHADOWCONFIG_QUERY_SHADOWDB_URL = API_TAKIN_CONFCENTER_URI
        + "shadowTableConfig/queryShadowDbPage";
    // API.01.08.010 添加影子库数据源
    public static final String API_TRO_CONFCENTER_SHADOW_DATASOURCE_SAVE_URL = API_TAKIN_CONFCENTER_URI
        + "shadowTableConfig/saveShadowTableDataSource";
    // API.01.08.011 修改影子库数据源
    public static final String API_TRO_CONFCENTER_SHADOW_DATASOURCE_UPDATE_URL = API_TAKIN_CONFCENTER_URI
        + "shadowTableConfig/updateShadowTableDataSource";
    // API.01.08.012 获取数据源 所对应的应用
    public static final String API_TRO_CONFCENTER_SHADOW_DATASOURCE_GET_APPLICATION_URL = API_TAKIN_CONFCENTER_URI
        + "shadowTableConfig/getDatasourceAppliction";

    /**
     * 9 TRO 全局配置
     */
    // API.01.09.001 查询全局配置
    public static final String API_TRO_CONFCENTER_GLOBAL_CONFIG_QUERY = API_TAKIN_CONFCENTER_URI
        + "troConfig/queryGlobalConfig";
    // API.01.09.002 更新全局配置
    public static final String API_TRO_CONFCENTER_GLOBAL_CONFIG_UPDATE = API_TAKIN_CONFCENTER_URI
        + "troConfig/updateGlobalConfig";

    /**
     * 10 链路拓扑图配置
     */
    // API.10.01.001 链路拓扑图Excel上传
    public static final String API_TRO_LINKTOPOLOGY_IMPORT_EXCEL_URI = API_TAKIN_CONFCENTER_URI
        + "linkTopology/importExcel";

    // API.10.01.002  通过链路分组查询 链路拓扑图
    public static final String API_TRO_LINKTOPOLOGY_QUERY_LINK_GROUP_URI = API_TAKIN_CONFCENTER_URI
        + "linkTopology/queryLinkGroup";
    // API.10.01.003 查询应用瓶颈数量
    public static final String API_TRO_LINKTOPOLOGY_QUERY_BOTTLENECK_COUNT_URI = API_TAKIN_CONFCENTER_URI
        + "linkTopology/query/linkBottleSummary";
    //API.10.01.004 查询链路节点瓶颈详情
    public static final String API_TRO_LINKTOPOLOGY_QUERY_BOTTLENECK_DETAIL_URI = API_TAKIN_CONFCENTER_URI
        + "linkTopology/query/linkBottleDetail";

    /**
     * 11 压测时间记录
     */
    //API.01.11.001 保存开始压测时间
    public static final String TRO_CONFCENTER_ADD_PRESSURETIME = API_TAKIN_CONFCENTER_URI + "pressureTime/add";
    public static final String TRO_CONFCENTER_UPDATE_PRESSURETIME = API_TAKIN_CONFCENTER_URI + "pressureTime/update";
    public static final String TRO_CONFCENTER_QUERY_LATEST_PRESSURETIME = API_TAKIN_CONFCENTER_URI + "pressureTime/query";

    /**
     * 压测控制模块
     */
    public static final String API_TRO_PRESSURECONTROL_URI = "/pressurecontrol/";

    /**
     * 压测控制子模块
     */
    public static final String API_TRO_PRESSURECONTROL_EXECUTE_URI = API_TRO_PRESSURECONTROL_URI + "execute";
    public static final String API_TRO_PRESSURECONTROL_START_URI = API_TRO_PRESSURECONTROL_URI + "start";
    public static final String API_TRO_PRESSURECONTROL_PROPORTION_URI = API_TRO_PRESSURECONTROL_URI + "proportion";
    public static final String API_TRO_PRESSURECONTROL_STOP_URI = API_TRO_PRESSURECONTROL_URI + "stop";
    public static final String API_TRO_PRESSURECONTROL_BASE_LINK_URI = API_TRO_PRESSURECONTROL_URI + "baselink";
    public static final String API_TRO_PRESSURECONTROL_BASE_LINK_CAN_STOP_LIST = API_TRO_PRESSURECONTROL_URI
        + "canstop/list";
    public static final String API_TRO_PRESSURECONTROL_BASE_LINK_CAN_START_LIST = API_TRO_PRESSURECONTROL_URI
        + "canstart/list";

    /**
     * 压测前后置准备模块
     */
    public static final String API_TRO_PRESSUREREADY_URI = "/pressureready/";

    /**
     * 01 数据构建子模块
     */
    // API.02.01.001 根据条件查询构建信息
    public static final String API_TRO_PRESSUREREADY_BUILDDATA_QUERY_BUILDINFO_URI = API_TRO_PRESSUREREADY_URI
        + "builddata/query/buildinfo";
    // API.02.01.002 新增脚本执行状态接口
    public static final String API_TRO_PRESSUREREADY_BUILDDATA_UPDATE_SCRIPTSTATUS_URI = API_TRO_PRESSUREREADY_URI
        + "builddata/update/scriptstatus";
    // API.02.01.003 查询脚本构建状态接口
    public static final String API_TRO_PRESSUREREADY_BUILDDATA_QUERY_SCRIPTBUILDSTATUS_URI = API_TRO_PRESSUREREADY_URI
        + "builddata/query/scriptbuildstatus";
    // API.02.01.004 批量清理接口
    public static final String API_TRO_PRESSUREREADY_BUILDDATA_QUERY_BATCHCLEAN_URI = API_TRO_PRESSUREREADY_URI
        + "builddata/execute/batchclean";
    // API.02.01.005 构建执行脚本接口
    public static final String API_TRO_PRESSUREREADY_BUILDDATA_EXECUTE_SCRIPT_URI = API_TRO_PRESSUREREADY_URI
        + "builddata/execute/script";
    // API.02.01.006 数据构建调试开关接口
    public static final String API_TRO_PRESSUREREADY_BUILDDATA_DEBUG_SWITCH_URI = API_TRO_PRESSUREREADY_URI
        + "builddata/debug/switch";

    /**
     * 02 压测检测子模块
     */
    // API.02.02.001 查询压测检测接口(包含异常检测)
    public static final String API_TRO_PRESSUREREADY_PMCHECK_QUERY_CHECKLIST_URI = API_TRO_PRESSUREREADY_URI
        + "pmcheck/query/checklist";
    // API.02.02.002 影子库整体同步检测 接口(查询构建表)
    public static final String API_TRO_PRESSUREREADY_PMCHECK_CHECK_BASICDATA_URI = API_TRO_PRESSUREREADY_URI
        + "pmcheck/check/shadowlib";
    // API.02.02.003 白名单检测实时接口(包含dubbo和http)
    public static final String API_TRO_PRESSUREREADY_PMCHECK_CHECK_WLIST_URI = API_TRO_PRESSUREREADY_URI
        + "pmcheck/check/wlist";
    // API.02.02.004 缓存预热检测
    public static final String API_TRO_PRESSUREREADY_CACHE_CHECK_CACHE_URI = API_TRO_PRESSUREREADY_URI
        + "pmcheck/check/cache";
    // API.02.02.005 批量检测接口
    public static final String API_TRO_PRESSUREREADY_PMCHECK_CHECK_BATCHCHECK_URI = API_TRO_PRESSUREREADY_URI
        + "pmcheck/check/batchcheck";
    // API.02.02.006 压测检测调试开关接口
    public static final String API_TRO_PRESSUREREADY_PMCHECK_DEBUG_SWITCH_URI = API_TRO_PRESSUREREADY_URI
        + "pmcheck/debug/switch";

    /**
     * 03 压测总览图子模块
     */
    // API.02.03.001 查询二级链路拓扑图
    public static final String API_TRO_PRESSURECONTROL_QUERY_SECONDLINKTOPOLOGY_URI = API_TRO_PRESSURECONTROL_URI
        + "query/secondlinktopology";

    /**
     * 压测监控模块
     */
    public static final String API_TRO_MONITOR_URI = "/monitor/";

    /**
     * 01 压测监控子模块
     */
    // API.03.02.001 查询压测检测告警列表查询接口
    public static final String API_TRO_MONITOR_QUERY_ALARMLIST_URI = API_TRO_MONITOR_URI + "alarm/list";
    // API.03.02.001 添加告警
    public static final String API_TRO_MONITOR_ADD_ALARM_URI = API_TRO_MONITOR_URI + "alarm/add";
    // API.03.02.002 查询压测检测报告列表查询接口
    public static final String API_TRO_MONITOR_QUERY_REPORTLIST_URI = API_TRO_MONITOR_URI + "report/list";
    // API.03.02.003 压测场景控制接口
    public static final String API_TRO_MONITOR_SCENARIO_CONTROL_URI = API_TRO_MONITOR_URI + "scenario/control";
    // API.03.02.004 查询压测检测报告详情查询接口
    public static final String API_TRO_MONITOR_QUERY_REPORT_DETAIL_URI = API_TRO_MONITOR_URI + "report/detail";
    // API.03.02.004 业务监控实时拓扑图
    public static final String API_TRO_MONITOR_REALTIME_TOPOLOCIGAL_URI = API_TRO_MONITOR_URI + "business/topological";
    // API.03.02.004 压测折线时序图
    public static final String API_TRO_MONITOR_TIME_SERIES_URI = API_TRO_MONITOR_URI + "business/timeSeries";
    // API.03.02.004 获取业务监控列表
    public static final String API_TRO_MONITOR_LIST_URI = API_TRO_MONITOR_URI + "business/list";

    /**
     * 权限模块
     */
    public static final String API_TRO_AUTHORITY_URI = "/authority/";
    // API.04.01.001 按钮权限控制
    public static final String API_TRO_BUTTON_AUTHORITY_URI = API_TRO_AUTHORITY_URI + "check";

    /**
     * 压测辅助模块
     */
    public static final String API_TRO_PRESSUREMEASUREMENT_ASSIST_URI = "/assist/";

    /**
     * 01 压测辅助虚拟消费子模块
     */
    // API.05.01.001 启动消费脚本
    public static final String API_TRO_ASSIST_START_CONSUMER_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "startconsumer";
    // API.05.01.002 停止消费脚本
    public static final String API_TRO_ASSIST_STOP_CONSUMER_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "stopconsumer";
    // API.05.01.003 保存MQ消息
    public static final String API_TRO_MQMSG_ADD_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI + "mqmsg/add";
    // API.05.01.004 修改MQ消息
    public static final String API_TRO_MQMSG_UPDATE_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI + "mqmsg/update";
    // API.05.01.005 删除MQ消息
    public static final String API_TRO_MQMSG_DELETE_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI + "mqmsg/delete";
    // API.05.01.006 查询MQ消息列表
    public static final String API_TRO_MQMSG_QUERY_LIST_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "mqmsg/query/list";
    // API.05.01.007 查询MQ消息详情
    public static final String API_TRO_MQMSG_QUERY_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI + "mqmsg/query";
    // API.05.01.008 查询MQ消息类型
    public static final String API_TRO_MQMSG_QUERY_TYPE_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "mqmsg/query/type";

    /**
     * 02 抽数子模块
     */
    // API.05.02.001 启动/停止抽数接口
    public static final String API_TRO_ASSIST_START_LOADDATA_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI + "loaddata";
    // API.05.02.002 新增数据库配置接口
    public static final String API_TRO_DATABASECONF_SAVE_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI + "dbconf/add";
    // API.05.02.003 修改数据库配置接口
    public static final String API_TRO_DATABASECONF_UPDATE_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "dbconf/update";
    // API.05.02.004 逻辑删除数据库信息
    public static final String API_TRO_DATABASECONF_DELETE_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "dbconf/delete";
    // API.05.02.005 查询数据库配置信息列表
    public static final String API_TRO_DATABASECONF_QUERY_LIST_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "dbconf/query/list";
    // API.05.02.006 根据id查询数据库配置信息详情
    public static final String API_TRO_DATABASECONF_QUERY_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "dbconf/query/detail";
    // API.05.02.007 查询数据库类型
    public static final String API_TRO_DATABASECONF_SINGLE_START_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "dbconf/query/type";
    // API.05.02.008 抽数sql上传接口
    public static final String API_TRO_SQL_UPLOAD_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI + "dbconf/sql/upload";
    // API.05.02.009 抽数sql批量上传接口
    public static final String API_TRO_SQL_BATCH_UPLOAD_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "dbconf/sql/batch/upload";
    // API.05.02.010 抽数sql下载接口
    public static final String API_TRO_SQL_DOWNLOAD_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "dbconf/sql/download";
    // API.05.02.007 查询数据库类型
    public static final String API_TRO_DATABASECONF_QUERY_BASIC_LINK_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "dbconf/query/basicLink";

    // API.05.02.011 批量启动/禁用抽数接口
    public static final String API_TRO_ASSIST_BATCH＿ENABLE_OR_DISABLE_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "dbconf/enableOrDisable";
    // API.05.02.012 查询数据表状态接口
    public static final String API_TRO_DATABASECONF_QUERY_LOADSTATUS_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "dbconf/query/loadStatus";

    // API.05.02.013 效验 TRO自己 oracle 与 mysql 数据连接
    public static final String API_TRO_DATABASECONF_CHECK_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "dbconf/checkTroDBConn";
    // API.05.02.013 效验 TRO 配置的抽数 数据连接
    public static final String API_TRO_DATABASECONF_OTHER_CHECK_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "dbconf/checkOtherDBConn";
    // API.05.02.014 保存 数据回传配置 添加接口
    public static final String API_TRO_RETURNDATA_SAVE_CONFIG_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "returnData/save";
    // API.05.02.015 更新 数据回传配置 更改接口
    public static final String API_TRO_RETURNDATA_UPDATE_CONFIG_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "returnData/update";
    // API.05.02.016 删除 数据回传配置 删除接口
    public static final String API_TRO_RETURNDATA_DELETE_CONFIG_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "returnData/delete";
    // API.05.02.017  数据回传配置 查询接口
    public static final String API_TRO_RETURNDATA_QUERY_PAGE_CONFIG_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "returnData/queryPage";
    // API.05.02.018  数据回传配置 查询详情接口
    public static final String API_TRO_RETURNDATA_QUERY_DETAIL_CONFIG_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "returnData/queryDetail";
    // API.05.02.019  下载jar包
    public static final String API_TRO_RETURNDATA_DOWNLOAD_JAR_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "returnData/downloadJar";
    // API.05.02.020  启动停止jar包
    public static final String API_TRO_RETURNDATA_OPERATE_SPT_JAR_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "returnData/operateSptJar";

    /**
     * 03 MQ虚拟生产消息
     */
    // API.05.03.001 ESB/IBM 虚拟发送消息
    public static final String API_TRO_MQPRODUCER_EBM_SENDMSG_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "produce/ebm/mqmsg";
    // API.05.03.002 ROCKETMQ 虚拟发送消息
    public static final String API_TRO_MQPRODUCER_ROCKETMQ_SENDMSG_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "produce/rocketmq/mqmsg";
    // API.05.03.003 ESB/IBM/ROCKETMQ停止虚拟发送消息
    public static final String API_TRO_MQPRODUCER_STOP_SENDMSG_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "produce/mqmsg/stop";

    // API.05.03.004 新增ESB/IBM虚拟发送消息
    public static final String API_TRO_MQPRODUCER_EBM_ADD_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "produce/mqmsg/ebm/add";
    // API.05.03.005 删除ESB/IBM虚拟发送消息
    public static final String API_TRO_MQPRODUCER_EBM_DEL_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "produce/mqmsg/ebm/del";
    // API.05.03.006 修改ESB/IBM虚拟发送消息
    public static final String API_TRO_MQPRODUCER_EBM_UPDATE_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "produce/mqmsg/ebm/update";
    // API.05.03.007 查询ESB/IBM虚拟发送消息列表
    public static final String API_TRO_MQPRODUCER_EBM_QUERYLIST_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "produce/mqmsg/ebm/querylist";
    // API.05.03.008 根据id查询ESB/IBM虚拟发送消息详情
    public static final String API_TRO_MQPRODUCER_EBM_QUERYBYID_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "produce/mqmsg/ebm/querybyid";

    // API.05.03.009 新增ROCKETMQ虚拟发送消息
    public static final String API_TRO_MQPRODUCER_ROCKETMQ_ADD_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "produce/mqmsg/rocketmq/add";
    // API.05.03.010 删除ROCKETMQ虚拟发送消息
    public static final String API_TRO_MQPRODUCER_ROCKETMQ_DEL_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "produce/mqmsg/rocketmq/del";
    // API.05.03.011 修改ROCKETMQ虚拟发送消息
    public static final String API_TRO_MQPRODUCER_ROCKETMQ_UPDATE_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "produce/mqmsg/rocketmq/update";
    // API.05.03.012 查询ROCKETMQ虚拟发送消息列表
    public static final String API_TRO_MQPRODUCER_ROCKETMQ_QUERYLIST_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "produce/mqmsg/rocketmq/querylist";
    // API.05.03.013  根据id查询ROCKETMQ虚拟发送消息详情
    public static final String API_TRO_MQPRODUCER_ROCKETMQ_QUERYBYID_URI = API_TRO_PRESSUREMEASUREMENT_ASSIST_URI
        + "produce/mqmsg/rocketmq/querybyid";

    /**
     * 防作弊模块
     */
    public static final String API_TRO_PREVENT_CHEAT_URI = "/preventcheat/";

    /**
     * 01 应用信息配置管理
     */
    // API.06.01.001 查询应用各种开关
    public static final String API_TRO_CONFCENTER_APPLICATION_CONFIG_PAGE = API_TRO_PREVENT_CHEAT_URI
        + "applicationConfig/queryApplicationConfigPage";
    // API.06.01.002 查询某个应用的开关与全局开关
    public static final String API_TRO_CONFCENTER_APPLICATION_CONFIG_QUERY = API_TRO_PREVENT_CHEAT_URI
        + "applicationConfig/queryConfig";
    // API.06.01.003 批量更新应用
    public static final String API_TRO_CONFCENTER_APPLICATION_CONFIG_BATCH_UPDATE = API_TRO_PREVENT_CHEAT_URI
        + "applicationConfig/updateApplicationConfigBatch";

    /**
     * 02 应用信息上传
     */
    // API.06.02.001 应用信息上传 上传可能作弊 与 SQL 解析异常 以后还可以上传其他的
    public static final String API_TRO_APPLICATION_INFO_UPLOAD_UPLOAD = API_TRO_PREVENT_CHEAT_URI
        + "applicationInfo/uploadInfo";
    // API.06.02.002 上传信息查询分页
    public static final String API_TRO_APPLICATION_INFO_UPLOAD_QUERY_PAGE = API_TRO_PREVENT_CHEAT_URI
        + "applicationInfo/queryInfoPage";
    // API.06.02.003 上传信息类型字典
    public static final String API_TRO_APPLICATION_INFO_UPLOAD_INFO_TYPE = API_TRO_PREVENT_CHEAT_URI
        + "applicationInfo/queryInfoType";

    /**
     * 物理隔离模块
     */
    public static final String API_TRO_PHYSICAL_ISOLATION_URI = "/physicalisolation/";

    // API.07.01.001 预隔离接口
    public static final String API_TRO_NETWORK_ISOLATION_URI = API_TRO_PHYSICAL_ISOLATION_URI + "network/pre";
    public static final String API_TRO_NETWORK_ISOLATION_CREATE_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "network/createNetworkIsolate";
    public static final String API_TRO_NETWORK_ISOLATION_QUERY_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "network/queryNetworkIsolate";
    public static final String API_TRO_NETWORK_ISOLATION_UPDATE_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "network/updateNetworkIsolate";
    public static final String API_TRO_NETWORK_ISOLATION_DELETE_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "network/deleteNetworkIsolate";
    public static final String API_TRO_NETWORK_ISOLATION_ISOLATE_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "network/isoLateApp";
    public static final String API_TRO_NETWORK_ISOLATION_PREISOLATE_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "network/preIsoLateApp";
    public static final String API_TRO_NETWORK_ISOLATION_QUERY_CONFIG_DETAIL_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "network/queryNetworkIsolateDetail";

    public static final String API_TRO_MQ_ISOLATION_CREATE_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "mq/createMqIsolate";
    public static final String API_TRO_MQ_ISOLATION_QUERY_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "mq/queryMqIsolate";
    public static final String API_TRO_MQ_ISOLATION_UPDATE_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "mq/updateMqIsolate";
    public static final String API_TRO_MQ_ISOLATION_DELETE_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "mq/deleteMqIsolate";
    public static final String API_TRO_MQ_ISOLATION_QUERY_DETAIL_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "mq/queryMqIsolateDetail";
    public static final String API_TRO_MQ_ISOLATION_ISOLATE_MQ_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "mq/isolateMq";
    public static final String API_TRO_MQ_ISOLATION_RETUEN_ISOLATE_MQ_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "mq/returnIsolateMq";
    public static final String API_TRO_MQ_ISOLATION_IS_CLEAR_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "mq/isClearForBroker";
    public static final String API_TRO_MQ_ISOLATION_STOP_WRITE_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "mq/stopWriteToBroker";
    public static final String API_TRO_MQ_ISOLATION_RESUME_WRITE_CONFIG_URI = API_TRO_PHYSICAL_ISOLATION_URI
        + "mq/resumeWriteToBroker";

    /**
     * 链路探活模块,编号08
     */
    public static final String API_TRO_LINK_RESEARCH_LIVE_URI = "linkResearchLive";

    // API.08.01.001 增加链路探活配置
    public static final String API_TRO_LINK_RESEARCH_LIVE_ADD = API_TRO_LINK_RESEARCH_LIVE_URI + "/add";
    // API.08.01.002 更新链路探活配置
    public static final String API_TRO_LINK_RESEARCH_LIVE_UPDATE = API_TRO_LINK_RESEARCH_LIVE_URI + "/update";
    // API.08.01.003 查询链路探活配置列表
    public static final String API_TRO_LINK_RESEARCH_LIVE_QUERY_LIST = API_TRO_LINK_RESEARCH_LIVE_URI + "/query/list";
    // API.08.01.004 删除/批量删除链路探活配置
    public static final String API_TRO_LINK_RESEARCH_LIVE_DELETE = API_TRO_LINK_RESEARCH_LIVE_URI + "/delete";
    // API.08.01.005 查询链路探活配置详情
    public static final String API_TRO_LINK_RESEARCH_LIVE_QUERY_DETAIL = API_TRO_LINK_RESEARCH_LIVE_URI
        + "/query/detail";
    // API.08.01.006 启动链路探活
    public static final String API_TRO_LINK_RESEARCH_LIVE_START = API_TRO_LINK_RESEARCH_LIVE_URI + "/startResearchLive";
    // API.08.01.007 停止链路探活
    public static final String API_TRO_LINK_RESEARCH_LIVE_STOP = API_TRO_LINK_RESEARCH_LIVE_URI + "/stopResearchLive";
    // API.08.02.001 查询链路探活告警列表
    public static final String API_TRO_LINK_RESEARCH_LIVE_ALARM = API_TRO_LINK_RESEARCH_LIVE_URI + "/query/alarmList";

    /**
     * 混沌模块
     */
    public static final String API_TRO_CHAOS_URI = "chaos/";

    // API.06.01.001 获取场景控制台详情信息
    public static final String API_TRO_CHAOS_SCENE_RECORD_LIST = API_TRO_CHAOS_URI + "console/queryList";
    public static final String API_TRO_CHAOS_CONSOLE_LIST_URI = API_TRO_CHAOS_URI + "console/detail";
    public static final String API_TRO_CHAOS_CONSOLE_TASKISSUE_URI = API_TRO_CHAOS_URI + "console/taskIssue";
    public static final String API_TRO_CHAOS_CONSOLE_STOPISSUE_URI = API_TRO_CHAOS_URI + "console/stopIssue";
    public static final String API_TRO_CHAOS_CONSOLE_FAILURE_REPORT = API_TRO_CHAOS_URI
        + "console/failure/report/{recordId}";
    public static final String API_TRO_CHAOS_CONSOLE_FAILURE_CONSOLE = API_TRO_CHAOS_URI
        + "console/failure/scene/{sceneId}";
    public static final String API_TRO_CHAOS_SCENE_HOST_RECORD_LIST = API_TRO_CHAOS_URI
        + "console/host/record/list/{recordId}";

    /**
     * 应用节点
     */
    // API.07.01.001 根据编号加载节点信息
    public static final String API_TRO_CHAOS_HOST_GET_BY_ID = API_TRO_CHAOS_URI + "host/get/{id}";
    // API.07.01.002 根据编号删除节点
    public static final String API_TRO_CHAOS_HOST_DELETE_BY_ID = API_TRO_CHAOS_URI + "host/delete/{id}";
    // API.07.01.003 启用/禁用节点
    public static final String API_TRO_CHAOS_HOST_CHANGE_STATUS = API_TRO_CHAOS_URI + "host/status/{id}/{status}";
    // API.07.01.004 新增节点
    public static final String API_TRO_CHAOS_HOST_ADD = API_TRO_CHAOS_URI + "host/add";
    // API.07.01.005 更新节点
    public static final String API_TRO_CHAOS_HOST_UPDATE = API_TRO_CHAOS_URI + "host/update";
    // API.07.01.006 分页查询节点
    public static final String API_TRO_CHAOS_HOST_QUERY = API_TRO_CHAOS_URI + "host/query";
    // API.07.01.007 excel导入节点
    public static final String API_TRO_CHAOS_HOST_IMPORT = API_TRO_CHAOS_URI + "host/import";
    // API.07.01.008 excel模板下载
    public static final String API_TRO_CHAOS_HOST_DOWNLOAD = API_TRO_CHAOS_URI + "host/down/template";

    /**
     * 命令模板
     */
    // API.07.02.001 根据编号加载命令信息
    public static final String API_TRO_CHAOS_COMMAND_TEMPLATE_GET_BY_ID = API_TRO_CHAOS_URI + "cmd/template/get/{id}";
    // API.07.02.002 根据编号删除命令
    public static final String API_TRO_CHAOS_COMMAND_TEMPLATE_DELETE_BY_ID = API_TRO_CHAOS_URI
        + "cmd/template/delete/{id}";
    // API.07.02.003 启用/禁用命令
    public static final String API_TRO_CHAOS_COMMAND_TEMPLATE_CHANGE_STATUS = API_TRO_CHAOS_URI
        + "cmd/template/status/{id}/{status}";
    // API.07.02004 新增命令
    public static final String API_TRO_CHAOS_COMMAND_TEMPLATE_ADD = API_TRO_CHAOS_URI + "cmd/template/add";
    // API.07.02.005 更新命令
    public static final String API_TRO_CHAOS_COMMAND_TEMPLATE_UPDATE = API_TRO_CHAOS_URI + "cmd/template/update";
    // API.07.02.006 分页查询命令
    public static final String API_TRO_CHAOS_COMMAND_TEMPLATE_QUERY = API_TRO_CHAOS_URI + "cmd/template/query";
    // API.07.02.007 查询命令
    public static final String API_TRO_CHAOS_COMMAND_TEMPLATE_SELECTS = API_TRO_CHAOS_URI + "cmd/template/selects";

    /**
     * 应用插件
     */
    // API.07.03.001 上传插件文件
    public static final String API_TRO_CHAOS_PLUGIN_UPLOAD = API_TRO_CHAOS_URI + "plugin/upload";
    // API.07.03.002 根据编号删除应用插件
    public static final String API_TRO_CHAOS_PLUGIN_DELETE_BY_ID = API_TRO_CHAOS_URI + "plugin/delete/{id}";
    // API.07.02.003 启用/禁用应用插件
    public static final String API_TRO_CHAOS_PLUGIN_CHANGE_STATUS = API_TRO_CHAOS_URI + "plugin/status/{id}/{status}";
    // API.07.02004 新增应用插件
    public static final String API_TRO_CHAOS_PLUGIN_ADD = API_TRO_CHAOS_URI + "plugin/add";
    // API.07.02.005 查询最大版本号
    public static final String API_TRO_CHAOS_PLUGIN_LAST_VER = API_TRO_CHAOS_URI + "plugin/last/version/{appId}";
    // API.07.02.006 分页查询应用插件
    public static final String API_TRO_CHAOS_PLUGIN_QUERY = API_TRO_CHAOS_URI + "plugin/query";
    // API.07.02.007 查询应用插件
    public static final String API_TRO_CHAOS_PLUGIN_SELECTS = API_TRO_CHAOS_URI + "plugin/selects";

    /**
     * 应用服务节点
     */
    // API.07.01.001 根据编号加载节点信息
    public static final String API_TRO_CHAOS_APPSERVICE_GET_BY_ID = API_TRO_CHAOS_URI + "appServer/get/{id}";
    // API.07.01.002 根据编号删除节点
    public static final String API_TRO_CHAOS_APPSERVICE_DELETE_BY_ID = API_TRO_CHAOS_URI + "appServer/delete/{id}";
    // API.07.01.003 启用/禁用节点
    public static final String API_TRO_CHAOS_APPSERVICE_CHANGE_STATUS = API_TRO_CHAOS_URI
        + "appServer/status/{id}/{state}";
    // API.07.01.004 新增节点
    public static final String API_TRO_CHAOS_APPSERVICE_ADD = API_TRO_CHAOS_URI + "appServer/add";
    // API.07.01.005 更新节点
    public static final String API_TRO_CHAOS_APPSERVICE_UPDATE = API_TRO_CHAOS_URI + "appServer/update";
    // API.07.01.006 分页查询节点
    public static final String API_TRO_CHAOS_APPSERVICE_QUERY = API_TRO_CHAOS_URI + "appServer/queryList";
    // API.07.01.006 分页查询节点
    public static final String API_TRO_CHAOS_APPSERVICE_TYPE_LIST = API_TRO_CHAOS_URI + "appServer/serverTypes";
    // API.07.01.006 分页查询节点
    public static final String API_TRO_CHAOS_APPSERVICE_UPLOAD = API_TRO_CHAOS_URI + "appServer/upload";

    public static final String API_TRO_CHAOS_APPSERVICE_LIST = API_TRO_CHAOS_URI + "appServer/appServices/{appCode}";
    public static final String API_TRO_CHAOS_APPSERVICE_DOWNLOAD = API_TRO_CHAOS_URI + "appServer/down/template";
    /**
     * 故障演练场景
     */
    public static final String API_TRO_CHAOS_SCENE_GET_BY_ID = API_TRO_CHAOS_URI + "scene/get/{id}";
    // API.07.01.002 根据编号删除节点
    public static final String API_TRO_CHAOS_SCENE_DELETE_BY_ID = API_TRO_CHAOS_URI + "scene/delete/{id}";
    // API.07.01.003 启用/禁用节点
    public static final String API_TRO_CHAOS_SCENE_CHANGE_STATUS = API_TRO_CHAOS_URI + "scene/status/{id}/{state}";
    // API.07.01.004 新增节点
    public static final String API_TRO_CHAOS_SCENE_ADD = API_TRO_CHAOS_URI + "scene/add";
    // API.07.01.005 更新节点
    public static final String API_TRO_CHAOS_SCENE_UPDATE = API_TRO_CHAOS_URI + "scene/update";
    // API.07.01.006 分页查询节点
    public static final String API_TRO_CHAOS_SCENE_QUERY = API_TRO_CHAOS_URI + "scene/queryList";
    /**
     * 故障检测规则
     */
    public static final String API_TRO_CHAOS_SCENE_RULE_GET_BY_ID = API_TRO_CHAOS_URI + "sceneRule/get/{id}";
    // API.07.01.002 根据编号删除节点
    public static final String API_TRO_CHAOS_SCENE_RULE_DELETE_BY_ID = API_TRO_CHAOS_URI + "sceneRule/delete/{id}";
    // API.07.01.004 新增节点
    public static final String API_TRO_CHAOS_SCENE_RULE_ADD = API_TRO_CHAOS_URI + "sceneRule/add";
    // API.07.01.005 更新节点
    public static final String API_TRO_CHAOS_SCENE_RULE_UPDATE = API_TRO_CHAOS_URI + "sceneRule/update";
    // API.07.01.006 分页查询节点
    public static final String API_TRO_CHAOS_SCENE_RULE_QUERY = API_TRO_CHAOS_URI + "sceneRule/queryList";

    //物理隔离

    /**
     * 隔离app模块
     */
    public static final String API_TRO_ISOLATION = "isolation";

    // 隔离app应用
    public static final String API_TRO_ISOLATION_UPDATE_ISOLATION_APP = "update/isolationapp";
    // 恢复隔离app应用
    public static final String API_TRO_ISOLATION_UPDATE_REISOLATION_APP = "update/reisolationapp";

    // 恢复隔离app应用
    public static final String API_TRO_ISOLATION_ROCKETMQ_QUERY = "query/rockemtMqIsoQuery";

    /* 注册中心增删改查 */
    public static final String API_TRO_ISOLATION_SAVE_REG_CONFIG = "saveRegConfig";
    public static final String API_TRO_ISOLATION_DELETE_REG_CONFIGS = "deleteRegConfigs";
    public static final String API_TRO_ISOLATION_UPDATE_REG_CONFIG = "updateRegConfig";
    public static final String API_TRO_ISOLATION_QUERY_REG_CONFIG_PAGE = "queryRegConfigPage";
    public static final String API_TRO_ISOLATION_QUERY_REG_CONFIGS = "queryRegConfigs";

    /* 引用增删改查 */
    public static final String API_TRO_ISOLATION_QUERY_APP_CONFIG_DICT = "queryAppConfigDict";
    public static final String API_TRO_ISOLATION_SAVE_APP_CONFIG = "saveAppConfig";
    public static final String API_TRO_ISOLATION_DELETE_APP_CONFIGS = "deleteAppConfigs";
    public static final String API_TRO_ISOLATION_UPDATE_APP_CONFIG = "updateAppConfig";
    public static final String API_TRO_ISOLATION_QUERY_APP_CONFIG_PAGE = "queryAppConfigPage";
    public static final String API_TRO_ISOLATION_QUERY_APP_CONFIGS = "queryAppConfigs";

    /* **********影子JOB********** */
    public static final String API_TRO_SIMPLIFY_SHADOW_JOB_CONFIGS = "shadow/job/";
    public static final String API_TRO_SIMPLIFY_SHADOW_QUERY_CONFIGS = API_TRO_SIMPLIFY_SHADOW_JOB_CONFIGS + "query";
    public static final String API_TRO_SIMPLIFY_SHADOW_INSERT_CONFIGS = API_TRO_SIMPLIFY_SHADOW_JOB_CONFIGS + "insert";
    public static final String API_TRO_SIMPLIFY_SHADOW_UPDATE_CONFIGS = API_TRO_SIMPLIFY_SHADOW_JOB_CONFIGS + "update";
    public static final String API_TRO_SIMPLIFY_SHADOW_UPDATE_STATUS_CONFIGS = API_TRO_SIMPLIFY_SHADOW_JOB_CONFIGS
        + "updateStatus";
    public static final String API_TRO_SIMPLIFY_SHADOW_DELETE_CONFIGS = API_TRO_SIMPLIFY_SHADOW_JOB_CONFIGS + "delete";
    public static final String API_TRO_SIMPLIFY_SHADOW_QUERY_DETAIL_CONFIGS = API_TRO_SIMPLIFY_SHADOW_JOB_CONFIGS
        + "query/detail";
    public static final String API_TRO_SIMPLIFY_SHADOW_QUERY_APPNAME_CONFIGS = API_TRO_SIMPLIFY_SHADOW_JOB_CONFIGS
        + "queryByAppName";
    /* **********影子JOB********** */
}



