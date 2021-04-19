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
        String dbType = "";
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
        return "";  
    }

    private static String buildOracle(final String tableName) {
        return "";
    }

    private static String buildSqlServer(final String tableName) {
        return "";
    }

    private static String buildPostgresql(final String tableName) {
        return "";

    }

}
