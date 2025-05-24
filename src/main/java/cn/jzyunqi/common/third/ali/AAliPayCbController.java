//package cn.jzyunqi.common.third.ali;
//
//import cn.jzyunqi.common.exception.BusinessException;
//import jakarta.annotation.Resource;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import java.math.BigDecimal;
//import java.util.Map;
//
///**
// * 支付宝回调接口
// *
// * @author wiiyaya
// * @since 2021/5/9.
// */
//public abstract class AAliPayCbController implements PayCallbackProcessor {
//
//    @Resource
//    private AliPayStrange aliPayStrange;
//
//    /**
//     * 支付宝支付回调
//     *
//     * @param paramMap 请求参数
//     */
//    @RequestMapping
//    @ResponseBody
//    public void payApplyWeixinCallback(@RequestBody String params, @RequestParam Map<String, String> paramMap,
//                                       @RequestParam(value = "out_trade_no", required = false) String applyPayNo,
//                                       @RequestParam(value = "trade_no") String actualPayNo,
//                                       @RequestParam(value = "price", required = false) BigDecimal actualPayAmount
//    ) throws BusinessException {
//        PayCallbackDto payCallbackDto = new PayCallbackDto();
//        payCallbackDto.setApplyPayNo(applyPayNo);
//        payCallbackDto.setActualPayType("ali");
//        payCallbackDto.setActualPayNo(actualPayNo);
//        payCallbackDto.setActualPayAmount(actualPayAmount);
//        payCallbackDto.setReturnParamMap(paramMap);
//        payCallbackDto.setReturnParam(params);
//
//        verifyPayCallback(aliPayStrange, payCallbackDto);
//    }
//}
