package io.shulie.takin.cloud.biz.input.report;

import lombok.Data;

/**
 * @ClassName WarnCreateInput
 * @Description
 * @Author qianshui
 * @Date 2020/11/18 上午11:49
 */
@Data
public class WarnCreateInput {

    private Long id;

    private Long ptId;

    private Long slaId;

    private String slaName;

    private Long businessActivityId;

    private String businessActivityName;

    private String warnContent;

    private Double realValue;

    private String warnTime;
}
