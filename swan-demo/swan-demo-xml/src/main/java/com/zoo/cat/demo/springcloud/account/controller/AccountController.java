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

package com.zoo.cat.demo.springcloud.account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.zoo.cat.annotation.Cat;
import org.zoo.cat.annotation.TransTypeEnum;

import com.zoo.cat.demo.springcloud.account.dto.AccountDTO;
import com.zoo.cat.demo.springcloud.account.service.AccountService;

import java.math.BigDecimal;

/**
 * AccountController.
 * @author dzc
 */
@RestController
@RequestMapping("/account")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping("/payment")
    public Boolean save(@RequestBody AccountDTO accountDO) {
        return accountService.payment(accountDO);
    }

    @RequestMapping("/findByUserId")
    @Cat(retryMax=10,timeoutMills=2000,pattern = TransTypeEnum.NOTICE)
    public BigDecimal findByUserId(@RequestParam("userId") String userId) {
        return accountService.findByUserId(userId)!=null?accountService.findByUserId(userId).getBalance():new BigDecimal(0);
    }




}
