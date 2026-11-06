/*
 * 岸端企业平台 3.1.0 基础档案表
 * 适用：MySQL 8.4 LTS
 * 范围：biz_company、biz_ship、biz_ship_channel、biz_terminal、biz_duty_room
 * 说明：仅新增业务表，不修改 3.0.0 已发布脚本；脚本可重复执行。
 *       字段依据《全国客船危险品船智能监控系统数据对接规范（2.0）》9.1.1、9.1.2、9.2.1。
 */

/*企业档案，字段对应规范 9.1.1*/
CREATE TABLE IF NOT EXISTS biz_company
(
    id                         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    tenant_id                  VARCHAR(64)     DEFAULT NULL COMMENT '全国平台系统企业标识，注册成功后回填',
    company_name               VARCHAR(255)    NOT NULL COMMENT '企业名称',
    company_addr               VARCHAR(500)    NOT NULL DEFAULT '' COMMENT '企业地址',
    region_code                VARCHAR(20)     NOT NULL DEFAULT '' COMMENT '企业所属行政区域代码',
    uniform_social_credit_code VARCHAR(50)     NOT NULL COMMENT '企业统一社会信用代码',
    legal_name                 VARCHAR(100)    NOT NULL DEFAULT '' COMMENT '企业法人',
    legal_phone_number         VARCHAR(50)     NOT NULL DEFAULT '' COMMENT '企业法人电话',
    status                     TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    create_time                DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time                DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_company_credit_code (uniform_social_credit_code),
    UNIQUE KEY uk_company_tenant (tenant_id),
    KEY idx_company_name (company_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='企业档案';

/*船舶档案，字段对应规范 9.1.2；企业值班室也以 ship_cn=AJJK+tenantId+序号 注册*/
CREATE TABLE IF NOT EXISTS biz_ship
(
    id                      BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    company_id              BIGINT UNSIGNED NOT NULL COMMENT '所属企业ID，对应 biz_company.id',
    tenant_id               VARCHAR(64)     DEFAULT NULL COMMENT '冗余企业标识，便于上报',
    ship_cn                 VARCHAR(64)     NOT NULL COMMENT '船舶识别号/企业值班室识别号',
    ship_mmsi               VARCHAR(20)     DEFAULT NULL COMMENT 'MMSI，危险品船必填',
    ship_name               VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '船舶名称，平台展示用',
    ship_length             DECIMAL(10, 2)  DEFAULT NULL COMMENT '船长，单位米，保留2位小数',
    ship_width              DECIMAL(10, 2)  DEFAULT NULL COMMENT '船宽，单位米，保留2位小数',
    ship_passenger_capacity INT             DEFAULT NULL COMMENT '客船客位数',
    ship_type               VARCHAR(64)     DEFAULT NULL COMMENT '船型：客船/油船/气体运输船/化学品液货船',
    ship_status             VARCHAR(32)     DEFAULT NULL COMMENT '船舶状态，按规范 9.6.1',
    status                  TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    create_time             DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time             DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ship_cn (ship_cn),
    KEY idx_ship_company (company_id),
    KEY idx_ship_mmsi (ship_mmsi),
    KEY idx_ship_name (ship_name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='船舶档案';

/*业务视频通道与国标通道映射，字段对应规范 9.2.1*/
CREATE TABLE IF NOT EXISTS biz_ship_channel
(
    id                    BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    ship_id               BIGINT UNSIGNED NOT NULL COMMENT '所属船舶ID，对应 biz_ship.id',
    video_channel_code    VARCHAR(64)     NOT NULL COMMENT '业务通道编号，全国平台注册用',
    video_channel_name    VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '通道名称',
    video_channel_support VARCHAR(32)     DEFAULT NULL COMMENT '支持功能，逗号分隔：0回放 1对讲 2下载 3抓拍',
    video_type            VARCHAR(32)     DEFAULT NULL COMMENT '取流协议：ws/hls/http-flv/ws-flv',
    gb_device_id          VARCHAR(50)     DEFAULT NULL COMMENT '国标设备编号',
    gb_channel_id         VARCHAR(50)     DEFAULT NULL COMMENT '国标通道编号',
    camera_name           VARCHAR(255)    DEFAULT NULL COMMENT '摄像头名称',
    talk_enabled          TINYINT         NOT NULL DEFAULT 0 COMMENT '是否支持对讲：1是 0否',
    status                TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    create_time           DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time           DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ship_channel_code (video_channel_code),
    UNIQUE KEY uk_ship_channel_gb (gb_device_id, gb_channel_id),
    KEY idx_ship_channel_ship (ship_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='船舶视频通道与国标映射';

/*终端档案与在线状态*/
CREATE TABLE IF NOT EXISTS biz_terminal
(
    id               BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    ship_id          BIGINT UNSIGNED NOT NULL COMMENT '所属船舶ID，对应 biz_ship.id',
    terminal_uuid    VARCHAR(64)     NOT NULL COMMENT '终端唯一标识/设备机号',
    gb_device_id     VARCHAR(50)     DEFAULT NULL COMMENT '关联国标设备编号',
    terminal_model   VARCHAR(128)    DEFAULT NULL COMMENT '终端型号',
    software_version VARCHAR(64)     DEFAULT NULL COMMENT '软件版本',
    online_status    TINYINT         NOT NULL DEFAULT 0 COMMENT '在线状态：1在线 0离线',
    last_online_time DATETIME        DEFAULT NULL COMMENT '最后在线时间',
    cache_status     TINYINT         NOT NULL DEFAULT 0 COMMENT '缓存状态：0正常 1补传中 2异常',
    status           TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    create_time      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_terminal_uuid (terminal_uuid),
    KEY idx_terminal_ship (ship_id),
    KEY idx_terminal_device (gb_device_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='船舶终端档案';

/*企业值班室档案，duty_room_code 按规范以 ship_cn 形式注册*/
CREATE TABLE IF NOT EXISTS biz_duty_room
(
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    company_id     BIGINT UNSIGNED NOT NULL COMMENT '所属企业ID，对应 biz_company.id',
    duty_room_code VARCHAR(64)     NOT NULL COMMENT '值班室识别号：AJJK+tenantId+序号',
    duty_room_name VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '值班室名称',
    region_code    VARCHAR(20)     DEFAULT NULL COMMENT '所属行政区域代码',
    contact_name   VARCHAR(100)    DEFAULT NULL COMMENT '联系人',
    contact_phone  VARCHAR(50)     DEFAULT NULL COMMENT '联系电话',
    gb_device_id   VARCHAR(50)     DEFAULT NULL COMMENT '值班室摄像头/国标设备编号',
    status         TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    create_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_duty_room_code (duty_room_code),
    KEY idx_duty_room_company (company_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='企业值班室档案';
