package com.pamirs.takin.entity.domain.query;


import lombok.Data;
import lombok.EqualsAndHashCode;
import io.shulie.takin.common.beans.page.PagingDevice;

/**
 * @author mubai
 * @date 2020-05-11 11:35
 */

@Data
@EqualsAndHashCode(callSuper = true)
public class CloudAccountQueryParam extends PagingDevice {

    /**
     * 平台id
     */
    private Long platformId;

}
