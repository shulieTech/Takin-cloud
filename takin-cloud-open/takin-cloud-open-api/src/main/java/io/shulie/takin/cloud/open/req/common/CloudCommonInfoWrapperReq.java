package io.shulie.takin.cloud.open.req.common;

import java.io.Serializable;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 公共信息请求
 *
 * @author lipeng
 * @date 2021-06-24 4:24 下午
 */
@Data
@ApiModel("公共信息请求入参")
public class CloudCommonInfoWrapperReq extends CloudUserCommonRequestExt implements Serializable {
    //暂时无入参
}
