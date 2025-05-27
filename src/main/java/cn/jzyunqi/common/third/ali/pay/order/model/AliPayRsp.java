package cn.jzyunqi.common.third.ali.pay.order.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/7/7.
 */
@Getter
@Setter
public class AliPayRsp implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234075832170371461L;

    private String sign;

    @JsonProperty("alipay_trade_query_response")
    private TradeQueryRsp tradeQueryRsp;

    @JsonProperty("alipay_trade_refund_response")
    private TradeRefundRsp tradeRefundRsp;
}
