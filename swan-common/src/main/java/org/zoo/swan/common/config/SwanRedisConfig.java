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

import org.redisson.config.ClusterServersConfig;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;

import lombok.Data;

/**
 * The SwanRedisConfig.
 *
 * @author dzc
 */
@Data
public class SwanRedisConfig {

	/**
	 * cluster集群开关
	 * */
    private Boolean cluster = false;
    
	/**
	 * sentinel集群开关
	 * */
    private Boolean sentinel = false;
    
	/**
	 * 单节点开关
	 * */
    private Boolean single = true;

 
    /**
     * 单例模式 
     * 配置格式：127.0.0.1:6379
     * */
    private SingleServerConfig singleServerConfig;
    
    /**
     * 哨兵配置
     * */
    private SentinelServersConfig sentinelServersConfig;

    /**
     * Cluster集群配置 
     * */
    private ClusterServersConfig clusterServersConfig;

    
    
    /**
     * 布隆过滤器配置
     * */
    private RBloomFilterConfig rBloomFilterConfig = new RBloomFilterConfig();

}
