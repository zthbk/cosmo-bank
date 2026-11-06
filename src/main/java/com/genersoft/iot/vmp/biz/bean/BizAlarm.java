package com.genersoft.iot.vmp.biz.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * AI 告警主表
 */
@Data
@Schema(description = "AI 告警")
public class BizAlarm {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "系统企业标识")
    private String tenantId;

    @Schema(description = "业务船舶ID")
    private Long shipId;

    @Schema(description = "船舶识别号/企业值班室识别号")
    private String shipCn;

    @Schema(description = "MMSI")
    private String shipMmsi;

    @Schema(description = "船舶名称")
    private String shipName;

    @Schema(description = "告警编号，全局唯一")
    private String alarmCode;

    @Schema(description = "告警时间")
    private String alarmTime;

    @Schema(description = "告警类型编号")
    private String alarmTypeCode;

    @Schema(description = "告警模型名称")
    private String alarmModelName;

    @Schema(description = "告警场景编号")
    private String alarmSceneType;

    @Schema(description = "风险等级：0高风险 1中风险 2低风险")
    private String alarmLevel;

    @Schema(description = "摄像头名称")
    private String cameraName;

    @Schema(description = "业务视频通道ID")
    private Long videoChannelId;

    @Schema(description = "业务视频通道编号")
    private String videoChannelCode;

    @Schema(description = "同一告警连续触发次数")
    private Integer repeatCount;

    @Schema(description = "持续时长，分钟")
    private Integer durationMinutes;

    @Schema(description = "是否已读：1已读 0未读")
    private Integer isRead;

    @Schema(description = "是否处置：1已处置 0未处置")
    private Integer dealStatus;

    @Schema(description = "是否有效：1正确 0误报")
    private Integer isValid;

    @Schema(description = "是否及时：1及时 0延误")
    private Integer isTimely;

    @Schema(description = "告警状态：0告警中 1告警解除")
    private Integer status;

    @Schema(description = "经度，WGS84")
    private BigDecimal longitude;

    @Schema(description = "纬度，WGS84")
    private BigDecimal latitude;

    @Schema(description = "航速，节")
    private BigDecimal shipSpeed;

    @Schema(description = "航向，度")
    private BigDecimal shipAngle;

    @Schema(description = "告警具体位置")
    private String shipLocation;

    @Schema(description = "船舶状态")
    private String shipStatus;

    @Schema(description = "船型")
    private String shipType;

    @Schema(description = "告警图片锚框")
    private String relativeBox;

    @Schema(description = "告警图片URL")
    private String alarmPicUrl;

    @Schema(description = "告警视频URL")
    private String alarmVideoUrl;

    @Schema(description = "平台接收时间")
    private String receiveTime;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}
