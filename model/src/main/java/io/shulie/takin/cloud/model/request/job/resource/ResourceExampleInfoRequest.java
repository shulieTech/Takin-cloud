package io.shulie.takin.cloud.model.request.job.resource;

import java.util.List;

import lombok.Data;

/**
 * 资源实例信息上报
 *
 * @author chenhongqiao@shulie.com
 */
@Data
public class ResourceExampleInfoRequest {
    private String name;
    /**
     * engine状态
     */
    private String businessState;
    private String status;
    private Integer restart;
    private String startTime;
    private String ip;
    private String hostIp;
    /**
     * 容器节点的事件
     */
    private List<Object> events;
    private String error;
}
