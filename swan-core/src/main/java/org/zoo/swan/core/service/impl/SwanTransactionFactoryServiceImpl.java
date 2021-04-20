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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.springframework.stereotype.Service;
import org.zoo.swan.annotation.Swan;
import org.zoo.swan.annotation.TransTypeEnum; 
import org.zoo.swan.core.service.SwanTransactionFactoryService;
import org.zoo.swan.core.service.handler.CheckTokenHandler;
import org.zoo.swan.core.service.handler.CreateTokenHandler;
import org.zoo.swan.core.utils.JoinPointUtils;
import java.lang.reflect.Method;
import java.util.Objects;


/**
 * swanTransactionFactoryServiceImpl.
 *
 * @author dzc
 */
@SuppressWarnings("rawtypes")
@Service("swanTransactionFactoryService")
public class SwanTransactionFactoryServiceImpl implements SwanTransactionFactoryService  {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwanTransactionFactoryServiceImpl.class);
     
    
    /**
     * acquired SwanTransactionHandler.
     *
     * @param context {@linkplain SwanTransactionContext}
     * @return Class
     */
    @Override
    public Class factoryOf(final ProceedingJoinPoint point) { 
        Method method = JoinPointUtils.getMethod(point);
        final Swan swan = method.getAnnotation(Swan.class);
        final TransTypeEnum value = swan.value();
        if(Objects.nonNull(value) && value.equals(TransTypeEnum.QUERY)) { 
         	LOGGER.debug("下发Token"); 
         	return CreateTokenHandler.class;
        }else if(Objects.nonNull(value) && value.equals(TransTypeEnum.SAVE)) { 
         	LOGGER.debug("校验是否重复保存"); 
         	return CheckTokenHandler.class;
        }
        return CreateTokenHandler.class;
        	 
    }
}
