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

    /**
     * 转换
     *
     * @param result 入参
     * @return 出参
     */
    EnginePluginSimpleInfoOutput of(EnginePluginSimpleInfoResult result);

    /**
     * 批量转换
     *
     * @param results 入参(批量)
     * @return 出参(批量)
     */
    List<EnginePluginSimpleInfoOutput> ofs(List<EnginePluginSimpleInfoResult> results);
}
