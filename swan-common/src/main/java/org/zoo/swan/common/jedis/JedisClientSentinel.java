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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.config.SwanRedisConfig;
import org.zoo.swan.common.utils.RepositoryPathUtils;

/**
 * JedisClientSentinel.
 * @author dzc
 */
public class JedisClientSentinel implements JedisClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisClientSentinel.class);

	RedissonClient redissonClient = null;

	/**布隆过滤器*/
	RBloomFilter<String> bloomFilter = null;
		
	public JedisClientSentinel(RedissonClient redissonClient, SwanRedisConfig swanRedisConfig, SwanConfig swanConfig) {
		this.redissonClient = redissonClient;
		bloomFilter = initBloomFilter(redissonClient,swanRedisConfig,swanConfig);
	}

	/**初始化布隆过滤器
	 * @param swanConfig */
	public RBloomFilter<String> initBloomFilter(RedissonClient redissonClient, SwanRedisConfig swanRedisConfig, SwanConfig swanConfig) {
		RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(RepositoryPathUtils.buildRedisKey(swanConfig.getApplicationName(), swanRedisConfig.getRBloomFilterConfig().getName()));
		bloomFilter.tryInit(swanRedisConfig.getRBloomFilterConfig().getTotalNum(),swanRedisConfig.getRBloomFilterConfig().getErrorRate());
		LOGGER.info("布隆过滤器初始化成功,容错率:{},预计已经插入数量:{},容量:{},内存使用量:{}bit",bloomFilter.getFalseProbability(),bloomFilter.count(),bloomFilter.getSize(),bloomFilter.sizeInMemory()); 
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
