package com.pamirs.takin.entity.domain.vo.scenemanage;

import lombok.Data;

/**
 * @author 何仲奇
 * TODO 新增一张表，用于记录启动记录
 * @date 2020/9/24 9:57 上午
 */
@Data
public class SceneManageStartRecordVO {
    /**
     * 任务ID
     */
    private Long resultId;

    /**
     * 场景ID
     */
    private Long sceneId;

    /**
     * 客户Id 新增
     */
    private Long customerId;

    private Boolean success;

    private String errorMsg;

    public SceneManageStartRecordVO(Long resultId, Long sceneId, Long customerId, Boolean success,
        String errorMsg) {
        this.resultId = resultId;
        this.sceneId = sceneId;
        this.customerId = customerId;
        this.success = success;
        this.errorMsg = errorMsg;
    }

    /**
     * create Builder method
     **/
    public static SceneManageStartRecordVO.Builder build(Long sceneId, Long resultId, Long customerId) {
        return new SceneManageStartRecordVO.Builder(sceneId, resultId, customerId);
    }

    public static class Builder {
        private Long resultId;
        private Long sceneId;
        private Long customerId;
        private Boolean success;
        private String errorMsg;

        Builder(Long sceneId, Long resultId, Long customerId) {
            this.sceneId = sceneId;
            this.resultId = resultId;
            this.customerId = customerId;
        }

        public SceneManageStartRecordVO.Builder success(Boolean success) {
            this.success = success;
            return this;
        }

        public SceneManageStartRecordVO.Builder errorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return this;
        }

        public SceneManageStartRecordVO build() {
            return new SceneManageStartRecordVO(resultId, sceneId, customerId, success, errorMsg);
        }
    }

}
