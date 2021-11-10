package io.shulie.takin.cloud.open.resp.report;

import java.io.Serializable;
import java.util.List;

import io.shulie.takin.cloud.common.bean.scenemanage.ScriptNodeSummaryBean;
import io.shulie.takin.ext.content.user.CloudUserCommonRequestExt;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author moriarty
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NodeTreeSummaryResp extends CloudUserCommonRequestExt implements Serializable {
    private List<ScriptNodeSummaryBean> scriptNodeSummaryBeans;
}
