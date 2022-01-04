package io.shulie.takin.cloud.biz.convertor;

import io.shulie.takin.cloud.biz.pojo.PressureTaskPo;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.data.model.mysql.PressureTaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

/**
 * @Author: liyuanba
 * @Date: 2021/12/29 4:43 下午
 */
@Mapper(componentModel = "spring", imports = {JsonUtil.class})
public interface PressureTaskConvertor {
    PressureTaskConvertor INSTANCE = Mappers.getMapper(PressureTaskConvertor.class);

    @Mappings(
        value = {
            @Mapping(target = "sceneType", source = "sceneType.getCode()"),
            @Mapping(target = "businessActivityConfig", source = "java(JsonUtil.toJson(businessActivityConfig))"),
            @Mapping(target = "uploadFiles", source = "java(JsonUtil.toJson(uploadFiles))"),
            @Mapping(target = "enginePlugins", source = "java(JsonUtil.toJson(enginePlugins))"),
            @Mapping(target = "scriptNodes", source = "java(JsonUtil.toJson(scriptNodes))"),
        }
    )
    PressureTaskEntity of(PressureTaskPo po);
}
