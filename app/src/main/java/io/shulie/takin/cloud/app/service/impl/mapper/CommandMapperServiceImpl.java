package io.shulie.takin.cloud.app.service.impl.mapper;

import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.app.entity.CommandEntity;
import io.shulie.takin.cloud.app.mapper.CommandMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.cloud.app.service.mapper.CommandMapperService;

/**
 * Mapper - IService - Impl - 命令
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class CommandMapperServiceImpl extends ServiceImpl<CommandMapper, CommandEntity> implements CommandMapperService {
}
