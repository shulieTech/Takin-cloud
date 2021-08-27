package io.shulie.takin.cloud.common.constants;

/**
 * zk 节点路径
 *
 * @author moriarty
 */
public interface ZkNodePathConstants {

    /**
     * 是否输出ptl文件
     */
    String PTL_ENABLE_PATH = "/pradar/config/ptl/fileEnable";

    /**
     * ptl是否只输出异常信息
     */
    String PTL_ERROR_ONLY_PATH = "/pradar/config/ptl/errorOnly";

    /**
     * ptl是否只输出超过固定接口调用时长的日志
     */
    String PTL_TIMEOUT_ONLY_PATH = "/pradar/config/ptl/timeoutOnly";

    /**
     * 接口调用时长阈值
     */
    String PTL_TIMEOUT_THRESHOLD_PATH = "/pradar/config/ptl/timeoutThreshold";

    /**
     * 默认接口调用超时时间
     */
    Long DEFAULT_TIMEOUT_THRESHOLD = 500L;

    /**
     * 日志是否截断
     */
    String PTL_LOG_CUTOFF_PATH = "/pradar/config/ptl/cutoff";

    /**
     *上传日志到大数据节点列表
     */
    String AMDB_LOG_UPLOAD_NODE_LIST_PATH = "/config/log/pradar/server";

    /**
     * 日志采样率
     */
    String LOG_SAMPLING_PATH = "/config/log/trace/simpling";

    /**
     * 本地挂载的场景ID
     *
     */
    String LOCAL_MOUNT_SCENE_IDS_PATH = "/config/engine/local/mount/sceneIds";
}
