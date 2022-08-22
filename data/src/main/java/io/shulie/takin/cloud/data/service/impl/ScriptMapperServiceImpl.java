package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.ScriptEntity;
import io.shulie.takin.cloud.data.mapper.ScriptMapper;
import io.shulie.takin.cloud.data.service.ScriptMapperService;

/**
 * Mapper - IService - Impl - 脚本
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ScriptMapperServiceImpl
    extends ServiceImpl<ScriptMapper, ScriptEntity>
    implements ScriptMapperService {
}
