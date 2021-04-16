/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.zoo.swan.core.utils;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The type JoinPointUtils utils.
 *
 * @author dzc
 */
public class JoinPointUtils {

	/**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JoinPointUtils.class);
    


    public static Method getMethod(ProceedingJoinPoint point) {
     	MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        try {
			 method = point.getTarget().getClass().getDeclaredMethod(signature.getName(), method.getParameterTypes());
		} catch (NoSuchMethodException e) {
			LOGGER.error("获取注解标记"); 
			e.printStackTrace();
		} catch (SecurityException e) {
			LOGGER.error("获取注解标记"); 
			e.printStackTrace();
		}
        return method;
    }

  
}
