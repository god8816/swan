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

package org.zoo.swan.core.concurrent.threadlocal;

import org.zoo.swan.common.bean.context.SwanTransactionContext;

/**
 * this is save cat transactionContext in threadLocal.
 * @author dzc
 */
public final class SwanTransactionContextLocal {

    private static final ThreadLocal<SwanTransactionContext> CURRENT_LOCAL = new ThreadLocal<>();

    private static final SwanTransactionContextLocal TRANSACTION_CONTEXT_LOCAL = new SwanTransactionContextLocal();

    private SwanTransactionContextLocal() {

    }

    /**
     * singleton TransactionContextLocal.
     * @return this
     */
    public static SwanTransactionContextLocal getInstance() {
        return TRANSACTION_CONTEXT_LOCAL;
    }

    /**
     * set value.
     * @param context context
     */
    public void set(final SwanTransactionContext context) {
        CURRENT_LOCAL.set(context);
    }

    /**
     * get value.
     * @return TccTransactionContext
     */
    public SwanTransactionContext get() {
        return CURRENT_LOCAL.get();
    }

    /**
     * clean threadLocal for gc.
     */
    public void remove() {
        CURRENT_LOCAL.remove();
    }
}
