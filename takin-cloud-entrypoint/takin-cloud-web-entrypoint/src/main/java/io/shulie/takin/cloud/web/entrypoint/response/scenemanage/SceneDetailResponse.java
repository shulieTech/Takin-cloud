package io.shulie.takin.cloud.web.entrypoint.response.scenemanage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import io.shulie.takin.cloud.common.bean.TimeBean;
import io.shulie.takin.cloud.common.enums.machine.EnumResult;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author qianshui
 * @date 2020/5/18 下午8:26
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneDetailResponse extends CloudUserCommonRequestExt implements Serializable {

    private static final long serialVersionUID = 1453217777875591954L;

    private Long id;

    private String sceneName;

    private String updateTime;

    private String lastPtTime;

    private EnumResult status;

    private Integer concurrenceNum;

    private Integer ipNum;

    private TimeBean pressureTestTime;

    private EnumResult pressureMode;

    private TimeBean increasingTime;

    private Integer step;

    private BigDecimal estimateFlow;

    private List<BusinessActivityDetailResponse> businessActivityConfig;

    private List<ScriptDetailResponse> uploadFile;

    private List<SlaDetailResponse> stopCondition;

    private List<SlaDetailResponse> warningCondition;

    @Data
    public static class ScriptDetailResponse implements Serializable {

        private static final long serialVersionUID = 2391812420921319265L;

        private String fileName;

        private String uploadTime;

        private EnumResult fileType;

        private Long uploadedData;

        private EnumResult isSplit;
    }

    @Data
    public static class SlaDetailResponse implements Serializable {

        private static final long serialVersionUID = 9171434959213456889L;

        private String ruleName;

        private String businessActivity;

        private String rule;

        private EnumResult status;
    }
}
