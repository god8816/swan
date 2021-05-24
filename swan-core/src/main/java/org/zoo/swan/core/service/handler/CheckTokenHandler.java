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

import java.lang.reflect.Method;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.zoo.swan.annotation.Swan;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.constant.CommonConstant;
import org.zoo.swan.common.exception.SwanException;
import org.zoo.swan.core.coordinator.SwanCoordinatorService;
import org.zoo.swan.core.service.SwanTransactionHandler;
import org.zoo.swan.core.utils.JoinPointUtils;

import com.alibaba.fastjson.JSON;


/**
 * 检查下发Token是否重复
 * @author dzc
 */
@Component
public class CheckTokenHandler implements SwanTransactionHandler {
	
    private static final Logger logger = LoggerFactory.getLogger(CheckTokenHandler.class);
      
    @Autowired
    private SwanConfig swanConfig;
    
    @Autowired
    private SwanCoordinatorService swanCoordinatorService;
    
    @Override
    public Object handler(final ProceedingJoinPoint point) throws Throwable {
    	    Method method = JoinPointUtils.getMethod(point);
        final Swan swan = method.getAnnotation(Swan.class);
        final String errorMsg = swan.errorMsg();
        final RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        
        String tokenValue = request.getHeader(swanConfig.getTokenKey());
        if(!CommonConstant.Mode.equals(swanConfig.getMode())) {
         	Cookie[] cookies = request.getCookies();
         	if(cookies != null && cookies.length > 0){
         	     for (Cookie cookie : cookies){
         	     	tokenValue = cookie.getValue();
         	     	break;
         	     }
         	 } 
        }
        
        boolean isExistStatus = swanCoordinatorService.isExist(tokenValue);
        if(!isExistStatus) {
         	Boolean saveStatus = swanCoordinatorService.add(tokenValue);
         	logger.info("用户TokenID保存状态,"+swanConfig.getTokenKey()+"=="+tokenValue+",状态："+saveStatus);
            return point.proceed();
        }
        logger.info("用户重复提交,"+swanConfig.getTokenKey()+"=="+tokenValue);
        
        SwanException swanException = new SwanException(-1,errorMsg);
        String errorMsgObj = JSON.toJSONString(swanException);
        HttpServletResponse response  = ((ServletRequestAttributes) requestAttributes).getResponse();
		response.setContentType("application/json; charset=utf-8");
        ServletOutputStream sos = response.getOutputStream();
		sos.write(errorMsgObj.getBytes());
		return null;
    }
}
