import http from './http'

/**
 * 获取通知列表
 */
export function getNotifications(type = null) {
  return http.get('/notification/list', { params: { type } })
}

/**
 * 标记通知为已读
 */
export function markNotificationAsRead(id) {
  return http.put(`/notification/read/${id}`)
}

/**
 * 标记所有通知为已读
 */
export function markAllNotificationsAsRead() {
  return http.put('/notification/read-all')
}

/**
 * 删除通知
 */
export function deleteNotification(id) {
  return http.delete(`/notification/${id}`)
}

/**
 * 获取未读通知数量
 */
export function getUnreadNotificationCount() {
  return http.get('/notification/unread-count')
}