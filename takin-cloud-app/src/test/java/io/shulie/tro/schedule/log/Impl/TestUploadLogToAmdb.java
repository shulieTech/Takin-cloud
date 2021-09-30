//package io.shulie.takin.schedule.log.Impl;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.Date;
//import java.util.Objects;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//import javax.annotation.Resource;
//
//import com.alibaba.fastjson.JSONArray;
//
//import com.pamirs.takin.entity.dao.report.TReportMapper;
//import io.shulie.takin.app.Application;
//
//import io.shulie.takin.cloud.biz.task.JmeterLogUploadTask;
//import io.shulie.takin.cloud.biz.utils.FileFetcher;
//import io.shulie.takin.cloud.common.enums.scenemanage.SceneRunTaskStatusEnum;
//import io.shulie.takin.cloud.common.redis.RedisClientUtils;
//import io.shulie.takin.cloud.data.dao.sceneTask.SceneTaskJmeteLogUploadDAO;
//import io.shulie.takin.cloud.data.model.mysql.SceneJmeterlogUploadEntity;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {Application.class})
//public class TestUploadLogToAmdb {
////    @Autowired
////    private PushLogService pushLogService;
////
////    @Autowired
////    private SceneTaskJmeteLogUploadDAO logUploadDAO;
////    @Resource
////    private TReportMapper tReportMapper;
////
////    @Autowired
////    private RedisClientUtils redisClientUtils;
////
////    @Value("${/Users/moriarty/Documents}")
////    private String nfsDir;
////    private static Long position = 0L;
////
////    @Value("${test.boolean.key:false}")
////    private boolean testBoolean;
////
////    @Test
////    public void testPushLog() {
////        String fileName = "/Users/moriarty/Documents/pressure-1625468258826.jtl";
////        System.out.println(System.currentTimeMillis());
////        try {
////            boolean flag = true;
////            FileFetcher fileFetcher = new FileFetcher(new File(fileName));
////            while (flag) {
////                byte[] data = this.readFile(position, fileName, fileFetcher);
////                if (Objects.nonNull(data) && data.length > 0) {
////                    System.out.println(new String(data));
////                        pushLogService.pushLogToAmdb(data, "1.6");
////                    position += data.length;
////                } else {
////                    flag = false;
////                }
////            }
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
////        //pushLogService.pushLogToAmdb(content,"1.6");
////    }
//
//    public byte[] readFile(Long position, String filePath, FileFetcher fileFetcher) throws IOException {
//        File file = new File(filePath);
//        if (!file.exists() || !file.isFile()) {
//            return null;
//        }
//        if (position > file.length()) {
//            return null;
//        }
//        if (fileFetcher == null) {
//            fileFetcher = new FileFetcher(file);
//        }
//        byte[] data = fileFetcher.read(position, 1024 * 1024L);
//        position += data.length;
//        String fileName = file.getName().substring(0, file.getName().lastIndexOf(".jtl"));
//        return data;
//    }
//
//    //@Test
//    //public void getSampling() {
//    //    String sampling = pushLogService.getSampling();
//    //    System.out.println(sampling);
//    //}
//
////    @Test
////    public void testInsertLog() {
////        SceneJmeterlogUploadEntity entity = new SceneJmeterlogUploadEntity() {{
////            setFileName("/Users/moriarty/Documents/shulie/tetFile.jtl");
////            setSceneId(123L);
////            setReportId(1901L);
////            setTenantId(9725L);
////            setTaskStatus(SceneRunTaskStatusEnum.ENDED.getCode());
////            setUploadStatus(2);
////            setCreateTime(new Date());
////            setUploadCount(20880L);
////        }};
////        logUploadDAO.insertRecord(entity);
////    }
////
////    @Test
////    public void testThread() {
////        //new Thread(new JmeterLogUploadTask(117L,1119L,9725L,logUploadDAO,
////        //    redisClientUtils,pushLogService,tReportMapper,nfsDir)).start();
////        Executors.newSingleThreadExecutor().submit((new JmeterLogUploadTask(135L, 1230L, 9725L, logUploadDAO,
////            redisClientUtils, pushLogService, tReportMapper, nfsDir, "pressure-1625468258826.jtl")));
////        try {
////            TimeUnit.SECONDS.sleep(60L);
////        } catch (InterruptedException e) {
////            e.printStackTrace();
////        }
////    }
//
//    @Test
//    public void test001() {
//        System.out.println(testBoolean);
//    }
//
//    @Test
//    public void testTransfer() {
//        String str = "[{end=255084, partition=null, start=0}]";
//
//        JSONArray jsonArray = JSONArray.parseArray(str.replaceAll("=", ":"));
//        for (Object o : jsonArray) {
//            System.out.println(o);
//        }
//
//    }
//
//}
