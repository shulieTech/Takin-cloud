package io.shulie.takin.cloud.app.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.shulie.takin.cloud.app.entity.ResourceExample;
import io.shulie.takin.cloud.app.mapper.ResourceExampleMapper;
import io.shulie.takin.cloud.app.mapper.ResourceMapper;
import io.shulie.takin.cloud.app.service.ResourceService;
import org.springframework.stereotype.Service;

/**
 * 资源服务 - 实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ResourceServiceImpl implements ResourceService {
    @Resource
    ResourceMapper resourceMapper;
    @Resource
    ResourceExampleMapper resourceExampleMapper;

    @Override
    public List<ResourceExample> listExample(Long resourceId) {
        // 查询条件
        Wrapper<ResourceExample> wrapper = new LambdaQueryWrapper<ResourceExample>()
            .eq(ResourceExample::getResourceId, resourceId);
        // 执行查询
        return resourceExampleMapper.selectList(wrapper);
    }
}
