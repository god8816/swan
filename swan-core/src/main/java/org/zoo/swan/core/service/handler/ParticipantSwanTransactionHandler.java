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
import org.zoo.swan.common.bean.context.SwanTransactionContext;
import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.utils.DefaultValueUtils;
import org.zoo.swan.core.service.SwanTransactionHandler;
import org.zoo.swan.core.service.executor.SwanTransactionExecutor;
import java.lang.reflect.Method;

/**
 * Participant Handler.
 *
 * @author dzc
 */
@Component
public class ParticipantSwanTransactionHandler implements SwanTransactionHandler {

    private final SwanTransactionExecutor swanTransactionExecutor;
    

    /**
     * Instantiates a new Participant cat transaction handler.
     *
     * @param catTransactionExecutor the cat transaction executor
     */
    @Autowired
    public ParticipantSwanTransactionHandler(final SwanTransactionExecutor catTransactionExecutor) {
        this.swanTransactionExecutor = catTransactionExecutor;
    }


	@Override
    public Object handler(final ProceedingJoinPoint point, final SwanTransactionContext context) throws Throwable {
     	SwanTransaction swanTransaction = null;
     	final SwanTransaction currentTransaction;
        
        Method method = ((MethodSignature) (point.getSignature())).getMethod();
        return DefaultValueUtils.getDefaultValue(method.getReturnType());
    }

}
