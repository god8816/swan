package org.zoo.swan.core.spi;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.zoo.swan.common.config.SwanConfig;
import org.zoo.swan.common.constant.CommonConstant;
import org.zoo.swan.common.token.TokenGenerate;
import org.zoo.swan.core.helper.SpringBeanUtils;

public class SwanUtil {
	
    private static final Logger logger = LoggerFactory.getLogger(SwanUtil.class);
	
    private static SwanConfig swanConfig;
    
    private static TokenGenerate tokenGenerate;
    
    
	static {
		swanConfig = SpringBeanUtils.getInstance().getBean(SwanConfig.class);
		tokenGenerate = SpringBeanUtils.getInstance().getBean(TokenGenerate.class);
	}
	
	
    /**
     * 下发token
     */
    public static void sendToken(){
     	try {
            final RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            HttpServletResponse request = ((ServletRequestAttributes) requestAttributes).getResponse();
            if(CommonConstant.modeType.equals(swanConfig.getModeType())) {
            	  request.setHeader(swanConfig.getTokenKey(), tokenGenerate.getTokenId());
            }else {
                Cookie cookie = new Cookie(swanConfig.getTokenKey(), tokenGenerate.getTokenId());
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                request.addCookie(cookie);
            }
            logger.warn("下发成功框架模式={},key={},value={}",swanConfig.getModeType(),swanConfig.getTokenKey(), tokenGenerate.getTokenId());
        } catch (IllegalStateException ex) {
         	logger.error("下发token异常:" + ex.getLocalizedMessage());
        }
    }

}
