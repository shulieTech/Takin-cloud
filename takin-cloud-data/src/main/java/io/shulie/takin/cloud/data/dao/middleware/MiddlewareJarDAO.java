package io.shulie.takin.cloud.data.dao.middleware;

import java.util.List;

import io.shulie.takin.cloud.data.model.mysql.MiddlewareJarEntity;

/**
 * 中间件包表(MiddlewareJar)表数据库 dao
 *
 * @author liuchuan
 * @since 2021-06-01 10:41:18
 */
public interface MiddlewareJarDAO {

    /**
     * 批量创建
     *
     * @param createParams 创建参数
     * @return 是否成功
     */
    boolean saveBatch(List<MiddlewareJarEntity> createParams);

    /**
     * 通过 avgList 批量删除
     *
     * @param agvList artifactId_groupId_version 列表
     * @return 操作结果
     */
    boolean removeByAgvList(List<String> agvList);

    /**
     * 通过 artifactIds 查询列表
     *
     * @param artifactIds artifactIds
     * @return 列表
     */
    List<MiddlewareJarEntity> listByArtifactIds(List<String> artifactIds);

}

