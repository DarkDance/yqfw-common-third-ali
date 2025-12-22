package cn.jzyunqi.common.third.ali.sms;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.third.ali.sms.send.AliSmsApiProxy;
import cn.jzyunqi.common.third.ali.sms.send.enums.Action;
import cn.jzyunqi.common.third.ali.sms.send.model.SendSmsRsp;
import cn.jzyunqi.common.utils.StringUtilPlus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

/**
 * @author wiiyaya
 * @since 2025/5/27
 */
@Slf4j
public class AliSmsClient {

    @Resource
    private AliSmsAuthRepository aliSmsAuthRepository;

    @Resource
    private AliSmsApiProxy aliSmsApiProxy;

    @Resource
    private ObjectMapper objectMapper;

    public final Sender sender = new Sender();

    public class Sender {

        public SendSmsRsp sendSms(String accessKeyId, String smsSign, String phoneNumbers, String templateCode, Map<String, String> templateParamMap) throws BusinessException {
            AliSmsAuth aliSmsAuth = aliSmsAuthRepository.choosAliSmsAuth(accessKeyId);
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
}
