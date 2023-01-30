package io.shulie.takin.cloud.model.request.job.pressure;

import io.shulie.takin.cloud.model.callback.Sla;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SlaNotifyRequest implements Serializable {

    private Long pressureId;

    private List<Sla.SlaInfo> slaEventEntityList;
}
