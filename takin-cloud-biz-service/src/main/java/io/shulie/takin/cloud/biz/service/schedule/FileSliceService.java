package io.shulie.takin.cloud.biz.service.schedule;

import com.pamirs.takin.entity.domain.vo.file.FileSliceRequest;
import io.shulie.takin.cloud.data.model.mysql.SceneBigFileSliceEntity;
import io.shulie.takin.cloud.data.param.scenemanage.SceneBigFileSliceParam;

/**
 * @author moriarty
 */
public interface FileSliceService {

    String IS_SPLIT = "isSplit";

    String IS_ORDER_SPLIT = "isOrderSplit";

    /**
     * 大文件分片
     *  文件分片分两种情况：1. 根据场景要启动的pod数量分片，这种情况不保证文件中数据的顺序
     *                   2. 根据文件中指定列的顺序拆分，这种情况耗时较长，需要逐行处理，而且要求文件顺序正确，不能存在内容的穿插
     * @param request
     * @return -
     */
    boolean fileSlice(FileSliceRequest request);

    /**
     * 查询文件分片信息
     * @param request
     * @return -
     */
    SceneBigFileSliceEntity getOneByParam(FileSliceRequest request);

    /**
     * 查询文件分片状态
     * @param request
     * @return -
     */
    Integer isFileSliced(FileSliceRequest request);

    /**
     * 更新SceneScriptRef
     * @param request
     * @return -
     */
    Boolean updateFileRefExtend(FileSliceRequest request);

    void asyncSliceFile(FileSliceRequest request);

    void preSlice(SceneBigFileSliceParam param);
}
