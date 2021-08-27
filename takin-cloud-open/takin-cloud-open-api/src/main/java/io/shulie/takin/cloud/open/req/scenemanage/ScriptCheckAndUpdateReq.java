package io.shulie.takin.cloud.open.req.scenemanage;

import java.io.Serializable;
import java.util.List;

import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @author 无涯
 * @Package io.shulie.takin.cloud.open.bean.scenemanage
 * @description:
 * @date 2020/10/22 8:16 下午
 */
@Data
public class ScriptCheckAndUpdateReq extends CloudUserCommonRequestExt implements Serializable {

    private static final long serialVersionUID = 33734315777916535L;

    /**
     * 业务请求
     */
    private List<String> request;

    /**
     * 脚本路径
     */
    private String uploadPath;


    private boolean absolutePath;

    /**
     * 是否更新脚本
     */
    private boolean update;
}
