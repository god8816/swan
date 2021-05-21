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

package org.zoo.swan.core.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.zoo.swan.core.spi.SwanCoordinatorRepository;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时复位redis布隆过滤器
 * @author dzc
 */
@Service("swanBloomFilterScheduled")
public class SwanBloomFilterScheduled{

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwanBloomFilterScheduled.class);
 
    ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(1);
    
    private SwanCoordinatorRepository swanCoordinatorRepository;
 

    public void init(SwanCoordinatorRepository swanCoordinatorRepository){
    	  this.swanCoordinatorRepository = swanCoordinatorRepository;
    	  selfRecovery();
    }


    /**
     * 定时清除掉redis布隆过滤器
     */
    private void selfRecovery() {
	    	scheduledThreadPool.scheduleWithFixedDelay(() -> {
	            try {
	            	   
	             	LOGGER.debug("清理swan存储");
	             	swanCoordinatorRepository.reset();
	            } catch (Exception e) {
	                LOGGER.error("swan存储清理异常:", e);
	            } 
	    },0,1, TimeUnit.HOURS);
    }
}
