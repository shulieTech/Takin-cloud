package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @Author: mubai
 * @Date: 2020-05-11 11:34
 * @Description:
 */

@Data
public class CloudPlatformQueryParam extends PagingDevice implements Serializable {
    private static final long serialVersionUID = -3799724100009834088L;

    private Long id;

    private String name;

    private Boolean status;

    private String jarName;

}
