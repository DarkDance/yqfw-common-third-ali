package cn.jzyunqi.common.third.ali.oss;

import cn.jzyunqi.common.third.ali.oss.object.AliOssObjApiProxy;
import cn.jzyunqi.common.utils.DateTimeUtilPlus;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.RandomUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.net.URLEncoder;
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
@Slf4j
public class AliOssClient {

    @Resource
    private AliOssAuthRepository aliOssAuthRepository;

    @Resource
    private AliOssObjApiProxy aliOssObjApiProxy;

    public final Obj obj = new Obj();

    public class Obj {
        public void fetch(String url, String bucket, String fileName) {
            aliOssObjApiProxy.fetch(url, bucket, fileName);
        }
    }
}
