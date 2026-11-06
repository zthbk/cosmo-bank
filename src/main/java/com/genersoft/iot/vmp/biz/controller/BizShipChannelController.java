package com.genersoft.iot.vmp.biz.controller;

import com.genersoft.iot.vmp.biz.bean.BizShipChannel;
import com.genersoft.iot.vmp.biz.service.IBizShipChannelService;
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

@Tag(name = "船舶视频通道")
@RestController
@RequestMapping("/biz/channel")
@RequiredArgsConstructor
public class BizShipChannelController {

    private final IBizShipChannelService channelService;

    @Operation(summary = "分页查询视频通道", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @Parameter(name = "page", description = "当前页")
    @Parameter(name = "count", description = "每页数量")
    @Parameter(name = "shipId", description = "所属船舶ID")
    @Parameter(name = "keyword", description = "通道名称、编号或摄像头名称")
    @Parameter(name = "status", description = "状态：1启用 0停用")
    @GetMapping("/list")
    public PageInfo<BizShipChannel> list(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer count,
                                         @RequestParam(required = false) Long shipId,
                                         @RequestParam(required = false) String keyword,
                                         @RequestParam(required = false) Integer status) {
        return channelService.queryList(page, count, shipId, keyword, status);
    }

    @Operation(summary = "查询视频通道详情", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @GetMapping("/{id}")
    public BizShipChannel detail(@PathVariable Long id) {
        BizShipChannel channel = channelService.queryById(id);
        if (channel == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "视频通道不存在");
        }
        return channel;
    }

    @Operation(summary = "新增视频通道", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @PostMapping("/add")
    public void add(@RequestBody BizShipChannel channel) {
        channelService.add(channel);
    }

    @Operation(summary = "更新视频通道", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @PostMapping("/update")
    public void update(@RequestBody BizShipChannel channel) {
        channelService.update(channel);
    }

    @Operation(summary = "删除视频通道", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        channelService.delete(id);
    }
}
