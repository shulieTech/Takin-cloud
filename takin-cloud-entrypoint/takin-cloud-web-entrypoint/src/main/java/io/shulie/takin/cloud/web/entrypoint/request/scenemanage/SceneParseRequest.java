package io.shulie.takin.cloud.web.entrypoint.request.scenemanage;


import lombok.Data;

/**
 * @author qianshui
 * @date 2020/5/15 下午4:13
 */
@Data
public class SceneParseRequest {

    private Long scriptId;

    private String uploadPath;
}
