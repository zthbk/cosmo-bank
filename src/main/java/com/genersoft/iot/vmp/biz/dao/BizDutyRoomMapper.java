package com.genersoft.iot.vmp.biz.dao;

import com.genersoft.iot.vmp.biz.bean.BizDutyRoom;
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
public interface BizDutyRoomMapper {

    @Insert("INSERT INTO biz_duty_room (company_id, duty_room_code, duty_room_name, region_code, contact_name, "
            + "contact_phone, gb_device_id, status) VALUES (#{companyId}, #{dutyRoomCode}, #{dutyRoomName}, "
            + "#{regionCode}, #{contactName}, #{contactPhone}, #{gbDeviceId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(BizDutyRoom dutyRoom);

    @Update("UPDATE biz_duty_room SET company_id=#{companyId}, duty_room_code=#{dutyRoomCode}, "
            + "duty_room_name=#{dutyRoomName}, region_code=#{regionCode}, contact_name=#{contactName}, "
            + "contact_phone=#{contactPhone}, gb_device_id=#{gbDeviceId}, status=#{status} WHERE id=#{id}")
    int update(BizDutyRoom dutyRoom);

    @Delete("DELETE FROM biz_duty_room WHERE id=#{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT * FROM biz_duty_room WHERE id=#{id}")
    BizDutyRoom selectById(@Param("id") Long id);

    @Select("<script>SELECT * FROM biz_duty_room WHERE 1=1"
            + "<if test='companyId != null'> AND company_id=#{companyId}</if>"
            + "<if test='keyword != null'> AND (duty_room_name LIKE concat('%', #{keyword}, '%') "
            + "OR duty_room_code LIKE concat('%', #{keyword}, '%') OR contact_name LIKE concat('%', #{keyword}, '%'))</if>"
            + "<if test='status != null'> AND status=#{status}</if>"
            + " ORDER BY id DESC</script>")
    List<BizDutyRoom> selectList(@Param("companyId") Long companyId, @Param("keyword") String keyword,
                                 @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM biz_duty_room WHERE company_id=#{companyId}")
    int countByCompanyId(@Param("companyId") Long companyId);

    @Select("<script>SELECT COUNT(*) FROM biz_duty_room WHERE duty_room_code=#{dutyRoomCode}"
            + "<if test='excludeId != null'> AND id != #{excludeId}</if></script>")
    int countByDutyRoomCode(@Param("dutyRoomCode") String dutyRoomCode, @Param("excludeId") Long excludeId);
}
