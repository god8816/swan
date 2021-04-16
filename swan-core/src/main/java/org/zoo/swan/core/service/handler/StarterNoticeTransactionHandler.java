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
import org.zoo.swan.common.exception.SwanException;
import org.zoo.swan.core.concurrent.threadlocal.SwanTransactionContextLocal;
import org.zoo.swan.core.disruptor.DisruptorProviderManage;
import org.zoo.swan.core.disruptor.handler.SwanConsumerTransactionDataHandler;
import org.zoo.swan.core.service.SwanTransactionHandler;
import org.zoo.swan.core.service.SwanTransactionHandlerAlbum;
import org.zoo.swan.core.service.executor.SwanTransactionExecutor;

/**
 * EN：this is notice transaction starter.
 * CN：正向消息通知实现类
 * @author dzc
 */
@Component
public class StarterNoticeTransactionHandler implements SwanTransactionHandler, SmartApplicationListener {

    private final SwanTransactionExecutor swanTransactionExecutor;

    private final SwanConfig swanConfig;

    private DisruptorProviderManage<SwanTransactionHandlerAlbum> disruptorProviderManage;

    /**
     * Instantiates a new Starter swan transaction handler.
     *
     * @param swanTransactionExecutor the swan transaction executor
     * @param swanConfig              the swan config
     */
    @Autowired
    public StarterNoticeTransactionHandler(final SwanTransactionExecutor swanTransactionExecutor, final SwanConfig swanConfig) {
        this.swanTransactionExecutor = swanTransactionExecutor;
        this.swanConfig = swanConfig;
    }

    @Override
    public Object handler(final ProceedingJoinPoint point, final SwanTransactionContext context) throws Throwable {
        Object returnValue;
        try {
            SwanTransaction swanTransaction = swanTransactionExecutor.preTryNotice(point);
            try {
                //execute try
             	Long startTime = System.currentTimeMillis();
                returnValue = point.proceed();
                Long endTime = System.currentTimeMillis();
                if(swanTransaction.getTimeoutMills()>0 && endTime-startTime>swanTransaction.getTimeoutMills()) {
                	   throw new SwanException("method "+swanTransaction.getTargetMethod()+" timeout..");
                }
               
            } catch (Throwable throwable) {
                //if exception again notice
                final SwanTransaction currentTransaction = swanTransactionExecutor.getCurrentTransaction();
                disruptorProviderManage.getProvider().onData(() -> swanTransactionExecutor.notice(currentTransaction));
                throw throwable;
            }
            //正常执行完毕删除补偿记录
            final SwanTransaction currentTransaction = swanTransactionExecutor.getCurrentTransaction();
            disruptorProviderManage.getProvider().onData(() -> swanTransactionExecutor.executeHandler(true,currentTransaction,null));
        } finally {
            SwanTransactionContextLocal.getInstance().remove();
            swanTransactionExecutor.remove();
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
    public void onApplicationEvent(ApplicationEvent appliswanionEvent) {
        if (swanConfig.getStarted()) {
            disruptorProviderManage = new DisruptorProviderManage<>(new SwanConsumerTransactionDataHandler(),
                    swanConfig.getAsyncThreads(),
                    DisruptorProviderManage.DEFAULT_SIZE);
            disruptorProviderManage.startup();
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE - 2;
    }
}
