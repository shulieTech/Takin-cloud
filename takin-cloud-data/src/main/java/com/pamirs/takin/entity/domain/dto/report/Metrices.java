package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;

import lombok.Data;

/**
 * @ClassName ApplicationDTO
 * @Description 应用对象
 * @Author qianshui
 * @Date 2020/7/22 下午3:14
 */
@Data
public class Metrices implements Serializable {
    /**
     * 时间
     */
    private Long time;

    /**
     * tps
     */
    private Double avgTps;
}
