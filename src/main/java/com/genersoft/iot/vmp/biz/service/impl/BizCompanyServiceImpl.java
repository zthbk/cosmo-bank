package com.genersoft.iot.vmp.biz.service.impl;

import com.genersoft.iot.vmp.biz.bean.BizCompany;
import com.genersoft.iot.vmp.biz.dao.BizCompanyMapper;
import com.genersoft.iot.vmp.biz.dao.BizDutyRoomMapper;
import com.genersoft.iot.vmp.biz.dao.BizShipMapper;
import com.genersoft.iot.vmp.biz.service.IBizCompanyService;
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
public class BizCompanyServiceImpl implements IBizCompanyService {

    @Autowired
    private BizCompanyMapper companyMapper;

    @Autowired
    private BizShipMapper shipMapper;

    @Autowired
    private BizDutyRoomMapper dutyRoomMapper;

    @Override
    public PageInfo<BizCompany> queryList(Integer page, Integer count, String keyword, Integer status) {
        PageHelper.startPage(BizQueryUtil.page(page), BizQueryUtil.count(count));
        List<BizCompany> list = companyMapper.selectList(BizQueryUtil.keyword(keyword), status);
        return new PageInfo<>(list);
    }

    @Override
    public BizCompany queryById(Long id) {
        return companyMapper.selectById(id);
    }

    @Override
    @Transactional
    public void add(BizCompany company) {
        validate(company, false);
        if (company.getStatus() == null) {
            company.setStatus(1);
        }
        if (companyMapper.countByCreditCode(company.getUniformSocialCreditCode(), null) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "统一社会信用代码已存在");
        }
        try {
            if (companyMapper.insert(company) == 0) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "新增企业失败");
            }
        } catch (DuplicateKeyException e) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "统一社会信用代码或企业标识已存在");
        }
        log.info("[企业档案] 新增企业 id={}, name={}", company.getId(), company.getCompanyName());
    }

    @Override
    @Transactional
    public void update(BizCompany company) {
        validate(company, true);
        BizCompany existing = companyMapper.selectById(company.getId());
        if (existing == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "企业不存在");
        }
        if (company.getStatus() == null) {
            company.setStatus(existing.getStatus());
        }
        if (companyMapper.countByCreditCode(company.getUniformSocialCreditCode(), company.getId()) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "统一社会信用代码已存在");
        }
        try {
            if (companyMapper.update(company) == 0) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "更新企业失败");
            }
        } catch (DuplicateKeyException e) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "统一社会信用代码或企业标识已存在");
        }
        log.info("[企业档案] 更新企业 id={}, name={}", company.getId(), company.getCompanyName());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        BizCompany existing = companyMapper.selectById(id);
        if (existing == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "企业不存在");
        }
        if (shipMapper.countByCompanyId(id) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "企业下存在船舶，不能删除");
        }
        if (dutyRoomMapper.countByCompanyId(id) > 0) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "企业下存在值班室，不能删除");
        }
        if (companyMapper.deleteById(id) == 0) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "删除企业失败");
        }
        log.info("[企业档案] 删除企业 id={}, name={}", id, existing.getCompanyName());
    }

    private void validate(BizCompany company, boolean requireId) {
        if (company == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "企业信息不能为空");
        }
        if (requireId && company.getId() == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "企业ID不能为空");
        }
        if (ObjectUtils.isEmpty(company.getCompanyName())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "企业名称不能为空");
        }
        if (ObjectUtils.isEmpty(company.getUniformSocialCreditCode())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "统一社会信用代码不能为空");
        }
    }
}
