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

package org.zoo.swan.core.coordinator;

import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.enums.SwanActionEnum;

/**
 * this is save transaction log service.
 * @author dzc
 */
public interface SwanCoordinatorService {

    /**
     * init swan config.
     *
     * @param swanConfig {@linkplain SwanConfig}
     * @throws Exception exception
     */
    void start(SwanConfig swanConfig) throws Exception;

    /**
     * save tccTransaction.
     *
     * @param swanTransaction {@linkplain SwanTransaction }
     * @return id
     */
    String save(SwanTransaction swanTransaction);

    /**
     * find by transId.
     *
     * @param transId  transId
     * @return {@linkplain SwanTransaction }
     */
    SwanTransaction findByTransId(String transId);

    /**
     * remove transaction.
     *
     * @param id  transaction pk.
     * @return true success
     */
    boolean remove(String id);

    /**
     * update.
     * @param swanTransaction {@linkplain SwanTransaction }
     */
    void update(SwanTransaction swanTransaction);

    /**
     * update TccTransaction .
     * this is only update Participant field.
     * @param swanTransaction  {@linkplain SwanTransaction }
     * @return rows
     */
    int updateParticipant(SwanTransaction swanTransaction);

    /**
     * update TccTransaction status.
     * @param id  pk.
     * @param status   {@linkplain SwanActionEnum}
     * @return rows
     */
    int updateStatus(String id, Integer status);

}
