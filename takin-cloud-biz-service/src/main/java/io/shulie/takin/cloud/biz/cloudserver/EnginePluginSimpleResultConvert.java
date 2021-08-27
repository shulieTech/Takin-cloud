package io.shulie.takin.cloud.biz.cloudserver;

import io.shulie.takin.cloud.biz.output.engine.EnginePluginSimpleInfoOutput;
import io.shulie.takin.cloud.data.result.engine.EnginePluginSimpleInfoResult;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 引擎插件简单参数转换
 *
 * @author lipeng
 * @date 2021-01-20 5:12 下午
 */
@Mapper
public interface EnginePluginSimpleResultConvert {

    EnginePluginSimpleResultConvert INSTANCE = Mappers.getMapper(EnginePluginSimpleResultConvert.class);

    EnginePluginSimpleInfoOutput of(EnginePluginSimpleInfoResult result);

    List<EnginePluginSimpleInfoOutput> ofs(List<EnginePluginSimpleInfoResult> results);
}
