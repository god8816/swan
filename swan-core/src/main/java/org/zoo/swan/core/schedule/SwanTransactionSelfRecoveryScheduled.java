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

package org.zoo.swan.core.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.zoo.swan.annotation.TransTypeEnum;
import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.enums.SwanActionEnum;
import org.zoo.swan.common.enums.SwanRoleEnum;
import org.zoo.swan.common.utils.CollectionUtils;
import org.zoo.swan.common.utils.LogUtil;
import org.zoo.swan.core.concurrent.threadpool.SwanThreadFactory;
import org.zoo.swan.core.helper.SpringBeanUtils;
import org.zoo.swan.core.service.recovery.SwanTransactionRecoveryService;
import org.zoo.swan.core.spi.SwanCoordinatorRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * EN:The type Cat transaction self recovery scheduled.
 * CN:定时补偿失败的请求
 * @author dzc
 */
@Component
public class SwanTransactionSelfRecoveryScheduled implements SmartApplicationListener {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwanTransactionSelfRecoveryScheduled.class);

    private final SwanConfig catConfig;

    private volatile AtomicBoolean isInit = new AtomicBoolean(false);

    private ScheduledExecutorService scheduledExecutorService;

    private SwanCoordinatorRepository catCoordinatorRepository;

    private SwanTransactionRecoveryService catTransactionRecoveryService;

    @Autowired(required = false)
    public SwanTransactionSelfRecoveryScheduled(final SwanConfig catConfig) {
        this.catConfig = catConfig;
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> aClass) {
        return aClass == ContextRefreshedEvent.class;
    }

    @Override
    public boolean supportsSourceType(Class<?> aClass) {
        return true;
    }

    @Override
    public void onApplicationEvent(@NonNull final ApplicationEvent event) {
        if (!isInit.compareAndSet(false, true)) {
            return;
        }
        catCoordinatorRepository = SpringBeanUtils.getInstance().getBean(SwanCoordinatorRepository.class);
        this.scheduledExecutorService =
                new ScheduledThreadPoolExecutor(1,
                        SwanThreadFactory.create("cat-transaction-self-recovery", true));
        catTransactionRecoveryService = new SwanTransactionRecoveryService(catCoordinatorRepository);
        selfRecovery();
    }

    /**
     * if have some exception by schedule execute cat transaction log.
     */
    private void selfRecovery() {
        scheduledExecutorService
                .scheduleWithFixedDelay(() -> {
                    LogUtil.debug(LOGGER, "self recovery execute delayTime:{}", catConfig::getScheduledDelay);
                    try {
                        final List<SwanTransaction> catTransactions = catCoordinatorRepository.listAllByDelay(acquireData());
                        if (CollectionUtils.isEmpty(catTransactions)) {
                            return;
                        }
                        for (SwanTransaction catTransaction : catTransactions) {
                           
            
                        }
                    } catch (Exception e) {
                        LOGGER.error("cat scheduled transaction log is error:", e);
                    } 
                }, catConfig.getScheduledInitDelay(), catConfig.getScheduledDelay(), TimeUnit.SECONDS);

    }

    private Date acquireData() {
        return new Date(LocalDateTime.now().atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli() - (catConfig.getRecoverDelayTime() * 1000));
    }


}
