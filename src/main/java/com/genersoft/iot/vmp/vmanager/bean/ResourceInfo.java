package com.genersoft.iot.vmp.vmanager.bean;

public class ResourceInfo {

    private ResourceBaseInfo device;
    private ResourceBaseInfo channel;

    public ResourceBaseInfo getDevice() {
        return device;
    }

    public void setDevice(ResourceBaseInfo device) {
        this.device = device;
    }

    public ResourceBaseInfo getChannel() {
        return channel;
    }

    public void setChannel(ResourceBaseInfo channel) {
        this.channel = channel;
    }
}
