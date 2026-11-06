package com.genersoft.iot.vmp.biz.service.impl;

import com.genersoft.iot.vmp.biz.bean.BizTerminal;
import com.genersoft.iot.vmp.biz.dao.BizShipMapper;
import com.genersoft.iot.vmp.biz.dao.BizTerminalMapper;
import com.genersoft.iot.vmp.biz.service.IBizTerminalService;
import com.genersoft.iot.vmp.biz.util.BizQueryUtil;
import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

@Slf4j
@Service
public class BizTerminalServiceImpl implements IBizTerminalService {

    @Autowired
    private BizTerminalMapper terminalMapper;

    @Autowired
    private BizShipMapper shipMapper;

    @Override
    public PageInfo<BizTerminal> queryList(Integer page, Integer count, Long shipId, String keyword,
                                           Integer status) {
        PageHelper.startPage(BizQueryUtil.page(page), BizQueryUtil.count(count));
        List<BizTerminal> list = terminalMapper.selectList(shipId, BizQueryUtil.keyword(keyword), status);
        return new PageInfo<>(list);
    }

    @Override
    public BizTerminal queryById(Long id) {
        return terminalMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(BizTerminal terminal) {
        validate(terminal, false);
        checkShipExists(terminal.getShipId());
        if (terminal.getOnlineStatus() == null) {
            terminal.setOnlineStatus(0);
        }
        if (terminal.getCacheStatus() == null) {
            terminal.setCacheStatus(0);
        }
        if (terminal.getStatus() == null) {
            terminal.setStatus(1);
        }
        if (terminalMapper.countByTerminalUuid(terminal.getTerminalUuid(), null) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "终端标识已存在");
        }
        try {
            if (terminalMapper.insert(terminal) == 0) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "新增终端失败");
            }
        } catch (DuplicateKeyException e) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "终端标识已存在");
        }
        log.info("[终端档案] 新增终端 id={}, uuid={}", terminal.getId(), terminal.getTerminalUuid());
    }

    @Override
    @Transactional
    public void update(BizTerminal terminal) {
        validate(terminal, true);
        BizTerminal existing = terminalMapper.selectById(terminal.getId());
        if (existing == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "终端不存在");
        }
        checkShipExists(terminal.getShipId());
        if (terminal.getOnlineStatus() == null) {
            terminal.setOnlineStatus(existing.getOnlineStatus());
        }
        if (terminal.getCacheStatus() == null) {
            terminal.setCacheStatus(existing.getCacheStatus());
        }
        if (terminal.getStatus() == null) {
            terminal.setStatus(existing.getStatus());
        }
        if (terminalMapper.countByTerminalUuid(terminal.getTerminalUuid(), terminal.getId()) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "终端标识已存在");
        }
        try {
            if (terminalMapper.update(terminal) == 0) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "更新终端失败");
            }
        } catch (DuplicateKeyException e) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "终端标识已存在");
        }
        log.info("[终端档案] 更新终端 id={}, uuid={}", terminal.getId(), terminal.getTerminalUuid());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        BizTerminal existing = terminalMapper.selectById(id);
        if (existing == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "终端不存在");
        }
        if (terminalMapper.deleteById(id) == 0) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "删除终端失败");
        }
        log.info("[终端档案] 删除终端 id={}, uuid={}", id, existing.getTerminalUuid());
    }

    private void checkShipExists(Long shipId) {
        if (shipMapper.selectById(shipId) == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "所属船舶不存在");
        }
    }

    private void validate(BizTerminal terminal, boolean requireId) {
        if (terminal == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "终端信息不能为空");
        }
        if (requireId && terminal.getId() == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "终端ID不能为空");
        }
        if (terminal.getShipId() == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "所属船舶不能为空");
        }
        if (ObjectUtils.isEmpty(terminal.getTerminalUuid())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "终端标识不能为空");
        }
    }
}
