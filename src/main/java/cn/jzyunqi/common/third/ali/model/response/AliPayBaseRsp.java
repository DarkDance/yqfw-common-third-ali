package cn.jzyunqi.common.third.ali.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author wiiyaya
 * @date 2018/7/7.
 */
@Getter
@Setter
public class AliPayBaseRsp implements Serializable {
    @Serial
    private static final long serialVersionUID = -1428570491663837398L;

    private String code;

    private String msg;

    @JsonProperty("sub_code")
    private String subCode;

    @JsonProperty("sub_msg")
    private String subMsg;
}
