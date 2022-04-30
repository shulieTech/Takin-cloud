package io.shulie.takin.cloud.model.response;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import io.shulie.takin.cloud.constant.enums.ThreadGroupType;
import io.shulie.takin.cloud.model.request.StartRequest.ThreadConfigInfo;

/**
 * 任务配置
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Schema(description = "配置内容体")
public class JobConfig {
    /**
     * 任务主键
     */
    @Schema(description = "任务主键")
    private Long jobId;
    /**
     * 关键字
     */
    @Schema(description = "关键词")
    private String ref;
    /**
     * 线程组类型
     */
    private ThreadGroupType type;
    /**
     * 配置内容
     */
    @Schema(description = "配置内容")
    private ThreadConfigInfo context;

    public static void main(String[] args) throws JsonProcessingException {
        String s = "{\"data\":[{\"jobId\":8,\"ref\":\"7dae7383a28b5c45069b528a454d1164\",\"type\":102,"
            + "\"context\":{\"ref\":null,\"type\":102,\"duration\":300,\"number\":2,\"tps\":0,\"growthTime\":180,"
            + "\"growthStep\":null}}],\"msg\":\"SUCCESS\",\"total\":null,\"success\":true}";
        ObjectMapper objectMapper = new ObjectMapper();
        ApiResult<List<JobConfig>> jobConfigs = objectMapper.readValue(s, new TypeReference<ApiResult<List<JobConfig>>>() {});
        System.out.println(jobConfigs);
    }
}
