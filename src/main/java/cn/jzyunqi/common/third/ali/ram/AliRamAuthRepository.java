package cn.jzyunqi.common.third.ali.ram;

import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wiiyaya
 * @since 2025/6/30
 */
public abstract class AliRamAuthRepository implements InitializingBean {

    private final Map<String, AliRamAuth> authMap = new ConcurrentHashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        List<AliRamAuth> AliRamAuthList = initAliRamAuthList();
        for (AliRamAuth aliRamAuth : AliRamAuthList) {
            authMap.put(aliRamAuth.getAccessKeyId(), aliRamAuth);
        }
    }

    public AliRamAuth choosAliRamAuth(String wxAppId) {
        return authMap.get(wxAppId);
    }

    public void addAliRamAuth(AliRamAuth aliRamAuth) {
        authMap.put(aliRamAuth.getAccessKeyId(), aliRamAuth);
    }

    public void removeAliRamAuth(String wxAppId) {
        authMap.remove(wxAppId);
    }

    public List<AliRamAuth> getAliRamAuthList() {
        return new ArrayList<>(authMap.values());
    }

    public abstract List<AliRamAuth> initAliRamAuthList();
}
