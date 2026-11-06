import { defineStore } from 'pinia'
import { login as loginApi, getUserInfo as getUserInfoApi, logout as logoutApi } from '@/api/auth'

function readStoredUser() {
  try {
    return JSON.parse(localStorage.getItem('login-user') || 'null')
  } catch (e) {
    return null
  }
}

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('access-token') || '',
    userInfo: readStoredUser()
  }),
  getters: {
    isLoggedIn: (state) => Boolean(state.token)
  },
  actions: {
    async login(username, password) {
      const data = await loginApi(username, password)
      this.token = data.accessToken || ''
      localStorage.setItem('access-token', this.token)

      const user = { ...data }
      delete user.accessToken
      this.userInfo = user
      localStorage.setItem('login-user', JSON.stringify(user))
      return data
    },
    async fetchUserInfo() {
      const data = await getUserInfoApi()
      this.userInfo = data
      localStorage.setItem('login-user', JSON.stringify(data))
      return data
    },
    async logout() {
      try {
        await logoutApi()
      } catch (e) {
        // 退出接口失败不影响本地清理
      }
      this.token = ''
      this.userInfo = null
      localStorage.removeItem('access-token')
      localStorage.removeItem('login-user')
    }
  }
})
