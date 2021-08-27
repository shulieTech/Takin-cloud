package io.shulie.takin.cloud.biz.input.scenemanage;

import java.io.Serializable;

import lombok.Data;

/**
 * @ClassName SceneIpNumInput
 * @Description
 * @Author qianshui
 * @Date 2020/5/15 下午3:28
 */
@Data
public class SceneIpNumInput implements Serializable {

    private static final long serialVersionUID = 5601318389362884272L;

    private Integer concurrenceNum;
}
