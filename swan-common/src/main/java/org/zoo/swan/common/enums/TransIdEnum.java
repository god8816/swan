/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.zoo.swan.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 全局key生成方案
 * @author dzc
 */
@RequiredArgsConstructor
@Getter
public enum TransIdEnum {

    /**
     * UUID.
     */
	UUID("UUID"),

    /**
     * SnowFlake
     */
	SnowId("SnowFlake");

  

    private final String serialize;

    /**
     * ID 生成方案
     */
    public static TransIdEnum acquire(final String serialize) {
        Optional<TransIdEnum> serializeEnum =
                Arrays.stream(TransIdEnum.values())
                        .filter(v -> Objects.equals(v.getSerialize(), serialize))
                        .findFirst();
        return serializeEnum.orElse(TransIdEnum.UUID);
    }

}
