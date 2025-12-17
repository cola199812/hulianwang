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

