<!-- 云端录像 -->

# 云端录像

云端录像管理 ZLMediaKit 录制在 `media.record-path` 下的录像文件，默认路径为 ZLM 的 `www/record`。

## 1. 录像来源

- 国标点播/回放：由 `user-settings.record-sip` 控制是否录制。
- 录像计划：通过 `/api/record/plan` 配置通道与时间段，按计划录制。
- 当前不支持推流、拉流代理录制；相关模块已删除。

## 2. 查询与播放

- 列表：`GET /api/cloud/record/list`
- 日期列表：`GET /api/cloud/record/date/list`
- 播放地址：`GET /api/cloud/record/play/path`
- 下载/打包：`GET /api/cloud/record/download/zip`、`GET /api/cloud/record/zip`
- 收藏：`GET /api/cloud/record/collect/add`、`GET /api/cloud/record/collect/delete`

## 3. 保存策略

- `media.record-path`：录像存储路径；修改前先迁移旧文件，数据库记录绝对路径。
- `media.record-day`：保存天数。
- 录像计划与通道绑定关系见 `/api/record/plan/channel/list`。
