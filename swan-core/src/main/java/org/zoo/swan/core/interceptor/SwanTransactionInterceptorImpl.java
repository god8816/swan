package org.zoo.swan.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zoo.swan.core.service.SwanTransactionAspectService;



/**
 * SwanTransactionInterceptorImpl.
 *
 * @author dzc
 */
@Component
public class SwanTransactionInterceptorImpl implements SwanTransactionInterceptor {

    private final SwanTransactionAspectService swanTransactionAspectService;

    @Autowired
    public SwanTransactionInterceptorImpl(final SwanTransactionAspectService swanTransactionAspectService) {
        this.swanTransactionAspectService = swanTransactionAspectService;
    }

    @Override
    public Object interceptor(final ProceedingJoinPoint pjp) throws Throwable {
     	return swanTransactionAspectService.invoke(pjp);
    }

}
