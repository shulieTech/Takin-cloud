package io.shulie.takin.cloud.web.entrypoint.response.scenemanage;

import java.math.BigDecimal;

import lombok.Data;

/**
 * @author qianshui
 * @date 2020/5/18 下午8:36
 */
@Data
public class BusinessActivityDetailResponse {

    private transient Long businessActivityId;

    private String businessActivityName;

    private Integer targetTPS;

    private Integer targetRT;

    private BigDecimal targetSuccessRate;

    private BigDecimal targetSA;

}
