package cn.jzyunqi.common.third.ali.sms.send.model;

import cn.jzyunqi.common.third.ali.common.model.AliYunBaseRsp;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * @author wiiyaya
 * @date 2018/5/22.
 */
@Getter
@Setter
public class SendSmsRsp extends AliYunBaseRsp {
    @Serial
    private static final long serialVersionUID = 9099950577474852485L;

    /**
     * 发送回执ID,可根据该ID查询具体的发送状态
     */
    @JsonProperty("BizId")
    private String bizId;
}
