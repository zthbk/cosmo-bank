package com.genersoft.iot.vmp.biz.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 船舶档案
 */
@Data
@Schema(description = "船舶档案")
public class BizShip {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "所属企业ID")
    private Long companyId;

    @Schema(description = "冗余企业标识")
    private String tenantId;

    @Schema(description = "船舶识别号/企业值班室识别号")
    private String shipCn;

    @Schema(description = "MMSI")
    private String shipMmsi;

    @Schema(description = "船舶名称")
    private String shipName;

    @Schema(description = "船长，单位米")
    private BigDecimal shipLength;

    @Schema(description = "船宽，单位米")
    private BigDecimal shipWidth;

    @Schema(description = "客船客位数")
    private Integer shipPassengerCapacity;

    @Schema(description = "船型")
    private String shipType;

    @Schema(description = "船舶状态，按规范 9.6.1")
    private String shipStatus;

    @Schema(description = "状态：1启用 0停用")
    private Integer status;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}
