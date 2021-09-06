package io.shulie.takin.cloud.common.enums;

/**
 * @author 何仲奇
 * @date 2020/9/25 12:56 下午
 */
public enum SceneManageErrorEnum {
    PRESSURE_MEASUREMENT_FAILED("压测失败"),
    SCENEMANAGE__NOT_FIND_SCENE("更新场景生命周期失败，场景不存在"),
    SCENEMANAGE_UPDATE_LIFECYCLE_NOT_FIND_SCENE("更新场景生命周期失败，场景不存在"),
    SCENEMANAGE_UPDATE_LIFECYCLE_UNKNOWN_STATE("更新场景生命周期失败，未知状态"),
    SCENEMANAGE_UPDATE_LIFECYCLE_CHECK_FAILED("更新场景生命周期失败，check状态错误"),
    SCENEMANAGE_UPDATE_LIFECYCLE_FAIL("更新场景生命周期失败，系统运行错误");

    /**
     * 错误信息
     */
    private String errorMessage;

    SceneManageErrorEnum(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
