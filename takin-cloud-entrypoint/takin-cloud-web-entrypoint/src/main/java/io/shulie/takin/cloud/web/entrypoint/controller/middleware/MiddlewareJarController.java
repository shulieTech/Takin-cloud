package io.shulie.takin.cloud.web.entrypoint.controller.middleware;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import io.shulie.takin.cloud.biz.service.middleware.MiddlewareJarService;
import io.shulie.takin.cloud.common.constants.APIUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(APIUrls.TRO_API_URL + "middlewareJar")
@Api(tags = "接口: 中间件jar包")
public class MiddlewareJarController {

    @Autowired
    private MiddlewareJarService middlewareJarService;

    @ApiOperation("|_ 导入")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "file", value = "导入文件 excel", required = true,
            dataType = "file", paramType = "form")
    })
    @PostMapping("/import")
    public void importMJ(@RequestParam MultipartFile file, HttpServletResponse response) throws IOException {
        Workbook workbook = middlewareJarService.importMJ(file);
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
    @PostMapping("/compare")
    public void compareMJ(@RequestParam List<MultipartFile> files, HttpServletResponse response) throws IOException {
        Workbook workbook = middlewareJarService.compareMJ(files);
        response.setHeader("content-Type", "application/vnd.ms-excel");
        // 下载文件的名称
        response.setHeader("Content-Disposition", "attachment;filename=compareResult.xlsx");
        workbook.write(response.getOutputStream());
    }

}
