package io.shulie.takin.cloud.web.entrypoint.convert;

import io.shulie.takin.cloud.biz.output.scenemanage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.web.entrypoint.response.scenemanage.SceneManageWrapperResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Author: mubai
 * @Date: 2020-10-30 11:32
 * @Description:
 */

@Mapper
public interface SceneManageRespConvertor {

    SceneManageRespConvertor INSTANCE = Mappers.getMapper(SceneManageRespConvertor.class);

    SceneManageWrapperResponse of(SceneManageWrapperOutput output);

}
