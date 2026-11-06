package com.genersoft.iot.vmp.biz.service.impl;

import com.genersoft.iot.vmp.biz.bean.BizShip;
import com.genersoft.iot.vmp.biz.bean.BizShipChannel;
import com.genersoft.iot.vmp.biz.dao.BizShipChannelMapper;
import com.genersoft.iot.vmp.biz.dao.BizShipMapper;
import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BizShipChannelServiceImplTest {

    @Mock
    private BizShipChannelMapper channelMapper;

    @Mock
    private BizShipMapper shipMapper;

    @InjectMocks
    private BizShipChannelServiceImpl channelService;

    @Test
    void add_duplicateGbChannel_throwsControllerException() {
        BizShipChannel channel = new BizShipChannel();
        channel.setShipId(1L);
        channel.setVideoChannelCode("CH001");
        channel.setVideoChannelName("驾驶室主摄像头");
        channel.setGbDeviceId("34020000001320000001");
        channel.setGbChannelId("34020000001310000001");

        when(shipMapper.selectById(1L)).thenReturn(new BizShip());
        when(channelMapper.countByChannelCode("CH001", null)).thenReturn(0);
        when(channelMapper.countByGbChannel("34020000001320000001",
                "34020000001310000001", null)).thenReturn(1);

        ControllerException exception = assertThrows(ControllerException.class, () -> channelService.add(channel));

        assertEquals(ErrorCode.ERROR400.getCode(), exception.getCode());
        verify(channelMapper, never()).insert(any());
    }

    @Test
    void add_shipNotExists_throwsControllerException() {
        BizShipChannel channel = new BizShipChannel();
        channel.setShipId(9L);
        channel.setVideoChannelCode("CH002");
        channel.setVideoChannelName("机舱监控");

        when(shipMapper.selectById(9L)).thenReturn(null);

        ControllerException exception = assertThrows(ControllerException.class, () -> channelService.add(channel));

        assertEquals(ErrorCode.ERROR400.getCode(), exception.getCode());
        verify(channelMapper, never()).insert(any());
    }
}
