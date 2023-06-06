package io.shulie.takin.cloud.data.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.shulie.takin.cloud.sdk.model.ScriptNodeSummaryBean;

import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author xjz@io.shulie
 * @date 2023/5/19
 * @desc 报告脚本节点本地缓存工具类
 */
public class ReportScriptNodeLocalCache {
    
    private ReportScriptNodeLocalCache(){}
    
    private  static final Cache<Long, List<ScriptNodeSummaryBean>> CACHE ;
    
     static {
         // 设置写入10分钟后过期
        CACHE = Caffeine.newBuilder().expireAfterAccess(1,TimeUnit.HOURS)
                // 缓存初始化数量
                .initialCapacity(100)
                // 缓存最大数量
                .maximumSize(10000)
                .build();
    }


    /**
     * 设置缓存
     * @param key 缓存key
     * @param obj 缓存值
     */
    public static void setCache(Long key, List<ScriptNodeSummaryBean> obj){
        CACHE.put(key, obj);
    }

    /**
     * 缓存是否存在
     * @param key 缓存key
     * @return 缓存只
     */
    public static boolean exit(Long key){
         return CACHE.getIfPresent(key) != null;
    }

    /**
     * 获取缓存
     * @param key 缓存key
     * @return 缓存值
     */
    public static List<ScriptNodeSummaryBean> getCache(Long key){
        return CACHE.getIfPresent(key);
    }

    /**
     * 删除缓存
     * @param key 缓存key
     */
    public static void delCache(Long key){
        CACHE.invalidate(key);
    }
}
