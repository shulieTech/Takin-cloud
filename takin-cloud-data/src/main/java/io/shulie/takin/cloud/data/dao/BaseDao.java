package io.shulie.takin.cloud.data.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.shulie.takin.cloud.common.constants.PageHelperConstants;

/**
 * @Author: liyuanba
 * @Date: 2021/12/29 10:00 上午
 */
public class BaseDao {
    /**
     * 初始化page的当前页和每页返回的记录数
     */
    public void initPage(Page<?> page) {
        if (page.getCurrent() <= 0) {
            page.setCurrent(1L);
        }
        if (page.getCurrent() > PageHelperConstants.MAX_PAGE) {
            page.setCurrent(PageHelperConstants.MAX_PAGE);
        }
        if (page.getSize() <= 0) {
            page.setSize(PageHelperConstants.DEFAULT_PAGE_SIZE);
        }
        if (page.getSize() > PageHelperConstants.MAX_PAGE_SIZE) {
            page.setSize(PageHelperConstants.MAX_PAGE_SIZE);
        }
    }
}
