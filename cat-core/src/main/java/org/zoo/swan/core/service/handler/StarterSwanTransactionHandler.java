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

package org.zoo.swan.core.service.handler;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;
import org.zoo.swan.common.bean.context.SwanTransactionContext;
import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.enums.SwanActionEnum;
import org.zoo.swan.core.concurrent.threadlocal.SwanTransactionContextLocal;
import org.zoo.swan.core.disruptor.DisruptorProviderManage;
import org.zoo.swan.core.disruptor.handler.SwanConsumerTransactionDataHandler;
import org.zoo.swan.core.service.SwanTransactionHandler;
import org.zoo.swan.core.service.SwanTransactionHandlerAlbum;
import org.zoo.swan.core.service.executor.SwanTransactionExecutor;

/**
 * this is cat transaction starter.
 *
 * @author dzc
 */
@Component
public class StarterSwanTransactionHandler implements SwanTransactionHandler, SmartApplicationListener {

    private final SwanTransactionExecutor catTransactionExecutor;

    private final SwanConfig catConfig;

    private DisruptorProviderManage<SwanTransactionHandlerAlbum> disruptorProviderManage;

    /**
     * Instantiates a new Starter cat transaction handler.
     *
     * @param catTransactionExecutor the cat transaction executor
     * @param catConfig              the cat config
     */
    @Autowired
    public StarterSwanTransactionHandler(final SwanTransactionExecutor catTransactionExecutor, final SwanConfig catConfig) {
        this.catTransactionExecutor = catTransactionExecutor;
        this.catConfig = catConfig;
    }

    @Override
    public Object handler(final ProceedingJoinPoint point, final SwanTransactionContext context) throws Throwable {
        Object returnValue;
        try {
            SwanTransaction catTransaction = catTransactionExecutor.preTry(point);
            try {
                //execute try
                returnValue = point.proceed(); 
            } catch (Throwable throwable) {
                //if exception ,execute cancel
                final SwanTransaction currentTransaction = catTransactionExecutor.getCurrentTransaction();
                disruptorProviderManage.getProvider().onData(() -> catTransactionExecutor.cancel(currentTransaction));
                throw throwable;
            }
            //execute confirm
            final SwanTransaction currentTransaction = catTransactionExecutor.getCurrentTransaction();
            disruptorProviderManage.getProvider().onData(() -> catTransactionExecutor.confirm(currentTransaction));
        } finally {
            SwanTransactionContextLocal.getInstance().remove();
            catTransactionExecutor.remove();
        }
        return returnValue;
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
        if (catConfig.getStarted()) {
            disruptorProviderManage = new DisruptorProviderManage<>(new SwanConsumerTransactionDataHandler(),
                    catConfig.getAsyncThreads(),
                    DisruptorProviderManage.DEFAULT_SIZE);
            disruptorProviderManage.startup();
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 2;
    }
}
