package io.shulie.takin.cloud.common.enums.engine;

/**
 * @author 何仲奇
 * @date 2020/9/23 3:01 下午
 */
public enum EngineStatusEnum {
    STARTED("启动成功","started"),
    START_FAILED( "启动失败","startFail"),
    INTERRUPT( "中断","interrupt"),
    INTERRUPT_SUCCESSED("中断成功","interruptSuccess"),
    INTERRUPT_FAILED("中断失败","interruptFail");


    private String status;
    private String message;

    EngineStatusEnum(String message,String status) {
        this.message = message;
        this.status = status;
    }

    public  String getStatus() {
        return status;
    }


    public String getMessage() {
        return message;
    }

    public static EngineStatusEnum getEngineStatusEnum(String status) {
        for(EngineStatusEnum statusEnum:values()) {
            if(status.equals(statusEnum.getStatus())) {
                return statusEnum;
            }
        }
        return null;
    }

}
