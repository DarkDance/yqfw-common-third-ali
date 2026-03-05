package cn.jzyunqi.common.third.ali.sms.send;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.third.ali.sms.AliSmsAuth;
import cn.jzyunqi.common.third.ali.sms.AliSmsAuthHelper;
import cn.jzyunqi.common.third.ali.sms.send.enums.Action;
import cn.jzyunqi.common.third.ali.sms.send.model.SendSmsRsp;
import jakarta.annotation.Resource;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * @author wiiyaya
 * @since 2026/3/5
 */
public class AliSmsApi {

    @Resource
    private AliSmsApiProxy aliSmsApiProxy;

    @Resource
    private AliSmsAuthHelper aliSmsAuthHelper;

    @Resource
    private ObjectMapper objectMapper;

    public SendSmsRsp sendSms(String accessKeyId, String smsSign, String phoneNumbers, String templateCode, Map<String, String> templateParamMap) throws BusinessException {
        AliSmsAuth aliSmsAuth = aliSmsAuthHelper.choosAliSmsAuth(accessKeyId);
        String templateParam;
        try {
            templateParam = objectMapper.writeValueAsString(templateParamMap);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
        //2. 构造请求url
        return aliSmsApiProxy.sendSms(aliSmsAuth.getAccessKeyId(), Action.SendSms, phoneNumbers, smsSign, templateCode, templateParam);
    }

}
