<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Lock, User } from '@element-plus/icons-vue'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const formRef = ref()
const loading = ref(false)
const errorText = ref('')

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function submit() {
  if (!formRef.value) {
    return
  }
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) {
    return
  }

  loading.value = true
  errorText.value = ''
  try {
    await auth.login(form.username.trim(), form.password)
    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : ''
    router.replace(redirect || '/business/monitor')
  } catch (e) {
    errorText.value = e?.response?.data?.msg || e?.message || '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-panel">
      <h1 class="login-title">船舶智能监控系统管理平台</h1>
      <p class="login-subtitle">岸端企业平台</p>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" @submit.prevent>
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="form.username"
            :prefix-icon="User"
            placeholder="请输入用户名"
            autocomplete="username"
            @keyup.enter="submit"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            :prefix-icon="Lock"
            type="password"
            show-password
            placeholder="请输入密码"
            autocomplete="current-password"
            @keyup.enter="submit"
          />
        </el-form-item>

        <p v-if="errorText" class="login-error">{{ errorText }}</p>

        <el-button type="primary" class="login-button" :loading="loading" @click="submit">
          登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--brand-deep), var(--brand) 55%, var(--brand-light));
}

.login-panel {
  width: 420px;
  padding: 36px 40px 32px;
  background: var(--card-bg);
  border-radius: 6px;
  box-shadow: 0 12px 32px rgba(14, 52, 98, 0.28);
}

.login-title {
  margin: 0;
  color: var(--primary-deep);
  font-size: 24px;
  text-align: center;
  letter-spacing: 1px;
}

.login-subtitle {
  margin: 8px 0 24px;
  color: var(--text-muted);
  font-size: 13px;
  text-align: center;
  letter-spacing: 2px;
}

.login-error {
  margin: 0 0 12px;
  color: var(--danger);
  font-size: 13px;
}

.login-button {
  width: 100%;
  height: 40px;
  font-size: 16px;
  background: var(--primary);
  border-color: var(--primary);
}
</style>
