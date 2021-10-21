package io.shulie.takin.cloud.biz.config;

import io.shulie.takin.cloud.common.enums.deployment.DeploymentMethodEnum;
import io.shulie.takin.cloud.common.utils.CommonUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author liyuanba
 */
@Configuration
@Data
public class AppConfig {

    /**
     * 部署方式
     */
    @Value("${tro.cloud.deployment.method:private}")
    private String deploymentMethod;
    /**
     * 压测引擎版本
     */
    @Value("${pressure.engine.images}")
    private String pressureEngineImage;
    /**
     * 压测引擎名称
     */
    @Value("${pressure.engine.name}")
    private String pressureEngineImageName;
    /**
     * cloud版本
     */
    @Value("${info.app.version}")
    private String cloudVersion;

    @Value("${k8s.jvm.settings:-Xmx4096m -Xms4096m -Xss256K -XX:MaxMetaspaceSize=256m}")
    private String k8sJvmSettings;

    /**
     * 数据收集模式:redis，influxdb
     */
    @Value("${report.data.collector:redis}")
    private String collector;

    public DeploymentMethodEnum getDeploymentMethod() {
        return CommonUtil.getValue(DeploymentMethodEnum.PRIVATE, this.deploymentMethod, DeploymentMethodEnum::valueBy);
    }
}
