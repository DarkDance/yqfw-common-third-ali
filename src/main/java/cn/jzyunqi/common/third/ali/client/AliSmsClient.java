package cn.jzyunqi.common.third.ali.client;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.third.ali.model.response.SendSmsRsp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.util.TreeMap;

/**
 * @author wiiyaya
 * @date 2018/5/21.
 */
@Slf4j
public class AliSmsClient extends AliBaseClient {

    private static final String SMS_ENDPOINT = "http://dysmsapi.aliyuncs.com/";

    public AliSmsClient(String smsAccessKeyId, String smsAccessKeySecret) {
        super(smsAccessKeyId, smsAccessKeySecret);
    }

    /**
     * 发送短信
     *
     * @param smsSign       短信签名
     * @param phone         短信接收号码,支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码
     * @param templateCode  短信模板ID
     * @param templateParam 短信模板变量替换JSON串
     * @throws BusinessException 异常
     */
    public SendSmsRsp sendSms(String smsSign, String phone, String templateCode, Object templateParam) throws BusinessException {
        SendSmsRsp body;
        try {
            //1. 构建系统参数
            TreeMap<String, String> params = super.getPublicParamMap();

            //2. 填充业务参数
            params.put("Action", "SendSms"); //API的命名
            params.put("Version", "2017-05-25"); //API的版本
            params.put("RegionId", "cn-hangzhou"); //API支持的RegionID
            params.put("SignName", smsSign); //短信签名
            params.put("PhoneNumbers", phone); //短信接收号码,支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码
            params.put("TemplateCode", templateCode); //短信模板ID
            params.put("TemplateParam", super.getObjectMapper().writeValueAsString(templateParam)); //短信模板变量替换JSON串

            //3. 构造请求url
            String url = SMS_ENDPOINT + super.generateParamPopSign(params);
            RequestEntity<Object> requestEntity = new RequestEntity<>(HttpMethod.GET, new URI(url));
            ResponseEntity<SendSmsRsp> aliBaseRsp = getRestTemplate().exchange(requestEntity, SendSmsRsp.class);
            body = aliBaseRsp.getBody();
        } catch (Exception e) {
            log.error("======AliSmsClient.sendSms error", e);
            throw new BusinessException("common_error_ali_sms_send_error");
        }

        if (body == null || !"OK".equals(body.getCode())) {
            if (body == null) {
                body = new SendSmsRsp();
            }
            log.error("======AliSmsClient.sendSms error[{}][{}]", body.getCode(), body.getMessage());
            throw new BusinessException("common_error_ali_sms_send_failed");
        }else{
            return body;
        }
    }

    /**
     * 序列化参数
     *
     * @param templateParam 参数对象
     * @return JSON数据
     */
    public String serializeParam(Object templateParam) throws BusinessException {
        try {
            return super.getObjectMapper().writeValueAsString(templateParam);
        } catch (Exception e) {
            throw new BusinessException("common_error_serialize_param_error");
        }
    }

    /**
     * 反序列化
     *
     * @param templateParam json数据
     * @return 参数对象
     */
    public Object deserializeParam(String templateParam) throws BusinessException {
        try {
            return super.getObjectMapper().readValue(templateParam, Object.class);
        } catch (Exception e) {
            throw new BusinessException("common_error_deserialize_param_error");
        }
    }
}
