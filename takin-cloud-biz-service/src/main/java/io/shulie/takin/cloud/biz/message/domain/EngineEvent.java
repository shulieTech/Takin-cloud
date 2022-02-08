/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.cloud.biz.message.domain;

import lombok.Getter;

/**
 * @Author: liyuanba
 * @Date: 2022/1/28 9:30 上午
 */
public enum EngineEvent {
    /**
     * 启动失败
     */
    START_FAILED(3),
    /**
     * 启动压测
     */
    TEST_START(1),
    /**
     * 停止压测
     */
    TEST_END(2),
    ;
    @Getter
    private int code;
    EngineEvent(int code) {
        this.code = code;
    }
}
