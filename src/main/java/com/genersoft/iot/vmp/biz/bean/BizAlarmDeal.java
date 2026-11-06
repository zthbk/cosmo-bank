package com.genersoft.iot.vmp.biz.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 告警处置记录
 */
@Data
@Schema(description = "告警处置记录")
public class BizAlarmDeal {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "告警ID")
    private Long alarmId;

    @Schema(description = "是否有效：1正确 0误报")
    private Integer isValid;

    @Schema(description = "处置方式")
    private String dealType;

    @Schema(description = "处置结果")
    private String dealResult;

    @Schema(description = "处置时间")
    private String dealTime;

    @Schema(description = "处置人")
    private String opUser;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private String createTime;
}
