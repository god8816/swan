package org.zoo.swan.core.concurrent.threadlocal;

import java.util.Objects;

import org.zoo.swan.common.bean.context.SwanTransactionContext;

/**
 * transactionContext in threadLocal.
 * @author dzc
 */
public class RootContext {

	  private RootContext() {

	  }
	  
	  
     /**
      * Gets transId.
      *
      * @return the transId
      */
     public static String getTransId() {
     	SwanTransactionContext catTransactionContext = SwanTransactionContextLocal.getInstance().get();
     	if(Objects.nonNull(catTransactionContext)) {
     		  String transId = catTransactionContext.getTransId();
     	      return transId;
     	}else {
     		  return null;
     	}
    }
}
