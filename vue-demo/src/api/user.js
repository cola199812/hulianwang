import http from './http'

export function register(username, password, email, verificationCode) {
  return http.post('/user/register', { username, password, email, verificationCode })
}

export function sendCode(email) {
  return http.post('/user/send-code', null, { params: { email } })
}

export function login(username, password) {
  return http.post('/user/login', { username, password })
}

export function userInfo() {
  return http.get('/user/info')
}

export function userSimple(id) {
  return http.get(`/user/simple/${id}`)
}

export function logout() {
  return http.post('/user/logout')
}

export function resetPassword(email, verificationCode, newPassword) {
  return http.post('/user/reset-password', { email, verificationCode, newPassword })
}
