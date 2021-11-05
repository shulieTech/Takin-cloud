package io.shulie.takin.ext.content.enums;

import lombok.Getter;
import lombok.Setter;

public enum SamplerTypeEnum {

    /**
     * http请求
     */
    HTTP("HTTP"),
    /**
     * dubbo请求
     */
    DUBBO("DUBBO"),
//    /**
//     * ROCKETMQ请求
//     */
//    ROCKETMQ("ROCKETMQ"),
//    RABBITMQ("RABBITMQ"),
    /**
     * KAFKA 请求
     */
    KAFKA("KAFKA"),

    /**
     * JDBC 请求
     */
    JDBC("JDBC"),
    /**
     * 未知请求类型
     */
    UNKNOWN("UNKNOWN")
    ;


    SamplerTypeEnum(String type){
        this.type = type;
    }

    @Getter
    @Setter
    private String type;

}
