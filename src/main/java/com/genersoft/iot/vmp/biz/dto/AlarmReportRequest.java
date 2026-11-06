package com.genersoft.iot.vmp.biz.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 终端告警上报请求，字段对应对接规范 9.1.3
 */
@Data
@Schema(description = "终端告警上报请求")
public class AlarmReportRequest {

    @Schema(description = "系统企业标识")
    private String tenantId;

    @Schema(description = "企业名称")
    private String tenantName;

    @Schema(description = "MMSI")
    private String shipMmsi;

    @Schema(description = "船舶识别号/企业值班室识别号")
    private String shipCn;

    @Schema(description = "船舶名称")
    private String shipName;

    @Schema(description = "告警编号，全局唯一")
    private String alarmCode;

    @Schema(description = "告警时间 yyyy-MM-dd HH:mm:ss")
    private String alarmTime;

    @Schema(description = "告警类型编号")
    private String alarmTypeCode;

    @Schema(description = "告警场景：0驾驶室 1机舱 2甲板 3乘客公共处所 4岸基监控室 5泵舱 6船岸界面")
    private String alarmSceneType;

    @Schema(description = "风险等级：0高风险 1中风险 2低风险")
    private String alarmLevel;

    @Schema(description = "告警图片URL")
    private String alarmPic;

    @Schema(description = "告警图片防伪信息")
    private String alarmPicAcc;

    @Schema(description = "告警图片锚框")
    private String relativeBox;

    @Schema(description = "告警视频URL")
    private String alarmVideo;

    @Schema(description = "告警视频防伪信息")
    private String alarmVideoAcc;

    @Schema(description = "经度，WGS84")
    private BigDecimal shipLng;

    @Schema(description = "纬度，WGS84")
    private BigDecimal shipLat;

    @Schema(description = "航速，节")
    private BigDecimal shipSpeed;

    @Schema(description = "航向，度")
    private BigDecimal shipAngle;

    @Schema(description = "告警具体位置")
    private String shipLocation;

    @Schema(description = "载客人数")
    private Integer shipLoadingPeopleNum;

    @Schema(description = "装载车辆数")
    private Integer shipLoadingCarNum;

    @Schema(description = "船舶状态")
    private String shipStatus;
}
