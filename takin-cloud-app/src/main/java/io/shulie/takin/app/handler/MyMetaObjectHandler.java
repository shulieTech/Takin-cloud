package io.shulie.takin.app.handler;

import java.util.Objects;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * @author fanxx
 * @date 2020/11/4 11:02 上午
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        CloudUserCommonRequestExt userCommonExt = new CloudUserCommonRequestExt();
        CloudPluginUtils.fillUserData(userCommonExt);
        if (Objects.nonNull(userCommonExt.getTenantId())) {
            this.strictInsertFill(metaObject, "customerId", Long.class, userCommonExt.getTenantId());
        }
        if (Objects.nonNull(userCommonExt.getUserId())) {
            this.strictInsertFill(metaObject, "userId", Long.class, userCommonExt.getUserId());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {

    }
}
