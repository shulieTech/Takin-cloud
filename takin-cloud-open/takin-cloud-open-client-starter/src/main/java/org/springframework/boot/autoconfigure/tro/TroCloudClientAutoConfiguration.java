package org.springframework.boot.autoconfigure.tro;

import org.springframework.boot.autoconfigure.tro.properties.TroCloudClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author shiyajian
 * create: 2020-09-25
 */
@Configuration
@EnableConfigurationProperties(TroCloudClientProperties.class)
public class TroCloudClientAutoConfiguration {

}
