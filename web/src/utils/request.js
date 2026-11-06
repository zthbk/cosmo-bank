import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
  baseURL: import.meta.env.VITE_APP_BASE_URL || '',
  timeout: 20000
})

request.interceptors.request.use((config) => {
  const token = localStorage.getItem('access-token')
  if (token) {
    config.headers['access-token'] = token
  }
  return config
})

let redirecting = false
function redirectToLogin() {
  if (redirecting) {
    return
  }
  redirecting = true
  localStorage.removeItem('access-token')
  localStorage.removeItem('login-user')
  ElMessage.error('登录已过期，请重新登录')
  const redirect = encodeURIComponent(window.location.hash.replace(/^#/, ''))
  window.location.hash = `/login?redirect=${redirect}`
  setTimeout(() => {
    redirecting = false
  }, 1000)
}

request.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && typeof body === 'object' && 'code' in body) {
      if (body.code === 0) {
        return body.data
      }
      ElMessage.error(body.msg || '请求失败')
      return Promise.reject(new Error(body.msg || '请求失败'))
    }
    return body
  },
  (error) => {
    const status = error.response?.status
    if (status === 401) {
      redirectToLogin()
    } else if (status === 403) {
      ElMessage.error('当前账号无访问权限')
    } else {
      ElMessage.error(error.response?.data?.msg || error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

export default request
