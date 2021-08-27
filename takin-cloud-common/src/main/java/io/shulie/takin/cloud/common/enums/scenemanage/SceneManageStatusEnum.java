package io.shulie.takin.cloud.common.enums.scenemanage;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @ClassName SceneManageStatusEnum
 * @Description
 * @Author qianshui
 * @Date 2020/4/27 上午11:20
 */
public enum SceneManageStatusEnum {

    WAIT(0, "待启动"),
    JOB_CREATEING(3, "job创建中"),
    PRESSURE_NODE_RUNNING(4, "压力节点工作中"),
    ENGINE_RUNNING(5, "压测引擎已启动"),
    STARTING(1, "启动中"),
    PTING(2, "压测中"),
    STOP(6, "压测引擎停止压测"),
    //特殊情况
    FILESPLIT_RUNNING(7, "文件拆分中"),
    FILESPLIT_END(8, "文件拆分完成"),
    FAILED(9,"压测失败"),
    // 强制停止两个都停止
    FORCE_STOP(10,"强制停止")
    ;

    /**
     * 待启动
     * @return
     */
    public static SceneManageStatusEnum[] getAll() {
        return SceneManageStatusEnum.values();
    }

    /**
     * 待启动
     * @return
     */
    public static List<SceneManageStatusEnum> getFree() {
        List<SceneManageStatusEnum> free = Lists.newArrayList();
        free.add(WAIT);
        free.add(FAILED);
        free.add(FORCE_STOP);
        return free;
    }

    /**
     * 启动中
     * @return
     */
    public static List<SceneManageStatusEnum> getStarting() {
        List<SceneManageStatusEnum> starting = Lists.newArrayList();
        starting.add(STARTING);
        starting.add(JOB_CREATEING);
        starting.add(PRESSURE_NODE_RUNNING);
        starting.add(FILESPLIT_RUNNING);
        starting.add(FILESPLIT_END);
        return starting;
    }

    /**
     * 压测中
     * @return
     */
    public static List<SceneManageStatusEnum> getWorking() {
       List<SceneManageStatusEnum> working = Lists.newArrayList();
       working.add(ENGINE_RUNNING);
       working.add(PTING);
       working.add(STOP);
       return working;
    }

    private Integer value;

    private String desc;

    SceneManageStatusEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public Integer getValue() {
        return this.value;
    }

    public String getDesc() {
        return this.desc;
    }

    public static SceneManageStatusEnum getSceneManageStatusEnum(Integer status) {
        for(SceneManageStatusEnum statusEnum:values()) {
            if(status.equals(statusEnum.getValue())) {
                return statusEnum;
            }
        }
        return null;
    }

    public static Integer getAdaptStatus (Integer status) {
        SceneManageStatusEnum statusEnum = getSceneManageStatusEnum(status);
        if(SceneManageStatusEnum.getFree().contains(statusEnum)) {
            return WAIT.getValue();
        }
        if(SceneManageStatusEnum.getStarting().contains(statusEnum)) {
            return STARTING.getValue();
        }

        if(SceneManageStatusEnum.getWorking().contains(statusEnum)) {
            return PTING.getValue();
        }

        return statusEnum.getValue();
    }
    public static Boolean ifStop (Integer status) {
        SceneManageStatusEnum statusEnum = getSceneManageStatusEnum(status);
        if(SceneManageStatusEnum.getFree().contains(statusEnum) || SceneManageStatusEnum.getStarting().contains(statusEnum)) {
            return true;
        }
        return false;
    }

    public static Boolean ifFinished(Integer status) {
        SceneManageStatusEnum statusEnum = getSceneManageStatusEnum(status);
        if (SceneManageStatusEnum.getFree().contains(statusEnum) || SceneManageStatusEnum.STOP == statusEnum) {
            return true;
        }
        return false;
    }

    public static Boolean ifFree(Integer status) {
        SceneManageStatusEnum statusEnum = getSceneManageStatusEnum(status);
        if (SceneManageStatusEnum.getFree().contains(statusEnum)) {
            return true;
        }
        return false;
    }
}
