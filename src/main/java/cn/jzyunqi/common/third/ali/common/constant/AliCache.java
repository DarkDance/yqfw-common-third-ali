package cn.jzyunqi.common.third.ali.common.constant;

import cn.jzyunqi.common.support.spring.redis.Cache;
import cn.jzyunqi.common.third.ali.ram.sts.model.AliOssToken;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

/**
 * @author wiiyaya
 * @since 2025/6/30
 */
@Getter
@AllArgsConstructor
public enum AliCache implements Cache {

    /**
     * 阿里OSS缓存
     */
    THIRD_ALI_OSS_V(Duration.ofHours(1), Boolean.FALSE, AliOssToken.class),

    ;

    private final Duration expiration;

    private final Boolean autoRenew;

    private final Object valueType;
}
