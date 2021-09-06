package io.shulie.takin.cloud.common.enums.scenemanage;

/**
 * @author fanxx
 * @date 2020/4/20 下午2:36
 */
public enum TaskStatusEnum {
    NOT_START("待检测"),
    STARTED("启动成功"),
    FAILED("启动失败"),
    RUNNING("压测中"),
    FINISHED("压测结束");
    private String status;

    TaskStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
