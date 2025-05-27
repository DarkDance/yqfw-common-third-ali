package cn.jzyunqi.common.third.ali.oss.object;

import cn.jzyunqi.common.third.ali.common.AliHttpExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * @author wiiyaya
 * @since 2025/5/27
 */
@AliHttpExchange
@HttpExchange(url = "https://%s.oss-cn-hangzhou.aliyuncs.com/%s", accept = {"application/json"}, contentType = "application/json")
public interface AliOssObjApiProxy {

    void fetch(String url, String bucket, String fileName);
}
