package io.shulie.takin.cloud.data.service.impl;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.data.entity.CallbackLogEntity;
import io.shulie.takin.cloud.data.mapper.CallbackLogMapper;
import io.shulie.takin.cloud.data.service.CallbackLogMapperService;

/**
 * Mapper - IService - Impl - 回调日志
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class CallbackLogMapperServiceImpl
    extends ServiceImpl<CallbackLogMapper, CallbackLogEntity>
    implements CallbackLogMapperService {
}
