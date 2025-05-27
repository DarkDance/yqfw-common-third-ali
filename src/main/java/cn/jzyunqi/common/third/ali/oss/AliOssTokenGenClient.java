package cn.jzyunqi.common.third.ali.oss;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.feature.redis.Cache;
import cn.jzyunqi.common.feature.redis.RedisHelper;
import cn.jzyunqi.common.third.ali.client.AliStsClient;
import cn.jzyunqi.common.third.ali.oss.constant.AliOssTokenParams;
import cn.jzyunqi.common.third.ali.oss.model.AliOssToken;
import cn.jzyunqi.common.third.ali.model.response.AssumeRoleRsp;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author wiiyaya
 * @date 2019/3/18.
 */
public class AliOssTokenGenClient {

	private final AliStsClient aliStsClient;

	private final String roleArn;

	private final String region;

	private final String bucket;

    private final RedisHelper redisHelper;

    private final Cache tokenCache;

	public AliOssTokenGenClient(AliStsClient aliStsClient, String roleArn, String region, String bucket, RedisHelper redisHelper, Cache tokenCache) {
		this.aliStsClient = aliStsClient;
		this.roleArn = roleArn;
		this.region = region;
		this.bucket = bucket;
        this.redisHelper = redisHelper;
        this.tokenCache = tokenCache;
	}

	public AliOssToken uploadToken(String uid, Map<String, Object> params) throws BusinessException {
		AliOssToken aliOssToken = (AliOssToken) redisHelper.vGet(tokenCache, uid);
        if (aliOssToken != null && LocalDateTime.now().isBefore(aliOssToken.getExpiration())) {
            return aliOssToken;
        }

		AssumeRoleRsp assumeRoleRsp = aliStsClient.generateAssumeRole(roleArn, (String)params.get(AliOssTokenParams.ROLE_SESSION_NAME));
		aliOssToken = new AliOssToken();
		aliOssToken.setRegion(region); //地域
		aliOssToken.setAccessKeyId(assumeRoleRsp.getCredentials().getAccessKeyId()); //访问密钥标识
		aliOssToken.setAccessKeySecret(assumeRoleRsp.getCredentials().getAccessKeySecret()); //访问密钥
		aliOssToken.setStsToken(assumeRoleRsp.getCredentials().getSecurityToken()); //安全令牌
		aliOssToken.setBucket(bucket); //OSS存储空间
		//阿里返回的GMT+0时区的时间，转换成本地需要+8
		aliOssToken.setExpiration(assumeRoleRsp.getCredentials().getExpiration().plusHours(8).minusMinutes(10)); //token过期时间

        redisHelper.vPut(tokenCache, uid, aliOssToken);
		return aliOssToken;
	}
}
