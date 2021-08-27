package io.shulie.takin.cloud.data.result.scenemanage;

import java.io.Serializable;

import io.shulie.takin.cloud.common.enums.machine.EnumResult;
import lombok.Data;

/**
 * @ClassName ScrpitDetailDTO
 * @Description
 * @Author qianshui
 * @Date 2020/5/18 下午11:42
 */
@Data
public class ScriptDetailResult implements Serializable {

    private static final long serialVersionUID = 2391812420921319265L;

    private String fileName;

    private String uploadTime;

    private EnumResult fileType;

    private Long uploadedData;

    private EnumResult isSplit;
}
