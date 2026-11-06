package com.genersoft.iot.vmp.biz.dao;

import com.genersoft.iot.vmp.biz.bean.BizShipChannel;
import org.apache.ibatis.annotations.Delete;
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
public interface BizShipChannelMapper {

    @Insert("INSERT INTO biz_ship_channel (ship_id, video_channel_code, video_channel_name, video_channel_support, "
            + "video_type, gb_device_id, gb_channel_id, camera_name, talk_enabled, status) VALUES (#{shipId}, "
            + "#{videoChannelCode}, #{videoChannelName}, #{videoChannelSupport}, #{videoType}, #{gbDeviceId}, "
            + "#{gbChannelId}, #{cameraName}, #{talkEnabled}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(BizShipChannel channel);

    @Update("UPDATE biz_ship_channel SET ship_id=#{shipId}, video_channel_code=#{videoChannelCode}, "
            + "video_channel_name=#{videoChannelName}, video_channel_support=#{videoChannelSupport}, "
            + "video_type=#{videoType}, gb_device_id=#{gbDeviceId}, gb_channel_id=#{gbChannelId}, "
            + "camera_name=#{cameraName}, talk_enabled=#{talkEnabled}, status=#{status} WHERE id=#{id}")
    int update(BizShipChannel channel);

    @Delete("DELETE FROM biz_ship_channel WHERE id=#{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT * FROM biz_ship_channel WHERE id=#{id}")
    BizShipChannel selectById(@Param("id") Long id);

    @Select("<script>SELECT * FROM biz_ship_channel WHERE 1=1"
            + "<if test='shipId != null'> AND ship_id=#{shipId}</if>"
            + "<if test='keyword != null'> AND (video_channel_name LIKE concat('%', #{keyword}, '%') "
            + "OR video_channel_code LIKE concat('%', #{keyword}, '%') OR camera_name LIKE concat('%', #{keyword}, '%'))</if>"
            + "<if test='status != null'> AND status=#{status}</if>"
            + " ORDER BY id DESC</script>")
    List<BizShipChannel> selectList(@Param("shipId") Long shipId, @Param("keyword") String keyword,
                                    @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM biz_ship_channel WHERE ship_id=#{shipId}")
    int countByShipId(@Param("shipId") Long shipId);

    @Select("<script>SELECT COUNT(*) FROM biz_ship_channel WHERE video_channel_code=#{channelCode}"
            + "<if test='excludeId != null'> AND id != #{excludeId}</if></script>")
    int countByChannelCode(@Param("channelCode") String channelCode, @Param("excludeId") Long excludeId);

    @Select("<script>SELECT COUNT(*) FROM biz_ship_channel WHERE gb_device_id=#{gbDeviceId} "
            + "AND gb_channel_id=#{gbChannelId}"
            + "<if test='excludeId != null'> AND id != #{excludeId}</if></script>")
    int countByGbChannel(@Param("gbDeviceId") String gbDeviceId, @Param("gbChannelId") String gbChannelId,
                         @Param("excludeId") Long excludeId);
}
