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

export function postStats(postId) {
  return http.get(`/content/post/${postId}/stats`)
}

export function likePost(postId) {
  return http.post(`/content/post/${postId}/like`)
}

export function listComments(postId) {
  return http.get(`/content/comment/list/${postId}`)
}

export function addComment(data) {
  return http.post('/content/comment/add', data)
}

export function likeComment(commentId) {
  return http.post(`/content/comment/${commentId}/like`)
}

export function commentStats(commentId) {
  return http.get(`/content/comment/${commentId}/stats`)
}

export function deletePost(postId) {
  return http.delete(`/content/post/${postId}`)
}

export function listNearbyPosts(lat, lng, radius = 5000) {
  return http.get('/content/post/nearby', {
    params: { lat, lng, radius }
  })
}

export function getPresignedUrl(objectName) {
  return http.get('/content/upload/presigned-url', { params: { objectName } })
}

export function savePostImages(postId, images) {
  return http.post(`/content/post/${postId}/images`, images)
}

export function savePostVideo(postId, video) {
  return http.post(`/content/post/${postId}/video`, video)
}
