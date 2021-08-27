package io.shulie.takin.cloud.biz.convertor;

import com.pamirs.takin.entity.domain.dto.strategy.StrategyConfigDTO;
import io.shulie.takin.ext.content.enginecall.StrategyConfigExt;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @author zhaoyong
 */
@Mapper
public interface ScheduleConvertor {
    ScheduleConvertor INSTANCE = Mappers.getMapper(ScheduleConvertor.class);

    /**
     * 参数转换
     * @param config
     * @return
     */
    StrategyConfigExt ofStrategyConfig(StrategyConfigDTO config);
}
