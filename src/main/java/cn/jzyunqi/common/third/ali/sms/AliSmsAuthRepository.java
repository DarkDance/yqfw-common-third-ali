package cn.jzyunqi.common.third.ali.sms;

import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wiiyaya
 * @since 2025/5/26
 */
public abstract class AliSmsAuthRepository implements InitializingBean {

    private final Map<String, AliSmsAuth> authMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<AliSmsAuth> aliSmsAuthList = initAliSmsAuthList();
        for (AliSmsAuth aliSmsAuth : aliSmsAuthList) {
            authMap.put(aliSmsAuth.getAccessKeyId(), aliSmsAuth);
        }
    }

    public AliSmsAuth choosAliSmsAuth(String wxAppId) {
        return authMap.get(wxAppId);
    }

    public void addAliSmsAuth(AliSmsAuth aliSmsAuth) {
        authMap.put(aliSmsAuth.getAccessKeyId(), aliSmsAuth);
    }

    public void removeAliSmsAuth(String wxAppId) {
        authMap.remove(wxAppId);
    }

    public List<AliSmsAuth> getAliSmsAuthList() {
        return new ArrayList<>(authMap.values());
    }

    public abstract List<AliSmsAuth> initAliSmsAuthList();
}
