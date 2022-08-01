package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.ThreadConfigEntity;
import io.shulie.takin.cloud.data.mapper.ThreadConfigMapper;
import io.shulie.takin.cloud.data.service.ThreadConfigMapperService;

/**
 * Mapper - IService - Impl - 线程组配置
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ThreadConfigMapperServiceImpl extends ServiceImpl<ThreadConfigMapper, ThreadConfigEntity> implements ThreadConfigMapperService {
}
