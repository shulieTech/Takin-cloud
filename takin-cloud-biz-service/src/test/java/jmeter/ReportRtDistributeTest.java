package jmeter;

import com.google.common.collect.Maps;
import io.shulie.takin.cloud.biz.output.statistics.RtDataOutput;
import io.shulie.takin.cloud.biz.service.scene.impl.ReportEventServiceImpl;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ClassName:    ReportRtdistributeTest
 * Package:    jmeter
 * Description: 测试Rt分布
 * Datetime:    2022/5/27   17:45
 * Author:   chenhongqiao@shulie.com
 */
public class ReportRtDistributeTest {
    private static final List<Integer> INDEXS = Arrays.asList(99, 95, 90, 75, 50);
    private static final String PERCENTAGE = "%";
    private static final String MS = "ms";

    public static void main(String[] args) {
        List<String> percentDataList = new ArrayList<>();
        try {
            String path = "/Users/phine/Downloads/sf.txt";
            BufferedReader br = new BufferedReader(new FileReader(new File(path)));
            String line = "";
            while (StringUtils.isNotBlank(line = br.readLine())) {
                percentDataList.add(line);
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Map<Integer, RtDataOutput> percentMap = ReportEventServiceImpl.calcRtDistribution(ReportEventServiceImpl.resolvingPercentData(percentDataList));
        Map<String, String> resultMap = Maps.newLinkedHashMap();
        INDEXS.forEach(percent -> {
            resultMap.put(percent + PERCENTAGE, percentMap.get(percent).getTime() + MS);
            System.out.println(String.format("%s:%s", percent + PERCENTAGE, percentMap.get(percent).getTime() + MS));
        });

    }
}
