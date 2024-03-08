package cn.jzyunqi.common.third.ali.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/5/30.
 */
@Getter
@Setter
public class TradeAppPayResult implements Serializable {
    @Serial
    private static final long serialVersionUID = 425687588902210592L;

    /**
     * 申请支付单号
     */
    private String applyPayNo;

    /**
     * 签名
     */
    private String sign;
}
