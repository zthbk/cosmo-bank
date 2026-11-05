package com.genersoft.iot.vmp.gb28181.dao.provider;

import com.genersoft.iot.vmp.gb28181.bean.Device;
import com.genersoft.iot.vmp.gb28181.bean.Group;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ChannelProviderTest {

    private final ChannelProvider provider = new ChannelProvider();

    // ========== queryByGbDeviceIds ==========

    @Test
    void queryByGbDeviceIds_shouldUseBindVariables() {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceIds", Arrays.asList("DEV001", "DEV002"));
        String sql = provider.queryByGbDeviceIds(params);
        assertTrue(sql.contains("#{deviceIds[0]}"), "should use #{deviceIds[0]}");
        assertTrue(sql.contains("#{deviceIds[1]}"), "should use #{deviceIds[1]}");
        assertFalse(sql.contains("'DEV001'"), "should not contain raw quoted value");
        assertFalse(sql.contains("'DEV002'"), "should not contain raw quoted value");
    }

    @Test
    void queryByGbDeviceIds_shouldNotQuoteBindVariables() {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceIds", Collections.singletonList("INJECT' OR 1=1 --"));
        String sql = provider.queryByGbDeviceIds(params);
        assertTrue(sql.contains("#{deviceIds[0]}"), "should use bind variable for injection attempt");
        assertFalse(sql.contains("1=1"), "should not contain injection payload in SQL");
    }

    // ========== queryByGroupList ==========

    @Test
    void queryByGroupList_shouldUseBindVariables() {
        Map<String, Object> params = new HashMap<>();
        Group g1 = new Group();
        g1.setDeviceId("GRP001");
        Group g2 = new Group();
        g2.setDeviceId("GRP002");
        params.put("groupList", Arrays.asList(g1, g2));
        String sql = provider.queryByGroupList(params);
        assertTrue(sql.contains("#{groupList[0].deviceId}"), "should use #{groupList[0].deviceId}");
        assertTrue(sql.contains("#{groupList[1].deviceId}"), "should use #{groupList[1].deviceId}");
        assertFalse(sql.contains("GRP001"), "should not contain raw deviceId");
        assertFalse(sql.contains("GRP002"), "should not contain raw deviceId");
    }

    // ========== queryOnlineListsByGbDeviceIds ==========

    @Test
    void queryOnlineListsByGbDeviceIds_shouldUseBindVariables() {
        Map<String, Object> params = new HashMap<>();
        Device d1 = new Device();
        d1.setId(101);
        Device d2 = new Device();
        d2.setId(102);
        params.put("deviceList", Arrays.asList(d1, d2));
        String sql = provider.queryOnlineListsByGbDeviceIds(params);
        assertTrue(sql.contains("#{deviceList[0].id}"), "should use #{deviceList[0].id}");
        assertTrue(sql.contains("#{deviceList[1].id}"), "should use #{deviceList[1].id}");
        assertFalse(sql.contains("101"), "should not contain raw id");
        assertFalse(sql.contains("102"), "should not contain raw id");
    }

    @Test
    void queryOnlineListsByGbDeviceIds_withEmptyList_shouldNotHaveInClause() {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceList", Collections.emptyList());
        String sql = provider.queryOnlineListsByGbDeviceIds(params);
        assertFalse(sql.contains("data_device_id in ("), "should not have IN clause when empty");
    }

    @Test
    void queryOnlineListsByGbDeviceIds_withNullList_shouldNotHaveInClause() {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceList", null);
        String sql = provider.queryOnlineListsByGbDeviceIds(params);
        assertFalse(sql.contains("data_device_id in ("), "should not have IN clause when null");
    }

    // ========== queryByGbDeviceIds single element ==========

    @Test
    void queryByGbDeviceIds_withSingleElement() {
        Map<String, Object> params = new HashMap<>();
        params.put("deviceIds", Collections.singletonList("SINGLE01"));
        String sql = provider.queryByGbDeviceIds(params);
        assertEquals(1, countOccurrences(sql, "#{deviceIds[0]}"),
                "should have exactly one bind variable for single element");
        assertFalse(sql.contains("#{deviceIds[0]},"), "should not have trailing comma in IN clause");
        assertFalse(sql.contains(",#{deviceIds[0]}"), "should not have leading comma in IN clause");
    }

    // ========== helper ==========

    private int countOccurrences(String str, String substr) {
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(substr, idx)) != -1) {
            count++;
            idx += substr.length();
        }
        return count;
    }
}
