package com.genersoft.iot.vmp.biz.service;

import com.genersoft.iot.vmp.biz.bean.BizDutyRoom;
import com.github.pagehelper.PageInfo;

public interface IBizDutyRoomService {

    PageInfo<BizDutyRoom> queryList(Integer page, Integer count, Long companyId, String keyword, Integer status);

    BizDutyRoom queryById(Long id);

    void add(BizDutyRoom dutyRoom);

    void update(BizDutyRoom dutyRoom);

    void delete(Long id);
}
