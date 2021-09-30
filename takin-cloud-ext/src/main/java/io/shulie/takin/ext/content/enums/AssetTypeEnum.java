package io.shulie.takin.ext.content.enums;

/**
 * 压测报告、业务活动流量验证、脚本调试、巡检场景(暂时不做)
 * @author caijianying
 */

public enum AssetTypeEnum {
    PRESS_REPORT(1, "压测报告"),
    ACTIVITY_CHECK(2, "业务活动流量验证"),
    SCRIPT_DEBUG(3, "脚本调试"),
    PATRO_SCENE(4, "巡检场景");

    private Integer code;

    private String name;

    AssetTypeEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (AssetTypeEnum e : AssetTypeEnum.values()) {
            if (code.equals(e.getCode())) {
                return e.getName();
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
