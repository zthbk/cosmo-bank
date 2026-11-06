package com.genersoft.iot.vmp.biz.service.impl;

import com.alibaba.fastjson2.JSON;
import com.genersoft.iot.vmp.biz.bean.BizAlarm;
import com.genersoft.iot.vmp.biz.bean.BizAlarmDeal;
import com.genersoft.iot.vmp.biz.bean.BizAlarmFile;
import com.genersoft.iot.vmp.biz.bean.BizAlarmModel;
import com.genersoft.iot.vmp.biz.bean.BizApiClient;
import com.genersoft.iot.vmp.biz.bean.BizShip;
import com.genersoft.iot.vmp.biz.dao.BizAlarmDealMapper;
import com.genersoft.iot.vmp.biz.dao.BizAlarmFileMapper;
import com.genersoft.iot.vmp.biz.dao.BizAlarmMapper;
import com.genersoft.iot.vmp.biz.dao.BizAlarmModelMapper;
import com.genersoft.iot.vmp.biz.dao.BizApiClientMapper;
import com.genersoft.iot.vmp.biz.dao.BizShipMapper;
import com.genersoft.iot.vmp.biz.dto.AlarmDetail;
import com.genersoft.iot.vmp.biz.dto.AlarmReportRequest;
import com.genersoft.iot.vmp.biz.service.IBizAlarmService;
import com.genersoft.iot.vmp.biz.util.BizQueryUtil;
import com.genersoft.iot.vmp.biz.util.BizSignatureUtil;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BizAlarmServiceImpl implements IBizAlarmService {

    private static final DateTimeFormatter ALARM_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private BizAlarmMapper alarmMapper;

    @Autowired
    private BizAlarmFileMapper alarmFileMapper;

    @Autowired
    private BizAlarmDealMapper alarmDealMapper;

    @Autowired
    private BizAlarmModelMapper alarmModelMapper;

    @Autowired
    private BizApiClientMapper apiClientMapper;

    @Autowired
    private BizShipMapper shipMapper;

    @Override
    @Transactional
    public void report(String accessKey, String timestamp, String signature, String body) {
        if (ObjectUtils.isEmpty(accessKey) || ObjectUtils.isEmpty(timestamp) || ObjectUtils.isEmpty(signature)) {
            throw new ControllerException(ErrorCode.ERROR401.getCode(), "缺少接入签名参数");
        }
        BizApiClient client = apiClientMapper.selectByAccessKey(accessKey);
        if (client == null) {
            throw new ControllerException(ErrorCode.ERROR401.getCode(), "接入凭证无效");
        }
        BizSignatureUtil.verify(accessKey, timestamp, signature, body, client.getSecretKey());

        AlarmReportRequest request;
        try {
            request = JSON.parseObject(body, AlarmReportRequest.class);
        } catch (Exception e) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "告警报文格式错误");
        }
        validate(request);

        BizAlarm existing = alarmMapper.selectByAlarmCode(request.getAlarmCode());
        if (existing != null) {
            log.info("[告警接入] 重复告警，忽略 code={}", request.getAlarmCode());
            return;
        }

        BizShip ship = shipMapper.selectByShipCn(request.getShipCn());
        BizAlarm alarm = buildAlarm(request, ship);
        try {
            if (alarmMapper.insert(alarm) == 0) {
                throw new ControllerException(ErrorCode.ERROR100.getCode(), "保存告警失败");
            }
        } catch (DuplicateKeyException e) {
            log.info("[告警接入] 并发重复告警，忽略 code={}", request.getAlarmCode());
            return;
        }

        List<BizAlarmFile> files = buildFiles(alarm.getId(), request);
        if (!files.isEmpty()) {
            alarmFileMapper.batchInsert(files);
        }
        log.info("[告警接入] 保存告警 id={}, code={}, shipCn={}", alarm.getId(), alarm.getAlarmCode(),
                alarm.getShipCn());
    }

    @Override
    public PageInfo<BizAlarm> queryList(Integer page, Integer count, Long shipId, String shipCn, String keyword,
                                        String alarmLevel, Integer dealStatus, Integer isRead, Integer status,
                                        String beginTime, String endTime) {
        PageHelper.startPage(BizQueryUtil.page(page), BizQueryUtil.count(count));
        List<BizAlarm> list = alarmMapper.selectList(shipId, BizQueryUtil.keyword(shipCn),
                BizQueryUtil.keyword(keyword), BizQueryUtil.keyword(alarmLevel), dealStatus, isRead, status,
                BizQueryUtil.keyword(beginTime), BizQueryUtil.keyword(endTime));
        return new PageInfo<>(list);
    }

    @Override
    public AlarmDetail queryDetail(Long id) {
        BizAlarm alarm = alarmMapper.selectById(id);
        if (alarm == null) {
            throw new ControllerException(ErrorCode.ERROR404.getCode(), "告警不存在");
        }
        List<BizAlarmFile> files = alarmFileMapper.selectByAlarmId(id);
        List<BizAlarmDeal> deals = alarmDealMapper.selectByAlarmId(id);
        AlarmDetail detail = new AlarmDetail();
        detail.setAlarm(alarm);
        detail.setFiles(files == null ? new ArrayList<>() : files);
        detail.setDeals(deals == null ? new ArrayList<>() : deals);
        return detail;
    }

    @Override
    @Transactional
    public int markRead(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return alarmMapper.updateRead(ids);
    }

    @Override
    public List<BizAlarmModel> queryModels(Integer category, Integer enabled) {
        List<BizAlarmModel> list = alarmModelMapper.selectList(category, enabled);
        return list == null ? new ArrayList<>() : list;
    }

    private BizAlarm buildAlarm(AlarmReportRequest request, BizShip ship) {
        BizAlarm alarm = new BizAlarm();
        alarm.setTenantId(request.getTenantId());
        alarm.setShipCn(request.getShipCn());
        alarm.setShipMmsi(request.getShipMmsi());
        alarm.setShipName(request.getShipName());
        alarm.setAlarmCode(request.getAlarmCode());
        alarm.setAlarmTime(request.getAlarmTime());
        alarm.setAlarmTypeCode(request.getAlarmTypeCode());
        alarm.setAlarmSceneType(request.getAlarmSceneType());
        alarm.setAlarmLevel(request.getAlarmLevel());
        alarm.setRelativeBox(request.getRelativeBox());
        alarm.setAlarmPicUrl(request.getAlarmPic());
        alarm.setAlarmVideoUrl(request.getAlarmVideo());
        alarm.setLongitude(request.getShipLng());
        alarm.setLatitude(request.getShipLat());
        alarm.setShipSpeed(request.getShipSpeed());
        alarm.setShipAngle(request.getShipAngle());
        alarm.setShipLocation(request.getShipLocation());
        alarm.setShipStatus(request.getShipStatus());
        alarm.setRepeatCount(1);
        alarm.setIsRead(0);
        alarm.setDealStatus(0);
        alarm.setStatus(0);
        if (ship != null) {
            alarm.setShipId(ship.getId());
            alarm.setShipType(ship.getShipType());
            if (ObjectUtils.isEmpty(alarm.getShipName())) {
                alarm.setShipName(ship.getShipName());
            }
            if (ObjectUtils.isEmpty(alarm.getTenantId())) {
                alarm.setTenantId(ship.getTenantId());
            }
            if (ObjectUtils.isEmpty(alarm.getShipStatus())) {
                alarm.setShipStatus(ship.getShipStatus());
            }
        }
        return alarm;
    }

    private List<BizAlarmFile> buildFiles(Long alarmId, AlarmReportRequest request) {
        List<BizAlarmFile> files = new ArrayList<>();
        if (!ObjectUtils.isEmpty(request.getAlarmPic())) {
            files.add(buildFile(alarmId, 1, 0, request.getAlarmPic(), request.getAlarmPicAcc()));
        }
        if (!ObjectUtils.isEmpty(request.getAlarmVideo())) {
            files.add(buildFile(alarmId, 2, 0, request.getAlarmVideo(), request.getAlarmVideoAcc()));
        }
        return files;
    }

    private BizAlarmFile buildFile(Long alarmId, int fileType, int seqNo, String url, String md5) {
        BizAlarmFile file = new BizAlarmFile();
        file.setAlarmId(alarmId);
        file.setFileType(fileType);
        file.setSeqNo(seqNo);
        file.setUrl(url);
        file.setMd5(md5);
        return file;
    }

    private void validate(AlarmReportRequest request) {
        if (request == null) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "告警报文不能为空");
        }
        if (ObjectUtils.isEmpty(request.getShipCn())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "船舶识别号不能为空");
        }
        if (ObjectUtils.isEmpty(request.getAlarmCode())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "告警编号不能为空");
        }
        if (ObjectUtils.isEmpty(request.getAlarmTime())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "告警时间不能为空");
        }
        try {
            LocalDateTime.parse(request.getAlarmTime(), ALARM_TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "告警时间格式错误");
        }
        if (ObjectUtils.isEmpty(request.getAlarmTypeCode())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "告警类型不能为空");
        }
        if (ObjectUtils.isEmpty(request.getAlarmSceneType())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "告警场景不能为空");
        }
        if (ObjectUtils.isEmpty(request.getAlarmLevel())) {
            throw new ControllerException(ErrorCode.ERROR400.getCode(), "风险等级不能为空");
        }
    }
}
