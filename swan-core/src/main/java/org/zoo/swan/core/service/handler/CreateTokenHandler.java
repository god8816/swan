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

package org.zoo.swan.core.service.handler;

import javax.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.token.TokenGenerate;
import org.zoo.swan.common.utils.LogUtil;
import org.zoo.swan.common.utils.extension.ExtensionLoader;
import org.zoo.swan.core.helper.SpringBeanUtils;
import org.zoo.swan.core.service.SwanTransactionHandler;



/**
 * 下发唯一的tokenId
 * @author dzc
 */
@Component
public class CreateTokenHandler implements SwanTransactionHandler {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(CreateTokenHandler.class);
      
    @Autowired
    private SwanConfig swanConfig;
    
    @Autowired
    private TokenGenerate tokenGenerate;
    
   
  

    @Override
    public Object handler(final ProceedingJoinPoint point) throws Throwable {
     	try {
            final RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            HttpServletResponse request = ((ServletRequestAttributes) requestAttributes).getResponse();
            request.setHeader(swanConfig.getTokenKey(), tokenGenerate.getTokenId());
        } catch (IllegalStateException ex) {
            LogUtil.warn(LOGGER, () -> "下发token异常:" + ex.getLocalizedMessage());
        }
        return point.proceed();
    }
}
