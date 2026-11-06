package com.genersoft.iot.vmp.biz.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业档案
 */
@Data
@Schema(description = "企业档案")
public class BizCompany {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "全国平台系统企业标识")
    private String tenantId;

    @Schema(description = "企业名称")
    private String companyName;

    @Schema(description = "企业地址")
    private String companyAddr;

    @Schema(description = "企业所属行政区域代码")
    private String regionCode;

    @Schema(description = "企业统一社会信用代码")
    private String uniformSocialCreditCode;

    @Schema(description = "企业法人")
    private String legalName;

    @Schema(description = "企业法人电话")
    private String legalPhoneNumber;

    @Schema(description = "状态：1启用 0停用")
    private Integer status;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}
