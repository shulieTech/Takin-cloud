package io.shulie.takin.cloud.entrypoint.convert;

import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.sdk.response.scenemanage.SceneManageWrapperResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author mubai
 * @date 2020-10-30 11:32
 */

@Mapper
public interface SceneManageRespConvertor {

    SceneManageRespConvertor INSTANCE = Mappers.getMapper(SceneManageRespConvertor.class);

    /**
     * 数据转换
     *
     * @param output 原数据
     * @return 转换后数据
     */
    SceneManageWrapperResponse of(SceneManageWrapperOutput output);

}
