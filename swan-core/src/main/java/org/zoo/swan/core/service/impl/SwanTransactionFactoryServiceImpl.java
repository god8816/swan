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
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zoo.swan.annotation.Swan;
import org.zoo.swan.annotation.TransTypeEnum;
import org.zoo.swan.common.bean.context.SwanTransactionContext;
import org.zoo.swan.common.bean.variate.SwanDegradation;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.enums.SwanRoleEnum;
import org.zoo.swan.core.service.SwanTransactionFactoryService;
import org.zoo.swan.core.service.handler.ConsumeSwanNoticeTransactionHandler;
import org.zoo.swan.core.service.handler.ConsumeSwanTransactionHandler;
import org.zoo.swan.core.service.handler.LocalSwanTransactionHandler;
import org.zoo.swan.core.service.handler.ParticipantSwanTransactionHandler;
import org.zoo.swan.core.service.handler.StarterNoticeTransactionHandler;
import org.zoo.swan.core.service.handler.StarterSwanTransactionHandler;
import org.zoo.swan.core.utils.JoinPointUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * CatTransactionFactoryServiceImpl.
 *
 * @author dzc
 */
@SuppressWarnings("rawtypes")
@Service("catTransactionFactoryService")
public class SwanTransactionFactoryServiceImpl implements SwanTransactionFactoryService  {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwanTransactionFactoryServiceImpl.class);
    
    @Autowired
    private SwanConfig catConfig;
    
    /**
     * acquired CatTransactionHandler.
     *
     * @param context {@linkplain SwanTransactionContext}
     * @return Class
     */
    @Override
    public Class factoryOf(final ProceedingJoinPoint point,final SwanTransactionContext context) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = JoinPointUtils.getMethod(point);
        Class<?> declaringClass =  signature.getMethod().getDeclaringClass();
        
        final Swan swan = method.getAnnotation(Swan.class);
        final TransTypeEnum pattern = swan.pattern();
        if(Objects.isNull(pattern)) { 
         	LOGGER.error("事务补偿模式必须在TCC,SAGA,CC,NOTICE中选择"); 
         	return ConsumeSwanTransactionHandler.class;
        }
        
        //判断正向消息补偿模式
        if(swan.pattern().getCode()==TransTypeEnum.NOTICE.getCode()) {
        	  if(SwanDegradation.isStartDegradation(catConfig, declaringClass.getName(), method.getName())) {
               	if (Objects.isNull(context)) { 
                    return StarterNoticeTransactionHandler.class;
                } else {
                	    //1.0 spring cloud调用
                    if (context.getRole() == SwanRoleEnum.SPRING_CLOUD.getCode()) {
                        context.setRole(SwanRoleEnum.START.getCode());
                        return ConsumeSwanNoticeTransactionHandler.class;
                    }
                    //2.0 dubbo调用
                    if (context.getRole() == SwanRoleEnum.LOCAL.getCode()) {
                    	    return LocalSwanTransactionHandler.class;
                    }else if (context.getRole() == SwanRoleEnum.START.getCode()
                            || context.getRole() == SwanRoleEnum.INLINE.getCode()) {
                     	return ParticipantSwanTransactionHandler.class;
                    }
                    return ConsumeSwanNoticeTransactionHandler.class;
                } 
        	  }else {
        		   return ConsumeSwanTransactionHandler.class;
        	  }
        }else {
            if (Objects.isNull(context)) {
                return StarterSwanTransactionHandler.class;
            } else {
                //why this code?  because spring cloud invoke has proxy.
                if (context.getRole() == SwanRoleEnum.SPRING_CLOUD.getCode()) {
                    context.setRole(SwanRoleEnum.START.getCode());
                    return ConsumeSwanTransactionHandler.class;
                }
                // if context not null and role is inline  is ParticipantCatTransactionHandler.
                if (context.getRole() == SwanRoleEnum.LOCAL.getCode()) {
                    return LocalSwanTransactionHandler.class;
                } else if (context.getRole() == SwanRoleEnum.START.getCode()
                        || context.getRole() == SwanRoleEnum.INLINE.getCode()) {
                    return ParticipantSwanTransactionHandler.class;
                }
                return ConsumeSwanTransactionHandler.class;
            }
        }
    }
}
