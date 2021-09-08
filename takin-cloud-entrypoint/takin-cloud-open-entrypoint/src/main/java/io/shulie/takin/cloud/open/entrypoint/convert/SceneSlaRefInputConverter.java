package io.shulie.takin.cloud.open.entrypoint.convert;

import io.shulie.takin.cloud.biz.input.scenemanage.SceneSlaRefInput;
import io.shulie.takin.cloud.open.req.scenemanage.SceneSlaRefOpen;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mubai
 * @date 2020-10-29 10:56
 */

public class SceneSlaRefInputConverter {

    public static SceneSlaRefInput of(SceneSlaRefOpen sceneSlaRef) {
        SceneSlaRefInput out = new SceneSlaRefInput();
        BeanUtils.copyProperties(sceneSlaRef, out);
        return out;
    }

    public static List<SceneSlaRefInput> ofList(List<SceneSlaRefOpen> sceneSlaRefs) {
        List<SceneSlaRefInput> result = new ArrayList<>();
        sceneSlaRefs.forEach(sceneSlaRef -> result.add(of(sceneSlaRef)));
        return result;
    }

}
