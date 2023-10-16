package io.shulie.takin.cloud.model.resource;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 资源
 *
 * @author <a href="mailto:472546172@qq.com">张天赐</a>
 */
@Data
@Accessors(chain = true)
public class Resource {
    /**
     * CPU
     * <p>1代表一个逻辑核</p>
     * <p>0.1代表100微核</p>
     */
    Double cpu;
    /**
     * 内存
     * <p>1代码1Kb</p>
     */
    Long memory;
    /**
     * 资源类型
     */
    String type;
    /**
     * 名称
     */
    String name;
    /**
     * nfs服务端地址
     */
    private String nfsServer;
    /**
     * nfs服务端文件夹
     */
    private String nfsDir;

    private String ptlLogServer;

    /**
     * nfs已使用容量
     */
    private Long nfsUsableSpace;
    /**
     * nfs总容量
     */
    private Long nfsTotalSpace;
}
