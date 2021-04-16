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

/**
 * The enum Tcc transType Enum.
 *
 * @author dzc
 */
public enum TransTypeEnum {

    /**
     * EN:Tcc tcc pattern enum.
     * CN:TCC 模式
     */
    TCC(1, "try,confirm,cancel"),

    /**
     * EN：Cc tcc pattern enum.
     * CN：不执行try执行confirm,cancel阶段
     */
    CC(2, "confirm,cancel"),
    
    /**
     * EN：saga try,cancel 
     * CN：saga saga模式执行try,cancel阶段
     */
	SAGA(3, "try,cancel"),
	
    /**
     * EN：notice
     * CN：notice 消息通知模式
     */
	NOTICE(4, "notice");
	

    private Integer code;

    private String desc;

    TransTypeEnum(final Integer code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * Gets code.
     *
     * @return the code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * Sets code.
     *
     * @param code the code
     */
    public void setCode(final Integer code) {
        this.code = code;
    }

    /**
     * Gets desc.
     *
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets desc.
     *
     * @param desc the desc
     */
    public void setDesc(final String desc) {
        this.desc = desc;
    }
}
