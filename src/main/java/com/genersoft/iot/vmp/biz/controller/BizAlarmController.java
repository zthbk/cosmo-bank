package com.genersoft.iot.vmp.biz.controller;

import com.genersoft.iot.vmp.biz.bean.BizAlarm;
import com.genersoft.iot.vmp.biz.bean.BizAlarmModel;
import com.genersoft.iot.vmp.biz.dto.AlarmDetail;
import com.genersoft.iot.vmp.biz.service.IBizAlarmService;
import com.genersoft.iot.vmp.conf.security.JwtUtils;
import com.github.pagehelper.PageInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "AI 告警")
@RestController
@RequestMapping("/biz/alarm")
@RequiredArgsConstructor
public class BizAlarmController {

    private final IBizAlarmService alarmService;

    @Operation(summary = "终端告警上报", description = "AK/SK 签名接入，请求头 X-Access-Key、X-Timestamp、X-Signature")
    @PostMapping
    public void report(@RequestHeader(value = "X-Access-Key", required = false) String accessKey,
                       @RequestHeader(value = "X-Timestamp", required = false) String timestamp,
                       @RequestHeader(value = "X-Signature", required = false) String signature,
                       @RequestBody String body) {
        alarmService.report(accessKey, timestamp, signature, body);
    }

    @Operation(summary = "分页查询告警", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @Parameter(name = "page", description = "当前页")
    @Parameter(name = "count", description = "每页数量")
    @Parameter(name = "shipId", description = "船舶ID")
    @Parameter(name = "shipCn", description = "船舶识别号")
    @Parameter(name = "keyword", description = "船名、船舶识别号或告警类型")
    @Parameter(name = "alarmLevel", description = "风险等级：0高风险 1中风险 2低风险")
    @Parameter(name = "dealStatus", description = "是否处置：1已处置 0未处置")
    @Parameter(name = "isRead", description = "是否已读：1已读 0未读")
    @Parameter(name = "status", description = "告警状态：0告警中 1告警解除")
    @Parameter(name = "beginTime", description = "开始时间 yyyy-MM-dd HH:mm:ss")
    @Parameter(name = "endTime", description = "结束时间 yyyy-MM-dd HH:mm:ss")
    @GetMapping("/list")
    public PageInfo<BizAlarm> list(@RequestParam(defaultValue = "1") Integer page,
                                   @RequestParam(defaultValue = "50") Integer count,
                                   @RequestParam(required = false) Long shipId,
                                   @RequestParam(required = false) String shipCn,
                                   @RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) String alarmLevel,
                                   @RequestParam(required = false) Integer dealStatus,
                                   @RequestParam(required = false) Integer isRead,
                                   @RequestParam(required = false) Integer status,
                                   @RequestParam(required = false) String beginTime,
                                   @RequestParam(required = false) String endTime) {
        return alarmService.queryList(page, count, shipId, shipCn, keyword, alarmLevel, dealStatus, isRead, status,
                beginTime, endTime);
    }

    @Operation(summary = "查询告警详情", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @GetMapping("/{id}")
    public AlarmDetail detail(@PathVariable Long id) {
        return alarmService.queryDetail(id);
    }

    @Operation(summary = "标记告警已读", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @PostMapping("/read")
    public int markRead(@RequestBody List<Long> ids) {
        return alarmService.markRead(ids);
    }

    @Operation(summary = "查询告警模型", security = @SecurityRequirement(name = JwtUtils.HEADER))
    @Parameter(name = "category", description = "模型类别：1危险品船 2客船 3企业值班室")
    @Parameter(name = "enabled", description = "是否启用：1启用 0停用")
    @GetMapping("/model/list")
    public List<BizAlarmModel> modelList(@RequestParam(required = false) Integer category,
                                         @RequestParam(required = false) Integer enabled) {
        return alarmService.queryModels(category, enabled);
    }
}
