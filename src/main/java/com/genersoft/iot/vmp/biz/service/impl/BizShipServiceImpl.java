package com.genersoft.iot.vmp.biz.service.impl;

import com.genersoft.iot.vmp.biz.bean.BizShip;
import com.genersoft.iot.vmp.biz.dao.BizCompanyMapper;
import com.genersoft.iot.vmp.biz.dao.BizShipChannelMapper;
import com.genersoft.iot.vmp.biz.dao.BizShipMapper;
import com.genersoft.iot.vmp.biz.dao.BizTerminalMapper;
import com.genersoft.iot.vmp.biz.service.IBizShipService;
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
public class BizShipServiceImpl implements IBizShipService {

    @Autowired
    private BizShipMapper shipMapper;

    @Autowired
    private BizCompanyMapper companyMapper;

    @Autowired
    private BizShipChannelMapper channelMapper;

    @Autowired
    private BizTerminalMapper terminalMapper;

    @Override
    public PageInfo<BizShip> queryList(Integer page, Integer count, String keyword, Long companyId,
                                       Integer status) {
        PageHelper.startPage(BizQueryUtil.page(page), BizQueryUtil.count(count));
        List<BizShip> list = shipMapper.selectList(BizQueryUtil.keyword(keyword), companyId, status);
        return new PageInfo<>(list);
    }

    @Override
    public BizShip queryById(Long id) {
        return shipMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(BizShip ship) {
        validate(ship, false);
        if (ship.getStatus() == null) {
            ship.setStatus(1);
        }
        if (companyMapper.selectById(ship.getCompanyId()) == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "所属企业不存在");
        }
        if (shipMapper.countByShipCn(ship.getShipCn(), null) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "船舶识别号已存在");
        }
        try {
            if (shipMapper.insert(ship) == 0) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "新增船舶失败");
            }
        } catch (DuplicateKeyException e) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "船舶识别号已存在");
        }
        log.info("[船舶档案] 新增船舶 id={}, shipCn={}", ship.getId(), ship.getShipCn());
    }

    @Override
    @Transactional
    public void update(BizShip ship) {
        validate(ship, true);
        BizShip existing = shipMapper.selectById(ship.getId());
        if (existing == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "船舶不存在");
        }
        if (companyMapper.selectById(ship.getCompanyId()) == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "所属企业不存在");
        }
        if (ship.getStatus() == null) {
            ship.setStatus(existing.getStatus());
        }
        if (shipMapper.countByShipCn(ship.getShipCn(), ship.getId()) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "船舶识别号已存在");
        }
        try {
            if (shipMapper.update(ship) == 0) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "更新船舶失败");
            }
        } catch (DuplicateKeyException e) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "船舶识别号已存在");
        }
        log.info("[船舶档案] 更新船舶 id={}, shipCn={}", ship.getId(), ship.getShipCn());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        BizShip existing = shipMapper.selectById(id);
        if (existing == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "船舶不存在");
        }
        if (channelMapper.countByShipId(id) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "船舶下存在视频通道，不能删除");
        }
        if (terminalMapper.countByShipId(id) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "船舶下存在终端，不能删除");
        }
        if (shipMapper.deleteById(id) == 0) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "删除船舶失败");
        }
        log.info("[船舶档案] 删除船舶 id={}, shipCn={}", id, existing.getShipCn());
    }

    private void validate(BizShip ship, boolean requireId) {
        if (ship == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "船舶信息不能为空");
        }
        if (requireId && ship.getId() == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "船舶ID不能为空");
        }
        if (ship.getCompanyId() == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "所属企业不能为空");
        }
        if (ObjectUtils.isEmpty(ship.getShipCn())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "船舶识别号不能为空");
        }
    }
}
