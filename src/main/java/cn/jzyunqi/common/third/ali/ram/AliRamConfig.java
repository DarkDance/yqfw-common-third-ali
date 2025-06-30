package cn.jzyunqi.common.third.ali.ram;

import cn.jzyunqi.common.third.ali.common.AliHttpExchangeWrapper;
import cn.jzyunqi.common.third.ali.ram.sts.AliRamStsApiProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

/**
 * @author wiiyaya
 * @since 2025/6/30
 */
@Configuration
@Slf4j
public class AliRamConfig {

    @Bean
    @ConditionalOnMissingBean
    public AliHttpExchangeWrapper aliHttpExchangeWrapper() {
        return new AliHttpExchangeWrapper();
    }

    @Bean
    public AliRamClient aliRamClient() {
        return new AliRamClient();
    }

    @Bean
    public AliRamStsApiProxy aliRamStsApiProxy(WebClient.Builder webClientBuilder, AliRamAuthRepository aliRamAuthRepository) {
        WebClient webClient = webClientBuilder.clone()
                //.codecs(WxFormatUtils::jackson2Config)
                .build();

        WebClientAdapter webClientAdapter = WebClientAdapter.create(webClient);
        webClientAdapter.setBlockTimeout(Duration.ofSeconds(5));
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(webClientAdapter).build();
        return factory.createClient(AliRamStsApiProxy.class);
    }
}
