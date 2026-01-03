import http from './http'

// 获取通知列表
export function listNotifications() {
  return http.get('/notification/list')
}

// 创建通知
export function createNotification(notification) {
  return http.post('/notification/create', notification)
}

// 标记通知为已读
export function markAsRead(id) {
  return http.put(`/notification/${id}/read`)
}

// 删除通知
export function deleteNotification(id) {
  return http.delete(`/notification/${id}`)
}

// 获取活动通知
export function listActivityNotifications() {
  return http.get('/notification/activity')
}

// 创建活动报名通知
export function createActivityNotification(activityId, action) {
  return http.post('/notification/activity', {
    activityId,
    action,
    timestamp: new Date().toISOString()
  })
}