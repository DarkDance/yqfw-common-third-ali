package cn.jzyunqi.common.third.ali.ram.sts;

import cn.jzyunqi.common.third.ali.common.AliHttpExchange;
import cn.jzyunqi.common.third.ali.ram.sts.module.AssumeRoleRsp;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * @author wiiyaya
 * @since 2025/6/30
 */
@AliHttpExchange
@HttpExchange(url = "https://sts.aliyuncs.com/", accept = {"application/json"}, contentType = "application/json")
public interface AliRamStsApiProxy {

    AssumeRoleRsp assumeRole(@RequestParam String RoleArn, @RequestParam String RoleSessionName, @RequestParam(required = false) Long DurationSeconds, @RequestParam(required = false) String Policy, @RequestParam(required = false) String ExternalId);
}
