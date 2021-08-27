package io.shulie.takin.cloud.data.mapper.mysql;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.cloud.data.model.mysql.MiddlewareJarEntity;
import org.apache.ibatis.annotations.Param;

/**
 * 中间件包表(MiddlewareJar)表数据库 mapper
 *
 * @author liuchuan
 * @since 2021-06-01 10:41:18
 */
public interface MiddlewareJarMapper extends BaseMapper<MiddlewareJarEntity> {

    /**
     * 批量创建
     *
     * @param entities 实体对象列表
     * @return 插入行数
     */
    int insertBatch(@Param("entities") List<MiddlewareJarEntity> entities);

    /**
     * 通过 avgList 批量删除
     *
     * @param agvList artifactId_groupId_version 列表
     * @return 影响行数
     */
    int deleteByAgvList(@Param("agvList") List<String> agvList);

}

