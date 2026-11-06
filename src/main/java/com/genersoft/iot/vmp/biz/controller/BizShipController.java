package com.genersoft.iot.vmp.biz.controller;

import com.genersoft.iot.vmp.biz.bean.BizShip;
import com.genersoft.iot.vmp.biz.service.IBizShipService;
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

@Tag(name = "船舶档案")
@RestController
@RequestMapping("/biz/ship")
@RequiredArgsConstructor
public class BizShipController {

    private final IBizShipService shipService;

    @Operation(summary = "分页查询船舶", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @Parameter(name = "page", description = "当前页")
    @Parameter(name = "count", description = "每页数量")
    @Parameter(name = "keyword", description = "船名、船舶识别号或 MMSI")
    @Parameter(name = "companyId", description = "所属企业ID")
    @Parameter(name = "status", description = "状态：1启用 0停用")
    @GetMapping("/list")
    public PageInfo<BizShip> list(@RequestParam(defaultValue = "1") Integer page,
                                  @RequestParam(defaultValue = "10") Integer count,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) Long companyId,
                                  @RequestParam(required = false) Integer status) {
        return shipService.queryList(page, count, keyword, companyId, status);
    }

    @Operation(summary = "查询船舶详情", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @GetMapping("/{id}")
    public BizShip detail(@PathVariable Long id) {
        BizShip ship = shipService.queryById(id);
        if (ship == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "船舶不存在");
        }
        return ship;
    }

    @Operation(summary = "新增船舶", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @PostMapping("/add")
    public void add(@RequestBody BizShip ship) {
        shipService.add(ship);
    }

    @Operation(summary = "更新船舶", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @PostMapping("/update")
    public void update(@RequestBody BizShip ship) {
        shipService.update(ship);
    }

    @Operation(summary = "删除船舶", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        shipService.delete(id);
    }
}
