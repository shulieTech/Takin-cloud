package io.shulie.takin.app.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;

/**
 * @author fanxx
 * @date 2020/11/4 11:02 上午
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        ContextExt traceContext = CloudPluginUtils.getContext();
        this.strictInsertFill(metaObject, "user_id", Long.class, traceContext.getUserId());
        this.strictInsertFill(metaObject, "tenant_id", Long.class, traceContext.getTenantId());
        this.strictInsertFill(metaObject, "env_code", String.class, traceContext.getEnvCode());
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
