package io.shulie.takin.cloud.common.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author moriarty
 * @description 大文件分片
 */
public class FileSliceByLine {
    private static final Logger logger = LoggerFactory.getLogger(FileSliceByLine.class);

    private String filePath;
    private String separator;
    private Long prePosition;
    private Map<String, Integer> partitionMap;
    private Integer nextPartitionNum;
    private Map<Integer, FileSliceInfo> fileSliceInfoMap;
    private Integer orderColumnNum;

    private FileSliceByLine(String filePath, String separator, Integer columnNum) {
        this.filePath = filePath;
        this.separator = separator;
        this.fileSliceInfoMap = new TreeMap<>();
        this.prePosition = 0L;
        this.partitionMap = new HashMap<>();
        this.nextPartitionNum = 0;
        this.orderColumnNum = columnNum;
    }

    public Map<Integer, FileSliceInfo> sliceFile() throws TakinCloudException {
        BufferedReader reader = null;
        try {
            String fileEncoder = "UTF-8";
            reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(this.filePath), fileEncoder));
            String line;
            String partition;
            while ((line = reader.readLine()) != null) {
                String[] contents = line.split(this.separator);
                //根据用户输入的列号来进行排序，如果为空，则默认最后一列
                if (orderColumnNum == null || contents.length - 1 > orderColumnNum) {
                    partition = contents[contents.length - 1];
                } else {
                    partition = contents[orderColumnNum];
                }
                Integer partitionNum = partitionMap.get(partition);
                if (partitionNum != null) {
                    FileSliceInfo fileSliceInfo = this.fileSliceInfoMap.get(partitionNum);
                    fileSliceInfo.setEnd(fileSliceInfo.getEnd() + line.getBytes().length + 1);
                    this.fileSliceInfoMap.put(partitionNum, fileSliceInfo);
                    prePosition = fileSliceInfo.end;
                } else {
                    partitionMap.put(partition, nextPartitionNum);
                    FileSliceInfo fileSliceInfo = new FileSliceInfo();
                    fileSliceInfo.setPartition(nextPartitionNum);
                    fileSliceInfo.setStart(prePosition);
                    fileSliceInfo.setEnd(prePosition + line.getBytes().length + 1);
                    fileSliceInfoMap.put(nextPartitionNum, fileSliceInfo);
                    prePosition = prePosition + line.getBytes().length;
                    nextPartitionNum++;
                }
            }
            Map<Integer, FileSliceInfo> resultMap = new HashMap<>();
            FileSliceInfo sliceInfo;
            for (Map.Entry<Integer, FileSliceInfo> entry : fileSliceInfoMap.entrySet()) {
                sliceInfo = new FileSliceInfo() {{
                    setPartition(entry.getValue().getPartition());
                    setStart(entry.getValue().getStart());
                    setEnd(entry.getValue().getEnd() - 1);
                }};
                resultMap.put(entry.getKey(), sliceInfo);
            }
            return resultMap;
        } catch (Exception e) {
            throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_CSV_FILE_SPLIT_ERROR,e.getMessage(),e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                logger.error("异常代码【{}】,异常内容：文件关闭异常 --> 异常信息: {}",
                        TakinCloudExceptionEnum.FILE_CLOSE_ERROR,ex);
            }
        }
    }

    public static class Builder {
        private String filePath;
        private String separator;
        private Integer columnNum;

        public Builder(String filepath) {
            this.filePath = filepath;
            File file = new File(this.filePath);
            if (!file.exists()) {
                throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_CSV_FILE_SPLIT_ERROR,"文件不存在！filepath:[" + filepath + "]");
            }
        }

        public Builder withSeparator(String separator) {
            this.separator = separator;
            return this;
        }

        public Builder withOrderColumnNum(Integer columnNum) {
            this.columnNum = columnNum;
            return this;
        }

        public FileSliceByLine build() {
            return new FileSliceByLine(this.filePath, this.separator, this.columnNum);
        }
    }

    @Data
    public static class FileSliceInfo {
        private Integer partition;
        private Long start;
        private Long end;
    }

    public static void main(String[] args) {
        String filePath = "/Users/moriarty/Desktop/OrderInfo_02.csv";
        FileSliceByLine fileSliceUtil = new FileSliceByLine(filePath, ",", null);
        Long start = System.currentTimeMillis();
        System.out.println(start);
        Map<Integer, FileSliceInfo> stringFileSliceInfoMap = fileSliceUtil.sliceFile();
        JSONObject.toJSONString(stringFileSliceInfoMap);
        System.out.println(System.currentTimeMillis() - start);
        String s = JSONObject.toJSONString(stringFileSliceInfoMap);
        System.out.println(s);

        //for (Map.Entry<String, FileSliceInfo> entry : stringFileSliceInfoMap.entrySet()) {
        //    System.out.printf("topic:%s,start:%s,end:%s\n", entry.getKey(), entry.getValue().getStartPosition(),
        //        entry.getValue().getEndPosition());
        //}
        //FileSliceInfo sliceInfo = stringFileSliceInfoMap.get("2");
        //FileConsumer consumer = new FileConsumer(filePath,1024*1024,sliceInfo,"utf-8");
        //consumer.consumer();
    }
}
