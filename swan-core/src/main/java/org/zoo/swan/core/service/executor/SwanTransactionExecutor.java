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

package org.zoo.swan.core.service.executor;

import com.google.common.collect.Lists;
import org.aspectj.lang.ProceedingJoinPoint; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zoo.swan.annotation.Swan;
import org.zoo.swan.annotation.TransTypeEnum;
import org.zoo.swan.common.bean.context.SwanTransactionContext;
import org.zoo.swan.common.bean.entity.SwanInvocation;
import org.zoo.swan.common.bean.entity.SwanParticipant;
import org.zoo.swan.common.bean.entity.SwanTransaction;
import org.zoo.swan.common.enums.SwanActionEnum;
import org.zoo.swan.common.enums.SwanRoleEnum;
import org.zoo.swan.common.exception.SwanException;
import org.zoo.swan.common.exception.SwanRuntimeException; 
import org.zoo.swan.common.utils.CollectionUtils;
import org.zoo.swan.common.utils.LogUtil;
import org.zoo.swan.common.utils.StringUtils;
import org.zoo.swan.core.cache.SwanTransactionGuavaCacheManager;
import org.zoo.swan.core.concurrent.threadlocal.SwanTransactionContextLocal;
import org.zoo.swan.core.disruptor.publisher.SwanTransactionEventPublisher;
import org.zoo.swan.core.reflect.SwanReflector;
import org.zoo.swan.core.utils.JoinPointUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Optional; 


/**
 * this is swan transaction manager.
 *
 * @author dzc
 */
@Component
public class SwanTransactionExecutor {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwanTransactionExecutor.class);

    /**
     * transaction save threadLocal.
     */
    private static final ThreadLocal<SwanTransaction> CURRENT = new ThreadLocal<>();

    private final SwanTransactionEventPublisher swanTransactionEventPublisher;

    /**
     * Instantiates a new swan transaction executor.
     *
     * @param swanTransactionEventPublisher the swan transaction event publisher
     */
    @Autowired
    public SwanTransactionExecutor(final SwanTransactionEventPublisher swanTransactionEventPublisher) {
        this.swanTransactionEventPublisher = swanTransactionEventPublisher;
    }

    /**
     * transaction preTry.
     *
     * @param point cut point.
     * @return TccTransaction swan transaction
     */
    public SwanTransaction preTry(final ProceedingJoinPoint point) {
        LogUtil.debug(LOGGER, () -> "......swan transaction starter....");
        //build tccTransaction
        final SwanTransaction catTransaction = buildCatTransaction(point, SwanRoleEnum.START.getCode(), null);
      
        return catTransaction;
    }
    
    /**
     * transaction preTry.
     *
     * @param point cut point.
     * @return TccTransaction swan transaction
     */
    public SwanTransaction preTryNotice(final ProceedingJoinPoint point) {
        LogUtil.debug(LOGGER, () -> "......swan transaction starter....");
        //build noticeTransaction
        final SwanTransaction catTransaction = buildCatTransaction(point, SwanRoleEnum.START.getCode(), null);
         
        return catTransaction;
    }

    /**
     * this is Participant transaction preTry.
     *
     * @param context transaction context.
     * @param point   cut point
     * @return TccTransaction swan transaction
     */
    public SwanTransaction preTryParticipant(final SwanTransactionContext context, final ProceedingJoinPoint point) {
        LogUtil.debug(LOGGER, "participant swan transaction start..：{}", context::toString);
        final SwanTransaction catTransaction = buildCatTransaction(point, SwanRoleEnum.PROVIDER.getCode(), context.getTransId());
        
        return catTransaction;
    }
    

    /**
     * Call the confirm method and basically if the initiator calls here call the remote or the original method
     * However, the context sets the call confirm
     * The remote service calls the confirm method.
     *
     * @param currentTransaction {@linkplain SwanTransaction}
     * @return the object
     * @throws SwanRuntimeException ex
     */
    public Object confirm(final SwanTransaction currentTransaction) throws SwanRuntimeException {
        LogUtil.debug(LOGGER, () -> "swan transaction confirm .......！start");
        
        return null;
    }

    /**
     * cancel transaction.
     *
     * @param currentTransaction {@linkplain SwanTransaction}
     * @return the object
     */
    public Object cancel(final SwanTransaction currentTransaction) {
        LogUtil.debug(LOGGER, () -> "tcc cancel ...........start!");
        
        return null;
    }
    
    
    /**
     * transaction preNotice.
     *
     * @param point cut point.
     * @return TccTransaction swan transaction
     */
    public SwanTransaction preNotice(final ProceedingJoinPoint point) {
        LogUtil.debug(LOGGER, () -> "......swan transaction starter....");
        //build tccTransaction
      
        return null;
    }
    
    /**
     * this is Participant transaction preNoticeParticipant.
     *
     * @param context transaction context.
     * @param point   cut point
     * @return CatTransaction swan transaction
     */
    public SwanTransaction preNoticeParticipant(final SwanTransactionContext context, final ProceedingJoinPoint point) {
        LogUtil.debug(LOGGER, "participant swan transaction start..：{}", context::toString);
        final SwanTransaction catTransaction = buildCatTransaction(point, SwanRoleEnum.PROVIDER.getCode(), context.getTransId());
        
        return catTransaction;
    }

    /**
     * acquired by threadLocal.
     *
     * @return {@linkplain SwanTransaction}
     */
    public SwanTransaction getCurrentTransaction() {
        return CURRENT.get();
    }


    /**
     * clean threadLocal help gc.
     */
    public void remove() {
        CURRENT.remove();
    }

    /**
     * add participant.
     *
     * @param catParticipant {@linkplain SwanParticipant}
     */
    public void enlistParticipant(final SwanParticipant catParticipant) {
        if (Objects.isNull(catParticipant)) {
            return;
        }
        Optional.ofNullable(getCurrentTransaction())
                .ifPresent(c -> {
                    c.registerParticipant(catParticipant);
               
                });
    }

    /**
     * when nested transaction add participant.
     *
     * @param transId          key
     * @param catParticipant {@linkplain SwanParticipant}
     */
    public void registerByNested(final String transId, final SwanParticipant catParticipant) {
        if (Objects.isNull(catParticipant)
                || Objects.isNull(catParticipant.getCancelCatInvocation())
                || Objects.isNull(catParticipant.getConfirmCatInvocation())) {
            return;
        }
        final SwanTransaction catTransaction =
                SwanTransactionGuavaCacheManager.getInstance().getCatTransaction(transId);
        Optional.ofNullable(catTransaction)
                .ifPresent(transaction -> {
                    transaction.registerParticipant(catParticipant);
                 
                });
    }

    public void executeHandler(final boolean success, final SwanTransaction currentTransaction, final List<SwanParticipant> failList) {
        SwanTransactionGuavaCacheManager.getInstance().removeByKey(currentTransaction.getTransId());
         
    }

    private List<SwanParticipant> filterPoint(final SwanTransaction currentTransaction) {
        final List<SwanParticipant> catParticipants = currentTransaction.getSwanParticipants();
         
        return catParticipants;
    }

    private SwanTransaction buildCatTransaction(final ProceedingJoinPoint point, final int role, final String transId) {
        SwanTransaction catTransaction;
        if (StringUtils.isNoneBlank(transId)) {
            catTransaction = new SwanTransaction(transId);
        } else {
            catTransaction = new SwanTransaction();
        }
      
        Method method = JoinPointUtils.getMethod(point);
        Class<?> clazz = point.getTarget().getClass();
        Object[] args = point.getArgs();
    
   
    	    final Swan swan = method.getAnnotation(Swan.class);
        final TransTypeEnum pattern = swan.pattern();
        if(Objects.isNull(pattern)) {
         	LOGGER.error("事务补偿模式必须在TCC,SAGA,CC,NOTICE中选择"); 
        }
        catTransaction.setTargetClass(clazz.getName());
        catTransaction.setTargetMethod(method.getName());
        catTransaction.setPattern(pattern.getCode());
        catTransaction.setRetryMax(swan.retryMax());
        catTransaction.setTransType(swan.pattern().getDesc());
        catTransaction.setTimeoutMills(swan.timeoutMills());
        
        String targetMethod = method.getName(); 
        String confirmMethodName = swan.confirmMethod();
        String cancelMethodName = swan.cancelMethod();
        
        //判断是否是通知模式
        if(swan.pattern().getCode()==TransTypeEnum.NOTICE.getCode()) {
            SwanInvocation noticeInvocation = null;
            if (StringUtils.isNoneBlank(targetMethod)) {
                catTransaction.setTargetMethod(targetMethod);
                noticeInvocation = new SwanInvocation(clazz, targetMethod, method.getParameterTypes(), args);
            }
            final SwanParticipant catParticipant = new SwanParticipant(catTransaction.getTransId(), noticeInvocation);
            catTransaction.registerParticipant(catParticipant);
           
            return catTransaction;
        }else {
          	SwanInvocation confirmInvocation = null;
	        SwanInvocation cancelInvocation = null;
	        if (StringUtils.isNoneBlank(confirmMethodName)) {
	             catTransaction.setConfirmMethod(confirmMethodName);
	             confirmInvocation = new SwanInvocation(clazz, confirmMethodName, method.getParameterTypes(), args);
	        }
	        if (StringUtils.isNoneBlank(cancelMethodName)) {
	             catTransaction.setCancelMethod(cancelMethodName);
	             cancelInvocation = new SwanInvocation(clazz, cancelMethodName, method.getParameterTypes(), args);
	        }
	        final SwanParticipant catParticipant = new SwanParticipant(catTransaction.getTransId(), confirmInvocation, cancelInvocation);
	        catTransaction.registerParticipant(catParticipant);
	        return catTransaction;
        }
    }

    /**
     * EN: notice transaction. 
     * CN：消息通知事务补偿
     * @param currentTransaction {@linkplain SwanTransaction}
     * @return the object
     */
	public Object notice(final SwanTransaction currentTransaction) {
		    LogUtil.debug(LOGGER, () -> "notice compensate...........start!");
	        if (Objects.isNull(currentTransaction) || CollectionUtils.isEmpty(currentTransaction.getSwanParticipants())) {
	            return null;
	        }
 
	        final List<SwanParticipant> catParticipants = filterPoint(currentTransaction);
	        boolean success = true;
	        if (CollectionUtils.isNotEmpty(catParticipants)) {
	            List<SwanParticipant> failList = Lists.newArrayListWithCapacity(catParticipants.size());
	            List<Object> results = Lists.newArrayListWithCapacity(catParticipants.size());
	            for (SwanParticipant catParticipant : catParticipants) {
	                try {
	                  	Long startTime = System.currentTimeMillis();
	                    final Object result = SwanReflector.executor(catParticipant.getTransId(),
	                            SwanActionEnum.NOTICEING,
	                            catParticipant.getNoticeCatInvocation());
	                    Long endTime = System.currentTimeMillis();
	                    if(currentTransaction.getTimeoutMills()>0 && endTime-startTime>currentTransaction.getTimeoutMills()) {
	                      	throw new SwanException("method "+currentTransaction.getTargetMethod()+" timeout..");
	                    }
	                    results.add(result);
	                } catch (Exception e) {
	                    LogUtil.error(LOGGER, "execute notice ex:{}", () -> e);
	                    success = false;
	                    failList.add(catParticipant);
	                } finally {
	                    SwanTransactionContextLocal.getInstance().remove();
	                }
	            }
	            //删除补偿
	            executeHandler(success, currentTransaction, failList);
	            return results.get(0);
	        }
	        return null;
	}

}
