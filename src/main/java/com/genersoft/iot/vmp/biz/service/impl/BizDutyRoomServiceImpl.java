package com.genersoft.iot.vmp.biz.service.impl;

import com.genersoft.iot.vmp.biz.bean.BizDutyRoom;
import com.genersoft.iot.vmp.biz.dao.BizCompanyMapper;
import com.genersoft.iot.vmp.biz.dao.BizDutyRoomMapper;
import com.genersoft.iot.vmp.biz.service.IBizDutyRoomService;
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
public class BizDutyRoomServiceImpl implements IBizDutyRoomService {

    @Autowired
    private BizDutyRoomMapper dutyRoomMapper;

    @Autowired
    private BizCompanyMapper companyMapper;

    @Override
    public PageInfo<BizDutyRoom> queryList(Integer page, Integer count, Long companyId, String keyword,
                                           Integer status) {
        PageHelper.startPage(BizQueryUtil.page(page), BizQueryUtil.count(count));
        List<BizDutyRoom> list = dutyRoomMapper.selectList(companyId, BizQueryUtil.keyword(keyword), status);
        return new PageInfo<>(list);
    }

    @Override
    public BizDutyRoom queryById(Long id) {
        return dutyRoomMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(BizDutyRoom dutyRoom) {
        validate(dutyRoom, false);
        checkCompanyExists(dutyRoom.getCompanyId());
        if (dutyRoom.getStatus() == null) {
            dutyRoom.setStatus(1);
        }
        if (dutyRoomMapper.countByDutyRoomCode(dutyRoom.getDutyRoomCode(), null) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "值班室识别号已存在");
        }
        try {
            if (dutyRoomMapper.insert(dutyRoom) == 0) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "新增值班室失败");
            }
        } catch (DuplicateKeyException e) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "值班室识别号已存在");
        }
        log.info("[值班室档案] 新增值班室 id={}, code={}", dutyRoom.getId(), dutyRoom.getDutyRoomCode());
    }

    @Override
    @Transactional
    public void update(BizDutyRoom dutyRoom) {
        validate(dutyRoom, true);
        BizDutyRoom existing = dutyRoomMapper.selectById(dutyRoom.getId());
        if (existing == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "值班室不存在");
        }
        checkCompanyExists(dutyRoom.getCompanyId());
        if (dutyRoom.getStatus() == null) {
            dutyRoom.setStatus(existing.getStatus());
        }
        if (dutyRoomMapper.countByDutyRoomCode(dutyRoom.getDutyRoomCode(), dutyRoom.getId()) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "值班室识别号已存在");
        }
        try {
            if (dutyRoomMapper.update(dutyRoom) == 0) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "更新值班室失败");
            }
        } catch (DuplicateKeyException e) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "值班室识别号已存在");
        }
        log.info("[值班室档案] 更新值班室 id={}, code={}", dutyRoom.getId(), dutyRoom.getDutyRoomCode());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        BizDutyRoom existing = dutyRoomMapper.selectById(id);
        if (existing == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "值班室不存在");
        }
        if (dutyRoomMapper.deleteById(id) == 0) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "删除值班室失败");
        }
        log.info("[值班室档案] 删除值班室 id={}, code={}", id, existing.getDutyRoomCode());
    }

    private void checkCompanyExists(Long companyId) {
        if (companyMapper.selectById(companyId) == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "所属企业不存在");
        }
    }

    private void validate(BizDutyRoom dutyRoom, boolean requireId) {
        if (dutyRoom == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "值班室信息不能为空");
        }
        if (requireId && dutyRoom.getId() == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "值班室ID不能为空");
        }
        if (dutyRoom.getCompanyId() == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "所属企业不能为空");
        }
        if (ObjectUtils.isEmpty(dutyRoom.getDutyRoomCode())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "值班室识别号不能为空");
        }
        if (ObjectUtils.isEmpty(dutyRoom.getDutyRoomName())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "值班室名称不能为空");
        }
    }
}
