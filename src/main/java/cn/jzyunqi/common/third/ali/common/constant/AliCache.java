package cn.jzyunqi.common.third.ali.common.constant;

import cn.jzyunqi.common.feature.redis.Cache;
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
    THIRD_ALI_OSS_V(Duration.ofHours(1), Boolean.FALSE),

    ;

    private final Duration expiration;

    private final Boolean autoRenew;
}
