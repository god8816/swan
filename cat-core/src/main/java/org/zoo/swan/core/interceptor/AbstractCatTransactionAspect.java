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

package org.zoo.swan.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * this is {linkplain org.zoo.cat.annotation.Cat} aspect handler.
 *
 * @author dzc
 */
@Aspect
public abstract class AbstractCatTransactionAspect {

    private SwanTransactionInterceptor catTransactionInterceptor;

    /**
     * Sets cat transaction interceptor.
     *
     * @param catTransactionInterceptor the cat transaction interceptor
     */
    protected void setCatTransactionInterceptor(final SwanTransactionInterceptor catTransactionInterceptor) {
        this.catTransactionInterceptor = catTransactionInterceptor;
    }

    /**
     * this is point cut with {linkplain org.zoo.cat.annotation.Cat }.
     */
    @Pointcut("@annotation(org.zoo.cat.annotation.Cat)")
    public void catInterceptor() {
    }

    /**
     * this is around in {linkplain org.zoo.cat.annotation.Cat }.
     *
     * @param proceedingJoinPoint proceedingJoinPoint
     * @return Object object
     * @throws Throwable Throwable
     */
    @Around("catInterceptor()")
    public Object interceptTccMethod(final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return catTransactionInterceptor.interceptor(proceedingJoinPoint);
    }

    /**
     * spring Order.
     *
     * @return int order
     */
    public abstract int getOrder();
}
