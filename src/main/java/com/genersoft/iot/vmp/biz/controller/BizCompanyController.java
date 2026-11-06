package com.genersoft.iot.vmp.biz.controller;

import com.genersoft.iot.vmp.biz.bean.BizCompany;
import com.genersoft.iot.vmp.biz.service.IBizCompanyService;
import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.conf.security.JwtUtils;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "企业档案")
@RestController
@RequestMapping("/biz/company")
@RequiredArgsConstructor
public class BizCompanyController {

    private final IBizCompanyService companyService;

    @Operation(summary = "分页查询企业", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @Parameter(name = "page", description = "当前页")
    @Parameter(name = "count", description = "每页数量")
    @Parameter(name = "keyword", description = "企业名称或统一社会信用代码")
    @Parameter(name = "status", description = "状态：1启用 0停用")
    @GetMapping("/list")
    public PageInfo<BizCompany> list(@RequestParam(defaultValue = "1") Integer page,
                                     @RequestParam(defaultValue = "10") Integer count,
                                     @RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Integer status) {
        return companyService.queryList(page, count, keyword, status);
    }

    @Operation(summary = "查询企业详情", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @GetMapping("/{id}")
    public BizCompany detail(@PathVariable Long id) {
        BizCompany company = companyService.queryById(id);
        if (company == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "企业不存在");
        }
        return company;
    }

    @Operation(summary = "新增企业", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @PostMapping("/add")
    public void add(@RequestBody BizCompany company) {
        companyService.add(company);
    }

    @Operation(summary = "更新企业", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @PostMapping("/update")
    public void update(@RequestBody BizCompany company) {
        companyService.update(company);
    }

    @Operation(summary = "删除企业", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        companyService.delete(id);
    }
}
