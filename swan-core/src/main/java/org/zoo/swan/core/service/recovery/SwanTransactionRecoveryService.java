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

package org.zoo.swan.core.service.recovery;

import com.google.common.collect.Lists;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zoo.swan.common.bean.entity.SwanParticipant;
import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.enums.SwanActionEnum;
import org.zoo.swan.common.utils.CollectionUtils;
import org.zoo.swan.common.utils.LogUtil;
import org.zoo.swan.core.concurrent.threadlocal.SwanTransactionContextLocal;
import org.zoo.swan.core.reflect.SwanReflector;
import org.zoo.swan.core.spi.SwanCoordinatorRepository;

import java.util.List;

/**
 * The type Swan transaction recovery service.
 *
 * @author dzc
 */
public class SwanTransactionRecoveryService {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwanTransactionRecoveryService.class);

    private SwanCoordinatorRepository swanCoordinatorRepository;

    /**
     * Instantiates a new Swan transaction recovery service.
     *
     * @param swanCoordinatorRepository the swan coordinator repository
     */
    public SwanTransactionRecoveryService(final SwanCoordinatorRepository swanCoordinatorRepository) {
        this.swanCoordinatorRepository = swanCoordinatorRepository;
    }

    /**
     * Cancel.
     *
     * @param swanTransaction the swan transaction
     */
    public void cancel(final SwanTransaction swanTransaction) {
        final List<SwanParticipant> swanParticipants = swanTransaction.getSwanParticipants();
        List<SwanParticipant> failList = Lists.newArrayListWithCapacity(swanParticipants.size());
        boolean success = true;
        if (CollectionUtils.isNotEmpty(swanParticipants)) {
            for (SwanParticipant swanParticipant : swanParticipants) {
                try {
                    SwanReflector.executor(swanParticipant.getTransId(),
                            SwanActionEnum.CANCELING,
                            swanParticipant.getCancelCatInvocation());
                } catch (Exception e) {
                    LogUtil.error(LOGGER, "execute cancel exception:{}", () -> e);
                    success = false;
                    failList.add(swanParticipant);
                } finally {
                    SwanTransactionContextLocal.getInstance().remove();
                }
            }
            executeHandler(success, swanTransaction, failList);
        }

    }

    /**
     * Confirm.
     *
     * @param swanTransaction the swan transaction
     */
    public void confirm(final SwanTransaction swanTransaction) {
        final List<SwanParticipant> swanParticipants = swanTransaction.getSwanParticipants();
        List<SwanParticipant> failList = Lists.newArrayListWithCapacity(swanParticipants.size());
        boolean success = true;
         
    }
    
    
    /**
     * notice.
     * @param swanTransaction the swan transaction
     */
    public void notice(final SwanTransaction swanTransaction) {
        final List<SwanParticipant> swanParticipants = swanTransaction.getSwanParticipants();
        List<SwanParticipant> failList = Lists.newArrayListWithCapacity(swanParticipants.size());
        
    }

    private void executeHandler(final boolean success, final SwanTransaction currentTransaction, final List<SwanParticipant> failList) {
        if (success) {
            deleteTransaction(currentTransaction.getTransId());
        } else {
            currentTransaction.setSwanParticipants(failList);
            swanCoordinatorRepository.updateParticipant(currentTransaction);
        }
    }

    private void deleteTransaction(final String transId) {
        swanCoordinatorRepository.remove(transId);
    }
}
