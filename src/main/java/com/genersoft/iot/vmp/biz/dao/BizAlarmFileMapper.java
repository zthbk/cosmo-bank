package com.genersoft.iot.vmp.biz.dao;

import com.genersoft.iot.vmp.biz.bean.BizAlarmFile;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BizAlarmFileMapper {

    @Insert("<script>INSERT INTO biz_alarm_file (alarm_id, file_type, seq_no, url, md5, sha256, file_size) VALUES "
            + "<foreach collection='files' item='item' separator=','>(#{item.alarmId}, #{item.fileType}, "
            + "#{item.seqNo}, #{item.url}, #{item.md5}, #{item.sha256}, #{item.fileSize})</foreach></script>")
    int batchInsert(@Param("files") List<BizAlarmFile> files);

    @Select("SELECT * FROM biz_alarm_file WHERE alarm_id=#{alarmId} ORDER BY file_type, seq_no")
    List<BizAlarmFile> selectByAlarmId(@Param("alarmId") Long alarmId);
}
