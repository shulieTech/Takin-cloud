package com.pamirs.takin.entity.domain.dto.report;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * @Author 莫问
 * @Date 2020-04-21
 */
@Data
public class SceneActionDTO implements Serializable {

    /**
     *
     */
    private Long data;

    /**
     *
     */
    private List<String> msg;

}
