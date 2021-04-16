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

package org.zoo.swan.core.service.impl;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zoo.swan.common.bean.context.SwanTransactionContext;
import org.zoo.swan.core.helper.SpringBeanUtils;
import org.zoo.swan.core.service.SwanTransactionAspectService;
import org.zoo.swan.core.service.SwanTransactionFactoryService;
import org.zoo.swan.core.service.SwanTransactionHandler;

/**
 * CatTransactionAspectServiceImpl.
 *
 * @author dzc
 */
@Service("catTransactionAspectService")
@SuppressWarnings("unchecked")
public class SwanTransactionAspectServiceImpl implements SwanTransactionAspectService {

    private final SwanTransactionFactoryService catTransactionFactoryService;
    


    /**
     * Instantiates a new Cat transaction aspect service.
     *
     * @param catTransactionFactoryService the cat transaction factory service
     */
    @Autowired
    public SwanTransactionAspectServiceImpl(final SwanTransactionFactoryService catTransactionFactoryService) {
        this.catTransactionFactoryService = catTransactionFactoryService;
    }

    /**
     * cat transaction aspect.
     *
     * @param catTransactionContext {@linkplain  SwanTransactionContext}
     * @param point                   {@linkplain ProceedingJoinPoint}
     * @return object  return value
     * @throws Throwable exception
     */
    @Override
    public Object invoke(final SwanTransactionContext catTransactionContext, final ProceedingJoinPoint point) throws Throwable {
        final Class clazz = catTransactionFactoryService.factoryOf(point,catTransactionContext);
        final SwanTransactionHandler txTransactionHandler =
                (SwanTransactionHandler) SpringBeanUtils.getInstance().getBean(clazz);
        return txTransactionHandler.handler(point, catTransactionContext);
    }
}
