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

package org.zoo.swan.core.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.Weigher;

import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.utils.StringUtils;
import org.zoo.swan.core.coordinator.SwanCoordinatorService;
import org.zoo.swan.core.helper.SpringBeanUtils;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * use google guava cache.
 *
 * @author dzc
 */
public final class SwanTransactionGuavaCacheManager {

    private static final int MAX_COUNT = 1000000;

    private static final LoadingCache<String, SwanTransaction> LOADING_CACHE =
            CacheBuilder.newBuilder().maximumWeight(MAX_COUNT)
                    .weigher((Weigher<String, SwanTransaction>) (string, catTransaction) -> getSize())
                    .build(new CacheLoader<String, SwanTransaction>() {
                        @Override
                        public SwanTransaction load(final String key) {
                            return cacheCatTransaction(key);
                        }
                    });

    private static SwanCoordinatorService coordinatorService = SpringBeanUtils.getInstance().getBean(SwanCoordinatorService.class);

    private static final SwanTransactionGuavaCacheManager TCC_TRANSACTION_CACHE_MANAGER = new SwanTransactionGuavaCacheManager();

    private SwanTransactionGuavaCacheManager() {

    }

    /**
     * CatTransactionCacheManager.
     *
     * @return CatTransactionCacheManager
     */
    public static SwanTransactionGuavaCacheManager getInstance() {
        return TCC_TRANSACTION_CACHE_MANAGER;
    }

    private static int getSize() {
        return (int) LOADING_CACHE.size();
    }

    private static SwanTransaction cacheCatTransaction(final String key) {
        return Optional.ofNullable(coordinatorService.findByTransId(key)).orElse(new SwanTransaction());
    }

    /**
     * cache catTransaction.
     *
     * @param catTransaction {@linkplain SwanTransaction}
     */
    public void cacheCatTransaction(final SwanTransaction catTransaction) {
        LOADING_CACHE.put(catTransaction.getTransId(), catTransaction);
    }

    /**
     * acquire catTransaction.
     *
     * @param key this guava key.
     * @return {@linkplain SwanTransaction}
     */
    public SwanTransaction getCatTransaction(final String key) {
        try {
            return LOADING_CACHE.get(key);
        } catch (ExecutionException e) {
            return new SwanTransaction();
        }
    }

    /**
     * remove guava cache by key.
     *
     * @param key guava cache key.
     */
    public void removeByKey(final String key) {
        if (StringUtils.isNoneBlank(key)) {
            LOADING_CACHE.invalidate(key);
        }
    }

}
