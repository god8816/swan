package org.zoo.swan.core.cache;
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
 *//*


package org.zoo.cat.core.cache;

import org.zoo.cat.common.utils.CollectionUtils;
import org.zoo.cat.common.bean.entity.CatTransaction;
import org.zoo.cat.common.config.CatConfig;
import org.zoo.cat.common.enums.CatActionEnum;
import org.zoo.cat.common.enums.CatRoleEnum;
import org.zoo.cat.core.concurrent.threadpool.CatThreadFactory;
import org.zoo.cat.core.helper.SpringBeanUtils;
import org.zoo.cat.core.service.CatApplicationService;
import org.zoo.cat.core.service.recovery.CatTransactionRecoveryService;
import org.zoo.cat.core.spi.CatCoordinatorRepository;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

*/
/**
 * The type Cat transaction map db cache manager.
 *
 * @author dzc
 *//*

public class CatTransactionMapDbCacheManager implements DisposableBean, ApplicationListener<ContextRefreshedEvent> {

    private static final String FILE_NAME_SUFFIX = "-catTransaction.db";

    private static final String MAP_NAME_SUFFIX = "CatTransactionMap";

    private CatConfig catConfig;

    private CatApplicationService catApplicationService;

    private DB db;

    private ConcurrentMap<String, CatTransaction> transactionMap;

    private CatCoordinatorRepository catCoordinatorRepository;

    private CatTransactionRecoveryService catTransactionRecoveryService;

    */
/**
     * Put.
     *
     * @param catTransaction the cat transaction
     *//*

    public void put(final CatTransaction catTransaction) {
        transactionMap.put(catTransaction.getTransId(), catTransaction);
        // db.commit();
    }

    */
/**
     * Remove.
     *
     * @param id the id
     *//*

    public void remove(final String id) {
        transactionMap.remove(id);
        //db.commit();
    }

    */
/**
     * Get cat transaction.
     *
     * @param id the id
     * @return the cat transaction
     *//*

    public CatTransaction get(final String id) {
        return Optional.ofNullable(transactionMap.get(id))
                .orElse(catCoordinatorRepository.findById(id));
    }

    @Override
    public void destroy() {
        db.close();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        db = DBMaker.fileDB(buildFileName())
                .fileMmapEnableIfSupported()
                .fileMmapPreclearDisable()
                .cleanerHackEnable()
                .closeOnJvmShutdown()
                .closeOnJvmShutdownWeakReference()
                .checksumHeaderBypass()
                .concurrencyScale(catConfig.getConcurrencyScale())
                .make();

        transactionMap = db.hashMap(buildMapName())
                .keySerializer(Serializer.STRING)
                .valueSerializer(Serializer.JAVA)
                .createOrOpen();
        catCoordinatorRepository = SpringBeanUtils.getInstance().getBean(CatCoordinatorRepository.class);

        catTransactionRecoveryService = new CatTransactionRecoveryService(catCoordinatorRepository);


        handler(new ArrayList<>(transactionMap.values()));
    }

    private void handler(final List<CatTransaction> recoveryList) {
        if (CollectionUtils.isNotEmpty(recoveryList)) {
            Executor executor = new ThreadPoolExecutor(1,
                    1, 0, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(),
                    CatThreadFactory.create("cat-mapDb-execute",
                            false),
                    new ThreadPoolExecutor.AbortPolicy());
            executor.execute(() -> {
                for (CatTransaction catTransaction : recoveryList) {
                    if (catTransaction.getRole() == CatRoleEnum.START.getCode()) {
                        if (catTransaction.getStatus() == CatActionEnum.TRYING.getCode()
                                || catTransaction.getStatus() == CatActionEnum.CANCELING.getCode()) {
                            catTransactionRecoveryService.cancel(catTransaction);
                        } else if (catTransaction.getStatus() == CatActionEnum.CONFIRMING.getCode()) {
                            catTransactionRecoveryService.confirm(catTransaction);
                        }
                    }
                    remove(catTransaction.getTransId());
                }
            });
        }
    }

    private String buildFileName() {
        return catApplicationService.acquireName() + FILE_NAME_SUFFIX;
    }

    private String buildMapName() {
        return catApplicationService.acquireName() + MAP_NAME_SUFFIX;
    }

}
*/
