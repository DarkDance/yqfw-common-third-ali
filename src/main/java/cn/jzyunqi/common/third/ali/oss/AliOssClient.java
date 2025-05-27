package cn.jzyunqi.common.third.ali.oss;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.third.ali.client.AliBaseClient;
import cn.jzyunqi.common.utils.CollectionUtilPlus;
import cn.jzyunqi.common.utils.DateTimeUtilPlus;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.IOUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wiiyaya
 * @date 2018/5/23.
 */
@Slf4j
public class AliOssClient extends AliBaseClient {
    private static final List<String> SIGNED_PARAMS = CollectionUtilPlus.Array.asList(
            "acl", "uploads" //子资源标识，还有很多，暂未添加
            , "response-content-type", "response-content-language" //指定返回Header字段，还有很多，暂未添加
            , "x-oss-process" //文件处理方式
    );

    private static final String OSS_UPLOAD_ENDPOINT = "http://%s.oss-cn-hangzhou.aliyuncs.com/%s";

    private final String accessAccount;

    private final String stsToken;

    public AliOssClient(String accessAccount, String accessSecret, String stsToken) {
        super(accessAccount, accessSecret);
        this.accessAccount = accessAccount;
        this.stsToken = stsToken;
    }

    /**
     * 转存文件
     *
     * @param url    文件url
     * @param bucket 空间
     * @param fileName  文件名称
     */
    public void fetch(String url, String bucket, String fileName) throws BusinessException {
        try {
            URI sendMsgUri =URI.create(String.format(OSS_UPLOAD_ENDPOINT, bucket, fileName));

            String date = LocalDateTime.now(DateTimeUtilPlus.GMT0_ZONE_ID).format(DateTimeUtilPlus.GMT0_DATE_FORMAT);
            String contentType = "image/jpeg";
            byte[] content = IOUtilPlus.toByteArray(URI.create(url));
            String contentMd5 = DigestUtilPlus.MD5.sign(content, Boolean.TRUE);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", contentType);
            headers.set("Content-Length", String.valueOf(content.length));
            headers.set("Content-Md5", contentMd5);
            headers.set("Date", date);
            headers.set("X-OSS-Security-Token", stsToken);

            String sign = ossSign(prepareWaitSign(bucket, fileName, date, contentMd5, contentType, headers, null));
            headers.set("Authorization", "OSS " + accessAccount + ":" + sign);

            RequestEntity<byte[]> requestEntity = new RequestEntity<>(content, headers, HttpMethod.PUT, sendMsgUri);
            getRestTemplate().exchange(requestEntity, Object.class);
        } catch (Exception e) {
            log.error("======AliOssClient.fetch error", e);
            throw new BusinessException("common_error_ali_oss_url_fetch_failed");
        }
    }

    private String prepareWaitSign(String bucket, String fileName, String date, String contentMd5, String contentType, HttpHeaders headers, Map<String, String> params) {
        return HttpMethod.PUT + "\n" +
                contentMd5 + "\n" +
                contentType + "\n" +
                date + "\n" +
                prepareHeaders(headers, "x-oss-") +
                prepareResource(bucket, fileName, params);
    }


    private String prepareHeaders(HttpHeaders headers, String prefix) {
        return headers.entrySet().stream()
                .filter(entry -> StringUtilPlus.startsWithIgnoreCase(entry.getKey(), prefix))
                .map(entry -> StringUtilPlus.join(StringUtilPlus.lowerCase(entry.getKey()), ":", StringUtilPlus.join(entry.getValue().toArray()), "\n"))
                .sorted(String::compareTo)
                .collect(Collectors.joining(""));
    }

    private String prepareResource(String bucket, String fileName, Map<String,String> params) {
        StringBuilder builder = new StringBuilder();
        builder.append("/").append(bucket).append("/").append(fileName);

        String paramList = Optional.ofNullable(params).orElse(new HashMap<>()).entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .filter(entry -> SIGNED_PARAMS.contains(entry.getKey()) && entry.getValue() != null)
                .map(entry -> StringUtilPlus.join(entry.getKey(), "=", entry.getValue()))
                .collect(Collectors.joining("&"));
        if(StringUtilPlus.isNotBlank(paramList)){
            builder.append("?");
            builder.append(paramList);
        }
        return builder.toString();
    }
}
