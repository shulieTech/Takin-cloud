package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.ThreadConfigExampleEntity;
import io.shulie.takin.cloud.data.mapper.ThreadConfigExampleMapper;
import io.shulie.takin.cloud.data.service.ThreadConfigExampleMapperService;

/**
 * Mapper - IService - Impl - 线程组配置实例
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ThreadConfigExampleMapperServiceImpl
    extends ServiceImpl<ThreadConfigExampleMapper, ThreadConfigExampleEntity>
    implements ThreadConfigExampleMapperService {
}
