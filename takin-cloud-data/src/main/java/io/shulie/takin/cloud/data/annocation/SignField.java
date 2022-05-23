package io.shulie.takin.cloud.data.annocation;

import java.lang.annotation.*;

/**
 * @Author: 南风
 * @Date: 2022/2/22 5:30 下午
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface SignField {

    int order();
}
