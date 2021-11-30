package io.shulie.takin.cloud.biz.convertor.scenemanage;

import java.util.List;

import io.shulie.takin.cloud.biz.input.scenemanage.SceneBusinessActivityRefInput;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhaoyong
 */
@Mapper
public interface SceneManageInputConvertor {

    SceneManageInputConvertor INSTANCE = Mappers.getMapper(SceneManageInputConvertor.class);

    /**
     * 入参转换
     *
     * @param businessActivityConfig
     * @return -
     */
    List<SceneBusinessActivityRefInput> ofListSceneBusinessActivityRefInput(List<SceneBusinessActivityRefInput> businessActivityConfig);
}
