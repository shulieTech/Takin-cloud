package io.shulie.takin.cloud.common.enums;

import java.util.HashMap;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * @author moriarty
 */
@Getter
@AllArgsConstructor
public enum FileSliceStatusEnum {

    /**
     *
     */
    UNSLICED(0, "未分片"),
    SLICED(1, "已分片"),
    SLICING(2, "拆分中"),
    FILE_CHANGED(3, "文件变更");

    private final int code;
    private final String status;

    private static final HashMap<Integer, FileSliceStatusEnum> INSTANCES = new HashMap<>(4);

    static {
        for (FileSliceStatusEnum e : FileSliceStatusEnum.values()) {
            INSTANCES.put(e.getCode(), e);
        }
    }

    public static FileSliceStatusEnum of(int code) {
        return INSTANCES.get(code);
    }
}
