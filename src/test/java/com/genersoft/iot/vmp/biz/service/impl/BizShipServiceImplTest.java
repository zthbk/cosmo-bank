package com.genersoft.iot.vmp.biz.service.impl;

import com.genersoft.iot.vmp.biz.bean.BizCompany;
import com.genersoft.iot.vmp.biz.bean.BizShip;
import com.genersoft.iot.vmp.biz.dao.BizCompanyMapper;
import com.genersoft.iot.vmp.biz.dao.BizShipChannelMapper;
import com.genersoft.iot.vmp.biz.dao.BizShipMapper;
import com.genersoft.iot.vmp.biz.dao.BizTerminalMapper;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BizShipServiceImplTest {

    @Mock
    private BizShipMapper shipMapper;

    @Mock
    private BizCompanyMapper companyMapper;

    @Mock
    private BizShipChannelMapper channelMapper;

    @Mock
    private BizTerminalMapper terminalMapper;

    @InjectMocks
    private BizShipServiceImpl shipService;

    @Test
    void add_duplicateShipCn_throwsControllerException() {
        BizShip ship = new BizShip();
        ship.setCompanyId(1L);
        ship.setShipCn("CN20240715001");

        when(companyMapper.selectById(1L)).thenReturn(new BizCompany());
        when(shipMapper.countByShipCn("CN20240715001", null)).thenReturn(1);

        ControllerException exception = assertThrows(ControllerException.class, () -> shipService.add(ship));

        assertEquals(ErrorCode.ERROR400.getCode(), exception.getCode());
        verify(shipMapper, never()).insert(any());
    }

    @Test
    void add_companyNotExists_throwsControllerException() {
        BizShip ship = new BizShip();
        ship.setCompanyId(9L);
        ship.setShipCn("CN20240715002");

        when(companyMapper.selectById(9L)).thenReturn(null);

        ControllerException exception = assertThrows(ControllerException.class, () -> shipService.add(ship));

        assertEquals(ErrorCode.ERROR400.getCode(), exception.getCode());
        verify(shipMapper, never()).insert(any());
    }

    @Test
    void delete_shipHasChannels_throwsControllerException() {
        BizShip ship = new BizShip();
        ship.setId(1L);
        ship.setShipCn("CN20240715003");

        when(shipMapper.selectById(1L)).thenReturn(ship);
        when(channelMapper.countByShipId(1L)).thenReturn(1);

        ControllerException exception = assertThrows(ControllerException.class, () -> shipService.delete(1L));

        assertEquals(ErrorCode.ERROR400.getCode(), exception.getCode());
        verify(shipMapper, never()).deleteById(anyLong());
    }
}
