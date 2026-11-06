package com.genersoft.iot.vmp.biz.dao;

import com.genersoft.iot.vmp.biz.bean.BizApiClient;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface BizApiClientMapper {

    @Select("SELECT * FROM biz_api_client WHERE access_key=#{accessKey} AND status=1")
    BizApiClient selectByAccessKey(@Param("accessKey") String accessKey);
}
