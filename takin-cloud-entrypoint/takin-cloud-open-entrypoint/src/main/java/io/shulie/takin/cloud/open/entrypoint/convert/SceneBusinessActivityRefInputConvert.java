package io.shulie.takin.cloud.open.entrypoint.convert;

import io.shulie.takin.cloud.biz.input.scenemanage.SceneBusinessActivityRefInput;
import io.shulie.takin.cloud.open.req.scenemanage.SceneBusinessActivityRefOpen;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: mubai
 * @Date: 2020-10-29 15:20
 * @Description:
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
        list.stream().forEach(open -> {
            outs.add(of(open));
        });
        return outs;
    }
}
