package cn.jzyunqi.common.third.ali.pay.order.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author wiiyaya
 * @date 2018/5/30.
 */
@Getter
@Setter
public class TradeQueryResult implements Serializable {
    @Serial
    private static final long serialVersionUID = -6395253754694563533L;

    /**
     * 实际支付单号
     */
    private String tradeNo;

    /**
     * 实际支付金额
     */
    private BigDecimal totalAmount;

    /**
     * 查询返回字符串
     */
    private String responseStr;
}
