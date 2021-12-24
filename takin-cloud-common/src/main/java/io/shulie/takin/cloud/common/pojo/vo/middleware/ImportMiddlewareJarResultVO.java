package io.shulie.takin.cloud.common.pojo.vo.middleware;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.hutool.core.bean.BeanUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 中间件导入结果类
 *
 * @author liuchuan
 * @date 2021/6/1 4:22 下午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImportMiddlewareJarResultVO extends ImportMiddlewareJarVO {

    /**
     * 父类实例为入参的构造函数
     *
     * @param parent 父类实例
     */
    public ImportMiddlewareJarResultVO(ImportMiddlewareJarVO parent) {
        BeanUtil.copyProperties(parent, this);
    }
    @Excel(name = "备注信息", orderNum = "7")
    private String remark;

}
