package io.shulie.takin.cloud.constant;

/**
 * 调试信息
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class Message {

    private Message() {}

    public static final String TASK_ID = "taskId";
    public static final String RESOURCE_ID = "resourceId";
    public static final String TPS_NUMBER = "tps";
    public static final String THREAD_NUMBER = "number";
    public static final String MESSAGE_NAME = "message";
    public static final String WATCHMAN_MISS = "调度机未上报";
    public static final String MISS_JOB = "未找到任务:{}";
    public static final String MISS_RESOURCE = "未找到资源:{}";
    public static final String MISS_RESOURCE_EXAMPLE = "未找到资源实例";
    public static final String CAN_NOT_CONVERT_CPU = "无法解析的CPU值:{}";
    public static final String CAN_NOT_CONVERT_MEMORY = "无法解析的内存值:{}";
    public static final String UNKOWN_COMMAND_TYPE = "错误的命令类型:{}";
    public static final String EMPTY_METRICS_LIST = "空的指标数据集合";
    public static final String RESOURCE_SHORTAGE = "资源不足";
    public static final String COMMA = ",";
    public static final String UNKNOWN = "unknown";
    public static final String SCHEDULE_LOCK_KEY = "SCHEDULE_LOCK_{}";

}
