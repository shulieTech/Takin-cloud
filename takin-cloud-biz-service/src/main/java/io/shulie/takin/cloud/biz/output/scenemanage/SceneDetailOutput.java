package io.shulie.takin.cloud.biz.output.scenemanage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import io.shulie.takin.cloud.data.result.scenemanage.BusinessActivityDetailResult;
import io.shulie.takin.cloud.data.result.scenemanage.ScriptDetailResult;
import io.shulie.takin.cloud.data.result.scenemanage.SlaDetailResult;
import io.shulie.takin.cloud.common.enums.machine.EnumResult;
import io.shulie.takin.cloud.common.bean.TimeBean;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;

/**
 * @ClassName SceneDetailResult
 * @Description
 * @Author qianshui
 * @Date 2020/5/18 下午8:26
 */
@Data
public class SceneDetailOutput extends CloudUserCommonRequestExt implements Serializable {

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

    private List<BusinessActivityDetailResult> businessActivityConfig;

    private List<ScriptDetailResult> uploadFile;

    private List<SlaDetailResult> stopCondition;

    private List<SlaDetailResult> warningCondition;

    public static void main(String[] args) {
        SceneDetailOutput dto = new SceneDetailOutput();
        dto.setBusinessActivityConfig(Arrays.asList(new BusinessActivityDetailResult()));
        dto.setUploadFile(Arrays.asList(new ScriptDetailResult()));
        dto.setStopCondition(Arrays.asList(new SlaDetailResult()));
        dto.setWarningCondition(Arrays.asList(new SlaDetailResult()));
        System.out.println(JSON.toJSONString(dto, SerializerFeature.WriteMapNullValue));
    }
}
