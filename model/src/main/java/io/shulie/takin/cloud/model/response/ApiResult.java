package io.shulie.takin.cloud.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import io.shulie.takin.cloud.constant.Message;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Api结果
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
@Schema(description = "Api结果")
@SuppressWarnings({"unused", "rawtypes", "unchecked"})
public class ApiResult<T> {

    @Schema(description = "数据")
    private T data = null;
    @Schema(description = "描述")
    private String msg = null;
    @Schema(description = "数据总数")
    private Long total = null;
    @Schema(description = "业务成功标识")
    private boolean success = false;

    /**
     * 成功
     *
     * @param <T> 数据泛型
     * @return 成功的API结果
     */
    public static <T> ApiResult<T> success() {
        return success(null);
    }

    /**
     * 成功
     *
     * @param data 数据体
     * @param <T>  数据泛型
     * @return 成功的API结果
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult().setData(data).setSuccess(true).setMsg(Message.SUCCESS);
    }

    /**
     * 成功
     *
     * @param data  数据体
     * @param total 数据总条数
     * @param <T>   数据泛型
     * @return 成功的API结果
     */
    public static <T> ApiResult<T> success(T data, Long total) {
        return success(data).setTotal(total);
    }

    /**
     * 成功
     *
     * @param data 数据体
     * @param msg  成功信息
     * @param <T>  数据泛型
     * @return 成功的API结果
     */
    public static <T> ApiResult<T> success(T data, String msg) {
        return success(data).setMsg(msg);
    }

    /**
     * 成功
     *
     * @param data  数据体
     * @param total 数据总条数
     * @param msg   成功信息
     * @param <T>   数据泛型
     * @return 成功的API结果
     */
    public static <T> ApiResult<T> success(T data, Long total, String msg) {
        return success(data, total).setMsg(msg);
    }

    /**
     * 失败
     *
     * @param msg 失败信息
     * @return 失败的API结果
     */
    public static ApiResult<Object> fail(String msg) {
        return new ApiResult().setSuccess(false).setMsg(msg);
    }

    /**
     * 失败
     *
     * @param msg  失败信息
     * @param data 数据体
     * @return 失败的API结果
     */
    public static ApiResult<Object> fail(String msg, Object data) {
        return fail(msg).setData(data);
    }
}
