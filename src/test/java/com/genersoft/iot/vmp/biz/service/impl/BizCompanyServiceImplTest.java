package com.genersoft.iot.vmp.biz.service.impl;

import com.genersoft.iot.vmp.biz.bean.BizCompany;
import com.genersoft.iot.vmp.biz.dao.BizCompanyMapper;
import com.genersoft.iot.vmp.biz.dao.BizDutyRoomMapper;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BizCompanyServiceImplTest {

    @Mock
    private BizCompanyMapper companyMapper;

    @Mock
    private BizShipMapper shipMapper;

    @Mock
    private BizDutyRoomMapper dutyRoomMapper;

    @InjectMocks
    private BizCompanyServiceImpl companyService;

    @Test
    void add_duplicateCreditCode_throwsControllerException() {
        BizCompany company = new BizCompany();
        company.setCompanyName("测试航运有限公司");
        company.setUniformSocialCreditCode("91310115MA5678EFGH");

        when(companyMapper.countByCreditCode("91310115MA5678EFGH", null)).thenReturn(1);

        ControllerException exception = assertThrows(ControllerException.class, () -> companyService.add(company));

        assertEquals(ErrorCode.ERROR400.getCode(), exception.getCode());
        verify(companyMapper, never()).insert(any());
    }

    @Test
    void delete_companyHasShips_throwsControllerException() {
        BizCompany company = new BizCompany();
        company.setId(1L);
        company.setCompanyName("测试航运有限公司");

        when(companyMapper.selectById(1L)).thenReturn(company);
        when(shipMapper.countByCompanyId(1L)).thenReturn(1);

        ControllerException exception = assertThrows(ControllerException.class, () -> companyService.delete(1L));

        assertEquals(ErrorCode.ERROR400.getCode(), exception.getCode());
        verify(companyMapper, never()).deleteById(anyLong());
    }
}
