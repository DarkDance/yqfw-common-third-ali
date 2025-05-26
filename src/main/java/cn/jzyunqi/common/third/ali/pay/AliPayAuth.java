package cn.jzyunqi.common.third.ali.pay;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author wiiyaya
 * @since 2025/5/26
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AliPayAuth {

    /**
     * 应用唯一标识
     */
    private String appId;

    /**
     * 商户私钥
     */
    private String privateKey;

    /**
     * 支付宝公钥
     */
    private String publicKey;

    /**
     * 支付回调URL
     */
    private String callbackUrl;
}
