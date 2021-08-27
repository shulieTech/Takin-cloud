package com.pamirs.takin.entity.domain.query;

import java.io.Serializable;

import io.shulie.takin.common.beans.page.PagingDevice;
import lombok.Data;

/**
 * @Author: mubai
 * @Date: 2020-05-11 11:35
 * @Description:
 */

@Data
public class CloudAccountQueryParam extends PagingDevice implements Serializable {
    private static final long serialVersionUID = 7433678857499987225L;

    /**
     * 平台id
     */
    private Long platformId;

}
