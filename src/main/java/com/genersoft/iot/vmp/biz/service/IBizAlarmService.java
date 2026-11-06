package com.genersoft.iot.vmp.biz.service;

import com.genersoft.iot.vmp.biz.bean.BizAlarm;
import com.genersoft.iot.vmp.biz.bean.BizAlarmModel;
import com.genersoft.iot.vmp.biz.dto.AlarmDetail;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface IBizAlarmService {

    void report(String accessKey, String timestamp, String signature, String body);

    PageInfo<BizAlarm> queryList(Integer page, Integer count, Long shipId, String shipCn, String keyword,
                                 String alarmLevel, Integer dealStatus, Integer isRead, Integer status,
                                 String beginTime, String endTime);

    AlarmDetail queryDetail(Long id);

    int markRead(List<Long> ids);

    List<BizAlarmModel> queryModels(Integer category, Integer enabled);
}
