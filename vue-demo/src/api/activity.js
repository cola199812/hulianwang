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

export function listMyActivities() {
  return http.get('/activity/my-activities')
}

export function updateActivity(id, data) {
  return http.put(`/activity/${id}`, data)
}

export function deleteActivity(id) {
  return http.delete(`/activity/${id}`)
}

export function cancelJoin(activityId) {
  return http.delete('/activity/cancel-join', { data: { activityId } })
}

// 检查用户是否报名了特定活动
export async function checkIfJoined(activityId) {
  // 由于后端没有直接提供检查是否报名的API，我们可以通过获取用户的活动列表来检查
  const response = await listMyActivities();
  const myActivities = response.data || [];
  return myActivities.some(activity => activity.id === activityId);
}

