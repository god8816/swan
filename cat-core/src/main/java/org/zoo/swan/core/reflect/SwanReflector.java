/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.zoo.swan.core.reflect;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.zoo.swan.common.bean.context.SwanTransactionContext;
import org.zoo.swan.common.bean.entity.SwanInvocation;
import org.zoo.swan.common.enums.SwanActionEnum; 
import org.zoo.swan.core.concurrent.threadlocal.SwanTransactionContextLocal;
import org.zoo.swan.core.helper.SpringBeanUtils;

import java.util.Objects;

/**
 * The type Cat reflector.
 *
 * @author dzc
 */
public class SwanReflector {

    /**
     * Executor object.
     *
     * @param transId         the trans id
     * @param actionEnum      the action enum
     * @param catInvocation the cat invocation
     * @return the object
     * @throws Exception the exception
     */
    public static Object executor(final String transId, final SwanActionEnum actionEnum,
                                  final SwanInvocation catInvocation) throws Exception {
        SwanTransactionContext context = new SwanTransactionContext(); 
        context.setTransId(transId); 
        SwanTransactionContextLocal.getInstance().set(context);
        return execute(catInvocation);
    }

    @SuppressWarnings("unchecked")
    private static Object execute(final SwanInvocation catInvocation) throws Exception {
        if (Objects.isNull(catInvocation)) {
            return null;
        }
        final Class clazz = catInvocation.getTargetClass();
        final String method = catInvocation.getMethodName();
        final Object[] args = catInvocation.getArgs();
        final Class[] parameterTypes = catInvocation.getParameterTypes();
        final Object bean = SpringBeanUtils.getInstance().getBean(clazz);
        return MethodUtils.invokeMethod(bean, method, args, parameterTypes);

    }
}
