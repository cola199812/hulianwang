import http from './http'

export function listRoutes() {
  return http.get('/route/list')
}

export function createRoute(data) {
  return http.post('/route/create', data)
}

export function getRoute(id) {
  return http.get(`/route/${id}`)
}

export function getRouteComments(routeId) {
  return http.get(`/route/${routeId}/comment`)
}

export function getRouteRatingStats(routeId) {
  return http.get(`/route/${routeId}/rating-stats`)
}

export function addRouteComment(routeId, comment) {
  return http.post(`/route/${routeId}/comment`, comment)
}

export function likeRouteComment(commentId) {
  return http.post(`/route/comment/${commentId}/like`)
}

export function toggleRouteFavorite(routeId) {
  return http.post(`/route/${routeId}/favorite`)
}

export function checkRouteFavorite(routeId) {
  return http.get(`/route/${routeId}/favorite`)
}

// 获取我发布的路线
export function listMyRoutes() {
  return http.get('/route/my-routes')
}

// 更新路线
export function updateRoute(id, data) {
  return http.put(`/route/${id}`, data)
}

// 删除路线
export function deleteRoute(id) {
  return http.delete(`/route/${id}`)
}

