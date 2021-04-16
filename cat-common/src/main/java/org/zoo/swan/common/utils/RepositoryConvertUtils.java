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

package org.zoo.swan.common.utils;

import org.zoo.swan.common.bean.adapter.CoordinatorRepositoryAdapter;
import org.zoo.swan.common.bean.entity.SwanParticipant;
import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.exception.SwanException;
import org.zoo.swan.common.serializer.ObjectSerializer;
import org.zoo.swan.common.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * RepositoryConvertUtils.
 *
 * @author dzc
 */
public class RepositoryConvertUtils {

    /**
     * Convert byte [ ].
     *
     * @param swanTransaction   the tcc transaction
     * @param objectSerializer the object serializer
     * @return the byte [ ]
     * @throws SwanException the tcc exception
     */
    public static byte[] convert(final SwanTransaction swanTransaction, final ObjectSerializer objectSerializer) throws SwanException {
        CoordinatorRepositoryAdapter adapter = new CoordinatorRepositoryAdapter();
        adapter.setTransId(swanTransaction.getTransId());
        adapter.setLastTime(swanTransaction.getLastTime());
        adapter.setCreateTime(swanTransaction.getCreateTime());
        adapter.setRetriedCount(swanTransaction.getRetriedCount());
        adapter.setTargetClass(swanTransaction.getTargetClass());
        adapter.setTargetMethod(swanTransaction.getTargetMethod());
        adapter.setPattern(swanTransaction.getPattern());
        adapter.setVersion(swanTransaction.getVersion());
        return objectSerializer.serialize(adapter);
    }

    /**
     * Transform bean tcc transaction.
     *
     * @param contents         the contents
     * @param objectSerializer the object serializer
     * @return the tcc transaction
     * @throws SwanException the tcc exception
     */
    @SuppressWarnings("unchecked")
    public static SwanTransaction transformBean(final byte[] contents, final ObjectSerializer objectSerializer) throws SwanException {
        SwanTransaction swanTransaction = new SwanTransaction();
        final CoordinatorRepositoryAdapter adapter = objectSerializer.deSerialize(contents, CoordinatorRepositoryAdapter.class);
        List<SwanParticipant> swanParticipants = objectSerializer.deSerialize(adapter.getContents(), ArrayList.class);
        swanTransaction.setLastTime(adapter.getLastTime());
        swanTransaction.setRetriedCount(adapter.getRetriedCount());
        swanTransaction.setCreateTime(adapter.getCreateTime());
        swanTransaction.setTransId(adapter.getTransId());
        swanTransaction.setPattern(adapter.getPattern());
        swanTransaction.setTargetClass(adapter.getTargetClass());
        swanTransaction.setTargetMethod(adapter.getTargetMethod());
        swanTransaction.setVersion(adapter.getVersion());
        return swanTransaction;
    }

}
