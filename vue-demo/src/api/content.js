import http from './http'

export function createPost(data) {
  return http.post('/content/post/create', data)
}

export function listPosts() {
  return http.get('/content/post/list')
}

export function listMyPosts() {
  return http.get('/content/post/my')
}

export function getPost(id) {
  return http.get(`/content/post/${id}`)
}

export function uploadMedia(formData) {
  return http.post('/content/upload', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function listMyMedia() {
  return http.get('/content/media/list')
}

export function listMediaByPost(postId) {
  return http.get(`/content/media/by-post/${postId}`)
}
