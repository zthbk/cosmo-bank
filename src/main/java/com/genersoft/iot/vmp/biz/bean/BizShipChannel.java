package com.genersoft.iot.vmp.biz.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 船舶视频通道与国标通道映射
 */
@Data
@Schema(description = "船舶视频通道")
public class BizShipChannel {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "所属船舶ID")
    private Long shipId;

    @Schema(description = "业务通道编号")
    private String videoChannelCode;

    @Schema(description = "通道名称")
    private String videoChannelName;

    @Schema(description = "支持功能，逗号分隔：0回放 1对讲 2下载 3抓拍")
    private String videoChannelSupport;

    @Schema(description = "取流协议：ws/hls/http-flv/ws-flv")
    private String videoType;

    @Schema(description = "国标设备编号")
    private String gbDeviceId;

    @Schema(description = "国标通道编号")
    private String gbChannelId;

    @Schema(description = "摄像头名称")
    private String cameraName;

    @Schema(description = "是否支持对讲：1是 0否")
    private Integer talkEnabled;

    @Schema(description = "状态：1启用 0停用")
    private Integer status;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}
