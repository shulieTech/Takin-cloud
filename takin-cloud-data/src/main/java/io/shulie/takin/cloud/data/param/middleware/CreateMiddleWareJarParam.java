package io.shulie.takin.cloud.data.param.middleware;

import io.shulie.takin.cloud.data.model.mysql.MiddlewareJarEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 中间件jar包创建参数类
 *
 * @author liuchuan
 * @date 2021/6/1 3:08 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CreateMiddleWareJarParam extends MiddlewareJarEntity {

}
