package com.genersoft.iot.vmp.biz.service;

import com.genersoft.iot.vmp.biz.bean.BizTerminal;
import com.github.pagehelper.PageInfo;

public interface IBizTerminalService {

    PageInfo<BizTerminal> queryList(Integer page, Integer count, Long shipId, String keyword, Integer status);

    BizTerminal queryById(Long id);

    void add(BizTerminal terminal);

    void update(BizTerminal terminal);

    void delete(Long id);
}
