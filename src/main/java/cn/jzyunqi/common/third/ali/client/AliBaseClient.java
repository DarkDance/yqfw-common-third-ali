package cn.jzyunqi.common.third.ali.client;

import cn.jzyunqi.common.utils.DateTimeUtilPlus;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.RandomUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.TreeMap;

/**
 * @author wiiyaya
 * @date 2018/5/22.
 */
@Slf4j
public abstract class AliBaseClient {

    private final String accessKeyId;

    private final String accessKeySecret;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    protected AliBaseClient(String accessKeyId, String accessKeySecret) {
        this.accessKeyId = accessKeyId;
        this.accessKeySecret = accessKeySecret;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    protected RestTemplate getRestTemplate() {
        return restTemplate;
    }

    protected ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * 构建rest请求系统参数
     *
     * @return TreeMap
     */
    TreeMap<String, String> getPublicParamMap() {
        TreeMap<String, String> params = new TreeMap<>();
        // 1. 填充系统参数
        params.put("SignatureMethod", "HMAC-SHA1"); //签名算法
        params.put("SignatureVersion", "1.0");
        params.put("SignatureNonce", RandomUtilPlus.String.nextAlphanumeric(32)); //用于请求的防重放攻击
        params.put("AccessKeyId", accessKeyId); //子用户accessKey
        params.put("Timestamp", LocalDateTime.now(DateTimeUtilPlus.GMT0_ZONE_ID).format(DateTimeUtilPlus.ALI_BASE_DATE_FORMAT));// 这里一定要设置GMT0时区
        params.put("Format", "JSON"); //没传默认为JSON，可选填值：XML

        return params;
    }

    /**
     * 使用POP协议签名请求参数
     *
     * @param params 请求参数
     * @return 签名后请求参数
     * @throws Exception 异常
     */
    String generateParamPopSign(TreeMap<String, String> params) throws Exception {
        StringBuilder sortQueryStringTmp = new StringBuilder();
        params.forEach((key, value) -> {
            try {
                sortQueryStringTmp.append("&").append(this.specialUrlEncode(key)).append("=").append(this.specialUrlEncode(value));
            } catch (Exception e) {
                log.error("======generateParamPopSign URLEncoder error", e);
            }
        });
        String sign = this.popSign(this.specialUrlEncode(sortQueryStringTmp.substring(1))); //签名字符串
        return "?Signature=" + this.specialUrlEncode(sign) + sortQueryStringTmp;
    }

    /**
     * 签名
     *
     * @param stringToSign 需要签名的字符串
     * @return 签名后的字符串
     * @throws Exception 异常
     */
    public String ossSign(String stringToSign) throws Exception {
        return DigestUtilPlus.Mac.sign(stringToSign, accessKeySecret, DigestUtilPlus.MacAlgo.H_SHA1, Boolean.TRUE);
    }

    /**
     * 使用POP协议签名
     *
     * @param stringToSign 需要签名的字符串
     * @return 签名后的字符串
     * @throws Exception 异常
     */
    private String popSign(String stringToSign) throws Exception {
        return DigestUtilPlus.Mac.sign("GET&%2F&" + stringToSign, accessKeySecret + "&", DigestUtilPlus.MacAlgo.H_SHA1, Boolean.TRUE);
    }

    /**
     * 替换特殊URL编码，加号（+）替换成 %20、星号（*）替换成 %2A、%7E 替换回波浪号（~）
     *
     * @param value 原始字符串
     * @return 替换后的字符串
     * @throws Exception 异常
     */
    private String specialUrlEncode(String value) throws Exception {
        return URLEncoder.encode(value, StringUtilPlus.UTF_8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }
}
