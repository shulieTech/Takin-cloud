package io.shulie.takin.cloud.entrypoint.controller.middleware;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import io.shulie.takin.cloud.biz.service.middleware.MiddlewareJarService;
import io.shulie.takin.cloud.sdk.constant.EntrypointUrl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 中间件包表(MiddlewareJar)表控制层
 *
 * @author liuchuan
 * @since 2021-06-01 10:58:08
 */
@RestController
@Api(tags = "接口: 中间件jar包")
@RequestMapping(EntrypointUrl.BASIC + "/" + EntrypointUrl.MODULE_MIDDLEWARE_JAR)
public class MiddlewareJarController {

    @Resource(type = MiddlewareJarService.class)
    private MiddlewareJarService middlewareJarService;

    @ApiOperation("|_ 导入")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "file", value = "导入文件 excel", required = true,
            dataType = "file", paramType = "form")
    })
    @PostMapping(EntrypointUrl.METHOD_MODULE_MIDDLEWARE_JAR_IMPORT)
    public void importMiddlewareJar(@RequestParam MultipartFile file, HttpServletResponse response) throws IOException {
        Workbook workbook = middlewareJarService.importMiddlewareJar(file);
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的名称
        response.setHeader("Content-Disposition", "attachment;filename=importResult.xlsx");
        workbook.write(response.getOutputStream());
    }

    @ApiOperation("|_ 比对")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "files", value = "导入文件 excel 列表形式", required = true,
            dataType = "file", paramType = "form")
    })
    @PostMapping(EntrypointUrl.METHOD_MODULE_MIDDLEWARE_JAR_COMPARE)
    public void compareMiddlewareJar(@RequestParam List<MultipartFile> files, HttpServletResponse response) throws IOException {
        Workbook workbook = middlewareJarService.compareMiddlewareJar(files);
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的名称
        response.setHeader("Content-Disposition", "attachment;filename=compareResult.xlsx");
        workbook.write(response.getOutputStream());
    }

}
