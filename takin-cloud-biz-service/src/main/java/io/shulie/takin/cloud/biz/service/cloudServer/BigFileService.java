package io.shulie.takin.cloud.biz.service.cloudServer;

import java.io.File;

import com.pamirs.takin.entity.domain.vo.file.Part;
import io.shulie.takin.common.beans.response.ResponseResult;

/**
 * @Author: mubai
 * @Date: 2020-05-12 14:49
 * @Description:
 */
public interface BigFileService {

    /**
     * 上传文件
     * @param dto 数据传输对象
     * @return
     */
    ResponseResult upload(Part dto);

    /**
     * 整合文件形成大文件
     * @param param 参数功能
     * @return
     */
    ResponseResult compact(Part param);

    File getPradarUploadFile();


}
