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

package org.zoo.cat.spring.boot.starter.parent.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Primary;
import org.zoo.cat.core.bootstrap.CatTransactionBootstrap;
import org.zoo.cat.core.service.CatInitService;
import org.zoo.cat.spring.boot.starter.parent.config.CatConfigProperties;

/**
 * CatAutoConfiguration is spring boot starter handler.
 *
 * @author dzc
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableConfigurationProperties({CatConfigProperties.class})
@ComponentScan(basePackages = {"org.zoo.cat"})
public class CatAutoConfiguration {

    private final CatConfigProperties catConfigProperties;

    @Autowired(required = false)
    public CatAutoConfiguration(CatConfigProperties catConfigProperties) {
        this.catConfigProperties = catConfigProperties;
    }

    @Bean
    @Qualifier("catTransactionBootstrap")
    @Primary
    public CatTransactionBootstrap catTransactionBootstrap(CatInitService catInitService) {
        final CatTransactionBootstrap catTransactionBootstrap = new CatTransactionBootstrap(catInitService);
        catTransactionBootstrap.setBufferSize(catConfigProperties.getBufferSize());
        catTransactionBootstrap.setRetryMax(catConfigProperties.getRetryMax());
        catTransactionBootstrap.setRecoverDelayTime(catConfigProperties.getRecoverDelayTime());
        catTransactionBootstrap.setRepositorySuffix(catConfigProperties.getRepositorySuffix());
        catTransactionBootstrap.setRepositorySupport(catConfigProperties.getRepositorySupport());
        catTransactionBootstrap.setScheduledDelay(catConfigProperties.getScheduledDelay());
        catTransactionBootstrap.setScheduledInitDelay(catConfigProperties.getScheduledInitDelay());
        catTransactionBootstrap.setScheduledThreadMax(catConfigProperties.getScheduledThreadMax());
        catTransactionBootstrap.setSerializer(catConfigProperties.getSerializer());
        catTransactionBootstrap.setCatFileConfig(catConfigProperties.getCatFileConfig());
        catTransactionBootstrap.setCatDbConfig(catConfigProperties.getCatDbConfig());
        catTransactionBootstrap.setCatRedisConfig(catConfigProperties.getCatRedisConfig());
        catTransactionBootstrap.setCatZookeeperConfig(catConfigProperties.getCatZookeeperConfig());
        catTransactionBootstrap.setCatMongoConfig(catConfigProperties.getCatMongoConfig());
        catTransactionBootstrap.setConsumerThreads(catConfigProperties.getConsumerThreads());
        catTransactionBootstrap.setLoadFactor(catConfigProperties.getLoadFactor());
        catTransactionBootstrap.setAsyncThreads(catConfigProperties.getAsyncThreads());
        catTransactionBootstrap.setConcurrencyScale(catConfigProperties.getConcurrencyScale());
        return catTransactionBootstrap;
    }
}
