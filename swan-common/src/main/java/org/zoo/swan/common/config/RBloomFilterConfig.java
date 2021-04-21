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

package org.zoo.swan.common.config;

import lombok.Data;

/**
 * The RBloomFilterConfig.
 *
 * @author dzc
 */
@Data
public class RBloomFilterConfig {

	/**
	 * 布隆过滤器名称
	 * */
    private String name = "RBLOOMNAME";
    
    /**
     * 布隆过滤器预计统计总数
     * */
	private Long totalNum = 100000000L;
	
	/**
     * 期望误差率
     * */
	private Double errorRate = 0.00001;
   
}
