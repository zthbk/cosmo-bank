package com.genersoft.iot.vmp.biz.service;

import com.genersoft.iot.vmp.biz.bean.BizShip;
import com.github.pagehelper.PageInfo;

public interface IBizShipService {

    PageInfo<BizShip> queryList(Integer page, Integer count, String keyword, Long companyId, Integer status);

    BizShip queryById(Long id);

    void add(BizShip ship);

    void update(BizShip ship);

    void delete(Long id);
}
