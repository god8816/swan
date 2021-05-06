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

import java.util.Objects;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.constant.CommonConstant;
import org.zoo.swan.common.utils.RepositoryPathUtils;

/**
 * JedisClientSentinel.
 * @author dzc
 */
public class JedisClientSentinel implements JedisClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(JedisClientSentinel.class);

    private RedissonClient redissonClient = null;
    
    private SwanConfig swanConfig = null;
    
	/**历史布隆过滤器*/
	RBloomFilter<String> historyBloomFilter = null;
	
	/**当前布隆过滤器*/
	RBloomFilter<String> nowBloomFilter = null;
	
	
	public JedisClientSentinel(RedissonClient redissonClient, SwanConfig swanConfig) {
		this.redissonClient = redissonClient;
		this.swanConfig = swanConfig;
		initBloomFilter();	
	}

	/**
	 * 初始化布隆过滤器
	 * @param swanConfig
	 *  */
	public synchronized RBloomFilter<String> initBloomFilter() {
		//当前布隆过滤器初始化
		RBloomFilter<String> nowBloomFilter = redissonClient.getBloomFilter(RepositoryPathUtils.buildBloomFilterKey(CommonConstant.Now,swanConfig.getApplicationName(), swanConfig.getSwanRedisConfig().getRBloomFilterConfig().getName()));
		nowBloomFilter.tryInit(swanConfig.getSwanRedisConfig().getRBloomFilterConfig().getTotalNum(),swanConfig.getSwanRedisConfig().getRBloomFilterConfig().getErrorRate());
		
		//历史布隆过滤器初始化
		RBloomFilter<String> historyBloomFilter = redissonClient.getBloomFilter(RepositoryPathUtils.buildBloomFilterKey(CommonConstant.History,swanConfig.getApplicationName(), swanConfig.getSwanRedisConfig().getRBloomFilterConfig().getName()));
		nowBloomFilter.tryInit(swanConfig.getSwanRedisConfig().getRBloomFilterConfig().getTotalNum(),swanConfig.getSwanRedisConfig().getRBloomFilterConfig().getErrorRate());
		
		LOGGER.info("布隆过滤器初始化成功,容错率:{},预计已经插入数量:{},容量:{},内存使用量:{}bytes",nowBloomFilter.getFalseProbability(),nowBloomFilter.count(),nowBloomFilter.getSize(),nowBloomFilter.sizeInMemoryAsync()); 
		this.nowBloomFilter = nowBloomFilter;
		this.historyBloomFilter = historyBloomFilter;
		return nowBloomFilter;
	}

	@Override
	public boolean addToRBloomFilter(String key) {
		if(Objects.isNull(nowBloomFilter)) {
			initBloomFilter();
		}
		return nowBloomFilter.add(key);
	}

	@Override
	public boolean isContainsInRBloomFilter(String key) {
		if(Objects.isNull(nowBloomFilter) && Objects.isNull(historyBloomFilter)) {
			initBloomFilter();
		}
		
		return nowBloomFilter.contains(key)==true?nowBloomFilter.contains(key):historyBloomFilter.contains(key);
	}

	@Override
	public boolean resetRBloomFilter() {
		//当布隆过滤器的容量到达1%的时候就定时清理，进一步降低重复的概率. 算法：布隆过滤器redis实现是容量扩大76倍，此系统目标清理是使用容量达到目标的10%清理。
		if(nowBloomFilter.count()/nowBloomFilter.getSize()>0.001) { 
			//先删除历史的布隆过滤器
			historyBloomFilter.delete();
			//在将当前布隆过滤器修改为历史布隆过滤器
			nowBloomFilter.rename(RepositoryPathUtils.buildBloomFilterKey(CommonConstant.History,swanConfig.getApplicationName(), swanConfig.getSwanRedisConfig().getRBloomFilterConfig().getName()));
			 
			initBloomFilter();	
			LOGGER.debug("清理swan存储成功");
		}
		return true;
	}
}
