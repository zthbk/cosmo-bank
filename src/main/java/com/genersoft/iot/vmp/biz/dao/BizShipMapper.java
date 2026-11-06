package com.genersoft.iot.vmp.biz.dao;

import com.genersoft.iot.vmp.biz.bean.BizShip;
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
public interface BizShipMapper {

    @Insert("INSERT INTO biz_ship (company_id, tenant_id, ship_cn, ship_mmsi, ship_name, ship_length, ship_width, "
            + "ship_passenger_capacity, ship_type, ship_status, status) VALUES (#{companyId}, #{tenantId}, #{shipCn}, "
            + "#{shipMmsi}, #{shipName}, #{shipLength}, #{shipWidth}, #{shipPassengerCapacity}, #{shipType}, "
            + "#{shipStatus}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(BizShip ship);

    @Update("UPDATE biz_ship SET company_id=#{companyId}, tenant_id=#{tenantId}, ship_cn=#{shipCn}, "
            + "ship_mmsi=#{shipMmsi}, ship_name=#{shipName}, ship_length=#{shipLength}, ship_width=#{shipWidth}, "
            + "ship_passenger_capacity=#{shipPassengerCapacity}, ship_type=#{shipType}, ship_status=#{shipStatus}, "
            + "status=#{status} WHERE id=#{id}")
    int update(BizShip ship);

    @Delete("DELETE FROM biz_ship WHERE id=#{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT * FROM biz_ship WHERE id=#{id}")
    BizShip selectById(@Param("id") Long id);

    @Select("SELECT * FROM biz_ship WHERE ship_cn=#{shipCn}")
    BizShip selectByShipCn(@Param("shipCn") String shipCn);

    @Select("<script>SELECT * FROM biz_ship WHERE 1=1"
            + "<if test='keyword != null'> AND (ship_name LIKE concat('%', #{keyword}, '%') "
            + "OR ship_cn LIKE concat('%', #{keyword}, '%') OR ship_mmsi LIKE concat('%', #{keyword}, '%'))</if>"
            + "<if test='companyId != null'> AND company_id=#{companyId}</if>"
            + "<if test='status != null'> AND status=#{status}</if>"
            + " ORDER BY id DESC</script>")
    List<BizShip> selectList(@Param("keyword") String keyword, @Param("companyId") Long companyId,
                             @Param("status") Integer status);

    @Select("<script>SELECT COUNT(*) FROM biz_ship WHERE ship_cn=#{shipCn}"
            + "<if test='excludeId != null'> AND id != #{excludeId}</if></script>")
    int countByShipCn(@Param("shipCn") String shipCn, @Param("excludeId") Long excludeId);

    @Select("SELECT COUNT(*) FROM biz_ship WHERE company_id=#{companyId}")
    int countByCompanyId(@Param("companyId") Long companyId);
}
