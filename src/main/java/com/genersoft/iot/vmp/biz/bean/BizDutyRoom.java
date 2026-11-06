package com.genersoft.iot.vmp.biz.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 企业值班室档案
 */
@Data
@Schema(description = "企业值班室档案")
public class BizDutyRoom {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "所属企业ID")
    private Long companyId;

    @Schema(description = "值班室识别号：AJJK+tenantId+序号")
    private String dutyRoomCode;

    @Schema(description = "值班室名称")
    private String dutyRoomName;

    @Schema(description = "所属行政区域代码")
    private String regionCode;

    @Schema(description = "联系人")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "值班室摄像头/国标设备编号")
    private String gbDeviceId;

    @Schema(description = "状态：1启用 0停用")
    private Integer status;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}
