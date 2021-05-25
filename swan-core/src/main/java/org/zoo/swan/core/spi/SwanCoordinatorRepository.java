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

package org.zoo.swan.core.spi;

import org.zoo.swan.annotation.SwanSPI;
import org.zoo.swan.common.config.SwanConfig;

/**
 * CoordinatorRepository.
 * @author dzc
 */
@SwanSPI
public interface SwanCoordinatorRepository {
	
    /**
     * key保存
     * @param key key
     * @return  
     */
    boolean add(String key);

    /**
     * key是否存在
     * @param key key
     * @return  
     */
    boolean isExist(String key);

  
    /**
     * 环境初始化
     * @param swanConfig  
     */
    void init(SwanConfig swanConfig);
    
    
	/**
     * 重置存储，提高准确度和释放资源
     * @return the boolean
     */
	boolean reset();

    /**
     * 获取分布式锁
     * @param <T>
     * @param tokenId  tokenId
     */
	<T> T getLock(String tokenId);

    /**
     * 分布式锁解锁
     * @param <T>
     */
	<T> void unlock(T t);	

}
