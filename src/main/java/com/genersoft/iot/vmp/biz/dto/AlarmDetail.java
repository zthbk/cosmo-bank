package com.genersoft.iot.vmp.biz.dto;

import com.genersoft.iot.vmp.biz.bean.BizAlarm;
import com.genersoft.iot.vmp.biz.bean.BizAlarmDeal;
import com.genersoft.iot.vmp.biz.bean.BizAlarmFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "告警详情")
public class AlarmDetail {

    @Schema(description = "告警主表")
    private BizAlarm alarm;

    @Schema(description = "告警文件")
    private List<BizAlarmFile> files;

    @Schema(description = "处置记录")
    private List<BizAlarmDeal> deals;
}
