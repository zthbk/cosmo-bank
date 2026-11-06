package com.genersoft.iot.vmp.biz.dao;

import com.genersoft.iot.vmp.biz.bean.BizAlarmModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BizAlarmModelMapper {

    @Select("<script>SELECT * FROM biz_alarm_model WHERE 1=1"
            + "<if test='modelCategory != null'> AND model_category=#{modelCategory}</if>"
            + "<if test='enabled != null'> AND enabled=#{enabled}</if>"
            + " ORDER BY model_category, model_code</script>")
    List<BizAlarmModel> selectList(@Param("modelCategory") Integer modelCategory,
                                   @Param("enabled") Integer enabled);
}
