package org.springframework.boot.autoconfigure.tro.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-09-25
 */
@ConfigurationProperties(prefix = "takin.cloud")
@Component
@Data
public class TroCloudClientProperties {

    private String url;

}
