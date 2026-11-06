import { createRouter, createWebHashHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const Layout = () => import('@/layout/index.vue')
const Placeholder = () => import('@/views/placeholder/index.vue')

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { public: true, title: '登录' }
  },
  {
    path: '/business',
    component: Layout,
    redirect: '/business/monitor',
    children: [
      {
        path: 'monitor',
        name: 'Monitor',
        component: () => import('@/views/monitor/index.vue'),
        meta: { title: '动态监控' }
      },
      { path: 'alarm/list', name: 'AlarmList', component: Placeholder, meta: { title: '告警列表' } },
      { path: 'alarm/config', name: 'AlarmConfig', component: Placeholder, meta: { title: '告警配置' } },
      { path: 'alarm/area', name: 'AlarmArea', component: Placeholder, meta: { title: '特定范围' } },
      { path: 'alarm/analysis', name: 'AlarmAnalysis', component: Placeholder, meta: { title: '告警分析' } },
      { path: 'alarm/threshold', name: 'AlarmThreshold', component: Placeholder, meta: { title: '阈值配置' } },
      { path: 'video', name: 'Video', component: Placeholder, meta: { title: '视频监控' } },
      { path: 'talk', name: 'Talk', component: Placeholder, meta: { title: '智联通话' } },
      { path: 'ship/crew', name: 'Crew', component: Placeholder, meta: { title: '船员信息' } },
      { path: 'ship/shift', name: 'Shift', component: Placeholder, meta: { title: '营运班次' } },
      { path: 'safety', name: 'Safety', component: Placeholder, meta: { title: '安全信息' } }
    ]
  },
  { path: '/', redirect: '/business/monitor' },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: () => import('@/views/error/404.vue'),
    meta: { public: true, title: '页面不存在' }
  }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to) => {
  document.title = to.meta.title
    ? `${to.meta.title} - 船舶智能监控系统管理平台`
    : '船舶智能监控系统管理平台'

  const auth = useAuthStore()
  if (to.meta.public) {
    if (to.path === '/login' && auth.token) {
      return '/business/monitor'
    }
    return true
  }
  if (!auth.token) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  return true
})

export default router
