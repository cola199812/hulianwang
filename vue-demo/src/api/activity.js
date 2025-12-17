import http from './http'

export function createActivity(data) {
  return http.post('/activity/create', data)
}

export function listActivities() {
  return http.get('/activity/list')
}

export function joinActivity(activityId) {
  return http.post('/activity/join', { activityId })
}

