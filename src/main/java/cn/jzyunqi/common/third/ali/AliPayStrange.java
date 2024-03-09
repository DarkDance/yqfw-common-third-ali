package cn.jzyunqi.common.third.ali;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.feature.pay.PayHelper;
import cn.jzyunqi.common.third.ali.client.AliPayClient;

import java.math.BigDecimal;

/**
 * @author wiiyaya
 * @date 2024/3/9
 */
public class AliPayStrange implements PayHelper {

    private final AliPayClient aliPayClient;

    public AliPayStrange(AliPayClient aliPayClient){
        this.aliPayClient = aliPayClient;
    }

    @Override
    public Object signForPay(String uniqueNo, String title, String desc, BigDecimal amount, int expiresInMinutes, boolean creditSupport, String openId) throws BusinessException {
        return aliPayClient.signForPay(uniqueNo, title, desc, amount, expiresInMinutes, creditSupport);
    }
}
