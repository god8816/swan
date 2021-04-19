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

package com.zoo.cat.demo.springcloud.account.service.impl;

import org.zoo.cat.annotation.Cat;
import org.zoo.cat.annotation.TransTypeEnum;
import org.zoo.cat.common.bean.context.CatTransactionContext;
import org.zoo.cat.common.bean.entity.CatTransaction;
import org.zoo.cat.common.exception.CatRuntimeException;
import org.zoo.cat.core.concurrent.threadlocal.CatTransactionContextLocal;
import org.zoo.cat.core.concurrent.threadlocal.RootContext;
import org.zoo.cat.core.service.executor.CatTransactionExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zoo.cat.demo.springcloud.account.dto.AccountDTO;
import com.zoo.cat.demo.springcloud.account.entity.AccountDO;
import com.zoo.cat.demo.springcloud.account.mapper.AccountMapper;
import com.zoo.cat.demo.springcloud.account.service.AccountService;
import com.zoo.cat.demo.springcloud.account.service.InLineService;

/**
 * The type Account service.
 *
 * @author dzc
 */
@Service("accountService")
public class AccountServiceImpl implements AccountService {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);

    private final AccountMapper accountMapper;

    @Autowired
    private InLineService inLineService;

    /**
     * Instantiates a new Account service.
     *
     * @param accountMapper the account mapper
     */
    @Autowired(required = false)
    public AccountServiceImpl(final AccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    @Cat(confirmMethod = "confirm", cancelMethod = "cancel",pattern = TransTypeEnum.TCC)
    public boolean payment(final AccountDTO accountDTO) {
        LOGGER.debug("============执行try付款接口===============");
        accountMapper.update(accountDTO);
        //内嵌调用
        //inLineService.test();
        return Boolean.TRUE;
    }

    @Override
    @Cat(retryMax=10,timeoutMills=2000,pattern = TransTypeEnum.NOTICE)
    public AccountDO findByUserId(final String userId) {
     	//等待5秒模拟超时场景
	    try {
			Thread.sleep(5*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	    //获取事务ID
        String transId = RootContext.getTransId();
        LOGGER.info("transId: " + transId);
     	return accountMapper.findByUserId(userId);
    }

    /**
     * Confirm boolean.
     *
     * @param accountDTO the account dto
     * @return the boolean
     */
    public boolean confirm(final AccountDTO accountDTO) {
        LOGGER.debug("============执行confirm 付款接口===============");
        final int rows = accountMapper.confirm(accountDTO);
        return Boolean.TRUE;
    }


    /**
     * Cancel boolean.
     *
     * @param accountDTO the account dto
     * @return the boolean
     */
    public boolean cancel(final AccountDTO accountDTO) {
        LOGGER.debug("============执行cancel 付款接口===============");
        final int rows = accountMapper.cancel(accountDTO);
        if (rows != 1) {
            throw new CatRuntimeException("取消扣减账户异常！");
        }
        return Boolean.TRUE;
    }
}
