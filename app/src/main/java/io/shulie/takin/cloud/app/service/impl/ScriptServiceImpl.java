package io.shulie.takin.cloud.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import io.shulie.takin.cloud.app.service.ScriptService;

/**
 * 脚本服务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Service
public class ScriptServiceImpl implements ScriptService {

    @Override
    public Long announce(String scriptPath, List<String> dataFilePath, List<String> attachmentsPath) {
        return 0L;
    }

    @Override
    public void report(Long id, Boolean completed, String message) {
        // Do nothing, now.
    }
}
