package io.shulie.takin.cloud.open.resp.statistics;

import java.util.List;

import lombok.Data;

/**
 * @author 无涯
 * @Package io.shulie.takin.web.app.response.statistics
 * @date 2020/11/30 9:16 下午
 */
@Data
public class PressurePieTotalResp {
    private List<PressurePieTotal> data;
    private Integer total;
    @Data
    public static class PressurePieTotal {
        private String type;
        private Integer value;
    }
}
