package com.genersoft.iot.vmp.biz.service;

import com.genersoft.iot.vmp.biz.bean.BizCompany;
import com.github.pagehelper.PageInfo;

public interface IBizCompanyService {

    PageInfo<BizCompany> queryList(Integer page, Integer count, String keyword, Integer status);

    BizCompany queryById(Long id);

    void add(BizCompany company);

    void update(BizCompany company);

    void delete(Long id);
}
