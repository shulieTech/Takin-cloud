package io.shulie.takin.cloud.app.service.impl.mapper;

import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import io.shulie.takin.cloud.app.entity.CallbackLogEntity;
import io.shulie.takin.cloud.app.mapper.CallbackLogMapper;
import io.shulie.takin.cloud.app.service.mapper.CallbackLogMapperService;

/**
 * Mapper - IService - Impl - 回调
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class CallbackLogMapperServiceImpl extends ServiceImpl<CallbackLogMapper, CallbackLogEntity> implements CallbackLogMapperService {
}
