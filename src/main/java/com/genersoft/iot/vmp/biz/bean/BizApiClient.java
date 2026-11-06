package com.genersoft.iot.vmp.biz.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 终端与全国平台接入凭证
 */
@Data
@Schema(description = "接入凭证")
public class BizApiClient {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "调用方名称")
    private String clientName;

    @Schema(description = "Access Key")
    private String accessKey;

    @Schema(description = "Secret Key")
    private String secretKey;

    @Schema(description = "类型：1终端 2全国平台")
    private Integer clientType;

    @Schema(description = "状态：1启用 0停用")
    private Integer status;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}
