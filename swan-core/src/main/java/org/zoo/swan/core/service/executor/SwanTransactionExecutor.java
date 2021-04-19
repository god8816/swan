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

package org.zoo.swan.core.service.executor;
import org.aspectj.lang.ProceedingJoinPoint; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.zoo.swan.annotation.Swan;
import org.zoo.swan.annotation.TransTypeEnum;
import org.zoo.swan.common.bean.entity.SwanTransaction; 
import org.zoo.swan.common.utils.LogUtil;
import org.zoo.swan.common.utils.StringUtils; 
import org.zoo.swan.core.utils.JoinPointUtils;

import java.lang.reflect.Method;
import java.util.Objects;


/**
 * this is swan transaction manager.
 *
 * @author dzc
 */
@Component
public class SwanTransactionExecutor {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwanTransactionExecutor.class);


    /**
     * transaction preTry.
     *
     * @param point cut point.
     * @return TccTransaction swan transaction
     */
    public SwanTransaction preTry(final ProceedingJoinPoint point) {
        LogUtil.debug(LOGGER, () -> "......swan transaction starter....");
        //build tccTransaction
        final SwanTransaction swanTransaction = buildCatTransaction(point,"");
      
        return swanTransaction;
    }
   

    private SwanTransaction buildCatTransaction(final ProceedingJoinPoint point,final String transId) {
        SwanTransaction swanTransaction;
        if (StringUtils.isNoneBlank(transId)) {
            swanTransaction = new SwanTransaction(transId);
        } else {
            swanTransaction = new SwanTransaction();
        }
      
        Method method = JoinPointUtils.getMethod(point);
        Class<?> clazz = point.getTarget().getClass();
        Object[] args = point.getArgs();
    
   
    	    final Swan swan = method.getAnnotation(Swan.class);
        final TransTypeEnum pattern = swan.pattern();
        if(Objects.isNull(pattern)) {
         	LOGGER.error("事务补偿模式必须在TCC,SAGA,CC,NOTICE中选择"); 
        }
        
        return null;
    }

   
}
