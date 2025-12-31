package cn.jzyunqi.common.third.ali.common;

import cn.jzyunqi.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

/**
 * @author wiiyaya
 * @since 2025/5/26
 */
@Slf4j
@Aspect
@Order
public class AliHttpExchangeWrapper {

    /**
     * 所有标记了@WxHttpExchange的类下所有的方法
     */
    @Pointcut("within(@cn.jzyunqi.common.third.ali.common.AliHttpExchange *)")
    public void aliHttpExchange() {
    }

    @Around(value = "aliHttpExchange() ", argNames = "proceedingJoinPoint")
    public Object Around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.debug("======aliHttpExchange[{}] start=======", proceedingJoinPoint.getSignature().getName());
        Object resultObj;
        try {
            resultObj = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            log.debug("======aliHttpExchange[{}] proceed throw exception=======", proceedingJoinPoint.getSignature().getName());
            throw new BusinessException(e, "common_error_ali_http_exchange_error");
        }
        log.debug("======aliHttpExchange[{}] proceed success=======", proceedingJoinPoint.getSignature().getName());
        //if (resultObj instanceof WeixinRspV1 weixinRsp) {
        //    if (StringUtilPlus.isNotBlank(weixinRsp.getErrorCode()) && !"0".equals(weixinRsp.getErrorCode())) {
        //        throw new BusinessException("common_error_ali_http_exchange_failed", weixinRsp.getErrorCode(), weixinRsp.getErrorMsg());
        //    }
        //} else if (resultObj instanceof WeixinRspV3<?> weixinRsp) {
        //    if (StringUtilPlus.isNotBlank(weixinRsp.getCode()) && !"0".equals(weixinRsp.getCode())) {
        //        throw new BusinessException("common_error_ali_http_exchange_failed", weixinRsp.getCode(), weixinRsp.getMessage());
        //    }
        //}
        log.debug("======aliHttpExchange[{}] end=======", proceedingJoinPoint.getSignature().getName());
        return resultObj;
    }
}
