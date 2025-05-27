package cn.jzyunqi.common.third.ali.oss;

import cn.jzyunqi.common.third.ali.common.AliHttpExchangeWrapper;
import cn.jzyunqi.common.third.ali.oss.object.AliOssObjApiProxy;
import cn.jzyunqi.common.utils.DateTimeUtilPlus;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.RandomUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wiiyaya
 * @since 2025/5/27
 */
@Configuration
@Slf4j
public class AliOssConfig {

    private static final String ALGORITHM = "OSS4-HMAC-SHA256";
    private static final String HASHED_REQUEST_PAYLOAD = "UNSIGNED-PAYLOAD";

    @Bean
    @ConditionalOnMissingBean
    public AliHttpExchangeWrapper aliHttpExchangeWrapper() {
        return new AliHttpExchangeWrapper();
    }

    @Bean
    public AliOssClient aliOssClient() {
        return new AliOssClient();
    }

    @Bean
    public AliOssObjApiProxy tencentSmsSendApiProxy(WebClient.Builder webClientBuilder, AliOssAuthRepository aliOssAuthRepository) {
        WebClient webClient = webClientBuilder.clone()
                //.codecs(WxFormatUtils::jackson2Config)
                .filter(ExchangeFilterFunction.ofRequestProcessor(request -> {
                    String accessKeyId = (String) request.attribute("accessKeyId").orElse(null);
                    String signRegion = (String) request.attribute("signRegion").orElse(null);
                    AliOssAuth auth = aliOssAuthRepository.choosAliOssAuth(accessKeyId);

                    ClientRequest.Builder amendRequest = ClientRequest.from(request);
                    Map<String, String> actionHeaders = getSignHttpHeaders(request.method(), signRegion, request.url().getPath(), request.url().getQuery(), auth);
                    amendRequest.headers(headers -> headers.setAll(actionHeaders));
                    return Mono.just(amendRequest.build());
                })).build();

        WebClientAdapter webClientAdapter = WebClientAdapter.create(webClient);
        webClientAdapter.setBlockTimeout(Duration.ofSeconds(5));
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(webClientAdapter).build();
        return factory.createClient(AliOssObjApiProxy.class);
    }

    public Map<String, String> getSignHttpHeaders(HttpMethod method, String signRegion, String canonicalUri, String queryStr, AliOssAuth auth) {
        long timestamp = System.currentTimeMillis() / 1000;
        String currentTimestamp = LocalDateTime.ofEpochSecond(timestamp, 0, DateTimeUtilPlus.GMT0_ZONE_OFFSET).format(DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'"));
        String signDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String nonce = RandomUtilPlus.String.nextLowerAlphanumeric(32);
        Map<String, String> actionHeaders = new HashMap<>();
        actionHeaders.put("x-oss-content-sha256", HASHED_REQUEST_PAYLOAD);

        String canonicalQueryString = canonicalQueryString(queryStr);

        // ************* 步骤 1：拼接规范请求串 *************
        String scope = StringUtilPlus.join(signDate, StringUtilPlus.SLASH, signRegion, StringUtilPlus.SLASH, "oss/aliyun_v4_request");
        String canonicalHeaders = actionHeaders.entrySet().stream().map(entry -> StringUtilPlus.join(entry.getKey().toLowerCase(), StringUtilPlus.COLON, entry.getValue(), StringUtilPlus.ENTER)).sorted().collect(Collectors.joining());
        String additionalHeaders = StringUtilPlus.EMPTY;//actionHeaders.keySet().stream().map(String::toLowerCase).sorted().collect(Collectors.joining(StringUtilPlus.SEMICOLON));
        String canonicalRequest = StringUtilPlus.join(method, StringUtilPlus.ENTER, canonicalUri, StringUtilPlus.ENTER, canonicalQueryString, StringUtilPlus.ENTER, canonicalHeaders, StringUtilPlus.ENTER, additionalHeaders, StringUtilPlus.ENTER, HASHED_REQUEST_PAYLOAD);

        // ************* 步骤 2：拼接待签名字符串 *************
        String hashedCanonicalRequest = DigestUtilPlus.SHA.sign(canonicalRequest, cn.jzyunqi.common.utils.DigestUtilPlus.SHAAlgo._256, Boolean.FALSE).toLowerCase();
        String stringToSign = StringUtilPlus.join(ALGORITHM, StringUtilPlus.ENTER, currentTimestamp, StringUtilPlus.ENTER, scope, StringUtilPlus.ENTER, hashedCanonicalRequest);

        // ************* 步骤 3：计算签名 *************
        String signature = StringUtilPlus.EMPTY;
        try {
            byte[] dateKey = DigestUtilPlus.Mac.sign(signDate, "aliyun_v4" + auth.getAccessKeySecret(), DigestUtilPlus.MacAlgo.H_SHA256);
            byte[] dateRegionKey = DigestUtilPlus.Mac.sign(signRegion, dateKey, DigestUtilPlus.MacAlgo.H_SHA256);
            byte[] dateRegionServiceKey = DigestUtilPlus.Mac.sign("oss", dateRegionKey, DigestUtilPlus.MacAlgo.H_SHA256);
            byte[] signingKey = DigestUtilPlus.Mac.sign("aliyun_v4_request", dateRegionServiceKey, DigestUtilPlus.MacAlgo.H_SHA256);
            signature = DigestUtilPlus.Mac.sign(stringToSign, signingKey, DigestUtilPlus.MacAlgo.H_SHA256, Boolean.FALSE);
        } catch (Exception e) {
            log.error("======Ali oss request [{}][{}] signature error=======", auth.getAccessKeyId(), canonicalUri, e);
        }
        actionHeaders.put("Authorization", signature);
        return actionHeaders;
    }

    private String canonicalQueryString(String queryStr) {
        if (StringUtilPlus.isEmpty(queryStr)) {
            return StringUtilPlus.EMPTY;
        }
        return Arrays.stream(StringUtilPlus.split(queryStr, StringUtilPlus.AND))
                .sorted()
                .map(kv -> {
                    String[] temp = StringUtilPlus.split(kv, StringUtilPlus.LINK);
                    temp[0] = specialUrlEncode(temp[0]);
                    temp[1] = specialUrlEncode(temp[1]);
                    return StringUtilPlus.join(temp[0], StringUtilPlus.LINK, temp[1]);
                })
                .collect(Collectors.joining(StringUtilPlus.AND));
    }

    private String specialUrlEncode(String value) {
        return URLEncoder.encode(value, StringUtilPlus.UTF_8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }
}
