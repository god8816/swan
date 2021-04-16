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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zoo.swan.annotation.SwanSPI;
import org.zoo.swan.common.bean.adapter.CoordinatorRepositoryAdapter;
import org.zoo.swan.common.bean.entity.SwanNoticeSafe;
import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.config.SwanRedisConfig;
import org.zoo.swan.common.enums.RepositorySupportEnum;
import org.zoo.swan.common.exception.SwanException;
import org.zoo.swan.common.exception.SwanRuntimeException;
import org.zoo.swan.common.jedis.JedisClient;
import org.zoo.swan.common.jedis.JedisClientCluster;
import org.zoo.swan.common.jedis.JedisClientSentinel;
import org.zoo.swan.common.jedis.JedisClientSingle;
import org.zoo.swan.common.serializer.ObjectSerializer;
import org.zoo.swan.common.utils.LogUtil;
import org.zoo.swan.common.utils.RepositoryConvertUtils;
import org.zoo.swan.common.utils.RepositoryPathUtils;
import org.zoo.swan.common.utils.StringUtils;
import org.zoo.swan.core.spi.SwanCoordinatorRepository;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
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

    private ObjectSerializer objectSerializer;

    private JedisClient jedisClient;

    private String keyPrefix;

    @Override
    public int create(final SwanTransaction swanTransaction) {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, swanTransaction.getTransId());
            jedisClient.set(redisKey, RepositoryConvertUtils.convert(swanTransaction, objectSerializer));
            return ROWS;
        } catch (Exception e) {
            throw new SwanRuntimeException(e);
        }
    }

    @Override
    public int remove(final String id) {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, id);
            return jedisClient.del(redisKey).intValue();
        } catch (Exception e) {
            throw new SwanRuntimeException(e);
        }
    }

    @Override
    public int update(final SwanTransaction swanTransaction) throws SwanRuntimeException {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, swanTransaction.getTransId());
            swanTransaction.setVersion(swanTransaction.getVersion() + 1);
            swanTransaction.setLastTime(new Date());
            swanTransaction.setRetriedCount(swanTransaction.getRetriedCount());
            jedisClient.set(redisKey, RepositoryConvertUtils.convert(swanTransaction, objectSerializer));
            return ROWS;
        } catch (Exception e) {
            throw new SwanRuntimeException(e);
        }
    }

    @Override
    public int updateParticipant(final SwanTransaction swanTransaction) {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, swanTransaction.getTransId());
            byte[] contents = jedisClient.get(redisKey.getBytes());
            CoordinatorRepositoryAdapter adapter = objectSerializer.deSerialize(contents, CoordinatorRepositoryAdapter.class);
            adapter.setContents(objectSerializer.serialize(swanTransaction.getSwanParticipants()));
            jedisClient.set(redisKey, objectSerializer.serialize(adapter));
        } catch (SwanException e) {
            e.printStackTrace();
            return FAIL_ROWS;
        }
        return ROWS;
    }

    @Override
    public int updateStatus(final String id, final Integer status) {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, id);
            byte[] contents = jedisClient.get(redisKey.getBytes());
            if (contents != null) {
                CoordinatorRepositoryAdapter adapter = objectSerializer.deSerialize(contents, CoordinatorRepositoryAdapter.class);
                adapter.setStatus(status);
                jedisClient.set(redisKey, objectSerializer.serialize(adapter));
            }
        } catch (SwanException e) {
            e.printStackTrace();
            return FAIL_ROWS;
        }
        return ROWS;
    }

    @Override
    public SwanTransaction findById(final String id) {
        try {
            final String redisKey = RepositoryPathUtils.buildRedisKey(keyPrefix, id);
            byte[] contents = jedisClient.get(redisKey.getBytes());
            return RepositoryConvertUtils.transformBean(contents, objectSerializer);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<SwanTransaction> listAll() {
        try {
            List<SwanTransaction> transactions = Lists.newArrayList();
            Set<byte[]> keys = jedisClient.keys((keyPrefix + "*").getBytes());
            for (final byte[] key : keys) {
                byte[] contents = jedisClient.get(key);
                if (contents != null) {
                    transactions.add(RepositoryConvertUtils.transformBean(contents, objectSerializer));
                }
            }
            return transactions;
        } catch (Exception e) {
            throw new SwanRuntimeException(e);
        }
    }

    @Override
    public List<SwanTransaction> listAllByDelay(final Date date) {
        final List<SwanTransaction> swanTransactions = listAll();
        return swanTransactions.stream()
                .filter(tccTransaction -> tccTransaction.getLastTime().compareTo(date) < 0)
                .collect(Collectors.toList());
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

    @Override
    public void setSerializer(final ObjectSerializer objectSerializer) {
        this.objectSerializer = objectSerializer;
    }

    private void buildJedisPool(final SwanRedisConfig swanRedisConfig) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(swanRedisConfig.getMaxIdle());
        config.setMinIdle(swanRedisConfig.getMinIdle());
        config.setMaxTotal(swanRedisConfig.getMaxTotal());
        config.setMaxWaitMillis(swanRedisConfig.getMaxWaitMillis());
        config.setTestOnBorrow(swanRedisConfig.getTestOnBorrow());
        config.setTestOnReturn(swanRedisConfig.getTestOnReturn());
        config.setTestWhileIdle(swanRedisConfig.getTestWhileIdle());
        config.setMinEvictableIdleTimeMillis(swanRedisConfig.getMinEvictableIdleTimeMillis());
        config.setSoftMinEvictableIdleTimeMillis(swanRedisConfig.getSoftMinEvictableIdleTimeMillis());
        config.setTimeBetweenEvictionRunsMillis(swanRedisConfig.getTimeBetweenEvictionRunsMillis());
        config.setNumTestsPerEvictionRun(swanRedisConfig.getNumTestsPerEvictionRun());
        JedisPool jedisPool;
        if (swanRedisConfig.getCluster()) {
            LogUtil.info(LOGGER, () -> "build redis cluster ............");
            final String clusterUrl = swanRedisConfig.getClusterUrl();
            final Set<HostAndPort> hostAndPorts =
                    Lists.newArrayList(Splitter.on(";")
                            .split(clusterUrl))
                            .stream()
                            .map(HostAndPort::parseString).collect(Collectors.toSet());
            JedisCluster jedisCluster = new JedisCluster(hostAndPorts, config);
            jedisClient = new JedisClientCluster(jedisCluster);
        } else if (swanRedisConfig.getSentinel()) {
            LogUtil.info(LOGGER, () -> "build redis sentinel ............");
            final String sentinelUrl = swanRedisConfig.getSentinelUrl();
            final Set<String> hostAndPorts =
                    new HashSet<>(Lists.newArrayList(Splitter.on(";").split(sentinelUrl)));
            JedisSentinelPool pool =
                    new JedisSentinelPool(swanRedisConfig.getMasterName(), hostAndPorts,
                            config, swanRedisConfig.getTimeOut(), swanRedisConfig.getPassword());
            jedisClient = new JedisClientSentinel(pool);
        } else {
            if (StringUtils.isNoneBlank(swanRedisConfig.getPassword())) {
                jedisPool = new JedisPool(config, swanRedisConfig.getHostName(), swanRedisConfig.getPort(), swanRedisConfig.getTimeOut(), swanRedisConfig.getPassword());
            } else {
                jedisPool = new JedisPool(config, swanRedisConfig.getHostName(), swanRedisConfig.getPort(), swanRedisConfig.getTimeOut());
            }
            jedisClient = new JedisClientSingle(jedisPool);
        }
    }

	@Override
	public List<SwanNoticeSafe> countLogsByDelay(Date acquireData,String timeUnit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int removeLogsByDelay(Date acquireSecondsData) {
		// TODO Auto-generated method stub
		return 0;
	}

}
