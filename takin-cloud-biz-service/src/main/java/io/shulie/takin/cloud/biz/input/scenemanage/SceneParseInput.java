package io.shulie.takin.cloud.biz.input.scenemanage;

import java.io.Serializable;

import lombok.Data;

/**
 * @author qianshui
 * @date 2020/5/15 下午4:13
 */
@Data
public class SceneParseInput implements Serializable {

    private static final long serialVersionUID = 33734315777916535L;

    private Long scriptId;

    private String uploadPath;
}
