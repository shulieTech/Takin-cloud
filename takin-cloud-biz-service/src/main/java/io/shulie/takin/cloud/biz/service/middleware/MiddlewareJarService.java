package io.shulie.takin.cloud.biz.service.middleware;

import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

/**
 * 中间件包表(MiddlewareJar)表服务接口
 *
 * @author liuchuan
 * @since 2021-06-01 11:07:08
 */
public interface MiddlewareJarService {

    /**
     * 中间件jar 导入
     *
     * @param file excel 文件
     * @return 处理的数据相关备注
     */
    Workbook importMiddlewareJar(MultipartFile file);

    /**
     * 中间件jar 比对
     *
     * @param files excel 文件列表
     * @return 处理的数据相关备注
     */
    Workbook compareMiddlewareJar(List<MultipartFile> files);

}
