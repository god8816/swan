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

package org.zoo.swan.spring.boot.starter.parent.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.zoo.swan.gitignore.spring.boot.starter.parent.config.SwanConfigProperties;

/**
 * SwanAutoConfiguration is spring boot starter handler.
 *
 * @author dzc
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableConfigurationProperties({SwanConfigProperties.class})
@ComponentScan(basePackages = {"org.zoo.swan"})
public class SwanAutoConfiguration {

    private final SwanConfigProperties swanConfigProperties;

    @Autowired(required = false)
    public SwanAutoConfiguration(SwanConfigProperties swanConfigProperties) {
        this.swanConfigProperties = swanConfigProperties;
    }

    @Bean
    @Qualifier("swanTransactionBootstrap")
    @Primary
    public SwanTransactionBootstrap swanTransactionBootstrap(SwanInitService swanInitService) {
        final SwanTransactionBootstrap swanTransactionBootstrap = new SwanTransactionBootstrap(swanInitService);
        swanTransactionBootstrap.setBufferSize(swanConfigProperties.getBufferSize());
        swanTransactionBootstrap.setRetryMax(swanConfigProperties.getRetryMax());
        swanTransactionBootstrap.setRecoverDelayTime(swanConfigProperties.getRecoverDelayTime());
        swanTransactionBootstrap.setRepositorySuffix(swanConfigProperties.getRepositorySuffix());
        swanTransactionBootstrap.setRepositorySupport(swanConfigProperties.getRepositorySupport());
        swanTransactionBootstrap.setScheduledDelay(swanConfigProperties.getScheduledDelay());
        swanTransactionBootstrap.setScheduledInitDelay(swanConfigProperties.getScheduledInitDelay());
        swanTransactionBootstrap.setScheduledThreadMax(swanConfigProperties.getScheduledThreadMax());
        swanTransactionBootstrap.setSerializer(swanConfigProperties.getSerializer());
        swanTransactionBootstrap.setSwanFileConfig(swanConfigProperties.getSwanFileConfig());
        swanTransactionBootstrap.setSwanDbConfig(swanConfigProperties.getSwanDbConfig());
        swanTransactionBootstrap.setSwanRedisConfig(swanConfigProperties.getSwanRedisConfig());
        swanTransactionBootstrap.setSwanZookeeperConfig(swanConfigProperties.getSwanZookeeperConfig());
        swanTransactionBootstrap.setSwanMongoConfig(swanConfigProperties.getSwanMongoConfig());
        swanTransactionBootstrap.setConsumerThreads(swanConfigProperties.getConsumerThreads());
        swanTransactionBootstrap.setLoadFactor(swanConfigProperties.getLoadFactor());
        swanTransactionBootstrap.setAsyncThreads(swanConfigProperties.getAsyncThreads());
        swanTransactionBootstrap.setConcurrencyScale(swanConfigProperties.getConcurrencyScale());
        return swanTransactionBootstrap;
    }
}
