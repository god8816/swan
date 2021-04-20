package org.zoo.swan.core.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zoo.swan.common.utils.LogUtil;
import org.zoo.swan.core.service.SwanTransactionAspectService;



/**
 * SwanTransactionInterceptorImpl.
 *
 * @author dzc
 */
@Component
public class SwanTransactionInterceptorImpl implements SwanTransactionInterceptor {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SwanTransactionInterceptorImpl.class);

    private final SwanTransactionAspectService swanTransactionAspectService;

    @Autowired
    public SwanTransactionInterceptorImpl(final SwanTransactionAspectService swanTransactionAspectService) {
        this.swanTransactionAspectService = swanTransactionAspectService;
    }

    @Override
    public Object interceptor(final ProceedingJoinPoint pjp) throws Throwable {
    	    System.out.println(123);
     	try {
//            final RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
//            catTransactionContext = RpcMediator.getInstance().acquire(key -> {
//                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
//                return request.getHeader(key);
//            });
        } catch (IllegalStateException ex) {
            LogUtil.warn(LOGGER, () -> "can not acquire request info:" + ex.getLocalizedMessage());
        }
     	return swanTransactionAspectService.invoke(pjp);
    }

}
