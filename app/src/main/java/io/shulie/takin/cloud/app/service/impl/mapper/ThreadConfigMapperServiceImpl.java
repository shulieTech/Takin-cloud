package io.shulie.takin.cloud.app.service.impl.mapper;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.app.entity.ThreadConfigEntity;
import io.shulie.takin.cloud.app.mapper.ThreadConfigMapper;
import io.shulie.takin.cloud.app.service.mapper.ThreadConfigMapperService;

/**
 * Mapper - IService - Impl - 线程组配置
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ThreadConfigMapperServiceImpl extends ServiceImpl<ThreadConfigMapper, ThreadConfigEntity> implements ThreadConfigMapperService {
}
