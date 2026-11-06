package com.genersoft.iot.vmp.biz.controller;

import com.genersoft.iot.vmp.biz.bean.BizDutyRoom;
import com.genersoft.iot.vmp.biz.service.IBizDutyRoomService;
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

@Tag(name = "企业值班室")
@RestController
@RequestMapping("/biz/duty-room")
@RequiredArgsConstructor
public class BizDutyRoomController {

    private final IBizDutyRoomService dutyRoomService;

    @Operation(summary = "分页查询值班室", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @Parameter(name = "page", description = "当前页")
    @Parameter(name = "count", description = "每页数量")
    @Parameter(name = "companyId", description = "所属企业ID")
    @Parameter(name = "keyword", description = "值班室名称、识别号或联系人")
    @Parameter(name = "status", description = "状态：1启用 0停用")
    @GetMapping("/list")
    public PageInfo<BizDutyRoom> list(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer count,
                                      @RequestParam(required = false) Long companyId,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) Integer status) {
        return dutyRoomService.queryList(page, count, companyId, keyword, status);
    }

    @Operation(summary = "查询值班室详情", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @GetMapping("/{id}")
    public BizDutyRoom detail(@PathVariable Long id) {
        BizDutyRoom dutyRoom = dutyRoomService.queryById(id);
        if (dutyRoom == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "值班室不存在");
        }
        return dutyRoom;
    }

    @Operation(summary = "新增值班室", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @PostMapping("/add")
    public void add(@RequestBody BizDutyRoom dutyRoom) {
        dutyRoomService.add(dutyRoom);
    }

    @Operation(summary = "更新值班室", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @PostMapping("/update")
    public void update(@RequestBody BizDutyRoom dutyRoom) {
        dutyRoomService.update(dutyRoom);
    }

    @Operation(summary = "删除值班室", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        dutyRoomService.delete(id);
    }
}
