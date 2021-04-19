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

package org.zoo.swan.core.bootstrap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.core.helper.SpringBeanUtils;
import org.zoo.swan.core.service.SwanInitService;

/**
 * swan bootstrap.
 * @author dzc
 */
public class SwanTransactionBootstrap extends SwanConfig implements ApplicationContextAware {

    private final SwanInitService swanInitService;

    @Autowired
    public SwanTransactionBootstrap(final SwanInitService swanInitService) {
        this.swanInitService = swanInitService;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtils.getInstance().setCfgContext((ConfigurableApplicationContext) applicationContext);
        start(this);
    }

    private void start(final SwanConfig swanConfig) {
        swanInitService.initialization(swanConfig);
    }
}
