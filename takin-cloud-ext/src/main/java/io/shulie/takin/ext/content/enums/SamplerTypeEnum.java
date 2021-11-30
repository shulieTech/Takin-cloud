package io.shulie.takin.ext.content.enums;

import lombok.Getter;
import lombok.Setter;

public enum SamplerTypeEnum {

    /**
     * http请求
     */
    HTTP("HTTP", RpcTypeEnum.HTTP),
    /**
     * dubbo请求
     */
    DUBBO("DUBBO", RpcTypeEnum.DUBBO),
    //    /**
    //     * ROCKETMQ请求
    //     */
    //    ROCKETMQ("ROCKETMQ"),
    //    RABBITMQ("RABBITMQ"),
    /**
     * KAFKA 请求
     */
    KAFKA("KAFKA", RpcTypeEnum.MQ),

    /**
     * JDBC 请求
     */
    JDBC("JDBC", RpcTypeEnum.DB),
    /**
     * 未知请求类型
     */
    UNKNOWN("UNKNOWN", RpcTypeEnum.UNKNOWN);

    SamplerTypeEnum(String type, RpcTypeEnum rpcTypeEnum) {
        this.type = type;
        this.rpcTypeEnum = rpcTypeEnum;
    }

    @Getter
    @Setter
    private String type;

    @Getter
    @Setter
    private RpcTypeEnum rpcTypeEnum;
}
