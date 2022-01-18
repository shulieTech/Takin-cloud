package io.shulie.takin.cloud.entrypoint.convert;

import java.util.List;
import java.util.ArrayList;

import cn.hutool.core.bean.BeanUtil;
import org.apache.commons.collections4.CollectionUtils;

import io.shulie.takin.cloud.biz.input.scenemanage.SceneScriptRefInput;
import io.shulie.takin.cloud.sdk.model.request.scenemanage.SceneScriptRefOpen;

/**
 * @author mubai
 * @date 2020-10-29 16:12
 */
public class SceneScriptRefInputConvert {

    public static SceneScriptRefInput of(SceneScriptRefOpen in) {
        return BeanUtil.copyProperties(in, SceneScriptRefInput.class);
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
