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

package org.zoo.swan.common.bean.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * this cat transaction participant .
 *
 * @author dzc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwanParticipant implements Serializable {

    private static final long serialVersionUID = -2590970715288987627L;

    private String transId;

    private Integer status;

    private SwanInvocation confirmCatInvocation;

    private SwanInvocation cancelCatInvocation;
    
    private SwanInvocation noticeCatInvocation;

    public SwanParticipant(final String transId,
                            final SwanInvocation confirmCatInvocation,
                            final SwanInvocation cancelCatInvocation) {
        this.transId = transId;
        this.confirmCatInvocation = confirmCatInvocation;
        this.cancelCatInvocation = cancelCatInvocation;

    }
    
    public SwanParticipant(final String transId,
		            final SwanInvocation noticeCatInvocation) {
		this.transId = transId;
		this.noticeCatInvocation = noticeCatInvocation;
	}

}
