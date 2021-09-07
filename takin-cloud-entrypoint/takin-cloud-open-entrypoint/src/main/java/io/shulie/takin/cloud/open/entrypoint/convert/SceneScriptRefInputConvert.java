package io.shulie.takin.cloud.open.entrypoint.convert;

import io.shulie.takin.cloud.biz.input.scenemanage.SceneScriptRefInput;
import io.shulie.takin.cloud.open.req.scenemanage.SceneScriptRefOpen;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mubai
 * @date 2020-10-29 16:12
 */
public class SceneScriptRefInputConvert {

    public static SceneScriptRefInput of(SceneScriptRefOpen in) {
        SceneScriptRefInput out = new SceneScriptRefInput();
        BeanUtils.copyProperties(in, out);
        return out;
    }

    public static List<SceneScriptRefInput> ofList(List<SceneScriptRefOpen> list) {
        List<SceneScriptRefInput> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        list.forEach(open -> result.add(of(open)));
        return result;
    }

}
