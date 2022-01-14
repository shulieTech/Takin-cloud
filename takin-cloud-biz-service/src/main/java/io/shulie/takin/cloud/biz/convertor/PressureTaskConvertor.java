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
            @Mapping(target = "sceneType", source = "sceneType.code"),
            @Mapping(target = "businessActivityConfig", expression = "java(JsonUtil.toJson(po.getBusinessActivityConfig()))"),
            @Mapping(target = "uploadFiles", expression = "java(JsonUtil.toJson(po.getUploadFiles()))"),
            @Mapping(target = "enginePlugins", expression = "java(JsonUtil.toJson(po.getEnginePlugins()))"),
            @Mapping(target = "scriptNodes", expression = "java(JsonUtil.toJson(po.getScriptNodes()))"),
        }
    )
    PressureTaskEntity of(PressureTaskPo po);
}
