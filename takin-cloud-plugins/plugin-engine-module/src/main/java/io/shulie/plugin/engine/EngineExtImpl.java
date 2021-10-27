package io.shulie.plugin.engine;

import io.shulie.plugin.engine.util.JmxUtil;
import io.shulie.plugin.engine.util.SaxUtil;
import io.shulie.takin.cloud.common.utils.UrlUtil;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.ext.api.EngineExtApi;
import io.shulie.takin.ext.content.script.*;
import org.apache.commons.collections4.CollectionUtils;
import org.pf4j.Extension;

import java.util.*;

/**
 * @author zhaoyong
 * 脚本拓展实现
 */
@Extension
public class EngineExtImpl implements EngineExtApi {

    @Override
    public ScriptVerityRespExt verityScript(ScriptVerityExt scriptVerityExt) {
        ScriptVerityRespExt scriptVerityRespExt = new ScriptVerityRespExt();
        List<String> errorMsgList = new ArrayList<>();
        scriptVerityRespExt.setErrorMsg(errorMsgList);

        if (CollectionUtils.isEmpty(scriptVerityExt.getRequest())){
            throw new TakinCloudException(TakinCloudExceptionEnum.SCRIPT_VERITY_ERROR,"脚本校验业务活动不能为空");
        }

        ScriptParseExt scriptParseExt = SaxUtil.parseJmx(scriptVerityExt.getScriptPath());
        List<ScriptUrlExt> requestUrl = scriptParseExt.getRequestUrl();
        if (CollectionUtils.isEmpty(requestUrl)) {
            errorMsgList.add("脚本中没有获取到请求链接！");
            return scriptVerityRespExt;
        }

        Set<String> errorSet = new HashSet<>();
        int unbindCount = 0;
        Map<String, Integer> urlMap = new HashMap<>();
        for (String request : scriptVerityExt.getRequest()){
            Set<String> tempErrorSet = new HashSet<>();
            for (ScriptUrlExt urlVO : requestUrl) {
                if (UrlUtil.checkEqual(request, urlVO.getPath()) && urlVO.getEnable()) {
                    unbindCount = unbindCount + 1;
                    tempErrorSet.clear();
                    if (!urlMap.containsKey(urlVO.getName())) {
                        urlMap.put(urlVO.getName(), 1);
                    } else {
                        urlMap.put(urlVO.getName(), urlMap.get(urlVO.getName()) + 1);
                    }
                    break;
                } else {
                    tempErrorSet.add(request);
                }
            }
            errorSet.addAll(tempErrorSet);
        }

        Set<String> urlErrorSet = new HashSet<>();
        urlMap.forEach((k, v) -> {
            if (v > 1) {
                urlErrorSet.add("脚本中[" + k + "]重复" + v + "次");
            }
        });
        if (urlErrorSet.size() > 0) {
            errorMsgList.add("脚本文件配置不正确:" + urlErrorSet.toString());
        }
        //存在业务活动都关联不上脚本中的请求连接
        if (scriptVerityExt.getRequest().size() > unbindCount) {
            errorMsgList.add("业务活动与脚本文件不匹配:" + errorSet.toString());
        }
        return scriptVerityRespExt;
    }

    @Override
    public void updateScriptContent(String uploadPath) {
        SaxUtil.updateJmx(uploadPath);
    }

    @Override
    public ScriptParseExt parseScriptFile(String uploadPath) {
        return SaxUtil.parseJmx(uploadPath);
    }

    @Override
    public List<ScriptNode> buildNodeTree(String scriptFile) {
        return JmxUtil.buildNodeTree(scriptFile);
    }

    @Override
    public String getType() {
        return "jmeter_engine";
    }
}
