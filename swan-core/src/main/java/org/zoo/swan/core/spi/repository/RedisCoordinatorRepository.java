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

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zoo.swan.annotation.SwanSPI; 
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.config.SwanRedisConfig;
import org.zoo.swan.common.enums.RepositorySupportEnum;
import org.zoo.swan.common.exception.SwanRuntimeException;
import org.zoo.swan.common.jedis.JedisClient;
import org.zoo.swan.common.jedis.JedisClientCluster;
import org.zoo.swan.common.jedis.JedisClientSentinel;
import org.zoo.swan.common.jedis.JedisClientSingle;
import org.zoo.swan.common.token.TokenGenerate;
import org.zoo.swan.common.utils.LogUtil;
import org.zoo.swan.common.utils.RepositoryPathUtils;
import org.zoo.swan.common.utils.StringUtils;
import org.zoo.swan.core.spi.SwanCoordinatorRepository;


import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    private String keyPrefix;

  
    @Override
    public boolean isExist(final String key) {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, key);
            byte[] contents = jedisClient.(redisKey.getBytes());
            return false;
        } catch (Exception e) {
            return false;
        }
    }

  

    @Override
    public void init(final String modelName, final String appName,final SwanConfig swanConfig) {
        keyPrefix = RepositoryPathUtils.buildRedisKeyPrefix(modelName);
        final SwanRedisConfig swanRedisConfig = swanConfig.getSwanRedisConfig();
        try {
            buildJedisPool(swanRedisConfig);
        } catch (Exception e) {
            LogUtil.error(LOGGER, "redis init error please check you config:{}", e::getMessage);
            throw new SwanRuntimeException(e);
        }
    }

    @Override
    public String getScheme() {
        return RepositorySupportEnum.REDIS.getSupport();
    }

   

    private void buildJedisPool(final SwanRedisConfig swanRedisConfig) {
     	//password 等公共信息TODO 。。。。。。。
     	Config config = new Config(); 
        if (swanRedisConfig.getCluster()) {
            LogUtil.info(LOGGER, () -> "构建redis cluster模式............");
            RedissonClient redissonClient = Redisson.create(config);
            jedisClient = new JedisClientSingle(redissonClient);
            jedisClient = new JedisClientCluster(jedisClient);
        } else if (swanRedisConfig.getSentinel()) {
            LogUtil.info(LOGGER, () -> "构建redis哨兵模式 ............");
            SentinelServersConfig sentinelServersConfig = swanRedisConfig.getSentinelServersConfig();
            config.useSentinelServers().setMasterName(sentinelServersConfig.getMasterName())
            //TODO
            .addSentinelAddress("","");
            RedissonClient redissonClient = Redisson.create(config);
            jedisClient =  new JedisClientSentinel(redissonClient);
        } else if (swanRedisConfig.getSingle()) { 
         	LogUtil.info(LOGGER, () -> "构建redis 单点模式............");
         	RedissonClient redissonClient = Redisson.create(config);
            jedisClient = new JedisClientSingle(redissonClient);
        }
    }



	@Override
	public void setTokenGenerate(TokenGenerate transIdGenerate) {
		// TODO Auto-generated method stub
		
	}

}
