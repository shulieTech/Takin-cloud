package io.shulie.takin.schedule.engine.impl;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.assertj.core.util.Lists;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.shulie.takin.app.Application;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.cloud.open.api.engine.CloudEngineApi;
import io.shulie.takin.cloud.data.mapper.mysql.EnginePluginMapper;
import io.shulie.takin.cloud.biz.service.engine.EnginePluginService;
import io.shulie.takin.cloud.biz.input.engine.EnginePluginWrapperInput;
import io.shulie.takin.cloud.open.resp.engine.EnginePluginSimpleInfoResp;
import io.shulie.takin.cloud.open.req.engine.EnginePluginFetchWrapperReq;
import io.shulie.takin.cloud.biz.service.engine.EnginePluginFilesService;
import io.shulie.takin.cloud.biz.output.engine.EnginePluginSimpleInfoOutput;

/**
 * 引擎测试
 *
 * @author lipeng
 * @date 2021-01-06 4:08 下午
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
public class PressureEngineMapperTest {

    @Resource
    private EnginePluginMapper pressureEngineMapper;

    @Resource
    private EnginePluginService enginePluginService;

    @Resource
    private EnginePluginFilesService enginePluginFilesService;

    @Resource
    private CloudEngineApi cloudEngineApi;

    @Test
    public void testFindPluginFilesPathByPluginIds() {
        List<Long> ids = Lists.newArrayList();
        ids.add(6L);
        ids.add(7L);
        List<String> result = enginePluginFilesService.findPluginFilesPathByPluginIds(ids);
        System.out.println(result);
    }

    @Test
    public void testQueryAvailablePluginsByType() {
        List<String> queryParams = Lists.newArrayList();
        queryParams.add("dubbo");
        queryParams.add("kafka");

        Map<String, List<EnginePluginSimpleInfoOutput>> res = enginePluginService.findEngineAvailablePluginsByType(queryParams);
        System.out.println(JsonHelper.bean2Json(res));
    }

    @Test
    public void testQueryEnginePluginSupportedVersions() {
        List<Map> listSupportedVersions = pressureEngineMapper.selectEnginePluginSupportedVersions(1L);
        System.out.println(listSupportedVersions);
    }

    @Test
    public void testSaveEnginePlugin() {
        EnginePluginWrapperInput input = new EnginePluginWrapperInput();
        input.setPluginName("redis-all");
        input.setPluginType("redis");
        input.setPluginUploadPath("/test/aa/f");
        List<String> versions = Lists.newArrayList();
        versions.add("1.0");
        versions.add("2.0");
        input.setSupportedVersions(versions);
        enginePluginService.saveEnginePlugin(input);
    }

    @Test
    public void testSdk() {
        EnginePluginFetchWrapperReq request = new EnginePluginFetchWrapperReq();
        List<String> req = Lists.newArrayList();
        req.add("dubbo");
        request.setPluginTypes(req);
        Map<String, List<EnginePluginSimpleInfoResp>> res = cloudEngineApi.listEnginePlugins(request);
        List<EnginePluginSimpleInfoResp> resp = res.get("dubbo");
        resp.forEach(item -> System.out.println(item.getPluginName()));
    }

}
