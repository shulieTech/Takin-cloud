package io.shulie.takin.cloud.biz.service.statistics;

import java.util.Date;
import java.util.List;

import io.shulie.takin.cloud.sdk.model.response.statistics.FullResponse;
import io.shulie.takin.cloud.sdk.model.response.statistics.FullResponse.TopItem;

/**
 * 全量统计
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
public interface FullStatisticsService {

    /**
     * 全量统计
     *
     * @param startTime 启动时间
     * @param endTime   结束时间
     * @param topNumber 榜单数量
     * @return 统计结果
     */
    FullResponse full(Date startTime, Date endTime, Integer topNumber);

    /**
     * 统计场景信息
     *
     * @param startTime 启动时间
     * @param endTime   结束时间
     * @return -
     */
    FullResponse.Scene fullScene(Date startTime, Date endTime);

    /**
     * 统计报告信息
     *
     * @param startTime 启动时间
     * @param endTime   结束时间
     * @return -
     */
    FullResponse.Report fullReport(Date startTime, Date endTime);

    /**
     * TOP榜单
     *
     * @param number 榜单数量
     * @return -
     */
    List<TopItem> topList(int number, Date startTime, Date endTime);

}
