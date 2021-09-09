package io.shulie.takin.cloud.web.entrypoint.controller.strategy;

import java.util.List;

import javax.validation.Valid;

import com.pamirs.takin.entity.domain.dto.strategy.StrategyConfigDetailDTO;
import com.pamirs.takin.entity.domain.vo.strategy.StrategyConfigAddVO;
import com.pamirs.takin.entity.domain.vo.strategy.StrategyConfigQueryVO;
import com.pamirs.takin.entity.domain.vo.strategy.StrategyConfigUpdateVO;
import io.shulie.takin.cloud.biz.service.strategy.StrategyConfigService;
import io.shulie.takin.cloud.web.entrypoint.request.scenemanage.SceneManageDeleteRequest;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.ext.content.enginecall.StrategyConfigExt;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qianshui
 * @date 2020/5/9 下午3:27
 */
@RestController
@RequestMapping("/api/strategyconfig")
@Api(tags = "策略配置管理")
public class StrategyConfigController {

    @Autowired
    private StrategyConfigService strategyConfigService;

    @PostMapping
    @ApiOperation(value = "新增分配策略")
    public ResponseResult<?> add(@RequestBody @Valid StrategyConfigAddVO addVO) {
        return ResponseResult.success(strategyConfigService.add(addVO));
    }

    @PutMapping
    @ApiOperation(value = "修改分配策略")
    public ResponseResult<?> update(@RequestBody @Valid StrategyConfigUpdateVO updateVO) {
        return ResponseResult.success(strategyConfigService.update(updateVO));
    }

    @GetMapping
    @ApiOperation(value = "分配策略详情")
    public ResponseResult<StrategyConfigDetailDTO> getDetail(@RequestParam(value = "id") Long id) {
        return ResponseResult.success(strategyConfigService.getDetail(id));
    }

    @DeleteMapping
    @ApiOperation(value = "删除分配策略")
    public ResponseResult<?> delete(@RequestBody @Valid SceneManageDeleteRequest deleteVO) {
        strategyConfigService.delete(deleteVO.getId());
        return ResponseResult.success();
    }

    @GetMapping("/list")
    @ApiOperation(value = "分配策略列表")
    public ResponseResult<List<StrategyConfigExt>> getList(@ApiParam(name = "current", value = "页码") Integer current,
                                                           @ApiParam(name = "pageSize", value = "页大小") Integer pageSize) {

        /*
         * 1、封装参数
         * 2、调用查询服务
         * 3、返回指定格式
         */
        StrategyConfigQueryVO queryVO = new StrategyConfigQueryVO();
        queryVO.setCurrentPage(current);
        queryVO.setPageSize(pageSize);
        return ResponseResult.success(strategyConfigService.queryPageList(queryVO).getList());
    }
}
