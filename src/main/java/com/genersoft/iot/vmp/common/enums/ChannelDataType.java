package com.genersoft.iot.vmp.common.enums;

/**
 * 支持的通道数据类型
 */

public class ChannelDataType {

    public final static int GB28181 = 1;

    public final static String PLAY_SERVICE = "sourceChannelPlayService";
    public final static String PLAYBACK_SERVICE = "sourceChannelPlaybackService";
    public final static String DOWNLOAD_SERVICE = "sourceChannelDownloadService";
    public final static String PTZ_SERVICE = "sourceChannelPTZService";
    public final static String OTHER_SERVICE = "sourceChannelOtherService";
    public final static String BROADCAST_SERVICE = "sourceChannelBroadcastService";


    public static String getDateTypeDesc(Integer dataType) {
        if (dataType == null) {
            return "未知";
        }
        return switch (dataType) {
            case ChannelDataType.GB28181 -> "国标28181";
            default -> "未知";
        };
    }


}
