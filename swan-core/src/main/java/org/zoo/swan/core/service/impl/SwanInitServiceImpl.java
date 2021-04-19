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

package org.zoo.swan.core.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.serializer.TransIdGenerate;
import org.zoo.swan.common.utils.LogUtil;
import org.zoo.swan.common.utils.extension.ExtensionLoader;
import org.zoo.swan.core.coordinator.SwanCoordinatorService;
import org.zoo.swan.core.helper.SpringBeanUtils;
import org.zoo.swan.core.logo.SwanLogo;
import org.zoo.swan.core.service.SwanInitService;
import org.zoo.swan.core.spi.SwanCoordinatorRepository;

/**
 * swan init service.
 *
 * @author dzc
 */
@Service("swanInitService")
public class SwanInitServiceImpl implements SwanInitService {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwanInitServiceImpl.class);

    private final SwanCoordinatorService swanCoordinatorService;

    /**
     * Instantiates a new Swan init service.
     *
     * @param swanCoordinatorService the swan coordinator service
     */
    @Autowired
    public SwanInitServiceImpl(final SwanCoordinatorService swanCoordinatorService) {
        this.swanCoordinatorService = swanCoordinatorService;
    }

    /**
     * swan initialization.
     *
     * @param swanConfig {@linkplain SwanConfig}
     */
    @Override
    public void initialization(final SwanConfig swanConfig) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.info("swan shutdown now")));
        try {
            loadSpiSupport(swanConfig);
            swanCoordinatorService.start(swanConfig);
        } catch (Exception ex) {
            LogUtil.error(LOGGER, " swan init exception:{}", ex::getMessage);
            System.exit(1);
        }
        new SwanLogo().logo();
    }

    /**
     * load spi.
     *
     * @param swanConfig {@linkplain SwanConfig}
     */
    private void loadSpiSupport(final SwanConfig swanConfig) {
        //spi id
        final TransIdGenerate transIdGenerate = ExtensionLoader.getExtensionLoader(TransIdGenerate.class)
                .getActivateExtension(swanConfig.getTransIdGenerateSupport());

        //spi repository
        final SwanCoordinatorRepository repository = ExtensionLoader.getExtensionLoader(SwanCoordinatorRepository.class)
                .getActivateExtension(swanConfig.getRepositorySupport());

        repository.setTransIdGenerate(transIdGenerate);

        SpringBeanUtils.getInstance().registerBean(SwanCoordinatorRepository.class.getName(), repository);
    }
}
