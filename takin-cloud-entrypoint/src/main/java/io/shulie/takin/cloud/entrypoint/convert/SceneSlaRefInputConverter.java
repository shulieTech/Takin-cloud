package io.shulie.takin.cloud.entrypoint.convert;

import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.bean.BeanUtil;

import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneSlaRefOpen;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.SceneManageWrapperResponse.SceneSlaRefResponse;

/**
 * @author mubai
 * @date 2020-10-29 10:56
 */

public class SceneSlaRefInputConverter {

    public static SceneSlaRefResponse of(SceneSlaRefOpen sceneSlaRef) {
        return BeanUtil.copyProperties(sceneSlaRef, SceneSlaRefResponse.class);
    }

    public static List<SceneSlaRefResponse> ofList(List<SceneSlaRefOpen> sceneSlaRefs) {
        List<SceneSlaRefResponse> result = new ArrayList<>();
        sceneSlaRefs.forEach(sceneSlaRef -> result.add(of(sceneSlaRef)));
        return result;
    }

}
