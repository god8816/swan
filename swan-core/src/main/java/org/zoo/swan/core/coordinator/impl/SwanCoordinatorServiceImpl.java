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

import org.springframework.stereotype.Service;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.utils.StringUtils;
import org.zoo.swan.core.coordinator.SwanCoordinatorService;
import org.zoo.swan.core.helper.SpringBeanUtils;
import org.zoo.swan.core.spi.SwanCoordinatorRepository;

/**
 * impl swanCoordinatorService.
 *
 * @author dzc
 */
@Service("swanCoordinatorService")
public class SwanCoordinatorServiceImpl implements SwanCoordinatorService {

    private SwanCoordinatorRepository coordinatorRepository;

  
    @Override
    public void start(final SwanConfig swanConfig) {
        final String tableName = buildRepositorySuffix("");
        final String appName =  ""; //swanApplicationService.acquireName();
        coordinatorRepository = SpringBeanUtils.getInstance().getBean(SwanCoordinatorRepository.class);
        coordinatorRepository.init(tableName,appName, swanConfig);
    }


    @Override
    public boolean findByTransId(final String transId) {
        return coordinatorRepository.findById(transId);
    }

    private String buildRepositorySuffix(final String repositorySuffix) {
        if (StringUtils.isNoneBlank(repositorySuffix)) {
            return repositorySuffix;
        }
		return repositorySuffix; 
    }

}
