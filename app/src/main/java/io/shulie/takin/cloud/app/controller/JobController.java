package io.shulie.takin.cloud.app.controller;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import io.shulie.takin.cloud.app.entity.Job;
import io.shulie.takin.cloud.app.mapper.JobMapper;

/**
 * 任务
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Tag(name = "任务")
@RestController
@RequestMapping("/job")
public class JobController {
    @Resource
    JobMapper jobMapper;

    @Operation(summary = "任务列表")
    @RequestMapping(value = "list", method = {RequestMethod.POST})
    public List<Job> listAll() {
        return jobMapper.selectList(null);
    }
}
