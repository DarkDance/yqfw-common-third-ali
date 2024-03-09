package cn.jzyunqi.common.third.ali.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wiiyaya
 * @date 2018/5/31.
 */
@Getter
@AllArgsConstructor
public enum LiveProtocol {

    rtmp(""),

    flv(".flv"),

    m3u8(".m3u8"),
    ;

    private final String suffix;
}
