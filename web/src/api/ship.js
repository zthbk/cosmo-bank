import request from '@/utils/request'

export function listShips(params) {
  return request.get('/biz/ship/list', { params })
}

export function getShip(id) {
  return request.get(`/biz/ship/${id}`)
}

export function addShip(data) {
  return request.post('/biz/ship/add', data)
}

export function updateShip(data) {
  return request.post('/biz/ship/update', data)
}

export function deleteShip(id) {
  return request.delete(`/biz/ship/${id}`)
}
