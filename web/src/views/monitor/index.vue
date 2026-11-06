<script setup>
const stats = [
  { label: '船舶总数', value: '-' },
  { label: '在线船舶', value: '-' },
  { label: '今日告警', value: '-' },
  { label: '未处置告警', value: '-' }
]
</script>

<template>
  <div class="monitor">
    <div class="stat-row">
      <div v-for="item in stats" :key="item.label" class="stat-card">
        <div class="stat-value">{{ item.value }}</div>
        <div class="stat-label">{{ item.label }}</div>
      </div>
    </div>

    <div class="monitor-body">
      <section class="ship-panel">
        <div class="panel-title">船舶列表</div>
        <el-empty description="船舶档案接入后展示" :image-size="72" />
      </section>

      <section class="map-panel">
        <div class="map-placeholder">
          <div class="map-title">电子海图占位</div>
          <p>按路径 C 先行占位，海图能力后续补充。</p>
        </div>
      </section>

      <section class="alarm-panel">
        <div class="panel-title">历史告警</div>
        <el-empty description="告警数据接入后展示" :image-size="72" />
      </section>
    </div>
  </div>
</template>

<style scoped>
.monitor {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
}

.stat-row {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.stat-card {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: 4px;
  padding: 16px;
  text-align: center;
}

.stat-value {
  color: var(--primary-deep);
  font-size: 28px;
  font-weight: 700;
}

.stat-label {
  margin-top: 4px;
  color: var(--text-secondary);
  font-size: 13px;
}

.monitor-body {
  flex: 1;
  min-height: 420px;
  display: grid;
  grid-template-columns: 260px minmax(0, 1fr) 300px;
  gap: 16px;
}

.ship-panel,
.map-panel,
.alarm-panel {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: 4px;
  overflow: hidden;
}

.panel-title {
  height: 44px;
  line-height: 44px;
  padding: 0 16px;
  border-bottom: 1px solid var(--border);
  color: var(--text-main);
  font-weight: 600;
}

.map-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #eaf2f8;
}

.map-placeholder {
  text-align: center;
  color: var(--text-secondary);
}

.map-title {
  margin-bottom: 8px;
  color: var(--primary-deep);
  font-size: 20px;
  font-weight: 700;
}
</style>
