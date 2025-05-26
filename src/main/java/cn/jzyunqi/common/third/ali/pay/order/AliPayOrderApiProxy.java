package cn.jzyunqi.common.third.ali.pay.order;

import cn.jzyunqi.common.third.ali.common.AliHttpExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * @author wiiyaya
 * @since 2025/5/26
 */
@AliHttpExchange
@HttpExchange(url = "https://openapi.alipay.com", accept = {"application/json"}, contentType = "application/json")
public interface AliPayOrderApiProxy {
}
