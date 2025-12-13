package cn.jzyunqi.common.third.ali.sms;

import cn.jzyunqi.common.third.ali.common.AliHttpExchangeWrapper;
import cn.jzyunqi.common.third.ali.sms.send.AliSmsApiProxy;
import cn.jzyunqi.common.third.ali.sms.send.enums.Action;
import cn.jzyunqi.common.utils.DateTimeUtilPlus;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.RandomUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.ClientHttpRequestDecorator;
import org.springframework.lang.NonNull;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.time.Duration;
import java.time.LocalDateTime;
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
public class AliSmsConfig {

    private static final String ALGORITHM = "ACS3-HMAC-SHA256";

    @Bean
    @ConditionalOnMissingBean
    public AliHttpExchangeWrapper aliHttpExchangeWrapper() {
        return new AliHttpExchangeWrapper();
    }

    @Bean
    public AliSmsClient aliSmsClient() {
        return new AliSmsClient();
    }

    @Bean
    public AliSmsApiProxy aliSmsApiProxy(WebClient.Builder webClientBuilder, AliSmsAuthRepository aliSmsAuthRepository) {
        WebClient webClient = webClientBuilder.clone()
                //.codecs(WxFormatUtils::jackson2Config)
                .filter(ExchangeFilterFunction.ofRequestProcessor(request -> {
                    String accessKeyId = (String) request.attribute("accessKeyId").orElse(null);
                    Action action = (Action) request.attribute("action").orElse(null);
                    AliSmsAuth auth = aliSmsAuthRepository.choosAliSmsAuth(accessKeyId);

                    ClientRequest.Builder amendRequest = ClientRequest.from(request);
                    if (request.method() == HttpMethod.GET) {
                        Map<String, String> actionHeaders = getSignHttpHeaders(request.method(), request.url().getQuery(), null, action, auth);
                        amendRequest.headers(headers -> headers.setAll(actionHeaders));
                    } else {
                        amendRequest.body((outputMessage, context) -> request.body().insert(new ClientHttpRequestDecorator(outputMessage) {
                            @Override
                            public @NonNull Mono<Void> writeWith(@NonNull Publisher<? extends DataBuffer> body) {
                                return DataBufferUtils.join(body).flatMap(buffer -> {
                                    String bodyStr = buffer.toString(StringUtilPlus.UTF_8);
                                    Map<String, String> actionHeaders = getSignHttpHeaders(request.method(), request.url().getQuery(), bodyStr, action, auth);
                                    getHeaders().setAll(actionHeaders);
                                    return super.writeWith(Mono.just(buffer));
                                });
                            }
                        }, context));
                    }
                    return Mono.just(amendRequest.build());
                })).build();

        WebClientAdapter webClientAdapter = WebClientAdapter.create(webClient);
        webClientAdapter.setBlockTimeout(Duration.ofSeconds(5));
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(webClientAdapter).build();
        return factory.createClient(AliSmsApiProxy.class);
    }

    private Map<String, String> getSignHttpHeaders(HttpMethod method, String queryStr, String bodyStr, Action action, AliSmsAuth auth) {
        long timestamp = System.currentTimeMillis() / 1000;
        String currentDate = LocalDateTime.ofEpochSecond(timestamp, 0, DateTimeUtilPlus.UTC0_ZONE_OFFSET).format(DateTimeUtilPlus.ALI_BASE_DATE_FORMAT);
        String nonce = RandomUtilPlus.String.nextLowerAlphanumeric(32);
        Map<String, String> actionHeaders = new HashMap<>();
        if (StringUtilPlus.isNotEmpty(action.getContentType())) {
            actionHeaders.put("content-type", action.getContentType());
        }
        actionHeaders.put("host", action.getHost());
        actionHeaders.put("x-acs-action", action.name());
        actionHeaders.put("x-acs-date", currentDate);
        actionHeaders.put("x-acs-signature-nonce", nonce);
        actionHeaders.put("x-acs-version", action.getVersion());
        //headers.put("x-acs-security-token", "xxx"); //STS认证必传

        String canonicalQueryString = canonicalQueryString(queryStr);
        String hashedRequestPayload = hashedRequestPayload(bodyStr);
        actionHeaders.put("x-acs-content-sha256", hashedRequestPayload);

        // ************* 步骤 1：拼接规范请求串 *************
        String canonicalHeaders = actionHeaders.entrySet().stream().map(entry -> StringUtilPlus.join(entry.getKey().toLowerCase(), StringUtilPlus.COLON, entry.getValue(), StringUtilPlus.ENTER)).sorted().collect(Collectors.joining());
        String signedHeaders = actionHeaders.keySet().stream().map(String::toLowerCase).sorted().collect(Collectors.joining(StringUtilPlus.SEMICOLON));
        //RPC风格API使用正斜杠(/)作为CanonicalURI
        String canonicalRequest = StringUtilPlus.join(method, StringUtilPlus.ENTER, StringUtilPlus.SLASH, StringUtilPlus.ENTER, canonicalQueryString, StringUtilPlus.ENTER, canonicalHeaders, StringUtilPlus.ENTER, signedHeaders, StringUtilPlus.ENTER, hashedRequestPayload);

        // ************* 步骤 2：拼接待签名字符串 *************
        String hashedCanonicalRequest = DigestUtilPlus.SHA.sign(canonicalRequest, cn.jzyunqi.common.utils.DigestUtilPlus.SHAAlgo._256, Boolean.FALSE).toLowerCase();
        String stringToSign = StringUtilPlus.join(ALGORITHM, StringUtilPlus.ENTER, hashedCanonicalRequest);

        // ************* 步骤 3：计算签名 *************
        String signature = StringUtilPlus.EMPTY;
        try {
            signature = DigestUtilPlus.Mac.sign(stringToSign, auth.getAccessKeySecret(), DigestUtilPlus.MacAlgo.H_SHA256, Boolean.FALSE);
        } catch (Exception e) {
            log.error("======Tencent request [{}][{}] signature error=======", auth.getAccessKeyId(), action, e);
        }
        // ************* 步骤 4：拼接 Authorization *************
        String authorization = String.format("%s Credential=%s,SignedHeaders=%s,Signature=%s", ALGORITHM, auth.getAccessKeyId(), signedHeaders, signature);
        actionHeaders.put("Authorization", authorization);
        return actionHeaders;
    }

    private String canonicalQueryString(String queryStr) {
        if (StringUtilPlus.isEmpty(queryStr)) {
            return StringUtilPlus.EMPTY;
        }
        //TODO 当请求参数类型是array、object时，需要将参数平铺为一个映射结构（map）
        //https://help.aliyun.com/zh/sdk/product-overview/v3-request-structure-and-signature?spm=a2c4g.11186623.0.0.6fb04b19KGcrpc#sectiondiv-726-v1i-gel
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

    private String hashedRequestPayload(String bodyStr) {
        bodyStr = StringUtilPlus.isBlank(bodyStr) ? StringUtilPlus.EMPTY : bodyStr;
        return DigestUtilPlus.SHA.sign(bodyStr, DigestUtilPlus.SHAAlgo._256, Boolean.FALSE).toLowerCase();
    }

    private String specialUrlEncode(String value) {
        return URLEncoder.encode(value, StringUtilPlus.UTF_8).replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
    }
}
