package cn.jzyunqi.common.third.ali.oss;

import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wiiyaya
 * @since 2025/5/27
 */
public abstract class AliOssAuthRepository implements InitializingBean {

    private final Map<String, AliOssAuth> authMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<AliOssAuth> aliOssAuthList = initAliOssAuthList();
        for (AliOssAuth aliOssAuth : aliOssAuthList) {
            authMap.put(aliOssAuth.getAccessKeyId(), aliOssAuth);
        }
    }

    public AliOssAuth choosAliOssAuth(String wxAppId) {
        return authMap.get(wxAppId);
    }

    public void addAliOssAuth(AliOssAuth aliOssAuth) {
        authMap.put(aliOssAuth.getAccessKeyId(), aliOssAuth);
    }

    public void removeAliOssAuth(String wxAppId) {
        authMap.remove(wxAppId);
    }

    public List<AliOssAuth> getAliOssAuthList() {
        return new ArrayList<>(authMap.values());
    }

    public abstract List<AliOssAuth> initAliOssAuthList();
}
