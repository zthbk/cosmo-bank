package com.genersoft.iot.vmp.biz.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 船舶终端档案
 */
@Data
@Schema(description = "船舶终端档案")
public class BizTerminal {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "所属船舶ID")
    private Long shipId;

    @Schema(description = "终端唯一标识/设备机号")
    private String terminalUuid;

    @Schema(description = "关联国标设备编号")
    private String gbDeviceId;

    @Schema(description = "终端型号")
    private String terminalModel;

    @Schema(description = "软件版本")
    private String softwareVersion;

    @Schema(description = "在线状态：1在线 0离线")
    private Integer onlineStatus;

    @Schema(description = "最后在线时间")
    private String lastOnlineTime;

    @Schema(description = "缓存状态：0正常 1补传中 2异常")
    private Integer cacheStatus;

    @Schema(description = "状态：1启用 0停用")
    private Integer status;

    @Schema(description = "创建时间")
    private String createTime;

    @Schema(description = "更新时间")
    private String updateTime;
}
