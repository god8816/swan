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

package org.zoo.swan.common.bean.variate;


import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zoo.swan.common.bean.entity.SwanNoticeSafe;
import org.zoo.swan.common.config.SwanConfig;


/**
 * EN：SwanDegradation .
 * CN：系统降级判断
 * @author dzc
 */
public class SwanDegradation implements Serializable {

    private static final long serialVersionUID = -5108578223428529356L;
    
    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwanDegradation.class);
    

    public static volatile List<SwanNoticeSafe>  swanNoticeSafe;
    
    
    /**
     * EN: is start degradation.
     * CN: 是否开启降级
     * @param swanConfig swan配置
     * @param className className
     * @param methodName methodName
     */
    public static Boolean isStartDegradation(SwanConfig swanConfig,String className,String methodName) {
    	    if(swanConfig.getStarted()==false) {
    	    	  return false;
    	    }else {
        	    
		return true;
        }
    }
    
}
