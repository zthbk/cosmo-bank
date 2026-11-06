<!-- 接入设备 -->

# 接入设备

## 国标28181设备

设备接入主要是需要在设备上配置28181上级也就是WVP-PRO的信息，只有信息一致的情况才可以注册成功。设备注册成功后打开WVP->
国标设备,可以看到新增加的设备；[设备使用](./_content/ability/device_use.md)，  
主要有以下字段需要配置：

- sip->port  
  28181服务监听的端口

- sip->domain  
  domain宜采用ID统一编码的前十位编码。

- sip->id  
  28181服务ID

- sip->password  
  28181服务密码

- 配置信息在如下位置

![_media/img_16.png](_media/img_16.png)
***

### 1. 大华摄像头

![_media/img_10.png](_media/img_10.png)

### 2. 大华NVR

![_media/img_11.png](_media/img_11.png)

### 3. 宇视科技

![_media/img_25.png](_media/img_25.png)

### 3. 艾科威视摄像头

![_media/img_15.png](_media/img_15.png)

### 4. 水星摄像头

![_media/img_12.png](_media/img_12.png)

### 5. 海康摄像头

![_media/img_9.png](_media/img_9.png)

## 直播推流设备

当前版本已删除推流模块，不再支持 OBS 等推流设备接入；请使用 GB28181 设备接入。

## 接入非国标 IPC 设备或者其他流地址形式的设备

当前版本已删除拉流代理模块，不再支持非国标 IPC 或任意流地址接入；请使用 GB28181 设备接入。

[设备使用](_content/ability/device_use.md)
