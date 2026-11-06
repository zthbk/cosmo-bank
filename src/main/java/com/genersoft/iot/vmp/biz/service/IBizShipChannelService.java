package com.genersoft.iot.vmp.biz.service;

import com.genersoft.iot.vmp.biz.bean.BizShipChannel;
import com.github.pagehelper.PageInfo;

public interface IBizShipChannelService {

    PageInfo<BizShipChannel> queryList(Integer page, Integer count, Long shipId, String keyword, Integer status);

    BizShipChannel queryById(Long id);

    void add(BizShipChannel channel);

    void update(BizShipChannel channel);

    void delete(Long id);
}
