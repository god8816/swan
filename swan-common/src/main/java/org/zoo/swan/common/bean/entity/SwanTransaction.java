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
import lombok.Data;
import org.zoo.swan.common.utils.IdWorkerUtils;
import java.io.Serializable;
import java.util.Date;


/**
 * The SwanTransaction.
 *
 * @author dzc
 */
@Data
public class SwanTransaction implements Serializable {

    private static final long serialVersionUID = -6792063780987394917L;

    /**
     *  id.
     */
    private Long id;
    
    /**
     * appName.
     */
    private String appName;
    
    /**
     * transaction id.
     */
    private String transId;
    
  
    /**
     * createTime.
     */
    private Date createTime;

    
    public SwanTransaction() {
        this.transId = IdWorkerUtils.getInstance().createUUID();
        this.createTime = new Date();


    }

    public SwanTransaction(final String transId) {
        this.transId = transId;
        this.createTime = new Date(); 
    }
}
