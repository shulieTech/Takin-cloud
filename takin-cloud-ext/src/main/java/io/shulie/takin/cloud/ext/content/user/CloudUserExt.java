package io.shulie.takin.cloud.ext.content.user;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author by: hezhongqi
 * @date 2021/8/4 11:25
 */
@Data
public class CloudUserExt {
    @ApiModelProperty(name = "id", value = "用户id")
    private Long id;

    @ApiModelProperty(name = "tenantId", value = "租户id")
    private Long tenantId;

    @ApiModelProperty(name = "name", value = "登录账号")
    private String name;
    @ApiModelProperty(name = "nick", value = "用户名称")
    private String nick;
    @ApiModelProperty(name = "key", value = "用户key")
    private String key;
    @ApiModelProperty(name = "password", value = "密码")
    private String password;
    @ApiModelProperty(name = "model", value = "使用模式")
    private Integer model;
    @ApiModelProperty(name = "role", value = "角色")
    private Integer role;
    @ApiModelProperty(name = "status", value = "状态")
    private Integer status;
    @ApiModelProperty(name = "createTime", value = "创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;
    @ApiModelProperty(name = "updateTime", value = "更新时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtUpdate;

    @ApiModelProperty(value = "可用流量")
    private String flowAmount;

    @ApiModelProperty(value = "版本")
    private String version;

    /**
     * 登录渠道
     * 0-console 前端页面
     * 1-web 客户端license
     */
    @ApiModelProperty(value = "登录渠道")
    private Integer loginChannel;


}
