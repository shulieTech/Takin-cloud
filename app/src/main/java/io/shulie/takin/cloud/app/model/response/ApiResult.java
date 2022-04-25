package io.shulie.takin.cloud.app.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Api结果
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@SuppressWarnings("unused")
public class ApiResult {
    private static final String SUCCESS_MESSAGE = "SUCCESS";

    private Long total = null;
    private String msg = null;
    private Object data = null;
    private boolean success = false;

    /**
     * 成功
     *
     * @return 成功的API结果
     */
    public static ApiResult success() {
        return new ApiResult() {{
            setSuccess(true);
            setMsg(SUCCESS_MESSAGE);
        }};
    }

    /**
     * 成功
     *
     * @param data 数据体
     * @return 成功的API结果
     */
    public static ApiResult success(Object data) {
        return success().setData(data);
    }

    /**
     * 成功
     *
     * @param data  数据体
     * @param total 数据总条数
     * @return 成功的API结果
     */
    public static ApiResult success(Object data, Long total) {
        return success(data).setTotal(total);
    }

    /**
     * 成功
     *
     * @param data 数据体
     * @param msg  成功信息
     * @return 成功的API结果
     */
    public static ApiResult success(Object data, String msg) {
        return success().setData(data).setMsg(msg);
    }

    /**
     * 成功
     *
     * @param data  数据体
     * @param total 数据总条数
     * @param msg   成功信息
     * @return 成功的API结果
     */
    public static ApiResult success(Object data, Long total, String msg) {
        return success(data, total).setMsg(msg);
    }

    /**
     * 失败
     *
     * @param msg 失败信息
     * @return 失败的API结果
     */
    public static ApiResult fail(String msg) {
        return new ApiResult() {{
            setSuccess(false);
            setMsg(msg);
        }};
    }

    /**
     * 失败
     *
     * @param msg  失败信息
     * @param data 数据体
     * @return 失败的API结果
     */
    public static ApiResult fail(String msg, Object data) {
        return fail(msg).setData(data);
    }
}
