package io.shulie.takin.cloud.data.mapper.mysql;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryBean;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;

import java.util.List;

/**
 * @author -
 */
public interface SceneManageMapper extends BaseMapper<SceneManageEntity> {

    @InterceptorIgnore(tenantLine = "true")
    List<SceneManageEntity> getPageList(SceneManageQueryBean queryBean);
}