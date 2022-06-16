package io.shulie.takin.cloud.model.request;

import java.util.List;

import lombok.Data;

/**
 * ClassName:    ComputeNodeRequestVO
 * Package:    io.shulie.takin.drilling.sdk.vo.request
 * Description:
 * Datetime:    2022/4/24   14:41
 * Author:   chenhongqiao@shulie.com
 */
@Data
public class ResourceExampleInfoRequest  {
    private String name;
    private String businessState;   //engine状态
    private String status;
    private Integer restart;
    private String startTime;
    private String ip;
    private String hostIp;
    private List<Object> events;     //容器节点的事件
    private String error;
}
