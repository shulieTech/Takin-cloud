package io.shulie.takin.cloud.biz.output.middleware;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author liuchuan
 * @date 2021/6/1 7:49 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CompareMiddlewareJarResultVO extends CompareMiddlewareJarVO {

    /**
     * 父类实例为入参的构造函数
     *
     * @param parent 父类实例
     */
    public CompareMiddlewareJarResultVO(CompareMiddlewareJarVO parent) {
        BeanUtil.copyProperties(parent, this);
    }

    @Excel(name = "比对结果", orderNum = "4")
    private String statusDesc;

    @Excel(name = "比对备注", orderNum = "5")
    private String remark;

}
