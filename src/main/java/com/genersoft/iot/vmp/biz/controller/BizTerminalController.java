package com.genersoft.iot.vmp.biz.controller;

import com.genersoft.iot.vmp.biz.bean.BizTerminal;
import com.genersoft.iot.vmp.biz.service.IBizTerminalService;
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

@Tag(name = "船舶终端")
@RestController
@RequestMapping("/biz/terminal")
@RequiredArgsConstructor
public class BizTerminalController {

    private final IBizTerminalService terminalService;

    @Operation(summary = "分页查询终端", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @Parameter(name = "page", description = "当前页")
    @Parameter(name = "count", description = "每页数量")
    @Parameter(name = "shipId", description = "所属船舶ID")
    @Parameter(name = "keyword", description = "终端标识、国标设备编号或型号")
    @Parameter(name = "status", description = "状态：1启用 0停用")
    @GetMapping("/list")
    public PageInfo<BizTerminal> list(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer count,
                                      @RequestParam(required = false) Long shipId,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Integer status) {
        return terminalService.queryList(page, count, shipId, keyword, status);
    }

    @Operation(summary = "查询终端详情", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @GetMapping("/{id}")
    public BizTerminal detail(@PathVariable Long id) {
        BizTerminal terminal = terminalService.queryById(id);
        if (terminal == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "终端不存在");
        }
        return terminal;
    }

    @Operation(summary = "新增终端", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @PostMapping("/add")
    public void add(@RequestBody BizTerminal terminal) {
        terminalService.add(terminal);
    }

    @Operation(summary = "更新终端", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @PostMapping("/update")
    public void update(@RequestBody BizTerminal terminal) {
        terminalService.update(terminal);
    }

    @Operation(summary = "删除终端", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        terminalService.delete(id);
    }
}
