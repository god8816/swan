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

package org.zoo.swan.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * The interface swan.
 *
 * @author dzc
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Swan {

    /**
     * trans type enum.
     * CN：事务处理类型
     * @return the pattern enum
     */
    TransTypeEnum pattern() default TransTypeEnum.NOTICE;
    
	/**
     * EN：notice timeout Millisecond
     * CN：通知超时时间毫秒
     * @return timeOut 
     */
    int timeoutMills() default 0;
    
	/**
     * EN：notice retryMax
     * CN：最大重试次数
     * @return retryMax 
     */
    int retryMax() default 10;
    
    /**
     * EN：Confirm method string.
     * CN：confirm提交方法
     * @return the string
     */
    String confirmMethod() default "";

    /**
     * EN：Cancel method string.
     * CN：Cancel 提交方法
     * @return the string
     */
    String cancelMethod() default "";

}