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

package org.zoo.swan.common.jedis;

/**
 * JedisClient.
 *
 * @author dzc
 */
public interface JedisClient {

    /**
     * 在布隆过滤器添加key
     * @param key   the key
     * @return the boolean
     */
    boolean addToRBloomFilter(String key);
    
    /**
     * key是否在布隆过滤器中
     * @param key   the key
     * @return the boolean
     */
	boolean isContainsInRBloomFilter(String key);
	
	
	/**
     * 布隆过滤器复位，删老增新，提高布隆过滤器的准确度和释放资源
     * @return the boolean
     */
	boolean resetRBloomFilter();	
}
