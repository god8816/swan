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

import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zoo.swan.annotation.SwanSPI;
import org.zoo.swan.common.bean.entity.SwanNoticeSafe;
import org.zoo.swan.common.bean.entity.SwanParticipant;
import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.config.SwanDbConfig;
import org.zoo.swan.common.constant.CommonConstant;
import org.zoo.swan.common.enums.RepositorySupportEnum;
import org.zoo.swan.common.exception.SwanException;
import org.zoo.swan.common.exception.SwanRuntimeException;
import org.zoo.swan.common.serializer.ObjectSerializer;
import org.zoo.swan.common.utils.CollectionUtils;
import org.zoo.swan.common.utils.DbTypeUtils;
import org.zoo.swan.common.utils.LogUtil;
import org.zoo.swan.common.utils.RepositoryPathUtils;
import org.zoo.swan.common.utils.StringUtils;
import org.zoo.swan.core.helper.SqlHelper;
import org.zoo.swan.core.spi.SwanCoordinatorRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * jdbc impl .
 *
 * @author dzc
 */
@SwanSPI("db")
public class JdbcCoordinatorRepository implements SwanCoordinatorRepository {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcCoordinatorRepository.class);

    private DataSource dataSource;
    
    private String appName;

    private String tableName;

    private String currentDBType;

    private ObjectSerializer serializer;

    @Override
    public void setSerializer(final ObjectSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public int create(final SwanTransaction swanTransaction) {
        String sql = "insert into " + tableName + "(id,app_name,trans_id,trans_type,target_class,target_method,retry_max,retried_count,"
                + "create_time,last_time,version,status,invoswanion,role,pattern,confirm_method,cancel_method)"
                + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            final byte[] serialize = serializer.serialize(swanTransaction.getSwanParticipants());
            return executeUpdate(sql,swanTransaction.getId(),appName,swanTransaction.getTransId(),swanTransaction.getTransType(), swanTransaction.getTargetClass(), swanTransaction.getTargetMethod(),
            		    swanTransaction.getRetryMax(),swanTransaction.getRetriedCount(), swanTransaction.getCreateTime(), swanTransaction.getLastTime(),
                    swanTransaction.getVersion(), "", serialize, "",
                    swanTransaction.getPattern(), swanTransaction.getConfirmMethod(), swanTransaction.getCancelMethod());
        } catch (SwanException e) {
            e.printStackTrace();
            return FAIL_ROWS;
        }
    }

    @Override
    public int remove(final String id) {
        String sql = "delete from " + tableName + " where trans_id = ? and app_name='"+appName+"'";
        return executeUpdate(sql, id);
    }

    @Override
    public int update(final SwanTransaction swanTransaction) {
        final Integer currentVersion = swanTransaction.getVersion();
        swanTransaction.setLastTime(new Date());
        swanTransaction.setVersion(swanTransaction.getVersion() + 1);
        String sql = "update " + tableName
                + " set last_time = ?,version =?,retried_count =?,invoswanion=?,status=? ,pattern=? where trans_id = ? and version=? and app_name='"+appName+"'";
        try {
            final byte[] serialize = serializer.serialize(swanTransaction.getSwanParticipants());
            return executeUpdate(sql, swanTransaction.getLastTime(),
                    swanTransaction.getVersion(), swanTransaction.getRetriedCount(), serialize,
                    "", swanTransaction.getPattern(),
                    swanTransaction.getTransId(), currentVersion);
        } catch (SwanException e) {
            e.printStackTrace();
            return FAIL_ROWS;
        }
    }

    @Override
    public int updateParticipant(final SwanTransaction swanTransaction) {
        String sql = "update " + tableName + " set invoswanion=?  where trans_id = ?  and app_name='"+appName+"'";
        try {
            final byte[] serialize = serializer.serialize(swanTransaction.getSwanParticipants());
            return executeUpdate(sql, serialize, swanTransaction.getTransId());
        } catch (SwanException e) {
            e.printStackTrace();
            return FAIL_ROWS;
        }
    }

    @Override
    public int updateStatus(final String id, final Integer status) {
        String sql = "update " + tableName + " set status=?  where trans_id = ? and app_name='"+appName+"'";
        return executeUpdate(sql, status, id);
    }

    @Override
    public SwanTransaction findById(final String id) {
        String selectSql = "select * from " + tableName + " where trans_id=? and app_name='"+appName+"'";
        List<Map<String, Object>> list = executeQuery(selectSql, id);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(this::buildByResultMap)
                    .findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public List<SwanTransaction> listAll() {
        String selectSql = "select * from " + tableName + " where app_name='"+appName+"'";
        List<Map<String, Object>> list = executeQuery(selectSql);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(this::buildByResultMap)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<SwanTransaction> listAllByDelay(final Date date) {
        String sb = "select * from " + tableName + " where app_name='"+appName+"' and last_time <? and retried_count<retry_max";
        List<Map<String, Object>> list = executeQuery(sb, date);
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().filter(Objects::nonNull)
                    .map(this::buildByResultMap)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    private SwanTransaction buildByResultMap(final Map<String, Object> map) {
        SwanTransaction swanTransaction = new SwanTransaction();
        swanTransaction.setId((Long) map.get("id"));
        swanTransaction.setAppName((String) map.get("app_name"));
        swanTransaction.setTransId((String) map.get("trans_id"));
        swanTransaction.setTransType((String) map.get("trans_type"));
        swanTransaction.setRetryMax((Integer) map.get("retry_max"));
        swanTransaction.setRetriedCount((Integer) map.get("retried_count"));
        swanTransaction.setCreateTime((Date) map.get("create_time"));
        swanTransaction.setLastTime((Date) map.get("last_time"));
        swanTransaction.setVersion((Integer) map.get("version"));
        swanTransaction.setPattern((Integer) map.get("pattern"));
        byte[] bytes = (byte[]) map.get("invoswanion");
        try {
            final List<SwanParticipant> swanParticipants = serializer.deSerialize(bytes, CopyOnWriteArrayList.class);
            swanTransaction.setSwanParticipants(swanParticipants);
        } catch (SwanException e) {
            e.printStackTrace();
        }catch (Exception e) {
            //修改重试次数到最大重试次数
	    	    if(Objects.nonNull(e.getCause()) && Objects.nonNull(swanTransaction) && e.getCause() instanceof ClassNotFoundException) {
	    	       	   swanTransaction.setRetriedCount(swanTransaction.getRetryMax());
	               update(swanTransaction);
	    	    }
	    	    LogUtil.error(LOGGER, "反序列化日志异常，修改重试次数到最大:{}", e::getMessage);
	    	    e.printStackTrace();
		}
        return swanTransaction;
    }

    @Override
    public void init(final String modelName,final String appName, final SwanConfig txConfig) {
        try {
            final SwanDbConfig swanDbConfig = txConfig.getSwanDbConfig();
            if (swanDbConfig.getDataSource() != null && StringUtils.isBlank(swanDbConfig.getUrl())) {
                dataSource = swanDbConfig.getDataSource();
            } else {
                HikariDataSource hikariDataSource = new HikariDataSource();
                hikariDataSource.setJdbcUrl(swanDbConfig.getUrl());
                hikariDataSource.setDriverClassName(swanDbConfig.getDriverClassName());
                hikariDataSource.setUsername(swanDbConfig.getUsername());
                hikariDataSource.setPassword(swanDbConfig.getPassword());
                hikariDataSource.setMaximumPoolSize(swanDbConfig.getMaxActive());
                hikariDataSource.setMinimumIdle(swanDbConfig.getMinIdle());
                hikariDataSource.setConnectionTimeout(swanDbConfig.getConnectionTimeout());
                hikariDataSource.setIdleTimeout(swanDbConfig.getIdleTimeout());
                hikariDataSource.setMaxLifetime(swanDbConfig.getMaxLifetime());
                hikariDataSource.setConnectionTestQuery(swanDbConfig.getConnectionTestQuery());
                if (swanDbConfig.getDataSourcePropertyMap() != null && !swanDbConfig.getDataSourcePropertyMap().isEmpty()) {
                    swanDbConfig.getDataSourcePropertyMap().forEach(hikariDataSource::addDataSourceProperty);
                }
                dataSource = hikariDataSource;
            }
            this.tableName = RepositoryPathUtils.buildDbTableName(modelName);
            this.appName = appName;
            //save current database type
            this.currentDBType = DbTypeUtils.buildByDriverClassName(swanDbConfig.getDriverClassName());
            executeUpdate(SqlHelper.buildCreateTableSql(swanDbConfig.getDriverClassName(), tableName));
        } catch (Exception e) {
            LogUtil.error(LOGGER, "swan jdbc log init exception please check config:{}", e::getMessage);
            throw new SwanRuntimeException(e);
        }
    }

    @Override
    public String getScheme() {
        return RepositorySupportEnum.DB.getSupport();
    }

    private int executeUpdate(final String sql, final Object... params) {
        Connection connection = null;
        PreparedStatement ps = null;
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, convertDataTypeToDB(params[i]));
                }
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.error("executeUpdate-> " + e.getMessage());
            return FAIL_ROWS;
        } finally {
            close(connection, ps, null);
        }

    }

    private Object convertDataTypeToDB(final Object params) {
        //https://jdbc.postgresql.org/documentation/head/8-date-time.html
        if (CommonConstant.DB_POSTGRESQL.equals(currentDBType) && params instanceof java.util.Date) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(((Date) params).getTime()), ZoneId.systemDefault());
        }
        return params;
    }

    private List<Map<String, Object>> executeQuery(final String sql, final Object... params) {
        Connection connection = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Map<String, Object>> list = null;
        try {
            connection = dataSource.getConnection();
            ps = connection.prepareStatement(sql);
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    ps.setObject(i + 1, convertDataTypeToDB(params[i]));
                }
            }
            rs = ps.executeQuery();
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> rowData = Maps.newHashMap();
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnName(i), rs.getObject(i));
                }
                list.add(rowData);
            }
        } catch (SQLException e) {
            LOGGER.error("executeQuery-> " + e.getMessage());
        } finally {
            close(connection, ps, rs);
        }
        return list;
    }

    private void close(final Connection con, final PreparedStatement ps, final ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        try {
            if (ps != null) {
                ps.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }

    }

	@Override
	public List<SwanNoticeSafe> countLogsByDelay(Date date,String timeUnit) {
		String sb = "select  count(id) as num, target_method,target_class from "+tableName+"  where app_name='"+appName+"' and trans_type='notice' and create_time > ? group by target_method,target_class"; 
        List<Map<String, Object>> list = executeQuery(sb, date);
        if (CollectionUtils.isNotEmpty(list)) {
             List<SwanNoticeSafe> swanNoticeSafeList = new ArrayList<>();
             list.stream().filter(Objects::nonNull)
                    .forEach(item -> {
                      	        SwanNoticeSafe swanNoticeSafe = SwanNoticeSafe.builder()
                                .num((Long)item.get("num"))
                                .targetMethod((String)item.get("target_method"))
                                .targetClass((String)item.get("target_class"))
                                .timeUnit(timeUnit)
                                .build();
                      	      swanNoticeSafeList.add(swanNoticeSafe);
                    });
                    return swanNoticeSafeList;
        }
        return Collections.emptyList();
	}

	@Override
	public int removeLogsByDelay(Date acquireSecondsData) {
		 String sql = "delete from " + tableName + " where  create_time <= ? ";
	     return executeUpdate(sql, acquireSecondsData);
	}
	
}
