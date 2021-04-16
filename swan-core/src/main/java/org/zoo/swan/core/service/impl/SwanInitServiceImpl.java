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
import org.zoo.swan.common.serializer.ObjectSerializer;
import org.zoo.swan.common.utils.LogUtil;
import org.zoo.swan.common.utils.extension.ExtensionLoader;
import org.zoo.swan.core.coordinator.SwanCoordinatorService;
import org.zoo.swan.core.helper.SpringBeanUtils;
import org.zoo.swan.core.logo.SwanLogo;
import org.zoo.swan.core.service.SwanInitService;
import org.zoo.swan.core.spi.SwanCoordinatorRepository;

/**
 * cat init service.
 *
 * @author dzc
 */
@Service("catInitService")
public class SwanInitServiceImpl implements SwanInitService {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwanInitServiceImpl.class);

    private final SwanCoordinatorService catCoordinatorService;

    /**
     * Instantiates a new Cat init service.
     *
     * @param catCoordinatorService the cat coordinator service
     */
    @Autowired
    public SwanInitServiceImpl(final SwanCoordinatorService catCoordinatorService) {
        this.catCoordinatorService = catCoordinatorService;
    }

    /**
     * cat initialization.
     *
     * @param catConfig {@linkplain SwanConfig}
     */
    @Override
    public void initialization(final SwanConfig catConfig) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> LOGGER.info("cat shutdown now")));
        try {
            loadSpiSupport(catConfig);
            catCoordinatorService.start(catConfig);
        } catch (Exception ex) {
            LogUtil.error(LOGGER, " cat init exception:{}", ex::getMessage);
            System.exit(1);
        }
        new SwanLogo().logo();
    }

    /**
     * load spi.
     *
     * @param catConfig {@linkplain SwanConfig}
     */
    private void loadSpiSupport(final SwanConfig catConfig) {
        //spi serialize
        final ObjectSerializer serializer = ExtensionLoader.getExtensionLoader(ObjectSerializer.class)
                .getActivateExtension(catConfig.getSerializer());

        //spi repository
        final SwanCoordinatorRepository repository = ExtensionLoader.getExtensionLoader(SwanCoordinatorRepository.class)
                .getActivateExtension(catConfig.getRepositorySupport());

        repository.setSerializer(serializer);

        SpringBeanUtils.getInstance().registerBean(SwanCoordinatorRepository.class.getName(), repository);
    }
}
