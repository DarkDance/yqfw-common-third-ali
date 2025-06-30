package cn.jzyunqi.common.third.ali.ram;

import cn.jzyunqi.common.third.ali.ram.sts.AliRamStsApiProxy;
import cn.jzyunqi.common.third.ali.ram.sts.module.AssumeRoleRsp;
import cn.jzyunqi.common.utils.StringUtilPlus;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wiiyaya
 * @since 2025/6/30
 */
@Slf4j
public class AliRamClient {

    @Resource
    private AliRamStsApiProxy aliOssObjApiProxy;

    public final Sts sts = new Sts();

    public class Sts {
        public AssumeRoleRsp generateAssumeRole(String roleArn, String roleSessionName) {
            return aliOssObjApiProxy.assumeRole(roleArn, StringUtilPlus.leftPad(roleSessionName, 32, '0'), 3600L, null, null);
        }
    }
}
