package com.genersoft.iot.vmp.biz.dao;

import com.genersoft.iot.vmp.biz.bean.BizAlarmDeal;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BizAlarmDealMapper {

    @Insert("INSERT INTO biz_alarm_deal (alarm_id, is_valid, deal_type, deal_result, deal_time, op_user, remark) "
            + "VALUES (#{alarmId}, #{isValid}, #{dealType}, #{dealResult}, #{dealTime}, #{opUser}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insert(BizAlarmDeal deal);

    @Select("SELECT * FROM biz_alarm_deal WHERE alarm_id=#{alarmId} ORDER BY deal_time DESC, id DESC")
    List<BizAlarmDeal> selectByAlarmId(@Param("alarmId") Long alarmId);
}
