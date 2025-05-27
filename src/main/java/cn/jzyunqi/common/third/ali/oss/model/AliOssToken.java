package cn.jzyunqi.common.third.ali.oss.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wiiyaya
 * @date 2019/3/18.
 */
@Getter
@Setter
public class AliOssToken implements Serializable {
	@Serial
	private static final long serialVersionUID = 8659881007804847264L;

	/**
	 * OSS所在区域
	 */
	private String region;

	/**
	 * 虚拟用户账号
	 */
	private String accessKeyId;

	/**
	 * 虚拟用户密码
	 */
	private String accessKeySecret;

	/**
	 * 虚拟用户 token
	 */
	private String stsToken;

	/**
	 * 虚拟用户token过期时间
	 */
	private LocalDateTime expiration;

	/**
	 * OSS存储空间
	 */
	private String bucket;
}
