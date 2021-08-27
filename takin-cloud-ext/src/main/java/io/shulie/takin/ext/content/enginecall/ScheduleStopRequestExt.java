package io.shulie.takin.ext.content.enginecall;

import java.io.Serializable;

import lombok.Data;

/**
 * @Author 莫问
 * @Date 2020-05-12
 */
@Data
public class ScheduleStopRequestExt extends ScheduleEventRequestExt implements Serializable {

    private String jobName;

    private String engineInstanceRedisKey;
}
