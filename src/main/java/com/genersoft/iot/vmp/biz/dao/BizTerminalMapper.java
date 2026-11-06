package com.genersoft.iot.vmp.biz.dao;

import com.genersoft.iot.vmp.biz.bean.BizTerminal;
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
public interface BizTerminalMapper {

    @Insert("INSERT INTO biz_terminal (ship_id, terminal_uuid, gb_device_id, terminal_model, software_version, "
            + "online_status, last_online_time, cache_status, status) VALUES (#{shipId}, #{terminalUuid}, "
            + "#{gbDeviceId}, #{terminalModel}, #{softwareVersion}, #{onlineStatus}, #{lastOnlineTime}, "
            + "#{cacheStatus}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(BizTerminal terminal);

    @Update("UPDATE biz_terminal SET ship_id=#{shipId}, terminal_uuid=#{terminalUuid}, gb_device_id=#{gbDeviceId}, "
            + "terminal_model=#{terminalModel}, software_version=#{softwareVersion}, online_status=#{onlineStatus}, "
            + "last_online_time=#{lastOnlineTime}, cache_status=#{cacheStatus}, status=#{status} WHERE id=#{id}")
    int update(BizTerminal terminal);

    @Delete("DELETE FROM biz_terminal WHERE id=#{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT * FROM biz_terminal WHERE id=#{id}")
    BizTerminal selectById(@Param("id") Long id);

    @Select("<script>SELECT * FROM biz_terminal WHERE 1=1"
            + "<if test='shipId != null'> AND ship_id=#{shipId}</if>"
            + "<if test='keyword != null'> AND (terminal_uuid LIKE concat('%', #{keyword}, '%') "
            + "OR gb_device_id LIKE concat('%', #{keyword}, '%') OR terminal_model LIKE concat('%', #{keyword}, '%'))</if>"
            + "<if test='status != null'> AND status=#{status}</if>"
            + " ORDER BY id DESC</script>")
    List<BizTerminal> selectList(@Param("shipId") Long shipId, @Param("keyword") String keyword,
                                 @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM biz_terminal WHERE ship_id=#{shipId}")
    int countByShipId(@Param("shipId") Long shipId);

    @Select("<script>SELECT COUNT(*) FROM biz_terminal WHERE terminal_uuid=#{terminalUuid}"
            + "<if test='excludeId != null'> AND id != #{excludeId}</if></script>")
    int countByTerminalUuid(@Param("terminalUuid") String terminalUuid, @Param("excludeId") Long excludeId);
}
