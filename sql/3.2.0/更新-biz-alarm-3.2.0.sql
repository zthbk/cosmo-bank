/*
 * 岸端企业平台 3.2.0 告警业务表
 * 适用：MySQL 8.4 LTS
 * 范围：biz_alarm、biz_alarm_file、biz_alarm_deal、biz_alarm_model、biz_api_client
 * 说明：仅新增业务表，不修改 3.0.0/3.1.0 已发布脚本；脚本可重复执行。
 *       字段依据《全国客船危险品船智能监控系统数据对接规范（2.0）》9.1.3、9.1.5 与指南 2.1-2.3。
 */

/*AI 告警主表*/
CREATE TABLE IF NOT EXISTS biz_alarm
(
    id                 BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    tenant_id          VARCHAR(64)     DEFAULT NULL COMMENT '系统企业标识',
    ship_id            BIGINT UNSIGNED DEFAULT NULL COMMENT '业务船舶ID，对应 biz_ship.id',
    ship_cn            VARCHAR(64)     NOT NULL COMMENT '船舶识别号/企业值班室识别号',
    ship_mmsi          VARCHAR(20)     DEFAULT NULL COMMENT 'MMSI',
    ship_name          VARCHAR(255)    NOT NULL DEFAULT '' COMMENT '船舶名称',
    alarm_code         VARCHAR(128)    NOT NULL COMMENT '告警编号，全局唯一',
    alarm_time         DATETIME        NOT NULL COMMENT '告警时间，终端原始时间',
    alarm_type_code    VARCHAR(64)     NOT NULL COMMENT '告警类型编号',
    alarm_model_name   VARCHAR(255)    DEFAULT NULL COMMENT '告警模型名称',
    alarm_scene_type   VARCHAR(8)      DEFAULT NULL COMMENT '告警场景：0驾驶室 1机舱 2甲板 3乘客公共处所 4岸基监控室 5泵舱 6船岸界面',
    alarm_level        VARCHAR(8)      DEFAULT NULL COMMENT '风险等级：0高风险 1中风险 2低风险',
    camera_name        VARCHAR(255)    DEFAULT NULL COMMENT '摄像头名称',
    video_channel_id   BIGINT UNSIGNED DEFAULT NULL COMMENT '业务视频通道ID',
    video_channel_code VARCHAR(64)     DEFAULT NULL COMMENT '业务视频通道编号',
    repeat_count       INT             NOT NULL DEFAULT 1 COMMENT '同一告警连续触发次数',
    duration_minutes   INT             DEFAULT NULL COMMENT '持续时长，分钟',
    is_read            TINYINT         NOT NULL DEFAULT 0 COMMENT '是否已读：1已读 0未读',
    deal_status        TINYINT         NOT NULL DEFAULT 0 COMMENT '是否处置：1已处置 0未处置',
    is_valid           TINYINT         DEFAULT NULL COMMENT '是否有效：1正确 0误报',
    is_timely          TINYINT         DEFAULT NULL COMMENT '是否及时：1及时 0延误',
    status             TINYINT         NOT NULL DEFAULT 0 COMMENT '告警状态：0告警中 1告警解除',
    longitude          DECIMAL(10, 6)  DEFAULT NULL COMMENT '经度，WGS84',
    latitude           DECIMAL(10, 6)  DEFAULT NULL COMMENT '纬度，WGS84',
    ship_speed         DECIMAL(6, 1)   DEFAULT NULL COMMENT '航速，节',
    ship_angle         DECIMAL(6, 1)   DEFAULT NULL COMMENT '航向，度',
    ship_location      VARCHAR(255)    DEFAULT NULL COMMENT '告警具体位置',
    ship_status        VARCHAR(32)     DEFAULT NULL COMMENT '船舶状态，按规范 9.6.1',
    ship_type          VARCHAR(64)     DEFAULT NULL COMMENT '船型',
    relative_box       VARCHAR(512)    DEFAULT NULL COMMENT '告警图片锚框',
    alarm_pic_url      VARCHAR(1024)   DEFAULT NULL COMMENT '告警图片URL',
    alarm_video_url    VARCHAR(1024)   DEFAULT NULL COMMENT '告警视频URL',
    receive_time       DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '平台接收时间',
    create_time        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time        DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_alarm_code (alarm_code),
    KEY idx_alarm_ship_time (ship_cn, alarm_time),
    KEY idx_alarm_status_time (status, alarm_time),
    KEY idx_alarm_type_time (alarm_type_code, alarm_time),
    KEY idx_alarm_tenant (tenant_id),
    KEY idx_alarm_ship_id (ship_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='AI 告警主表';

/*告警文件，允许一告警多图/多段视频*/
CREATE TABLE IF NOT EXISTS biz_alarm_file
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    alarm_id    BIGINT UNSIGNED NOT NULL COMMENT '告警ID，对应 biz_alarm.id',
    file_type   TINYINT         NOT NULL COMMENT '文件类型：1图片 2视频',
    seq_no      INT             NOT NULL DEFAULT 0 COMMENT '同类文件序号',
    url         VARCHAR(1024)   NOT NULL COMMENT '文件URL',
    md5         VARCHAR(64)     DEFAULT NULL COMMENT 'MD5，与终端一致',
    sha256      VARCHAR(64)     DEFAULT NULL COMMENT 'SHA-256',
    file_size   BIGINT          DEFAULT NULL COMMENT '文件大小，字节',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_alarm_file (alarm_id, file_type, seq_no),
    KEY idx_alarm_file_alarm (alarm_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='告警图片与视频';

/*告警处置记录*/
CREATE TABLE IF NOT EXISTS biz_alarm_deal
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    alarm_id    BIGINT UNSIGNED NOT NULL COMMENT '告警ID，对应 biz_alarm.id',
    is_valid    TINYINT         DEFAULT NULL COMMENT '是否有效：1正确 0误报',
    deal_type   VARCHAR(32)     DEFAULT NULL COMMENT '处置方式',
    deal_result VARCHAR(512)    DEFAULT NULL COMMENT '处置结果',
    deal_time   DATETIME        DEFAULT NULL COMMENT '处置时间',
    op_user     VARCHAR(100)    DEFAULT NULL COMMENT '处置人',
    remark      VARCHAR(512)    DEFAULT NULL COMMENT '备注',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_alarm_deal_alarm (alarm_id),
    KEY idx_alarm_deal_time (deal_time)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='告警处置记录';

/*告警模型：危险品船 29 类、客船 18 类、企业值班室 4 类*/
CREATE TABLE IF NOT EXISTS biz_alarm_model
(
    id             BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    model_code     VARCHAR(64)     NOT NULL COMMENT '告警类型编号',
    model_name     VARCHAR(255)    NOT NULL COMMENT '告警模型名称',
    model_category TINYINT         NOT NULL COMMENT '模型类别：1危险品船 2客船 3企业值班室',
    scene_type     VARCHAR(8)      DEFAULT NULL COMMENT '适用场景编号',
    alarm_level    VARCHAR(8)      DEFAULT NULL COMMENT '默认风险等级：0高风险 1中风险 2低风险',
    enabled        TINYINT         NOT NULL DEFAULT 1 COMMENT '是否启用：1启用 0停用',
    create_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_alarm_model (model_code, model_category),
    KEY idx_alarm_model_category (model_category)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='告警模型与场景映射';

/*终端与全国平台对接凭证，secret_key 生产环境须加密存储*/
CREATE TABLE IF NOT EXISTS biz_api_client
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    client_name VARCHAR(128)    NOT NULL COMMENT '调用方名称',
    access_key  VARCHAR(64)     NOT NULL COMMENT 'Access Key',
    secret_key  VARCHAR(128)    NOT NULL COMMENT 'Secret Key，生产环境加密存储',
    client_type TINYINT         NOT NULL DEFAULT 1 COMMENT '类型：1终端 2全国平台',
    status      TINYINT         NOT NULL DEFAULT 1 COMMENT '状态：1启用 0停用',
    create_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_api_client_access_key (access_key)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='终端与全国平台接入凭证';
