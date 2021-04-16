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

import org.zoo.swan.common.enums.RepositorySupportEnum;
import org.zoo.swan.common.enums.SerializeEnum;

import lombok.Data;

/**
 * Swan config.
 *
 * @author dzc
 */
@Data
public class SwanConfig {

    /**
     * EN：Resource suffix this parameter please fill in about is the transaction store path.
     * If it's a table store this is a table suffix, it's stored the same way.
     * If this parameter is not filled in, the appliswanionName of the appliswanion is retrieved by default
     * CN: 补偿日志入库名称
     */
    private String repositorySuffix;

    /**
     * this is map db concurrencyScale.
     */
    private Integer concurrencyScale = 512;

    /**
     * EN：log serializer.{@linkplain SerializeEnum}
     * CN：默认序列化
     */
    private String serializer = "kryo";

    /**
     * EN：scheduledPool Thread size.
     * CN：scheduledPool线程大小
     */
    private int scheduledThreadMax = Runtime.getRuntime().availableProcessors() << 1;

    /**
     * EN：scheduledPool scheduledDelay unit SECONDS.
     * CN：定时器执行时间间隔
     */
    private int scheduledDelay = 60;

    /**
     * EN:scheduledPool scheduledInitDelay unit SECONDS.
     * CN:定时器延时执行时间
     */
    private int scheduledInitDelay = 120;

    /**
     * EN: retry max.
     * CN: 最大重试次数
     */
    private int retryMax = 3;

    /**
     * recoverDelayTime Unit seconds
     * (note that this time represents how many seconds after the local transaction was created before execution).
     */
    private int recoverDelayTime = 60;

    /**
     * Parameters when participants perform their own recovery.
     * 1.such as RPC calls time out
     * 2.such as the starter down machine
     */
    private int loadFactor = 2;

    /**
     * EN:repositorySupport.{@linkplain RepositorySupportEnum}
     * CN:选择模式db，redis，zk，mongo，file
     */
    private String repositorySupport = "db";

    /**
     * EN:disruptor bufferSize.
     * CN:
     */
    private int bufferSize = 4096 * 2 * 2;

    /**
     * EN：this is disruptor consumerThreads.
     * CN：
     */
    private int consumerThreads = Runtime.getRuntime().availableProcessors() << 1;

    /**
     * EN：this is swan async execute cancel or confirm or notice thread size.
     * CN： cancel or confirm or notice执行线程池
     */
    private int asyncThreads = Runtime.getRuntime().availableProcessors() << 1;

    /**
     * EN：when start this set true  actor set false.
     * CN：框架开关
     */
    private Boolean started = true;
   
    /**
     * EN: swan log config
     * CN: swan 日志自动清理配置
     */
    private SwanLogConfig swanLogConfig = new SwanLogConfig();

    /**
     * EN：db config.
     * CN：数据库配置
     */
    private SwanDbConfig swanDbConfig;

    /**
     * EN：redis config.
     * CN：redis配置
     */
    private SwanRedisConfig swanRedisConfig;


}
