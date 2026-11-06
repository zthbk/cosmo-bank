<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import moment from 'moment'
import { listShips } from '@/api/ship'
import { listAlarms } from '@/api/alarm'

const router = useRouter()

const loading = ref(false)
const ships = ref([])
const alarms = ref([])
const currentShipId = ref(null)
const shipTotal = ref(0)
const todayAlarmTotal = ref(0)
const undealtTotal = ref(0)

const stats = computed(() => [
  { label: '船舶总数', value: shipTotal.value },
  { label: '在线船舶', value: '-' },
  { label: '今日告警', value: todayAlarmTotal.value },
  { label: '未处置告警', value: undealtTotal.value }
])

const levelMap = {
  0: { text: '高风险', type: 'danger' },
  1: { text: '中风险', type: 'warning' },
  2: { text: '低风险', type: 'info' }
}

async function loadShips() {
  const data = await listShips({ page: 1, count: 100, status: 1 })
  ships.value = data?.list || []
  shipTotal.value = data?.total || 0
}

async function loadAlarms() {
  const params = { page: 1, count: 10 }
  if (currentShipId.value) {
    params.shipId = currentShipId.value
  }
  const data = await listAlarms(params)
  alarms.value = data?.list || []
}

async function loadStats() {
  const beginTime = moment().startOf('day').format('YYYY-MM-DD HH:mm:ss')
  const endTime = moment().format('YYYY-MM-DD HH:mm:ss')
  const [today, undealt] = await Promise.all([
    listAlarms({ page: 1, count: 1, beginTime, endTime }),
    listAlarms({ page: 1, count: 1, dealStatus: 0 })
  ])
  todayAlarmTotal.value = today?.total || 0
  undealtTotal.value = undealt?.total || 0
}

function selectShip(ship) {
  currentShipId.value = currentShipId.value === ship.id ? null : ship.id
  loadAlarms()
}

function openAlarm(alarm) {
  router.push({
    path: '/business/alarm/list',
    query: {
      shipId: alarm.shipId || undefined,
      keyword: alarm.shipCn || undefined
    }
  })
}

onMounted(async () => {
  loading.value = true
  try {
    await Promise.all([loadShips(), loadAlarms(), loadStats()])
  } catch (e) {
    // 请求层已统一提示
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="monitor" v-loading="loading">
    <div class="stat-row">
      <div v-for="item in stats" :key="item.label" class="stat-card">
        <div class="stat-value">{{ item.value }}</div>
        <div class="stat-label">{{ item.label }}</div>
      </div>
    </div>

    <div class="monitor-body">
      <section class="ship-panel">
        <div class="panel-title">船舶列表</div>
        <div class="ship-list">
          <div
            v-for="ship in ships"
            :key="ship.id"
            class="ship-item"
            :class="{ active: currentShipId === ship.id }"
            @click="selectShip(ship)"
          >
            <div class="ship-name">{{ ship.shipName || '-' }}</div>
            <div class="ship-code">{{ ship.shipCn }}</div>
          </div>
          <el-empty v-if="!ships.length" description="暂无船舶档案" :image-size="64" />
        </div>
      </section>

      <section class="map-panel">
        <div class="map-placeholder">
          <div class="map-title">电子海图占位</div>
          <p>按路径 C 先行占位，海图能力后续补充。</p>
        </div>
      </section>

      <section class="alarm-panel">
        <div class="panel-title">历史告警</div>
        <div class="alarm-list">
          <div v-for="alarm in alarms" :key="alarm.id" class="alarm-item" @click="openAlarm(alarm)">
            <div class="alarm-head">
              <span class="alarm-time">{{ alarm.alarmTime }}</span>
              <el-tag v-if="levelMap[alarm.alarmLevel]" :type="levelMap[alarm.alarmLevel].type" size="small">
                {{ levelMap[alarm.alarmLevel].text }}
              </el-tag>
            </div>
            <div class="alarm-name">{{ alarm.shipName || alarm.shipCn }}</div>
            <div class="alarm-type">{{ alarm.alarmModelName || alarm.alarmTypeCode }}</div>
          </div>
          <el-empty v-if="!alarms.length" description="暂无告警" :image-size="64" />
        </div>
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
  display: flex;
  flex-direction: column;
}

.panel-title {
  height: 44px;
  line-height: 44px;
  padding: 0 16px;
  border-bottom: 1px solid var(--border);
  color: var(--text-main);
  font-weight: 600;
  flex: none;
}

.ship-list,
.alarm-list {
  flex: 1;
  overflow: auto;
}

.ship-item {
  padding: 10px 16px;
  border-bottom: 1px solid var(--border);
  cursor: pointer;
}

.ship-item:hover,
.ship-item.active {
  background: var(--primary-soft);
}

.ship-name {
  color: var(--text-main);
  font-size: 14px;
}

.ship-code {
  margin-top: 2px;
  color: var(--text-muted);
  font-size: 12px;
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

.alarm-item {
  padding: 10px 16px;
  border-bottom: 1px solid var(--border);
  cursor: pointer;
}

.alarm-item:hover {
  background: var(--page-bg);
}

.alarm-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
}

.alarm-time {
  color: var(--text-muted);
  font-size: 12px;
}

.alarm-name {
  margin-top: 4px;
  color: var(--text-main);
  font-size: 14px;
}

.alarm-type {
  margin-top: 2px;
  color: var(--text-secondary);
  font-size: 12px;
}
</style>
