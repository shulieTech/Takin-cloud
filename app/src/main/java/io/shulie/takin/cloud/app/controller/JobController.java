package io.shulie.takin.cloud.app.controller;

import java.util.List;

import javax.annotation.Resource;

import io.shulie.takin.cloud.app.entity.Job;
import io.shulie.takin.cloud.app.mapper.JobMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Api(tags = "任务")
@RestController
@RequestMapping("/job")
public class JobController {
    @Resource
    JobMapper jobMapper;

    @ApiOperation("任务列表")
    @RequestMapping(value = "list", method = {RequestMethod.POST})
    public List<Job> listAll() {
        return jobMapper.selectList(null);
    }
}
