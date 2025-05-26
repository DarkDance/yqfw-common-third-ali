package cn.jzyunqi.common.third.ali.pay;

import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wiiyaya
 * @since 2025/5/26
 */
public abstract class AliPayAuthRepository implements InitializingBean {

    private final Map<String, AliPayAuth> authMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<AliPayAuth> aliPayAuthList = initAliPayAuthList();
        for (AliPayAuth aliPayAuth : aliPayAuthList) {
            authMap.put(aliPayAuth.getAppId(), aliPayAuth);
        }
    }

    public AliPayAuth choosAliPayAuth(String wxAppId) {
        return authMap.get(wxAppId);
    }

    public void addAliPayAuth(AliPayAuth aliPayAuth) {
        authMap.put(aliPayAuth.getAppId(), aliPayAuth);
    }

    public void removeAliPayAuth(String wxAppId) {
        authMap.remove(wxAppId);
    }

    public List<AliPayAuth> getAliPayAuthList() {
        return new ArrayList<>(authMap.values());
    }

    public abstract List<AliPayAuth> initAliPayAuthList();
}
