<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAlarm, listAlarms, markAlarmsRead } from '@/api/alarm'

const route = useRoute()

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const selectedRows = ref([])

const detailVisible = ref(false)
const detailLoading = ref(false)
const detail = ref(null)

const query = reactive({
  page: 1,
  count: 50,
  shipId: undefined,
  keyword: '',
  alarmLevel: '',
  dealStatus: '',
  isRead: '',
  status: '',
  range: []
})

const sceneMap = {
  0: '驾驶室',
  1: '机舱',
  2: '甲板',
  3: '乘客公共处所',
  4: '岸基监控室',
  5: '泵舱',
  6: '船岸界面'
}

const levelMap = {
  0: { text: '高风险', type: 'danger' },
  1: { text: '中风险', type: 'warning' },
  2: { text: '低风险', type: 'info' }
}

const statusMap = {
  0: '告警中',
  1: '告警解除'
}

function buildParams() {
  return {
    page: query.page,
    count: query.count,
    shipId: query.shipId || undefined,
    keyword: query.keyword || undefined,
    alarmLevel: query.alarmLevel === '' ? undefined : query.alarmLevel,
    dealStatus: query.dealStatus === '' ? undefined : query.dealStatus,
    isRead: query.isRead === '' ? undefined : query.isRead,
    status: query.status === '' ? undefined : query.status,
    beginTime: query.range?.[0] || undefined,
    endTime: query.range?.[1] || undefined
  }
}

async function fetchList() {
  loading.value = true
  try {
    const data = await listAlarms(buildParams())
    rows.value = data?.list || []
    total.value = data?.total || 0
  } catch (e) {
    rows.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function search() {
  query.page = 1
  fetchList()
}

function reset() {
  query.page = 1
  query.shipId = undefined
  query.keyword = ''
  query.alarmLevel = ''
  query.dealStatus = ''
  query.isRead = ''
  query.status = ''
  query.range = []
  fetchList()
}

function onSelectionChange(selection) {
  selectedRows.value = selection
}

async function openDetail(row) {
  detailVisible.value = true
  detailLoading.value = true
  detail.value = null
  try {
    detail.value = await getAlarm(row.id)
  } finally {
    detailLoading.value = false
  }
}

async function markRead(ids) {
  if (!ids.length) {
    return
  }
  await markAlarmsRead(ids)
  ElMessage.success('已标记为已读')
  fetchList()
}

function markSelectedRead() {
  markRead(selectedRows.value.map((row) => row.id))
}

function formatCoordinate(value) {
  return value === null || value === undefined || value === '' ? '-' : Number(value).toFixed(4)
}

onMounted(() => {
  if (route.query.shipId) {
    query.shipId = Number(route.query.shipId)
  }
  if (route.query.keyword) {
    query.keyword = String(route.query.keyword)
  }
  fetchList()
})
</script>

<template>
  <div class="alarm-page">
    <el-card shadow="never" class="filter-card">
      <el-form :inline="true" :model="query" class="filter-form">
        <el-form-item label="关键字">
          <el-input v-model="query.keyword" placeholder="船名、船舶识别号、告警类型" clearable />
        </el-form-item>
        <el-form-item label="风险等级">
          <el-select v-model="query.alarmLevel" placeholder="全部" clearable style="width: 120px">
            <el-option label="高风险" value="0" />
            <el-option label="中风险" value="1" />
            <el-option label="低风险" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="处置状态">
          <el-select v-model="query.dealStatus" placeholder="全部" clearable style="width: 120px">
            <el-option label="未处置" :value="0" />
            <el-option label="已处置" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="阅读状态">
          <el-select v-model="query.isRead" placeholder="全部" clearable style="width: 120px">
            <el-option label="未读" :value="0" />
            <el-option label="已读" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="告警时间">
          <el-date-picker
            v-model="query.range"
            type="datetimerange"
            value-format="YYYY-MM-DD HH:mm:ss"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 360px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">查询</el-button>
          <el-button @click="reset">重置</el-button>
          <el-button :disabled="!selectedRows.length" @click="markSelectedRead">批量已读</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table
        v-loading="loading"
        :data="rows"
        border
        stripe
        height="calc(100vh - 300px)"
        @selection-change="onSelectionChange"
      >
        <el-table-column type="selection" width="44" />
        <el-table-column prop="alarmTime" label="最近告警时间" width="170" />
        <el-table-column prop="shipName" label="船名" min-width="120" show-overflow-tooltip />
        <el-table-column label="地点" width="120">
          <template #default="{ row }">
            {{ sceneMap[row.alarmSceneType] || row.alarmSceneType || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="告警类型" min-width="140" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.alarmModelName || row.alarmTypeCode || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="风险等级" width="100">
          <template #default="{ row }">
            <el-tag v-if="levelMap[row.alarmLevel]" :type="levelMap[row.alarmLevel].type">
              {{ levelMap[row.alarmLevel].text }}
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="cameraName" label="摄像头名称" min-width="130" show-overflow-tooltip />
        <el-table-column prop="repeatCount" label="重复次数" width="90" />
        <el-table-column prop="durationMinutes" label="持续时长" width="90">
          <template #default="{ row }">
            {{ row.durationMinutes === null || row.durationMinutes === undefined ? '-' : `${row.durationMinutes} 分钟` }}
          </template>
        </el-table-column>
        <el-table-column label="已读" width="70">
          <template #default="{ row }">{{ row.isRead === 1 ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="已处置" width="80">
          <template #default="{ row }">{{ row.dealStatus === 1 ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="误报" width="70">
          <template #default="{ row }">
            {{ row.isValid === null || row.isValid === undefined ? '-' : row.isValid === 1 ? '否' : '是' }}
          </template>
        </el-table-column>
        <el-table-column label="及时" width="70">
          <template #default="{ row }">
            {{ row.isTimely === null || row.isTimely === undefined ? '-' : row.isTimely === 1 ? '是' : '否' }}
          </template>
        </el-table-column>
        <el-table-column prop="shipType" label="船型" width="110" />
        <el-table-column prop="shipSpeed" label="航速" width="80" />
        <el-table-column label="船舶位置" width="170">
          <template #default="{ row }">
            {{ formatCoordinate(row.longitude) }}, {{ formatCoordinate(row.latitude) }}
          </template>
        </el-table-column>
        <el-table-column label="当前状态" width="90">
          <template #default="{ row }">{{ statusMap[row.status] || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="openDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.count"
          :total="total"
          :page-sizes="[20, 50, 100]"
          background
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="search"
          @current-change="fetchList"
        />
      </div>
    </el-card>

    <el-drawer v-model="detailVisible" title="告警详情" size="520px">
      <div v-loading="detailLoading">
        <template v-if="detail?.alarm">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="告警时间">{{ detail.alarm.alarmTime }}</el-descriptions-item>
            <el-descriptions-item label="船名">{{ detail.alarm.shipName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="船舶识别号">{{ detail.alarm.shipCn }}</el-descriptions-item>
            <el-descriptions-item label="告警类型">
              {{ detail.alarm.alarmModelName || detail.alarm.alarmTypeCode }}
            </el-descriptions-item>
            <el-descriptions-item label="地点">
              {{ sceneMap[detail.alarm.alarmSceneType] || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="风险等级">
              {{ levelMap[detail.alarm.alarmLevel]?.text || '-' }}
            </el-descriptions-item>
            <el-descriptions-item label="船舶位置">
              {{ formatCoordinate(detail.alarm.longitude) }},
              {{ formatCoordinate(detail.alarm.latitude) }}
            </el-descriptions-item>
            <el-descriptions-item label="航速">{{ detail.alarm.shipSpeed || '-' }}</el-descriptions-item>
            <el-descriptions-item label="船型">{{ detail.alarm.shipType || '-' }}</el-descriptions-item>
            <el-descriptions-item label="状态">{{ statusMap[detail.alarm.status] || '-' }}</el-descriptions-item>
          </el-descriptions>

          <h4>告警文件</h4>
          <el-empty v-if="!detail.files?.length" description="无文件" :image-size="56" />
          <div v-for="file in detail.files" :key="file.id" class="file-row">
            <span>{{ file.fileType === 1 ? '图片' : '视频' }}</span>
            <a :href="file.url" target="_blank" rel="noopener">打开</a>
            <span class="muted">MD5：{{ file.md5 || '-' }}</span>
          </div>

          <h4>处置记录</h4>
          <el-empty v-if="!detail.deals?.length" description="暂无处置记录" :image-size="56" />
          <el-table v-else :data="detail.deals" size="small" border>
            <el-table-column prop="dealTime" label="处置时间" width="160" />
            <el-table-column prop="opUser" label="处置人" width="100" />
            <el-table-column prop="dealType" label="处置方式" width="100" />
            <el-table-column prop="dealResult" label="处置结果" show-overflow-tooltip />
          </el-table>
        </template>
      </div>
    </el-drawer>
  </div>
</template>

<style scoped>
.alarm-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.filter-card {
  margin-bottom: 0;
}

.filter-form {
  display: flex;
  flex-wrap: wrap;
  gap: 0 8px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.file-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  border-bottom: 1px solid var(--border);
}

.muted {
  color: var(--text-muted);
  font-size: 12px;
}

h4 {
  margin: 20px 0 8px;
}
</style>
