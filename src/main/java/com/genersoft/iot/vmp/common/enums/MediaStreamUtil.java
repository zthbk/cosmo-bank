package com.genersoft.iot.vmp.common.enums;

public class MediaStreamUtil {
    public final static String LOAD_MP4_APP = "mp4_record";
    public final static String RTP_APP = "rtp";
    public final static String RTP_STREAM_REST_PREFIX = "s";


    public final static String GB28181_TALK = "talk";
    public final static String GB28181_BROADCAST = "broadcast";

    public static boolean isGB28181(String app, String streamId) {
        return RTP_APP.equals(app) && !streamId.startsWith(RTP_STREAM_REST_PREFIX);
    }

    public static boolean isTalk(String app, String streamId) {
        return GB28181_TALK.equals(app);
    }

    public static boolean isBroadcast(String app, String streamId) {
    return GB28181_BROADCAST.equals(app);
    }
}
