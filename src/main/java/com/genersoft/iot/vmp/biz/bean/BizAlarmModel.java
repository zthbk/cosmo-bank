package com.genersoft.iot.vmp.biz.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 告警模型与场景映射
 */
@Data
@Schema(description = "告警模型")
public class BizAlarmModel {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "告警类型编号")
    private String modelCode;

    @Schema(description = "告警模型名称")
    private String modelName;

    @Schema(description = "模型类别：1危险品船 2客船 3企业值班室")
    private Integer modelCategory;

    @Schema(description = "适用场景编号")
    private String sceneType;

    @Schema(description = "默认风险等级")
    private String alarmLevel;

    @Schema(description = "是否启用：1启用 0停用")
    private Integer enabled;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}
