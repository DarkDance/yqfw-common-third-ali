package cn.jzyunqi.common.third.ali;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.third.ali.client.AliPayClient;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付宝回调接口
 *
 * @author wiiyaya
 * @date 2021/5/9.
 */
public abstract class AAliPayCbController {

    @Resource
    private AliPayClient aliPayClient;

    /**
     * 支付宝支付回调
     *
     * @param paramMap 请求参数
     */
    @RequestMapping
    @ResponseBody
    public void payApplyWeixinCallback(@RequestBody String params, @RequestParam Map<String, String> paramMap,
                                       @RequestParam(value = "out_trade_no", required = false) String applyPayNo,
                                       @RequestParam(value = "trade_no") String actualPayNo,
                                       @RequestParam(value = "price", required = false) BigDecimal actualPayAmount
                                       ) throws BusinessException {
        boolean checkResult = aliPayClient.payCallBackCheck(paramMap);
        if(checkResult){
            paySuccess(applyPayNo, actualPayNo, actualPayAmount, params);
        }else{
            throw new BusinessException("common_ali_pay_call_back_failed");
        }
    }

    /**
     * 支付成功回调
     *
     * @param applyPayNo 申请支付订单号
     * @param actualPayNo 实际支付订单号
     * @param actualPayAmount 实际支付金额
     * @param returnParam 返回参数
     */
    protected abstract void paySuccess(String applyPayNo, String actualPayNo, BigDecimal actualPayAmount, String returnParam);
}
