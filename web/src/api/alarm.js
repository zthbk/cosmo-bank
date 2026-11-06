import request from '@/utils/request'

export function listAlarms(params) {
  return request.get('/biz/alarm/list', { params })
}

export function getAlarm(id) {
  return request.get(`/biz/alarm/${id}`)
}

export function markAlarmsRead(ids) {
  return request.post('/biz/alarm/read', ids)
}

export function listAlarmModels(params) {
  return request.get('/biz/alarm/model/list', { params })
}
