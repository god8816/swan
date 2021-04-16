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

package org.zoo.swan.core.spi;

import org.zoo.swan.annotation.SwanSPI;
import org.zoo.swan.common.bean.entity.SwanNoticeSafe;
import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.serializer.ObjectSerializer;

import java.util.Date;
import java.util.List;

/**
 * CoordinatorRepository.
 * @author dzc
 */
@SwanSPI
public interface SwanCoordinatorRepository {

    int ROWS = 1;

    int FAIL_ROWS = 0;

    /**
     * create TccTransaction.
     *
     * @param catTransaction {@linkplain SwanTransaction}
     * @return rows 1
     */
    int create(SwanTransaction catTransaction);

    /**
     * delete TccTransaction.
     *
     * @param id  pk
     * @return rows 1
     */
    int remove(String id);

    /**
     * update TccTransaction.
     *
     * @param catTransaction {@linkplain SwanTransaction}
     * @return rows 1 success 0 fail
     */
    int update(SwanTransaction catTransaction);

    /**
     * update  participants.
     *
     * @param catTransaction {@linkplain SwanTransaction}
     * @return rows 1 success 0 fail
     */
    int updateParticipant(SwanTransaction catTransaction);


    /**
     * update status .
     * @param id  pk
     * @param status  status
     * @return rows 1 success 0 fail
     */
    int updateStatus(String id, Integer status);

    /**
     * acquired by id.
     *
     * @param id pk
     * @return {@linkplain SwanTransaction}
     */
    SwanTransaction findById(String id);

    /**
     * list all.
     *
     * @return {@linkplain SwanTransaction}
     */
    List<SwanTransaction> listAll();


    /**
     * 获取延迟多长时间后的事务信息,只要为了防止并发的时候，刚新增的数据被执行.
     *
     * @param date 延迟后的时间
     * @return {@linkplain SwanTransaction}
     */
    List<SwanTransaction> listAllByDelay(Date date);

    /**
     * init.
     *
     * @param tableName tableName
     * @param appName appName
     * @param catConfig {@linkplain SwanConfig}
     */
    void init(String tableName,String appName, SwanConfig catConfig);

    /**
     * set scheme.
     *
     * @return scheme
     */
    String getScheme();

    /**
     * set objectSerializer.
     *
     * @param objectSerializer {@linkplain ObjectSerializer}
     */
    void setSerializer(ObjectSerializer objectSerializer);

    /**
     * 统计延时时间段异常日志数目.
     *
     * @param acquireData 延迟后的时间
     * @return {@linkplain SwanNoticeSafe}
     */
	List<SwanNoticeSafe> countLogsByDelay(Date acquireData,String timeUnit);

    /**
     * 定时删除补偿日志.
     *
     * @param acquireSecondsData 延迟后的时间
     * @return {@linkplain Integer}
     */
	int removeLogsByDelay(Date acquireSecondsData);
}
