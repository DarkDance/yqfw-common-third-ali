package cn.jzyunqi.common.third.ali.sms.send;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.third.ali.common.AliHttpExchange;
import cn.jzyunqi.common.third.ali.sms.send.enums.Action;
import cn.jzyunqi.common.third.ali.sms.send.model.SendSmsRsp;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

/**
 * @author wiiyaya
 * @since 2025/5/26
 */
@AliHttpExchange
@HttpExchange(url = "https://dysmsapi.aliyuncs.com", accept = {"application/json"}, contentType = "application/json")
public interface AliSmsApiProxy {

    @PostExchange
    SendSmsRsp sendSms(@RequestAttribute String accessKeyId, @RequestAttribute Action action, @RequestParam String PhoneNumbers, @RequestParam String SignName, @RequestParam String TemplateCode, @RequestParam(required = false) String TemplateParam) throws BusinessException;

}
