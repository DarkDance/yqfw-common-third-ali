package cn.jzyunqi.common.third.ali.sms.send.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wiiyaya
 * @since 2025/5/27
 */
@Getter
@AllArgsConstructor
public enum Action {
    SendSms("dysmsapi.aliyuncs.com", null, "2019-07-11"),
    ;

    /**
     * 域名
     */
    private final String host;

    /**
     * action的内容
     */
    private final String contentType;

    /**
     * action的版本
     */
    private final String version;
}
