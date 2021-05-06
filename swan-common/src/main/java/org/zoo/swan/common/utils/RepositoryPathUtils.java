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

package org.zoo.swan.common.utils;


/**
 * The RepositoryPathUtils.
 *
 * @author dzc
 */
public class RepositoryPathUtils {

    /**
     * Build redis key string.
     *
     * @param keyPrefix the key prefix
     * @param id        the id
     * @return the string
     */
    public static String buildRedisKey(final String keyPrefix, final String id) {
        return String.join("_", keyPrefix, id);
    }

  
    /**
     * Gets full file name.
     *
     * @param filePath the file path
     * @param id       the id
     * @return the full file name
     */
    public static String getFullFileName(final String filePath, final String id) {
        return String.format("%s/%s", filePath, id);
    }
    
    /**
     * 创建布隆过滤器
     * */
    public static String buildBloomFilterKey(final String mark,final String keyPrefix, final String id) {
        return String.join("_",mark, keyPrefix, id);
    }

}
