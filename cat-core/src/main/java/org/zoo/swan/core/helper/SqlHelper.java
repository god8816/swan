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

package org.zoo.swan.core.helper;

import org.zoo.swan.common.constant.CommonConstant;
import org.zoo.swan.common.utils.DbTypeUtils;

/**
 * SqlHelper.
 *
 * @author dzc
 */
public class SqlHelper {

    /**
     * create table sql.
     *
     * @param driverClassName driverClassName .
     * @param tableName       table name .
     * @return sql.
     */
    public static String buildCreateTableSql(final String driverClassName, final String tableName) {
        String dbType = DbTypeUtils.buildByDriverClassName(driverClassName);
        switch (dbType) {
            case CommonConstant.DB_MYSQL:
                return buildMysql(tableName);
            case CommonConstant.DB_ORACLE:
                return buildOracle(tableName);
            case CommonConstant.DB_SQLSERVER:
                return buildSqlServer(tableName);
            case CommonConstant.DB_POSTGRESQL:
                return buildPostgresql(tableName);
            default:
                throw new RuntimeException("dbType not support ! The current support mysql oracle sqlserver postgresql.");
        }
    }

    private static String buildMysql(final String tableName) {
        return "CREATE TABLE IF NOT EXISTS `" +
                tableName +
                "` (" +
                "  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID'," +
                "  `app_name` varchar(30) NOT NULL COMMENT '项目名称'," +
                "  `trans_id` varchar(64) NOT NULL COMMENT '事务ID'," +
                "  `trans_type` varchar(20) NOT NULL COMMENT '事务类型模式'," +
                "  `target_class` varchar(256)  COMMENT '目标类'," +
                "  `target_method` varchar(128) COMMENT '目标方法'," +
                "  `confirm_method` varchar(128) COMMENT 'confirm方法'," +
                "  `cancel_method` varchar(128)  COMMENT 'cancel方法'," +
                "  `retry_max` tinyint NOT NULL COMMENT  '最大重试次数'," +
                "  `retried_count` tinyint NOT NULL COMMENT '已重试次数'," +
                "  `create_time` datetime NOT NULL COMMENT '创建时间'," +
                "  `last_time` datetime NOT NULL COMMENT '最后处理时间'," +
                "  `version` tinyint NOT NULL COMMENT '版本号'," +
                "  `status` tinyint NOT NULL COMMENT '执行阶段状态  0.开始执行try 1.try阶段完成 2.confirm阶段 3.cancel阶段 4.notice阶段'," +
                "  `invocation` longblob COMMENT '方法入参'," +
                "  `role` tinyint NOT NULL COMMENT '调用角色 1.发起者 2.消费者 3.提供者 4.本地调用 5.内嵌RPC调用 6.SpringCloud 7.http'," +
                "  `pattern` tinyint COMMENT '事务类型模式值'," +
                "  PRIMARY KEY (`id`))";  
    }

    private static String buildOracle(final String tableName) {
        return "CREATE TABLE IF NOT EXISTS `" +
                tableName +
                "` (" +
                "  `id` number(20) NOT NULL COMMENT 'ID'," +
                "  `app_name` varchar(30) NOT NULL COMMENT '项目名称'," +
                "  `trans_id` varchar(64) NOT NULL COMMENT '事务ID'," +
                "  `trans_type` varchar(20) NOT NULL COMMENT '事务类型模式'," +
                "  `target_class` varchar(256)  COMMENT '目标类'," +
                "  `target_method` varchar(128)  COMMENT '目标方法'," +
                "  `confirm_method` varchar(128)  COMMENT 'confirm方法'," +
                "  `cancel_method` varchar(128)  COMMENT 'cancel方法'," +
                "  `retry_max` int(3) NOT NULL COMMENT  '最大重试次数'," +
                "  `retried_count` int(3) NOT NULL COMMENT '已重试次数'," +
                "  `create_time` date NOT NULL COMMENT '创建时间'," +
                "  `last_time` date NOT NULL COMMENT '最后处理时间'," +
                "  `version` int(6) NOT NULL COMMENT '版本号'," +
                "  `status` int(2) NOT NULL  COMMENT '执行阶段状态  0.开始执行try 1.try阶段完成 2.confirm阶段 3.cancel阶段 4.notice阶段'," +
                "  `invocation` BLOB  COMMENT '方法入参'," +
                "  `role` int(2) NOT NULL COMMENT '调用角色 1.发起者 2.消费者 3.提供者 4.本地调用 5.内嵌RPC调用 6.SpringCloud 7.http'," +
                "  `pattern` int(2)  COMMENT '事务类型模式值'," +
                "  PRIMARY KEY (`id`))";
    }

    private static String buildSqlServer(final String tableName) {
        return "CREATE TABLE IF NOT EXISTS `" +
                tableName +
                "` (" +
                "  `id` BIGINT(20) NOT NULL COMMENT 'ID'," +
                "  `app_name` varchar(30) NOT NULL COMMENT '项目名称'," +
                "  `trans_id` varchar(64) NOT NULL COMMENT '事务ID'," +
                "  `trans_type` varchar(20) NOT NULL COMMENT '事务类型模式'," +
                "  `target_class` varchar(256)  COMMENT '目标类'," +
                "  `target_method` varchar(128)  COMMENT '目标方法'," +
                "  `confirm_method` varchar(128)  COMMENT 'confirm方法'," +
                "  `cancel_method` varchar(128)  COMMENT 'cancel方法'," +
                "  `retry_max` int(3) NOT NULL COMMENT  '最大重试次数'," +
                "  `retried_count` int(3) NOT NULL COMMENT '已重试次数'," +
                "  `create_time` datetime NOT NULL COMMENT '创建时间'," +
                "  `last_time` datetime NOT NULL COMMENT '最后处理时间'," +
                "  `version` int(6) NOT NULL COMMENT '版本号'," +
                "  `status` int(2) NOT NULL COMMENT '执行阶段状态 0.开始执行try 1.try阶段完成 2.confirm阶段 3.cancel阶段 4.notice阶段'," +
                "  `invocation` varbinary  COMMENT '方法入参'," +
                "  `role` int(2) NOT NULL COMMENT '调用角色 1.发起者 2.消费者 3.提供者 4.本地调用 5.内嵌RPC调用 6.SpringCloud 7.http'," +
                "  `pattern` int(2)  COMMENT '事务类型模式值'," +
                "  PRIMARY KEY (`id`))";
    }

    private static String buildPostgresql(final String tableName) {
        return " CREATE TABLE IF NOT EXISTS " +
                tableName +
                "(" +
                "  id BIGINT(20) NOT NULL COMMENT 'ID'," +
                "  app_name  varchar(30) NOT NULL COMMENT '项目名称'," +
                "  trans_id       VARCHAR(64) PRIMARY KEY COMMENT '事务ID'," +
                "  trans_type     varchar(20) NOT NULL COMMENT '事务类型模式'," +
                "  target_class   VARCHAR(256) COMMENT '目标类'," +
                "  target_method  VARCHAR(128) COMMENT '目标方法'," +
                "  confirm_method VARCHAR(128) COMMENT 'confirm方法'," +
                "  cancel_method  VARCHAR(128) COMMENT 'cancel方法'," +
                "  `retry_max` SMALLINT NOT NULL COMMENT  '最大重试次数'," +
                "  `retried_count` SMALLINT NOT NULL COMMENT '已重试次数'," +
                "  create_time    TIMESTAMP   NOT NULL COMMENT '创建时间'," +
                "  last_time      TIMESTAMP   NOT NULL COMMENT '最后处理时间'," +
                "  version        SMALLINT    NOT NULL COMMENT '版本号'," +
                "  status         SMALLINT    NOT NULL COMMENT '执行阶段状态  0.开始执行try 1.try阶段完成 2.confirm阶段 3.cancel阶段 4.notice阶段'," +
                "  invocation     BYTEA  COMMENT '方法入参'," +
                "  role           SMALLINT    NOT NULL COMMENT '调用角色 1.发起者 2.消费者 3.提供者 4.本地调用 5.内嵌RPC调用 6.SpringCloud 7.http'," +
                "  pattern        SMALLINT    NOT NULL  COMMENT '事务类型模式值'" +
                ");";

    }

}
