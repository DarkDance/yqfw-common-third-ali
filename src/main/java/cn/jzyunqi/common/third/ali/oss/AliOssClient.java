package cn.jzyunqi.common.third.ali.oss;

import cn.jzyunqi.common.feature.redis.RedisHelper;
import cn.jzyunqi.common.third.ali.common.constant.AliCache;
import cn.jzyunqi.common.third.ali.oss.object.AliOssObjApiProxy;
import cn.jzyunqi.common.third.ali.ram.AliRamClient;
import cn.jzyunqi.common.third.ali.ram.sts.module.AliOssToken;
import cn.jzyunqi.common.third.ali.ram.sts.module.AssumeRoleRsp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

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

    @Resource
    private AliRamClient aliRamClient;

    @Resource
    private RedisHelper redisHelper;

    public final Obj obj = new Obj();

    public class Obj {
        public void fetch(String url, String bucket, String fileName) {
            aliOssObjApiProxy.fetch(url, bucket, fileName);
        }
    }

    public AliOssToken uploadToken(String uid) {
        AliOssToken aliOssToken = (AliOssToken) redisHelper.vGet(AliCache.THIRD_ALI_OSS_V, uid);
        if (aliOssToken != null && LocalDateTime.now().isBefore(aliOssToken.getExpiration())) {
            return aliOssToken;
        }

        AliOssAuth aliOssAuth = aliOssAuthRepository.choosAliOssAuth(null);

        AssumeRoleRsp assumeRoleRsp = aliRamClient.sts.generateAssumeRole(aliOssAuth.getRoleArn(), uid);
        aliOssToken = new AliOssToken();
        aliOssToken.setRegion(aliOssAuth.getRegion()); //地域
        aliOssToken.setAccessKeyId(assumeRoleRsp.getCredentials().getAccessKeyId()); //访问密钥标识
        aliOssToken.setAccessKeySecret(assumeRoleRsp.getCredentials().getAccessKeySecret()); //访问密钥
        aliOssToken.setStsToken(assumeRoleRsp.getCredentials().getSecurityToken()); //安全令牌
        aliOssToken.setBucket(aliOssAuth.getBucket()); //OSS存储空间
        //阿里返回的GMT+0时区的时间，转换成本地需要+8
        aliOssToken.setExpiration(assumeRoleRsp.getCredentials().getExpiration().plusHours(8).minusMinutes(10)); //token过期时间

        redisHelper.vPut(AliCache.THIRD_ALI_OSS_V, uid, aliOssToken);
        return aliOssToken;
    }
}
