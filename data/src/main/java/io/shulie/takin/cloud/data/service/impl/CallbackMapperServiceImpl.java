package io.shulie.takin.cloud.data.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.cloud.data.entity.CallbackEntity;
import io.shulie.takin.cloud.data.mapper.CallbackMapper;
import io.shulie.takin.cloud.data.service.CallbackMapperService;
import org.springframework.stereotype.Service;

/**
 * Mapper - IService - Impl - 回调
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class CallbackMapperServiceImpl extends ServiceImpl<CallbackMapper, CallbackEntity> implements CallbackMapperService {
}
