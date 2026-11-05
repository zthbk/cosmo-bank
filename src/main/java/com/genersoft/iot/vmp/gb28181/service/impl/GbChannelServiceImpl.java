package com.genersoft.iot.vmp.gb28181.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.genersoft.iot.vmp.common.enums.ChannelDataType;
import com.genersoft.iot.vmp.conf.exception.ControllerException;
import com.genersoft.iot.vmp.gb28181.bean.*;
import com.genersoft.iot.vmp.gb28181.dao.CommonGBChannelMapper;
import com.genersoft.iot.vmp.gb28181.dao.GroupMapper;
import com.genersoft.iot.vmp.gb28181.dao.PlatformChannelMapper;
import com.genersoft.iot.vmp.gb28181.dao.RegionMapper;
import com.genersoft.iot.vmp.gb28181.event.EventPublisher;
import com.genersoft.iot.vmp.gb28181.event.channel.ChannelEvent;
import com.genersoft.iot.vmp.gb28181.service.IGbChannelService;
import com.genersoft.iot.vmp.gb28181.service.IPlatformChannelService;
import com.genersoft.iot.vmp.service.bean.GPSMsgInfo;
import com.genersoft.iot.vmp.streamPush.bean.StreamPush;
import com.genersoft.iot.vmp.utils.DateUtil;
import com.genersoft.iot.vmp.vmanager.bean.ErrorCode;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.CaseFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.*;

@Slf4j
@Service
public class GbChannelServiceImpl implements IGbChannelService {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private CommonGBChannelMapper commonGBChannelMapper;

    @Autowired
    private PlatformChannelMapper platformChannelMapper;

    @Autowired
    private IPlatformChannelService platformChannelService;

    @Autowired
    private RegionMapper regionMapper;

    @Autowired
    private GroupMapper groupMapper;

    @Override
    public CommonGBChannel queryByDeviceId(String gbDeviceId) {
        List<CommonGBChannel> commonGBChannels = commonGBChannelMapper.queryByDeviceId(gbDeviceId);
        if (commonGBChannels.isEmpty()) {
            return null;
        }else {
            return commonGBChannels.get(0);
        }
    }

    @Override
    public int add(CommonGBChannel commonGBChannel) {
        if (commonGBChannel.getDataType() == null || commonGBChannel.getDataDeviceId() == null) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "缺少通道数据类型或通道数据关联设备ID");
        }
        CommonGBChannel commonGBChannelInDb =  commonGBChannelMapper.queryByDataId(commonGBChannel.getDataType(), commonGBChannel.getDataDeviceId());
        Assert.isNull(commonGBChannelInDb, "此推流已经关联通道");

        // 检验国标编号是否重复
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByDeviceId(commonGBChannel.getGbDeviceId());
        Assert.isTrue(channelList.isEmpty(), "国标编号已经存在");

        commonGBChannel.setCreateTime(DateUtil.getNow());
        commonGBChannel.setUpdateTime(DateUtil.getNow());
        int result = commonGBChannelMapper.insert(commonGBChannel);
        try {
            // 发送通知
            eventPublisher.channelEventPublish(commonGBChannel, ChannelEvent.ChannelEventMessageType.ADD);
        } catch (Exception e) {
            log.warn("[通道移除通知] 发送失败，{}", commonGBChannel.getGbDeviceId(), e);
        }
        return result;
    }

    @Override
    @Transactional
    public int delete(int gbId) {
        // 移除国标级联关联的信息
        try {
            platformChannelService.removeChannel(gbId);
        }catch (Exception e) {
            log.error("[移除通道国标级联共享失败]", e);
        }

        CommonGBChannel channel = commonGBChannelMapper.queryById(gbId);
        if (channel != null) {
            commonGBChannelMapper.delete(gbId);
            try {
                // 发送通知
                eventPublisher.channelEventPublish(channel, ChannelEvent.ChannelEventMessageType.DEL);
            } catch (Exception e) {
                log.warn("[通道移除通知] 发送失败，{}", channel.getGbDeviceId(), e);
            }
        }
        return 1;
    }

    @Override
    @Transactional
    public void delete(Collection<Integer> ids) {
        // 移除国标级联关联的信息
        try {
            platformChannelService.removeChannels(new ArrayList<>(ids));
        }catch (Exception e) {
            log.error("[移除通道国标级联共享失败]", e);
        }
        List<CommonGBChannel> channelListInDb = commonGBChannelMapper.queryByIds(ids);
        if (channelListInDb.isEmpty()) {
            return;
        }
        commonGBChannelMapper.batchDelete(channelListInDb);
        try {
            // 发送通知
            eventPublisher.channelEventPublish(channelListInDb, ChannelEvent.ChannelEventMessageType.DEL);
        } catch (Exception e) {
            log.warn("[通道移除通知] 发送失败", e);
        }
    }

    @Override
    public int update(CommonGBChannel commonGBChannel) {
        log.info("[更新通道] 通道ID: {}, ", commonGBChannel.toString());
        if (commonGBChannel.getGbId() <= 0) {
            log.warn("[更新通道] 未找到数据库ID，更新失败， {}({})", commonGBChannel.getGbName(), commonGBChannel.getGbDeviceId());
            return 0;
        }
        // 确定编号是否重复
        List<CommonGBChannel> channels = commonGBChannelMapper.queryByDeviceId(commonGBChannel.getGbDeviceId());
        if (channels.size() > 1) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "国标编号重复，请修改编号后保存");
        }
        CommonGBChannel oldChannel = commonGBChannelMapper.queryById(commonGBChannel.getGbId());
        commonGBChannel.setUpdateTime(DateUtil.getNow());
        int result = commonGBChannelMapper.update(commonGBChannel);

        if (result > 0) {
            try {
                CommonGBChannel newChannel = commonGBChannelMapper.queryById(commonGBChannel.getGbId());
                // 发送通知
                eventPublisher.channelEventPublishForUpdate(newChannel, oldChannel);

                if (newChannel.getGbLongitude() != null && !Objects.equals(oldChannel.getGbLongitude(), newChannel.getGbLongitude())
                        && newChannel.getGbLatitude() != null && !Objects.equals(oldChannel.getGbLatitude(), newChannel.getGbLatitude())) {
                    MobilePosition mobilePosition = new MobilePosition();
                    mobilePosition.setChannelId(newChannel.getGbId());
                    mobilePosition.setChannelDeviceId(newChannel.getGbDeviceId());
                    mobilePosition.setCreateTime(DateUtil.getNow());
                    mobilePosition.setTimestamp(System.currentTimeMillis());
                    mobilePosition.setLongitude(newChannel.getGbLongitude());
                    mobilePosition.setLatitude(newChannel.getGbLatitude());
                    eventPublisher.mobilePositionEventPublish(mobilePosition);
                }

            } catch (Exception e) {
                log.warn("[更新通道通知] 发送失败，{}", JSONObject.toJSONString(commonGBChannel), e);
            }
        }
        return result;
    }

    @Override
    public int offline(CommonGBChannel commonGBChannel) {
        if (commonGBChannel.getGbId() <= 0) {
            log.warn("[通道离线] 未找到数据库ID，更新失败， {}({})", commonGBChannel.getGbName(), commonGBChannel.getGbDeviceId());
            return 0;
        }
        int result = commonGBChannelMapper.updateStatusById(commonGBChannel.getGbId(), "OFF");
        if (result > 0) {
            try {
                // 发送通知
                eventPublisher.channelEventPublish(commonGBChannel, ChannelEvent.ChannelEventMessageType.OFF);
            } catch (Exception e) {
                log.warn("[通道离线通知] 发送失败，{}", commonGBChannel.getGbDeviceId(), e);
            }
        }
        return result;
    }

    @Override
    @Transactional
    public int offline(List<CommonGBChannel> commonGBChannelList, boolean permission) {
        if (commonGBChannelList.isEmpty()) {
            log.warn("[多个通道离线] 通道数量为0，更新失败");
            return 0;
        }
        log.info("[通道离线] 共 {} 个", commonGBChannelList.size());
        int result = 0;
        if (permission) {
            int limitCount = 1000;
            for (int i = 0; i < commonGBChannelList.size(); i += limitCount) {
                int end = Math.min(i + limitCount, commonGBChannelList.size());
                List<CommonGBChannel> batchList = commonGBChannelList.subList(i, end);
                result += commonGBChannelMapper.updateStatusForListById(batchList, "OFF");
            }
            log.info("[通道离线] 保存入库 共 {} 个改变", result);
        }
        try {
            // 发送catalog
            eventPublisher.channelEventPublish(commonGBChannelList, ChannelEvent.ChannelEventMessageType.OFF);
        } catch (Exception e) {
            log.warn("[多个通道离线] 发送失败，数量：{}", commonGBChannelList.size(), e);
        }
        return result;
    }

    @Override
    public int online(CommonGBChannel commonGBChannel) {
        if (commonGBChannel.getGbId() <= 0) {
            log.warn("[通道上线] 未找到数据库ID，更新失败， {}({})", commonGBChannel.getGbName(), commonGBChannel.getGbDeviceId());
            return 0;
        }
        int result = commonGBChannelMapper.updateStatusById(commonGBChannel.getGbId(), "ON");
        if (result > 0) {
            try {
                // 发送通知
                eventPublisher.channelEventPublish(commonGBChannel, ChannelEvent.ChannelEventMessageType.ON);
            } catch (Exception e) {
                log.warn("[通道上线通知] 发送失败，{}", commonGBChannel.getGbDeviceId(), e);
            }
        }
        return 0;
    }

    @Override
    @Transactional
    public int online(List<CommonGBChannel> commonGBChannelList, boolean permission) {
        if (commonGBChannelList.isEmpty()) {
            log.warn("[多个通道上线] 通道数量为0，更新失败");
            return 0;
        }
        int result = 0;
        if (permission) {
            // 批量更新
            int limitCount = 1000;
            for (int i = 0; i < commonGBChannelList.size(); i += limitCount) {
                int end = Math.min(i + limitCount, commonGBChannelList.size());
                List<CommonGBChannel> batchList = commonGBChannelList.subList(i, end);
                result += commonGBChannelMapper.updateStatusForListById(batchList, "ON");
            }
        }
        try {
            // 发送catalog
            eventPublisher.channelEventPublish(commonGBChannelList, ChannelEvent.ChannelEventMessageType.ON);
        } catch (Exception e) {
            log.warn("[多个通道上线] 发送失败，数量：{}", commonGBChannelList.size(), e);
        }

        return result;
    }

    @Override
    @Transactional
    public void batchAdd(List<CommonGBChannel> commonGBChannels) {
        if (commonGBChannels.isEmpty()) {
            log.warn("[新增多个通道] 通道数量为0，更新失败");
            return;
        }
        // 批量保存（使用UPSERT防重复）
        int limitCount = 1000;
        int result = 0;
        for (int i = 0; i < commonGBChannels.size(); i += limitCount) {
            int end = Math.min(i + limitCount, commonGBChannels.size());
            List<CommonGBChannel> batchList = commonGBChannels.subList(i, end);
            result += commonGBChannelMapper.batchUpsert(batchList);
        }
        try {
            // 发送catalog
            eventPublisher.channelEventPublish(commonGBChannels, ChannelEvent.ChannelEventMessageType.ADD);
        } catch (Exception e) {
            log.warn("[多个通道新增] 发送失败，数量：{}", commonGBChannels.size(), e);
        }
        log.warn("[新增多个通道] 通道数量为{}，成功保存：{}", commonGBChannels.size(), result);
    }

    @Override
    public void batchUpdateForStreamPushRedisMsg(List<CommonGBChannel> commonGBChannels, boolean permission) {
        if (commonGBChannels.isEmpty()) {
            log.warn("[更新多个通道] 通道数量为0，更新失败");
            return;
        }
        List<CommonGBChannel> oldCommonGBChannelList = commonGBChannelMapper.queryOldChanelListByChannels(commonGBChannels);
        if (permission) {
            // 批量保存
            int limitCount = 1000;
            int result = 0;
            for (int i = 0; i < commonGBChannels.size(); i += limitCount) {
                int end = Math.min(i + limitCount, commonGBChannels.size());
                List<CommonGBChannel> batchList = commonGBChannels.subList(i, end);
                result += commonGBChannelMapper.batchUpdate(batchList);
            }
            log.info("[更新多个通道] 通道数量为{}，成功保存：{}", commonGBChannels.size(), result);
        }
        // 发送通过更新通知
        try {
            // 发送通知
            eventPublisher.channelEventPublishForUpdate(commonGBChannels, oldCommonGBChannelList);
        } catch (Exception e) {
            log.warn("[更新多个通道] 发送失败，{}个", commonGBChannels.size(), e);
        }
    }

    @Override
    @Transactional
    public void updateStatus(List<CommonGBChannel> commonGBChannels) {
        if (commonGBChannels.isEmpty()) {
            log.warn("[更新多个通道状态] 通道数量为0，更新失败");
            return;
        }
        List<CommonGBChannel> oldChanelListByChannels = commonGBChannelMapper.queryOldChanelListByChannels(commonGBChannels);
        int limitCount = 1000;
        int result = 0;
        for (int i = 0; i < commonGBChannels.size(); i += limitCount) {
            int end = Math.min(i + limitCount, commonGBChannels.size());
            List<CommonGBChannel> batchList = commonGBChannels.subList(i, end);
            result += commonGBChannelMapper.updateStatus(batchList);
        }
        log.warn("[更新多个通道状态] 通道数量为{}，成功保存：{}", commonGBChannels.size(), result);
        // 发送通过更新通知
        try {
            // 发送通知
            eventPublisher.channelEventPublishForUpdate(commonGBChannels, oldChanelListByChannels);
        } catch (Exception e) {
            log.warn("[更新多个通道] 发送失败，{}个", commonGBChannels.size(), e);
        }
    }



    @Override
    public CommonGBChannel getOne(int id) {
        return commonGBChannelMapper.queryById(id);
    }

    @Override
    public List<IndustryCodeType> getIndustryCodeList() {
        IndustryCodeTypeEnum[] values = IndustryCodeTypeEnum.values();
        List<IndustryCodeType> result = new ArrayList<>(values.length);
        for (IndustryCodeTypeEnum value : values) {
            result.add(IndustryCodeType.getInstance(value));
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public List<DeviceType> getDeviceTypeList() {
        DeviceTypeEnum[] values = DeviceTypeEnum.values();
        List<DeviceType> result = new ArrayList<>(values.length);
        for (DeviceTypeEnum value : values) {
            result.add(DeviceType.getInstance(value));
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public List<NetworkIdentificationType> getNetworkIdentificationTypeList() {
        NetworkIdentificationTypeEnum[] values = NetworkIdentificationTypeEnum.values();
        List<NetworkIdentificationType> result = new ArrayList<>(values.length);
        for (NetworkIdentificationTypeEnum value : values) {
            result.add(NetworkIdentificationType.getInstance(value));
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public void reset(int id, List<String> chanelFields) {
        log.info("[重置国标通道] id: {}", id);
        Assert.notEmpty(chanelFields, "待重置字段为空");
        CommonGBChannel channel = getOne(id);
        if (channel == null) {
            log.warn("[重置国标通道] 未找到对应Id的通道: id: {}", id);
            throw new ControllerException(ErrorCode.ERROR400);
        }
        if (channel.getDataType() != ChannelDataType.GB28181) {
            log.warn("[重置国标通道] 非国标下级通道无法重置: id: {}", id);
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "非国标下级通道无法重置");
        }
        List<String> dbFields = new ArrayList<>();

        for (String chanelField : chanelFields) {
            BeanWrapperImpl wrapper = new BeanWrapperImpl(channel);
            if (wrapper.isReadableProperty(chanelField)) {
                dbFields.add(CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, chanelField));
            }
        }
        Assert.notEmpty(dbFields, "待重置字段为空");

        // 这个多加一个参数,为了防止将非国标的通道通过此方法清空内容,导致意外发生
        commonGBChannelMapper.reset(id, dbFields, DateUtil.getNow());
        CommonGBChannel channelNew = getOne(id);
        // 发送通过更新通知
        try {
            // 发送通知
            eventPublisher.channelEventPublishForUpdate(channelNew, channel);
        } catch (Exception e) {
            log.warn("[通道移除通知] 发送失败，{}", channelNew.getGbDeviceId(), e);
        }
    }

    @Override
    public PageInfo<CommonGBChannel> queryListByCivilCode(int page, int count, String query, Boolean online, Integer channelType, String civilCode) {
        PageHelper.startPage(page, count);
        if (query != null) {
            query = query.replaceAll("/", "//")
                    .replaceAll("%", "/%")
                    .replaceAll("_", "/_");
        }
        List<CommonGBChannel> all = commonGBChannelMapper.queryListByCivilCode(query, online, channelType, civilCode);
        return new PageInfo<>(all);
    }

    @Override
    public PageInfo<CommonGBChannel> queryListByParentId(int page, int count, String query, Boolean online, Integer channelType, String groupDeviceId) {
        PageHelper.startPage(page, count);
        if (query != null) {
            query = query.replaceAll("/", "//")
                    .replaceAll("%", "/%")
                    .replaceAll("_", "/_");
        }
        List<CommonGBChannel> all = commonGBChannelMapper.queryListByParentId(query, online, channelType, groupDeviceId);
        return new PageInfo<>(all);
    }

    @Override
    public void removeCivilCode(List<Region> allChildren) {
        commonGBChannelMapper.removeCivilCode(allChildren);
        // TODO 是否需要通知上级, 或者等添加新的行政区划时发送更新通知

    }

    @Override
    public void addChannelToRegion(String civilCode, List<Integer> channelIds) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByIds(channelIds);
        if (channelList.isEmpty()) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "所有通道Id不存在");
        }
        List<CommonGBChannel> channelListForOld = new ArrayList<>(channelList);
        for (CommonGBChannel channel : channelList) {
            channel.setGbCivilCode(civilCode);
        }
        int result = commonGBChannelMapper.updateRegion(civilCode, channelList);
        // 发送通知
        if (result > 0) {
            platformChannelService.checkRegionAdd(channelList);
            try {
                // 发送catalog
                eventPublisher.channelEventPublishForUpdate(channelList, channelListForOld);
            } catch (Exception e) {
                log.warn("[多个通道添加行政区划] 发送失败，数量：{}", channelList.size(), e);
            }
        }
    }

    @Override
    @Transactional
    public void deleteChannelToRegion(String civilCode, List<Integer> channelIds) {
        if (!ObjectUtils.isEmpty(civilCode)) {
            deleteChannelToRegionByCivilCode(civilCode);
        }
        if (!ObjectUtils.isEmpty(channelIds)) {
            deleteChannelToRegionByChannelIds(channelIds);
        }
    }

    @Override
    public void deleteChannelToRegionByCivilCode(String civilCode) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByCivilCode(civilCode);
        if (channelList.isEmpty()) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "所有通道Id不存在");
        }
        int result = commonGBChannelMapper.removeCivilCodeByChannels(channelList);
        Region region = regionMapper.queryByDeviceId(civilCode);
        if (region == null) {
            platformChannelService.checkRegionRemove(channelList, null);
        }else {
            List<Region> regionList = new ArrayList<>();
            regionList.add(region);
            platformChannelService.checkRegionRemove(channelList, regionList);
        }
        // TODO 发送通知
//        if (result > 0) {
//            try {
//                // 发送catalog
//                eventPublisher.catalogEventPublish(null, channelList, CatalogEvent.UPDATE);
//            }catch (Exception e) {
//                log.warn("[多个通道添加行政区划] 发送失败，数量：{}", channelList.size(), e);
//            }
//        }
    }

    @Override
    public void deleteChannelToRegionByChannelIds(List<Integer> channelIds) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByIds(channelIds);
        if (channelList.isEmpty()) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "所有通道Id不存在");
        }
        int result = commonGBChannelMapper.removeCivilCodeByChannels(channelList);

        platformChannelService.checkRegionRemove(channelList, null);
        // TODO 发送通知
//        if (result > 0) {
//            try {
//                // 发送catalog
//                eventPublisher.catalogEventPublish(null, channelList, CatalogEvent.UPDATE);
//            }catch (Exception e) {
//                log.warn("[多个通道添加行政区划] 发送失败，数量：{}", channelList.size(), e);
//            }
//        }
    }

    @Override
    public void addChannelToRegionByGbDevice(String civilCode, List<Integer> deviceIds) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByDataTypeAndDeviceIds(ChannelDataType.GB28181, deviceIds);
        if (channelList.isEmpty()) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "所有通道Id不存在");
        }
        List<CommonGBChannel> channelListForOld = new ArrayList<>(channelList);
        for (CommonGBChannel channel : channelList) {
            channel.setGbCivilCode(civilCode);
        }
        int result = commonGBChannelMapper.updateRegion(civilCode, channelList);
        // 发送通知
        if (result > 0) {
            try {
                // 发送catalog
                eventPublisher.channelEventPublishForUpdate(channelList, channelListForOld);
            } catch (Exception e) {
                log.warn("[多个通道添加行政区划] 发送失败，数量：{}", channelList.size(), e);
            }
        }
    }

    @Override
    public void deleteChannelToRegionByGbDevice(List<Integer> deviceIds) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByDataTypeAndDeviceIds(ChannelDataType.GB28181, deviceIds);
        if (channelList.isEmpty()) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "所有通道Id不存在");
        }
        int result = commonGBChannelMapper.removeCivilCodeByChannels(channelList);
        platformChannelService.checkRegionRemove(channelList, null);
    }

    @Override
    @Transactional
    public void removeParentIdByBusinessGroup(String businessGroup) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByBusinessGroup(businessGroup);
        if (channelList.isEmpty()) {
            return;
        }
        int result = commonGBChannelMapper.removeParentIdByChannels(channelList);
        List<Group> groupList = groupMapper.queryByBusinessGroup(businessGroup);
        platformChannelService.checkGroupRemove(channelList, groupList);

    }

    @Override
    public void removeParentIdByGroupList(List<Group> groupList) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByGroupList(groupList);
        if (channelList.isEmpty()) {
            return;
        }
        commonGBChannelMapper.removeParentIdByChannels(channelList);
        platformChannelService.checkGroupRemove(channelList, groupList);
    }

    @Override
    public void updateBusinessGroup(String oldBusinessGroup, String newBusinessGroup) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByBusinessGroup(oldBusinessGroup);
        if (channelList.isEmpty()) {
            log.info("[更新业务分组] 发现未关联任何通道： {}", oldBusinessGroup);
            return;
        }
        List<CommonGBChannel> channelListForOld = new ArrayList<>(channelList);
        int result = commonGBChannelMapper.updateBusinessGroupByChannelList(newBusinessGroup, channelList);
        if (result > 0) {
            for (CommonGBChannel channel : channelList) {
                channel.setGbBusinessGroupId(newBusinessGroup);
            }
            // 发送catalog
            try {
                eventPublisher.channelEventPublishForUpdate(channelList, channelListForOld);
            } catch (Exception e) {
                log.warn("[多个通道业务分组] 发送失败，数量：{}", channelList.size(), e);
            }
        }
    }

    @Override
    public void updateParentIdGroup(String oldParentId, String newParentId) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByParentId(oldParentId);
        if (channelList.isEmpty()) {
            return;
        }
        List<CommonGBChannel> channelListForOld = new ArrayList<>(channelList);
        int result = commonGBChannelMapper.updateParentIdByChannelList(newParentId, channelList);
        if (result > 0) {
            for (CommonGBChannel channel : channelList) {
                channel.setGbParentId(newParentId);
            }
            // 发送catalog
            try {
                eventPublisher.channelEventPublishForUpdate(channelList, channelListForOld);
            } catch (Exception e) {
                log.warn("[多个通道业务分组] 发送失败，数量：{}", channelList.size(), e);
            }
        }
    }

    @Override
    @Transactional
    public void addChannelToGroup(String parentId, String businessGroup, List<Integer> channelIds) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByIds(channelIds);
        if (channelList.isEmpty()) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "所有通道Id不存在");
        }
        List<CommonGBChannel> channelListForOld = new ArrayList<>(channelList);
        int result = commonGBChannelMapper.updateGroup(parentId, businessGroup, channelList);
        for (CommonGBChannel commonGBChannel : channelList) {
            commonGBChannel.setGbParentId(parentId);
            commonGBChannel.setGbBusinessGroupId(businessGroup);
        }

        // 发送通知
        if (result > 0) {
            platformChannelService.checkGroupAdd(channelList);
            try {
                // 发送catalog
                eventPublisher.channelEventPublishForUpdate(channelList, channelListForOld);
            } catch (Exception e) {
                log.warn("[多个通道添加行政区划] 发送失败，数量：{}", channelList.size(), e);
            }
        }
    }

    @Override
    public void deleteChannelToGroup(String parentId, String businessGroup, List<Integer> channelIds) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByIds(channelIds);
        if (channelList.isEmpty()) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "所有通道Id不存在");
        }
        commonGBChannelMapper.removeParentIdByChannels(channelList);

        Group group = groupMapper.queryOneByDeviceId(parentId, businessGroup);
        if (group == null) {
            platformChannelService.checkGroupRemove(channelList, null);
        }else {
            List<Group> groupList = new ArrayList<>();
            groupList.add(group);
            platformChannelService.checkGroupRemove(channelList, groupList);
        }
    }

    @Override
    @Transactional
    public void addChannelToGroupByGbDevice(String parentId, String businessGroup, List<Integer> deviceIds) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByDataTypeAndDeviceIds(ChannelDataType.GB28181, deviceIds);
        if (channelList.isEmpty()) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "所有通道Id不存在");
        }
        List<CommonGBChannel>  channelListForOld = new ArrayList<>(channelList);

        for (CommonGBChannel channel : channelList) {
            channel.setGbParentId(parentId);
            channel.setGbBusinessGroupId(businessGroup);
        }
        int result = commonGBChannelMapper.updateGroup(parentId, businessGroup, channelList);

        for (CommonGBChannel commonGBChannel : channelList) {
            commonGBChannel.setGbParentId(parentId);
            commonGBChannel.setGbBusinessGroupId(businessGroup);
        }
        // 发送通知
        if (result > 0) {
            platformChannelService.checkGroupAdd(channelList);
            try {
                // 发送catalog
                eventPublisher.channelEventPublishForUpdate(channelList, channelListForOld);
            } catch (Exception e) {
                log.warn("[多个通道添加行政区划] 发送失败，数量：{}", channelList.size(), e);
            }
        }
    }

    @Override
    public void deleteChannelToGroupByGbDevice(List<Integer> deviceIds) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByDataTypeAndDeviceIds(ChannelDataType.GB28181, deviceIds);
        if (channelList.isEmpty()) {
            throw new ControllerException(ErrorCode.ERROR100.getCode(), "所有通道Id不存在");
        }
        commonGBChannelMapper.removeParentIdByChannels(channelList);
        platformChannelService.checkGroupRemove(channelList, null);
    }

    @Override
    public CommonGBChannel queryOneWithPlatform(Integer platformId, String channelDeviceId) {
        // 防止共享的通道编号重复
        List<CommonGBChannel> channelList = platformChannelMapper.queryOneWithPlatform(platformId, channelDeviceId);
        if (!channelList.isEmpty()) {
            return channelList.get(channelList.size() - 1);
        }else {
            return null;
        }
    }

    @Override
    public void updateCivilCode(String oldCivilCode, String newCivilCode) {
        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByCivilCode(oldCivilCode);
        if (channelList.isEmpty()) {
            return;
        }
        List<CommonGBChannel>  channelListForOld = new ArrayList<>(channelList);
        int result = commonGBChannelMapper.updateCivilCodeByChannelList(newCivilCode, channelList);
        if (result > 0) {
            for (CommonGBChannel channel : channelList) {
                channel.setGbCivilCode(newCivilCode);
            }
            // 发送catalog
            try {
                eventPublisher.channelEventPublishForUpdate(channelList, channelListForOld);
            } catch (Exception e) {
                log.warn("[多个通道业务分组] 发送失败，数量：{}", channelList.size(), e);
            }
        }
    }

    @Override
    public List<CommonGBChannel> queryListByStreamPushList(List<StreamPush> streamPushList) {
        return commonGBChannelMapper.queryListByStreamPushList(ChannelDataType.STREAM_PUSH, streamPushList);
    }

    @Override
    public PageInfo<CommonGBChannel> queryList(int page, int count, String query, Boolean online, Boolean hasRecordPlan,
                                               Integer channelType, String civilCode, String parentDeviceId) {
        PageHelper.startPage(page, count);
        if (query != null) {
            query = query.replaceAll("/", "//")
                    .replaceAll("%", "/%")
                    .replaceAll("_", "/_");
        }
        List<CommonGBChannel> all = commonGBChannelMapper.queryList(query, online,  hasRecordPlan, channelType, civilCode, parentDeviceId);
        return new PageInfo<>(all);
    }

    @Override
    public PageInfo<CommonGBChannel> queryListByCivilCodeForUnusual(int page, int count, String query, Boolean online, Integer channelType) {
        PageHelper.startPage(page, count);
        if (query != null) {
            query = query.replaceAll("/", "//")
                    .replaceAll("%", "/%")
                    .replaceAll("_", "/_");
        }
        List<CommonGBChannel> all = commonGBChannelMapper.queryListByCivilCodeForUnusual(query, online, channelType);
        return new PageInfo<>(all);
    }

    @Override
    public void clearChannelCivilCode(Boolean all, List<Integer> channelIds) {

        List<Integer> channelIdsForClear;
        if (all != null && all) {
            channelIdsForClear = commonGBChannelMapper.queryAllForUnusualCivilCode();
        }else {
            channelIdsForClear = channelIds;
        }
        commonGBChannelMapper.removeCivilCodeByChannelIds(channelIdsForClear);
    }

    @Override
    public PageInfo<CommonGBChannel> queryListByParentForUnusual(int page, int count, String query, Boolean online, Integer channelType) {
        PageHelper.startPage(page, count);
        if (query != null) {
            query = query.replaceAll("/", "//")
                    .replaceAll("%", "/%")
                    .replaceAll("_", "/_");
        }
        List<CommonGBChannel> all = commonGBChannelMapper.queryListByParentForUnusual(query, online, channelType);
        return new PageInfo<>(all);
    }

    @Override
    public void clearChannelParent(Boolean all, List<Integer> channelIds) {
        List<Integer> channelIdsForClear;
        if (all != null && all) {
            channelIdsForClear = commonGBChannelMapper.queryAllForUnusualParent();
        }else {
            channelIdsForClear = channelIds;
        }
        commonGBChannelMapper.removeParentIdByChannelIds(channelIdsForClear);
    }

    @Override
    public void updateGPSFromGPSMsgInfo(List<GPSMsgInfo> gpsMsgInfoList) {
        if (gpsMsgInfoList == null || gpsMsgInfoList.isEmpty()) {
            return;
        }
        // 此处来源默认为WGS84, 所以直接入库
        commonGBChannelMapper.updateGpsByDeviceId(gpsMsgInfoList);
//
//        Map<String, GPSMsgInfo> gpsMsgInfoMap = new ConcurrentReferenceHashMap<>();
//        for (GPSMsgInfo gpsMsgInfo : gpsMsgInfoList) {
//            gpsMsgInfoMap.put(gpsMsgInfo.getId(), gpsMsgInfo);
//        }
//
//        List<CommonGBChannel> channelList = commonGBChannelMapper.queryByGbDeviceIds(new ArrayList<>(gpsMsgInfoMap.keySet()));
//        if (channelList.isEmpty()) {
//            return;
//        }
//        channelList.forEach(commonGBChannel -> {
//            MobilePosition mobilePosition = new MobilePosition();
//            mobilePosition.setDeviceId(commonGBChannel.getGbDeviceId());
//            mobilePosition.setChannelId(commonGBChannel.getGbId());
//            mobilePosition.setDeviceName(commonGBChannel.getGbName());
//            mobilePosition.setCreateTime(DateUtil.getNow());
//            mobilePosition.setTime(DateUtil.getNow());
//            mobilePosition.setLongitude(commonGBChannel.getGbLongitude());
//            mobilePosition.setLatitude(commonGBChannel.getGbLatitude());
//            eventPublisher.mobilePositionEventPublish(mobilePosition);
//        });
    }

    @Transactional
    @Override
    public void updateGPS(List<CommonGBChannel> commonGBChannels) {
        int limitCount = 1000;
        for (int i = 0; i < commonGBChannels.size(); i += limitCount) {
            int end = Math.min(i + limitCount, commonGBChannels.size());
            List<CommonGBChannel> batchList = commonGBChannels.subList(i, end);
            commonGBChannelMapper.updateGps(batchList);
        }
    }

    @Override
    public CommonGBChannel queryCommonChannelByDeviceChannel(DeviceChannel channel) {
        return commonGBChannelMapper.queryCommonChannelByDeviceChannel(channel.getDataType(), channel.getDataDeviceId(), channel.getDeviceId());
    }
}
