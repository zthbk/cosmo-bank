import md5 from 'js-md5'
import request from '@/utils/request'

export function login(username, password) {
  return request.get('/api/user/login', {
    params: {
      username,
      password: md5(password)
    }
  })
}

export function getUserInfo() {
  return request.post('/api/user/userInfo')
}

export function logout() {
  return request.get('/api/user/logout')
}
