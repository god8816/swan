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

package org.zoo.swan.core.coordinator.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.utils.StringUtils;
import org.zoo.swan.core.coordinator.SwanCoordinatorService;
import org.zoo.swan.core.helper.SpringBeanUtils;
import org.zoo.swan.core.service.SwanApplicationService;
import org.zoo.swan.core.spi.SwanCoordinatorRepository;

/**
 * impl catCoordinatorService.
 *
 * @author dzc
 */
@Service("catCoordinatorService")
public class SwanCoordinatorServiceImpl implements SwanCoordinatorService {

    private SwanCoordinatorRepository coordinatorRepository;

    private final SwanApplicationService catApplicationService;

    @Autowired
    public SwanCoordinatorServiceImpl(final SwanApplicationService catApplicationService) {
        this.catApplicationService = catApplicationService;
    }

    @Override
    public void start(final SwanConfig catConfig) {
        final String tableName = buildRepositorySuffix(catConfig.getRepositorySuffix());
        final String appName = catApplicationService.acquireName();
        coordinatorRepository = SpringBeanUtils.getInstance().getBean(SwanCoordinatorRepository.class);
        coordinatorRepository.init(tableName,appName, catConfig);
    }

    @Override
    public String save(final SwanTransaction catTransaction) {
        final int rows = coordinatorRepository.create(catTransaction);
        if (rows > 0) {
            return catTransaction.getTransId();
        }
        return null;
    }

    @Override
    public SwanTransaction findByTransId(final String transId) {
        return coordinatorRepository.findById(transId);
    }

    @Override
    public boolean remove(final String id) {
        return coordinatorRepository.remove(id) > 0;
    }

    @Override
    public void update(final SwanTransaction catTransaction) {
        coordinatorRepository.update(catTransaction);
    }

    @Override
    public int updateParticipant(final SwanTransaction catTransaction) {
        return coordinatorRepository.updateParticipant(catTransaction);
    }

    @Override
    public int updateStatus(final String id, final Integer status) {
        return coordinatorRepository.updateStatus(id, status);
    }

    private String buildRepositorySuffix(final String repositorySuffix) {
        if (StringUtils.isNoneBlank(repositorySuffix)) {
            return repositorySuffix;
        } else {
            return catApplicationService.acquireName();
        }
    }

}
