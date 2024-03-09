package cn.jzyunqi.common.third.ali;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.feature.sms.SmsHelper;
import cn.jzyunqi.common.feature.sms.SmsResultDto;
import cn.jzyunqi.common.third.ali.client.AliSmsClient;
import cn.jzyunqi.common.third.ali.model.response.SendSmsRsp;
import cn.jzyunqi.common.utils.CollectionUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;

import java.util.List;

/**
 * @author wiiyaya
 * @date 2022/11/30.
 */
public class AliSmsStrange implements SmsHelper {

    private final AliSmsClient aliSmsClient;

    public AliSmsStrange(AliSmsClient aliSmsClient) {
        this.aliSmsClient = aliSmsClient;
    }

    @Override
    public List<SmsResultDto> sendSms(String smsSign, List<String> phoneList, String templateCode, List<String> templateParam) throws BusinessException {
        String phones = String.join(StringUtilPlus.COMMA, phoneList);
        SendSmsRsp sendSmsRsp = aliSmsClient.sendSms(smsSign, phones, templateCode, templateParam);
        SmsResultDto smsResultDto = new SmsResultDto();
        smsResultDto.setRequestId(sendSmsRsp.getRequestId());
        smsResultDto.setSerialNo(sendSmsRsp.getBizId());
        smsResultDto.setPhoneNumber(phones);
        return CollectionUtilPlus.Array.asList(smsResultDto);
    }
}
