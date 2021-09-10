package io.shulie.takin.cloud.web.entrypoint.convert;

import io.shulie.takin.cloud.biz.output.scene.manage.WarnDetailOutput;
import io.shulie.takin.cloud.web.entrypoint.response.scenemanage.WarnDetailResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author mubai
 * @date 2020-11-09 18:50
 */

@Mapper
public interface WarnDetailRespConvertor {
    WarnDetailRespConvertor INSTANCE = Mappers.getMapper(WarnDetailRespConvertor.class);

    WarnDetailResponse of(WarnDetailOutput output);

    List<WarnDetailResponse> ofList(List<WarnDetailOutput> warnDetailOutputs);
}
