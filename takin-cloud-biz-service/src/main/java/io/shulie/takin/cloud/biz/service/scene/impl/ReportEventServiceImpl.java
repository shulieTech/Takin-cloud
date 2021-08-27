package io.shulie.takin.cloud.biz.service.scene.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import io.shulie.takin.cloud.common.bean.collector.Metrics;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.shulie.takin.cloud.biz.service.scene.ReportEventService;

/**
 * @ClassName ReportEventServiceImpl
 * @Description
 * @Author qianshui
 * @Date 2020/7/20 下午4:23
 */
@Service
@Slf4j
public class ReportEventServiceImpl implements ReportEventService {

    private static final List<Integer> indexs = Arrays.asList(99, 95, 90, 75, 50);
    private static final String PERCENTAGE = "%";
    private static final String MS = "ms";
    @Autowired
    private InfluxWriter influxWriter;

    @Override
    public Map<String, String> queryAndCalcRtDistribute(String tableName, String bindRef) {
        StringBuffer sql = new StringBuffer();
        sql.append("select avg_rt as avgRt from ");
        sql.append(tableName);
        sql.append(" where transaction='");
        sql.append(bindRef);
        sql.append("'");
        List<Metrics> dataList = influxWriter.query(sql.toString(), Metrics.class);
        if (CollectionUtils.isEmpty(dataList)) {
            return null;
        }
        dataList.sort(((o1, o2) -> {
            //modify by lipeng 20210711 添加等于的情况 否则jdk1.7+之后会出现错误 
            //java.lang.IllegalArgumentException: Comparison method violates its general contract!
            //详细参见 https://www.cnblogs.com/firstdream/p/7204067.html
            return o1.getAvgRt().compareTo(o2.getAvgRt());
            //modify end
        }));
        int size = dataList.size();
        Map<String, String> resultMap = Maps.newLinkedHashMap();
        indexs.forEach(index -> {
            resultMap.put(index + PERCENTAGE, dataList.get(calcIndex(size, index)).getAvgRt() + MS);
        });
        return resultMap;
    }

    private int calcIndex(int size, int percentage) {
        if (percentage <= 0) {
            return 0;
        }
        if (percentage >= 100) {
            return size - 1;
        }
        BigDecimal b1 = new BigDecimal(size);
        BigDecimal b2 = new BigDecimal(percentage);
        int index = b1.multiply(b2.divide(new BigDecimal(100))).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        if (index >= size) {
            index = size - 1;
        }
        return index;
    }
}
