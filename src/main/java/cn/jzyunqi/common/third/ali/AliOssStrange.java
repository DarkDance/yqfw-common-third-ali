package cn.jzyunqi.common.third.ali;

import cn.jzyunqi.common.exception.BusinessException;
import cn.jzyunqi.common.feature.oss.OssHelper;
import cn.jzyunqi.common.third.ali.client.AliOssClient;
import cn.jzyunqi.common.third.ali.client.AliOssTokenGenClient;
import cn.jzyunqi.common.third.ali.constant.AliOssTokenParams;
import cn.jzyunqi.common.third.ali.model.AliOssToken;
import cn.jzyunqi.common.utils.DigestUtilPlus;
import cn.jzyunqi.common.utils.RandomUtilPlus;
import cn.jzyunqi.common.utils.StringUtilPlus;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public class AliOssStrange implements OssHelper {

    private final AliOssTokenGenClient aliOssTokenGenClient;

    public AliOssStrange(AliOssTokenGenClient aliOssTokenGenClient){
        this.aliOssTokenGenClient = aliOssTokenGenClient;
    }

    @Override
    public AliOssToken generateUploadToken(String uid) throws BusinessException {
        Map<String, Object> params = new HashMap<>();
        params.put(AliOssTokenParams.ROLE_SESSION_NAME, uid);

        return aliOssTokenGenClient.uploadToken(uid, params);
    }

    @Override
    public String getFirstImageFullUrl(String images, boolean pc, int width, int height) {
        StringBuilder fileName = new StringBuilder();
        if (StringUtilPlus.isNotEmpty(images)) {
            fileName.append(StringUtilPlus.split(images, StringUtilPlus.COMMA)[0]);
            fileName.append("?x-oss-process=image/resize,m_");
            fileName.append(pc ? "lfit" : "mfit");
            if (width > 0) {
                fileName.append(",w_");
                fileName.append(width);
            }
            if (height > 0) {
                fileName.append(",h_");
                fileName.append(height);
            }
        }
        return fileName.toString();
    }

    @Override
    public String restoreFile(String uid, String headImgUrl, String bucket) throws BusinessException {
        //获取sts角色token
        AliOssToken ossTokenRedisDto = generateUploadToken(uid);
        //构建新的AliOssHelper，否则token会失效
        AliOssClient aliOssHelper = new AliOssClient(ossTokenRedisDto.getAccessKeyId(), ossTokenRedisDto.getAccessKeySecret(), ossTokenRedisDto.getStsToken());

        String fileName = DigestUtilPlus.MD5.sign(StringUtilPlus.join(uid, "-", System.currentTimeMillis(), "-", RandomUtilPlus.String.nextAlphanumeric(32)), Boolean.FALSE) + ".jpg";
        aliOssHelper.fetch(headImgUrl, bucket, fileName);
        return fileName;
    }
}
