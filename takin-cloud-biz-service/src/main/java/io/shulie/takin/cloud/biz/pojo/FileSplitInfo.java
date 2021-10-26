/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.cloud.biz.pojo;

import io.shulie.takin.cloud.common.pojo.AbstractEntry;
import lombok.Data;

/**
 * 文件分割信息
 * @Author: liyuanba
 * @Date: 2021/10/12 5:28 下午
 */
@Data
public class FileSplitInfo extends AbstractEntry {
    private Long start;
    private Long end;

    public FileSplitInfo() {
    }

    public FileSplitInfo(Long start, Long end) {
        this.start = start;
        this.end = end;
    }
}
