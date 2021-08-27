package io.shulie.takin.ext.content.asset;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 账号信息
 *
 * @author 张天赐
 */
@Data
public class AccountInfoExt {
    private Long id;

    private Long uid;

    private Long accId;

    private Long parentBookId;

    private BigDecimal balance;

    private BigDecimal lockBalance;

    private BigDecimal totalBalance;

    private Integer subject;

    private Integer direct;

    private String rule;

    private BigDecimal ruleBalance;

    private Date startTime;

    private Date endTime;

    private Integer status;

    private Integer version;

    private Integer isDeleted;

    private Long tags;

    private Date gmtCreate;

    private Date gmtUpdate;

    private String features;
}
