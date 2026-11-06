<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowDown, Bell, Monitor, Phone, Ship, VideoCamera } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const now = ref('')
let timer = null

function tick() {
  const d = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  now.value = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(
    d.getHours()
  )}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

onMounted(() => {
  tick()
  timer = setInterval(tick, 1000)
})

onBeforeUnmount(() => {
  clearInterval(timer)
})

const activeMenu = computed(() => route.path)
const username = computed(() => auth.userInfo?.username || '管理员')

async function handleLogout() {
  await auth.logout()
  router.replace('/login')
}

function handleCommand(command) {
  if (command === 'logout') {
    handleLogout()
  }
}
</script>

<template>
  <el-container class="layout">
    <el-aside width="218px" class="aside">
      <div class="logo">船舶智能监控</div>
      <el-menu
        :default-active="activeMenu"
        :default-openeds="['alarm', 'ship']"
        router
        class="menu"
      >
        <el-menu-item index="/business/monitor">
          <el-icon><Monitor /></el-icon>
          <span>动态监控</span>
        </el-menu-item>

        <el-sub-menu index="alarm">
          <template #title>
            <el-icon><Bell /></el-icon>
            <span>告警管理</span>
          </template>
          <el-menu-item index="/business/alarm/list">告警列表</el-menu-item>
          <el-menu-item index="/business/alarm/config">告警配置</el-menu-item>
          <el-menu-item index="/business/alarm/area">特定范围</el-menu-item>
          <el-menu-item index="/business/alarm/analysis">告警分析</el-menu-item>
          <el-menu-item index="/business/alarm/threshold">阈值配置</el-menu-item>
        </el-sub-menu>

        <el-menu-item index="/business/video">
          <el-icon><VideoCamera /></el-icon>
          <span>视频监控</span>
        </el-menu-item>

        <el-menu-item index="/business/talk">
          <el-icon><Phone /></el-icon>
          <span>智联通话</span>
        </el-menu-item>

        <el-sub-menu index="ship">
          <template #title>
            <el-icon><Ship /></el-icon>
            <span>船舶管理</span>
          </template>
          <el-menu-item index="/business/ship/crew">船员信息</el-menu-item>
          <el-menu-item index="/business/ship/shift">营运班次</el-menu-item>
        </el-sub-menu>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header height="64px" class="header">
        <div class="header-time">{{ now }}</div>
        <div class="header-title">船舶智能监控系统管理平台</div>
        <div class="header-actions">
          <el-tooltip content="安全信息" placement="bottom">
            <el-button text circle class="header-icon" @click="router.push('/business/safety')">
              <el-icon><Bell /></el-icon>
            </el-button>
          </el-tooltip>
          <el-dropdown @command="handleCommand">
            <span class="user">
              {{ username }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.layout {
  height: 100%;
}

.aside {
  background: var(--card-bg);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--primary-deep);
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 1px;
  border-bottom: 1px solid var(--border);
}

.menu {
  border-right: none;
  flex: 1;
}

.header {
  background: linear-gradient(90deg, var(--brand-deep), var(--brand) 55%, var(--brand-light));
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-time {
  width: 240px;
  font-size: 14px;
  opacity: 0.9;
}

.header-title {
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 2px;
}

.header-actions {
  width: 240px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16px;
}

.header-icon {
  color: #fff;
}

.user {
  color: #fff;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
}

.main {
  background: var(--page-bg);
  padding: 16px;
  overflow: auto;
}
</style>
