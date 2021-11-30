package io.shulie.takin.cloud.entrypoint.convert;

import java.util.ArrayList;
import java.util.List;

import io.shulie.takin.cloud.biz.input.scenemanage.SceneBusinessActivityRefInput;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneBusinessActivityRefOpen;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

/**
 * @author mubai
 * @date 2020-10-29 15:20
 */
public class SceneBusinessActivityRefInputConvert {

    public static SceneBusinessActivityRefInput of(SceneBusinessActivityRefOpen open) {
        SceneBusinessActivityRefInput out = new SceneBusinessActivityRefInput();
        BeanUtils.copyProperties(open, out);
        return out;
    }

    public static List<SceneBusinessActivityRefInput> ofLists(List<SceneBusinessActivityRefOpen> list) {
        List<SceneBusinessActivityRefInput> outs = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return outs;
        }
        list.forEach(open -> outs.add(of(open)));
        return outs;
    }
}
