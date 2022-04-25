package io.shulie.takin.cloud.app.service.impl;

import java.util.List;

import javax.annotation.Resource;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import io.shulie.takin.cloud.app.entity.ResourceEntity;
import io.shulie.takin.cloud.app.service.CommandService;
import io.shulie.takin.cloud.app.util.ResourceUtil;
import io.shulie.takin.cloud.app.mapper.ResourceMapper;
import io.shulie.takin.cloud.app.entity.ResourceExampleEntity;
import io.shulie.takin.cloud.app.service.ResourceService;
import io.shulie.takin.cloud.app.service.WatchmanService;
import io.shulie.takin.cloud.app.mapper.ResourceExampleMapper;
import io.shulie.takin.cloud.app.model.request.ApplyResourceRequest;

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
    CommandService commandService;
    @Resource
    WatchmanService watchmanService;
    @Resource
    ResourceExampleMapper resourceExampleMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ResourceExampleEntity> listExample(Long resourceId) {
        // 查询条件
        Wrapper<ResourceExampleEntity> wrapper = new LambdaQueryWrapper<ResourceExampleEntity>()
            .eq(ResourceExampleEntity::getResourceId, resourceId);
        // 执行查询
        return resourceExampleMapper.selectList(wrapper);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean check(ApplyResourceRequest apply) {
        // 1. 声明需要的资源
        int number = apply.getNumber();
        Double requestCpu = ResourceUtil.convertCpu(apply.getCpu());
        Integer requestMemory = ResourceUtil.convertMemory(apply.getCpu());
        if (requestCpu == null || requestMemory == null) {return false;}
        // 2. 获取调度所属的资源列表
        List<Object> resourceList = watchmanService.getResourceList(apply.getWatchmanId());
        // 3. 循环判断每一个资源
        for (int i = 0; i < resourceList.size() && number > 0; i++) {
            Object resource = resourceList.get(i);
            if (resource != null) {
                // 4. 声明每个资源拥有的量化资源
                Double spareCpu = ResourceUtil.convertCpu(resource.toString());
                Integer spareMemory = ResourceUtil.convertMemory(resource.toString());
                // 空值校验
                if (spareCpu != null && spareMemory != null) {
                    // 5. 递减资源余量和申请的数量
                    while (number > 0 && spareCpu >= requestCpu && spareMemory >= requestMemory) {
                        number--;
                        spareCpu -= requestCpu;
                        spareMemory -= requestMemory;
                    }
                }
            }
        }
        // 6. 所需的资源数量是否全部满足了
        return number == 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String lock(ApplyResourceRequest apply, String callbackUrl) {
        // 0. 预检
        if (this.check(apply)) {
            // 1. 保存任务信息
            ResourceEntity resourceEntity = new ResourceEntity() {{
                setCallbackUrl(callbackUrl);
                setNumber(apply.getNumber());
                setWatchmanId(apply.getWatchmanId());
                // 创建时间由数据库维护
                setCpu(apply.getCpu());
                setLimitCpu(StrUtil.isBlank(apply.getLimitCpu()) ? apply.getCpu() : apply.getLimitCpu());
                setMemory(apply.getMemory());
                setLimitMemory(StrUtil.isBlank(apply.getLimitMemory()) ? apply.getMemory() : apply.getLimitMemory());
            }};
            resourceMapper.insert(resourceEntity);
            // 2. 创建任务实例
            for (int i = 0; i < apply.getNumber(); i++) {
                ResourceExampleEntity resourceExampleEntity = new ResourceExampleEntity() {{
                    setResourceId(resourceEntity.getId());
                    setWatchmanId(resourceEntity.getWatchmanId());
                    // 创建时间由数据库维护
                    setCpu(apply.getCpu());
                    setLimitCpu(StrUtil.isBlank(apply.getLimitCpu()) ? apply.getCpu() : apply.getLimitCpu());
                    setMemory(apply.getMemory());
                    setLimitMemory(StrUtil.isBlank(apply.getLimitMemory()) ? apply.getMemory() : apply.getLimitMemory());
                }};
                resourceExampleMapper.insert(resourceExampleEntity);
                // 3. 下发命令
                commandService.graspResource(resourceExampleEntity);
            }
            // end 返回资源主键
            return resourceEntity.getId().toString();
        }
        // end 预检失败则直接返回 NULL
        else {return null;}
    }
}
