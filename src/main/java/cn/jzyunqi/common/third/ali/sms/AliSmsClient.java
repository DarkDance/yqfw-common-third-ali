package cn.jzyunqi.common.third.ali.sms;

import cn.jzyunqi.common.third.ali.sms.send.AliSmsApiProxy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wiiyaya
 * @since 2025/5/27
 */
@Slf4j
public class AliSmsClient {

    @Resource
    public AliSmsApiProxy sms;
    
}
