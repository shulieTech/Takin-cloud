package io.shulie.takin.cloud.common.constants;

/**
 * 文件相关场景
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public class FileConstants {
    /**
     * 换行符
     */
    public static final char LINE_KEY = '\n';
    /**
     * 回车符
     */
    public static final char ENTER_KEY = '\r';

    public static final String SCRIPT_NAME_SUFFIX = "jmx";
    /**
     * 压测运行时脚本文件
     */
    public static final String RUNNING_SCRIPT_FILE_DIR = "runfle";
    /**
     * csv文件行分割符，用以区分行中的字段，以便找到分片号
     */
    public static final String CSV_FILE_LINE_DELIMITER = "@-@";
}
