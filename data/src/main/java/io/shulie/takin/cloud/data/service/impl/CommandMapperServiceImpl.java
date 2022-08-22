package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.CommandEntity;
import io.shulie.takin.cloud.data.mapper.CommandMapper;
import io.shulie.takin.cloud.data.service.CommandMapperService;

/**
 * Mapper - IService - Impl - 命令
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class CommandMapperServiceImpl
    extends ServiceImpl<CommandMapper, CommandEntity>
    implements CommandMapperService {
}
