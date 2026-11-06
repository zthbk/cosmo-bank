package com.genersoft.iot.vmp.biz.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 告警图片与视频
 */
@Data
@Schema(description = "告警文件")
public class BizAlarmFile {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "告警ID")
    private Long alarmId;

    @Schema(description = "文件类型：1图片 2视频")
    private Integer fileType;

    @Schema(description = "同类文件序号")
    private Integer seqNo;

    @Schema(description = "文件URL")
    private String url;

    @Schema(description = "MD5")
    private String md5;

    @Schema(description = "SHA-256")
    private String sha256;

    @Schema(description = "文件大小，字节")
    private Long fileSize;

    @Schema(description = "创建时间")
    private String createTime;
}
