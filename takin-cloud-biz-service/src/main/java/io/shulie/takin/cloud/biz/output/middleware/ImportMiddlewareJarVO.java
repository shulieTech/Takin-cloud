package io.shulie.takin.cloud.biz.output.middleware;

import lombok.Data;
import lombok.EqualsAndHashCode;

import cn.hutool.core.bean.BeanUtil;
import cn.afterturn.easypoi.excel.annotation.Excel;

import io.shulie.takin.cloud.data.model.mysql.MiddlewareJarEntity;
import lombok.NoArgsConstructor;

/**
 * 中间件jar包 导入, 接收对象
 *
 * @author liuchuan
 * @date 2021/4/26 11:00 上午
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImportMiddlewareJarVO extends MiddlewareJarEntity {

    /**
     * 父类实例为入参的构造函数
     *
     * @param parent 父类实例
     */
    public ImportMiddlewareJarVO(MiddlewareJarEntity parent) {
        BeanUtil.copyProperties(parent, this);
    }

    @Excel(name = "状态", orderNum = "6")
    private String statusDesc;

}
