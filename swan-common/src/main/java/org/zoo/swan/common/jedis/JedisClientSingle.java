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

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.zoo.swan.common.config.SwanRedisConfig;

/**
 * JedisClientSingle.
 * @author dzc
 */
public class JedisClientSingle implements JedisClient {
	
	RedissonClient redissonClient = null;

	/**布隆过滤器*/
	RBloomFilter<String> bloomFilter = null;
		
	public JedisClientSingle(RedissonClient redissonClient, SwanRedisConfig swanRedisConfig) {
		this.redissonClient = redissonClient;
		initBloomFilter(redissonClient,swanRedisConfig);
	}

	/**初始化布隆过滤器*/
	public RBloomFilter<String> initBloomFilter(RedissonClient redissonClient, SwanRedisConfig swanRedisConfig) {
		RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(swanRedisConfig.getRBloomFilterConfig().getName());
		bloomFilter.tryInit(swanRedisConfig.getRBloomFilterConfig().getTotalNum(),swanRedisConfig.getRBloomFilterConfig().getErrorRate());
		return bloomFilter;
	}

	@Override
	public boolean addToRBloomFilter(String key) {
		return bloomFilter.add(key);
	}

	@Override
	public boolean isContainsInRBloomFilter(String key) {
		// TODO Auto-generated method stub
		return bloomFilter.contains(key);
	}
   
}
