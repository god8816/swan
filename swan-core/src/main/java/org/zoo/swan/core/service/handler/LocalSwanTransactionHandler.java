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
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zoo.swan.annotation.Swan;
import org.zoo.swan.common.bean.context.SwanTransactionContext;
import org.zoo.swan.common.bean.entity.SwanInvocation;
import org.zoo.swan.common.bean.entity.SwanParticipant;
import org.zoo.swan.common.enums.SwanActionEnum;
import org.zoo.swan.common.utils.StringUtils;
import org.zoo.swan.core.service.SwanTransactionHandler;
import org.zoo.swan.core.service.executor.SwanTransactionExecutor;

import java.lang.reflect.Method;

/**
 * InlineCatTransactionHandler.
 * This is the method annotated by TCC within an actor.
 *
 * @author dzc
 */
@Component
public class LocalSwanTransactionHandler implements SwanTransactionHandler {

    private final SwanTransactionExecutor catTransactionExecutor;

    /**
     * Instantiates a new Local cat transaction handler.
     *
     * @param catTransactionExecutor the cat transaction executor
     */
    @Autowired
    public LocalSwanTransactionHandler(final SwanTransactionExecutor catTransactionExecutor) {
        this.catTransactionExecutor = catTransactionExecutor;
    }

    @Override
    public Object handler(final ProceedingJoinPoint point, final SwanTransactionContext context) throws Throwable {
        if (false) {
            MethodSignature signature = (MethodSignature) point.getSignature();
            Method method = signature.getMethod();
            Class<?> clazz = point.getTarget().getClass();
            Object[] args = point.getArgs();
            final Swan cat = method.getAnnotation(Swan.class);
            SwanInvocation confirmInvocation = null;
            String confirmMethodName = cat.confirmMethod();
            String cancelMethodName = cat.cancelMethod();
            if (StringUtils.isNoneBlank(confirmMethodName)) {
                confirmInvocation = new SwanInvocation(clazz, confirmMethodName, method.getParameterTypes(), args);
            }
            SwanInvocation cancelInvocation = null;
            if (StringUtils.isNoneBlank(cancelMethodName)) {
                cancelInvocation = new SwanInvocation(clazz, cancelMethodName, method.getParameterTypes(), args);
            }
            final SwanParticipant catParticipant = new SwanParticipant(context.getTransId(),
                    confirmInvocation, cancelInvocation);
            catTransactionExecutor.registerByNested(context.getTransId(), catParticipant);
        }
        return point.proceed();
    }

}
