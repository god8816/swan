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

package org.zoo.swan.core.disruptor.publisher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;
import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.enums.EventTypeEnum;
import org.zoo.swan.core.concurrent.ConsistentHashSelector;
import org.zoo.swan.core.concurrent.SingletonExecutor;
import org.zoo.swan.core.coordinator.SwanCoordinatorService;
import org.zoo.swan.core.disruptor.DisruptorProviderManage;
import org.zoo.swan.core.disruptor.event.SwanTransactionEvent;
import org.zoo.swan.core.disruptor.handler.SwanConsumerLogDataHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * event publisher.
 *
 * @author dzc
 */
@Component
public class SwanTransactionEventPublisher implements SmartApplicationListener {

    private volatile AtomicBoolean isInit = new AtomicBoolean(false);

    private DisruptorProviderManage<SwanTransactionEvent> disruptorProviderManage;

    private final SwanCoordinatorService coordinatorService;

    private final SwanConfig catConfig;

    @Autowired
    public SwanTransactionEventPublisher(final SwanCoordinatorService coordinatorService,
                                          final SwanConfig catConfig) {
        this.coordinatorService = coordinatorService;
        this.catConfig = catConfig;
    }

    /**
     * disruptor start.
     *
     * @param bufferSize this is disruptor buffer size.
     * @param threadSize this is disruptor consumer thread size.
     */
    private void start(final int bufferSize, final int threadSize) {
        List<SingletonExecutor> selects = new ArrayList<>();
        for (int i = 0; i < threadSize; i++) {
            selects.add(new SingletonExecutor("cat-log-disruptor" + i));
        }
        ConsistentHashSelector selector = new ConsistentHashSelector(selects);
        disruptorProviderManage =
                new DisruptorProviderManage<>(
                        new SwanConsumerLogDataHandler(selector, coordinatorService), 1, bufferSize);
        disruptorProviderManage.startup();
    }

    /**
     * publish disruptor event.
     *
     * @param catTransaction {@linkplain SwanTransaction }
     * @param type             {@linkplain EventTypeEnum}
     */
    public void publishEvent(final SwanTransaction catTransaction, final int type) {
        SwanTransactionEvent event = new SwanTransactionEvent();
        event.setType(type);
        event.setCatTransaction(catTransaction);
        disruptorProviderManage.getProvider().onData(event);
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
    public void onApplicationEvent(ApplicationEvent applicationEvent) {
        if (!isInit.compareAndSet(false, true)) {
            return;
        }
        start(catConfig.getBufferSize(), catConfig.getConsumerThreads());
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 1;
    }
}
