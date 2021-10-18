package io.shulie.takin.cloud.entrypoint.convert;

import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author mubai
 * @date 2020-10-30 12:56
 */

@Mapper
public interface SceneSlaRefRespConvertor {

    SceneSlaRefRespConvertor INSTANCE = Mappers.getMapper(SceneSlaRefRespConvertor.class);

    /**
     * 数据转换
     *
     * @param output 原数据
     * @return 转换后数据
     */
    SceneManageWrapperResponse.SceneSlaRefResponse of(SceneManageWrapperOutput.SceneSlaRefOutput output);

    /**
     * 数据转换(批量)
     *
     * @param output 原数据
     * @return 转换后数据
     */
    List<SceneManageWrapperResponse.SceneSlaRefResponse> ofList(List<SceneManageWrapperOutput.SceneSlaRefOutput> output);
}
