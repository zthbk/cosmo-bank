package com.genersoft.iot.vmp.biz.service.impl;

import com.genersoft.iot.vmp.biz.bean.BizShipChannel;
import com.genersoft.iot.vmp.biz.dao.BizShipChannelMapper;
import com.genersoft.iot.vmp.biz.dao.BizShipMapper;
import com.genersoft.iot.vmp.biz.service.IBizShipChannelService;
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
public class BizShipChannelServiceImpl implements IBizShipChannelService {

    @Autowired
    private BizShipChannelMapper channelMapper;

    @Autowired
    private BizShipMapper shipMapper;

    @Override
    public PageInfo<BizShipChannel> queryList(Integer page, Integer count, Long shipId, String keyword,
                                              Integer status) {
        PageHelper.startPage(BizQueryUtil.page(page), BizQueryUtil.count(count));
        List<BizShipChannel> list = channelMapper.selectList(shipId, BizQueryUtil.keyword(keyword), status);
        return new PageInfo<>(list);
    }

    @Override
    public BizShipChannel queryById(Long id) {
        return channelMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(BizShipChannel channel) {
        validate(channel, false);
        checkShipExists(channel.getShipId());
        if (channel.getTalkEnabled() == null) {
            channel.setTalkEnabled(0);
        }
        if (channel.getStatus() == null) {
            channel.setStatus(1);
        }
        checkUnique(channel, null);
        try {
            if (channelMapper.insert(channel) == 0) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "新增视频通道失败");
            }
        } catch (DuplicateKeyException e) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "通道编号或国标通道已存在");
        }
        log.info("[视频通道] 新增通道 id={}, code={}", channel.getId(), channel.getVideoChannelCode());
    }

    @Override
    @Transactional
    public void update(BizShipChannel channel) {
        validate(channel, true);
        BizShipChannel existing = channelMapper.selectById(channel.getId());
        if (existing == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "视频通道不存在");
        }
        checkShipExists(channel.getShipId());
        if (channel.getTalkEnabled() == null) {
            channel.setTalkEnabled(existing.getTalkEnabled());
        }
        if (channel.getStatus() == null) {
            channel.setStatus(existing.getStatus());
        }
        checkUnique(channel, channel.getId());
        try {
            if (channelMapper.update(channel) == 0) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "更新视频通道失败");
            }
        } catch (DuplicateKeyException e) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "通道编号或国标通道已存在");
        }
        log.info("[视频通道] 更新通道 id={}, code={}", channel.getId(), channel.getVideoChannelCode());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        BizShipChannel existing = channelMapper.selectById(id);
        if (existing == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "视频通道不存在");
        }
        if (channelMapper.deleteById(id) == 0) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "删除视频通道失败");
        }
        log.info("[视频通道] 删除通道 id={}, code={}", id, existing.getVideoChannelCode());
    }

    private void checkShipExists(Long shipId) {
        if (shipMapper.selectById(shipId) == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "所属船舶不存在");
        }
    }

    private void checkUnique(BizShipChannel channel, Long excludeId) {
        if (channelMapper.countByChannelCode(channel.getVideoChannelCode(), excludeId) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "通道编号已存在");
        }
        if (!ObjectUtils.isEmpty(channel.getGbDeviceId()) && !ObjectUtils.isEmpty(channel.getGbChannelId())
                && channelMapper.countByGbChannel(channel.getGbDeviceId(), channel.getGbChannelId(), excludeId) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "国标通道已绑定其他业务通道");
        }
    }

    private void validate(BizShipChannel channel, boolean requireId) {
        if (channel == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "视频通道信息不能为空");
        }
        if (requireId && channel.getId() == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "视频通道ID不能为空");
        }
        if (channel.getShipId() == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "所属船舶不能为空");
        }
        if (ObjectUtils.isEmpty(channel.getVideoChannelCode())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "通道编号不能为空");
        }
        if (ObjectUtils.isEmpty(channel.getVideoChannelName())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "通道名称不能为空");
        }
    }
}
