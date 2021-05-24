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
 * Swan 配置
 *
 * @author dzc
 */
@Data
public class SwanConfig {
	
    /**
     * 框架开关
     */
    private Boolean started = true;
    
    /**
     * 模式 header、cookie
     */
    private String mode = "cookie";
    
    /**
     * 服务名称
     */
    private String applicationName = "applicationName";
    
    /**
     * 存储 redis
     */
    private String repositorySupport = "redis";
      
    /**
     * Id生成策略 UUID、SnowId
     */
    private String tokenSupport = "UUID";
    
    /**
     * tokenKey
     */
    private String tokenKey = "tokenKey";
    
    /**
     * redis配置
     */
    private SwanRedisConfig swanRedisConfig;
}
