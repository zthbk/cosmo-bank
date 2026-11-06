package com.genersoft.iot.vmp.biz.dao;

import com.genersoft.iot.vmp.biz.bean.BizAlarm;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BizAlarmMapper {

    @Insert("INSERT INTO biz_alarm (tenant_id, ship_id, ship_cn, ship_mmsi, ship_name, alarm_code, alarm_time, "
            + "alarm_type_code, alarm_model_name, alarm_scene_type, alarm_level, camera_name, video_channel_id, "
            + "video_channel_code, repeat_count, duration_minutes, is_read, deal_status, is_valid, is_timely, status, "
            + "longitude, latitude, ship_speed, ship_angle, ship_location, ship_status, ship_type, relative_box, "
            + "alarm_pic_url, alarm_video_url) VALUES (#{tenantId}, #{shipId}, #{shipCn}, #{shipMmsi}, #{shipName}, "
            + "#{alarmCode}, #{alarmTime}, #{alarmTypeCode}, #{alarmModelName}, #{alarmSceneType}, #{alarmLevel}, "
            + "#{cameraName}, #{videoChannelId}, #{videoChannelCode}, #{repeatCount}, #{durationMinutes}, #{isRead}, "
            + "#{dealStatus}, #{isValid}, #{isTimely}, #{status}, #{longitude}, #{latitude}, #{shipSpeed}, "
            + "#{shipAngle}, #{shipLocation}, #{shipStatus}, #{shipType}, #{relativeBox}, #{alarmPicUrl}, "
            + "#{alarmVideoUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(BizAlarm alarm);

    @Select("SELECT * FROM biz_alarm WHERE alarm_code=#{alarmCode}")
    BizAlarm selectByAlarmCode(@Param("alarmCode") String alarmCode);

    @Select("SELECT * FROM biz_alarm WHERE id=#{id}")
    BizAlarm selectById(@Param("id") Long id);

    @Select("<script>SELECT * FROM biz_alarm WHERE 1=1"
            + "<if test='shipId != null'> AND ship_id=#{shipId}</if>"
            + "<if test='shipCn != null'> AND ship_cn=#{shipCn}</if>"
            + "<if test='keyword != null'> AND (ship_name LIKE concat('%', #{keyword}, '%') "
            + "OR ship_cn LIKE concat('%', #{keyword}, '%') OR alarm_type_code LIKE concat('%', #{keyword}, '%'))</if>"
            + "<if test='alarmLevel != null'> AND alarm_level=#{alarmLevel}</if>"
            + "<if test='dealStatus != null'> AND deal_status=#{dealStatus}</if>"
            + "<if test='isRead != null'> AND is_read=#{isRead}</if>"
            + "<if test='status != null'> AND status=#{status}</if>"
            + "<if test='beginTime != null'> AND alarm_time &gt;= #{beginTime}</if>"
            + "<if test='endTime != null'> AND alarm_time &lt;= #{endTime}</if>"
            + " ORDER BY alarm_time DESC, id DESC</script>")
    List<BizAlarm> selectList(@Param("shipId") Long shipId, @Param("shipCn") String shipCn,
                              @Param("keyword") String keyword, @Param("alarmLevel") String alarmLevel,
                              @Param("dealStatus") Integer dealStatus, @Param("isRead") Integer isRead,
                              @Param("status") Integer status, @Param("beginTime") String beginTime,
                              @Param("endTime") String endTime);

    @Update("<script>UPDATE biz_alarm SET is_read=1 WHERE id IN "
            + "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach></script>")
    int updateRead(@Param("ids") List<Long> ids);
}
