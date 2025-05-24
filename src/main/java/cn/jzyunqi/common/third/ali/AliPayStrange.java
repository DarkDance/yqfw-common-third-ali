//package cn.jzyunqi.common.third.ali;
//
//import cn.jzyunqi.common.exception.BusinessException;
//import cn.jzyunqi.common.third.ali.client.AliPayClient;
//import cn.jzyunqi.common.third.ali.model.TradeQueryResult;
//import cn.jzyunqi.common.third.ali.model.TradeRefundResult;
//
///**
// * @author wiiyaya
// * @since 2024/7/16
// */
//public class AliPayStrange implements PayHelper {
//
//    private final AliPayClient aliPayClient;
//
//    public AliPayStrange(AliPayClient aliPayClient) {
//        this.aliPayClient = aliPayClient;
//    }
//
//    @Override
//    public Object signForPay(PayApplyReqDto payApplyReqDto) throws BusinessException {
//        return aliPayClient.signForPay(
//                payApplyReqDto.getApplyPayNo(),
//                payApplyReqDto.getSkuName(),
//                payApplyReqDto.getSkuDetail(),
//                payApplyReqDto.getApplyPayAmount(),
//                payApplyReqDto.getExpireTime(),
//                payApplyReqDto.getCreditSupport()
//        );
//    }
//
//    @Override
//    public VerifyPayResult verifyPayCallback(PayCallbackDto payCallbackDto) throws BusinessException {
//        boolean checkResult = aliPayClient.payCallBackCheck(payCallbackDto.getReturnParamMap());
//        return checkResult ? VerifyPayResult.SUCCESS : VerifyPayResult.FAILED;
//    }
//
//    @Override
//    public VerifyPayResult verifyRefundCallback(PayCallbackDto payCallbackDto) throws BusinessException {
//        boolean checkResult = aliPayClient.payCallBackCheck(payCallbackDto.getReturnParamMap());
//        return checkResult ? VerifyPayResult.SUCCESS : VerifyPayResult.FAILED;
//    }
//
//    @Override
//    public PayCallbackDto queryPay(PayQueryReqDto payQueryReqDto) throws BusinessException {
//        TradeQueryResult tradeQueryResult = aliPayClient.queryPay(payQueryReqDto.getActualPayNo(), payQueryReqDto.getApplyPayNo());
//
//        PayCallbackDto payCallbackDto = new PayCallbackDto();
//        payCallbackDto.setApplyPayNo(payQueryReqDto.getApplyPayNo());
//        payCallbackDto.setActualPayNo(tradeQueryResult.getTradeNo());
//        payCallbackDto.setActualPayType("ali");
//        payCallbackDto.setActualPayAmount(tradeQueryResult.getTotalAmount());
//        payCallbackDto.setReturnParam(tradeQueryResult.getResponseStr());
//        return payCallbackDto;
//    }
//
//    @Override
//    public RefundCallbackDto executeRefund(RefundApplyReqDto refundApplyReqDto) throws BusinessException {
//        TradeRefundResult tradeRefundResult = aliPayClient.payRefund(refundApplyReqDto.getActualPayNo(), refundApplyReqDto.getApplyRefundNo(), refundApplyReqDto.getApplyRefundAmount(), refundApplyReqDto.getApplyRefundReason());
//
//        RefundCallbackDto refundCallbackDto = new RefundCallbackDto();
//        refundCallbackDto.setActualRefundNo(tradeRefundResult.getTradeNo());
//        refundCallbackDto.setActualRefundAmount(tradeRefundResult.getRefundFee());
//        refundCallbackDto.setReturnParam(tradeRefundResult.getResponseStr());
//        return refundCallbackDto;
//    }
//}
