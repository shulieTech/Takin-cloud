package io.shulie.takin.ext.content.enginecall;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 莫问
 * @date 2020-05-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScheduleStopRequestExt extends ScheduleEventRequestExt implements Serializable {

    private String jobName;

    private String engineInstanceRedisKey;
}
