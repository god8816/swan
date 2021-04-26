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

package org.zoo.swan.core.spi.repository;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zoo.swan.annotation.SwanSPI; 
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.config.SwanRedisConfig;
import org.zoo.swan.common.exception.SwanRuntimeException;
import org.zoo.swan.common.jedis.JedisClient;
import org.zoo.swan.common.jedis.JedisClientCluster;
import org.zoo.swan.common.jedis.JedisClientSentinel;
import org.zoo.swan.common.jedis.JedisClientSingle;
import org.zoo.swan.common.utils.LogUtil;
import org.zoo.swan.common.utils.RepositoryPathUtils;
import org.zoo.swan.core.spi.SwanCoordinatorRepository;

/**
 * redis impl.
 *
 * @author dzc
 */
@SwanSPI("redis")
public class RedisCoordinatorRepository implements SwanCoordinatorRepository {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisCoordinatorRepository.class);

    private JedisClient jedisClient;

    private SwanConfig swanConfig;

  
    @Override
    public boolean isExist(final String key) {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(swanConfig.getApplicationName(), key);
            boolean status = jedisClient.isContainsInRBloomFilter(redisKey);
            return status;
        } catch (Exception e) {
            return false;
        }
    }
    
    
	@Override
	public boolean add(String key) {
		try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(swanConfig.getApplicationName(), key);
            boolean status = jedisClient.addToRBloomFilter(redisKey);
            return status;
        } catch (Exception e) {
            return false;
        }
	}

    @Override
    public void init(final SwanConfig swanConfig) {
        final SwanRedisConfig swanRedisConfig = swanConfig.getSwanRedisConfig();
        try {
    	        this.swanConfig = swanConfig;
            buildJedisPool(swanRedisConfig);
        } catch (Exception e) {
            LogUtil.error(LOGGER, "redis 初始化失败请检查配置:{}", e::getMessage);
            throw new SwanRuntimeException(e);
        }
    }

    private void buildJedisPool(final SwanRedisConfig swanRedisConfig) {
     	Config config = new Config(); 
        if (swanRedisConfig.getCluster()) {
            LogUtil.info(LOGGER, () -> "构建redis cluster模式............");
            ClusterServersConfig clusterServersConfig = swanRedisConfig.getClusterServersConfig();
            String[] addressList = new String[clusterServersConfig.getNodeAddresses().size()];
            config.useClusterServers().addNodeAddress(clusterServersConfig.getNodeAddresses().toArray(addressList));
            config.useClusterServers().setUsername(clusterServersConfig.getUsername());
            config.useClusterServers().setPassword(clusterServersConfig.getPassword());
            config.useClusterServers().setTimeout(clusterServersConfig.getTimeout());
            RedissonClient redissonClient = Redisson.create(config);
            jedisClient = new JedisClientCluster(redissonClient,swanRedisConfig,swanConfig);
        } else if (swanRedisConfig.getSentinel()) {
            LogUtil.info(LOGGER, () -> "构建redis 哨兵模式 ............");
            SentinelServersConfig sentinelServersConfig = swanRedisConfig.getSentinelServersConfig();
            config.useSentinelServers().addSentinelAddress(String.join(",", sentinelServersConfig.getSentinelAddresses()));
            config.useSentinelServers().setUsername(sentinelServersConfig.getUsername());
            config.useSentinelServers().setPassword(sentinelServersConfig.getPassword());
            config.useSentinelServers().setTimeout(sentinelServersConfig.getTimeout());
            RedissonClient redissonClient = Redisson.create(config);
            jedisClient =  new JedisClientSentinel(redissonClient,swanRedisConfig,swanConfig);
        } else if (swanRedisConfig.getSingle()) { 
         	LogUtil.info(LOGGER, () -> "构建redis 单点模式............");
         	SingleServerConfig singleServerConfig = swanRedisConfig.getSingleServerConfig();
         	config.useSingleServer().setAddress(singleServerConfig.getAddress());
         	config.useSingleServer().setPassword(singleServerConfig.getPassword());
         	config.useSingleServer().setUsername(singleServerConfig.getUsername());
         	config.useSingleServer().setTimeout(singleServerConfig.getTimeout());
         	RedissonClient redissonClient = Redisson.create(config);
            jedisClient = new JedisClientSingle(redissonClient,swanRedisConfig,swanConfig);
        }
    }
}
