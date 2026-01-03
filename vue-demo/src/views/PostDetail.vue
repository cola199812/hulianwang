<template>
  <div class="p-4" style="max-width: 680px;margin:0 auto;">
    <el-button text @click="$router.back()" style="margin-bottom:12px;" class="back-btn">
      <el-icon><ArrowLeft /></el-icon>返回
    </el-button>

    <el-card shadow="never" class="post-card">
      <!-- 帖子内容 -->
      <div class="post-content">
        <!-- 标题区域 -->
        <div class="post-header">
          <div class="post-title">{{ post.title || '无标题' }}</div>
          <div v-if="post.locationName" class="post-location">
            <el-icon class="location-icon"><Location /></el-icon>
            {{ post.locationName }}
          </div>
        </div>

        <!-- 作者信息 -->
        <div class="author-info">
          <div class="author-avatar">
            <el-avatar :size="40" :src="authorAvatar" />
          </div>
          <div class="author-details">
            <div class="author-name">{{ author }}</div>
            <div class="post-time">{{ new Date(post.createTime).toLocaleString() }}</div>
          </div>
          <div class="post-stats">
            <span class="stat-item">
              <el-icon><View /></el-icon>
              {{ viewCount }}
            </span>
            <span class="stat-item">
              <el-icon><ChatDotRound /></el-icon>
              {{ commentCount }}
            </span>
          </div>
        </div>

        <!-- 正文内容 -->
        <div class="post-body">
          <div class="markdown-content">{{ post.markdown }}</div>
          
          <!-- 话题标签 -->
          <div v-if="post.topics && post.topics.length" class="topic-tags">
            <el-tag 
              v-for="t in post.topics" 
              :key="t.id" 
              type="primary" 
              size="small"
              class="topic-tag"
              @click="$router.push({ name: 'Creation', query: { tag: t.name } })"
              style="cursor: pointer;"
            >
              #{{ t.name }}
            </el-tag>
          </div>
        </div>

        <!-- 媒体展示 -->
        <div v-if="media.length" class="media-gallery">
          <template v-for="(m, index) in media" :key="m.id">
            <div class="media-item" :class="{ 'single-media': media.length === 1 }">
              <img 
                v-if="m.type==='image'" 
                :src="fullUrl(m.url)" 
                class="media-image"
                @click="openMediaViewer(index)"
              />
              <video 
                v-else 
                controls 
                :src="fullUrl(m.url)" 
                class="media-video"
                :poster="m.cover ? fullUrl(m.cover) : ''"
              />
              <div v-if="m.type === 'video'" class="video-indicator">
                <el-icon><VideoPlay /></el-icon>
              </div>
            </div>
          </template>
        </div>

        <!-- 互动操作 -->
        <div class="post-actions">
          <el-button 
            type="primary" 
            plain 
            :class="{ 'liked': isLiked }"
            @click="onLike"
          >
            <el-icon><Star /></el-icon>
            <span>点赞</span>
            <span v-if="like > 0" class="count">{{ like }}</span>
          </el-button>
          <el-button 
            type="info" 
            plain
            @click="scrollToComments"
          >
            <el-icon><ChatLineRound /></el-icon>
            <span>评论</span>
            <span v-if="commentCount > 0" class="count">{{ commentCount }}</span>
          </el-button>
          <el-button type="info" plain>
            <el-icon><Share /></el-icon>
            <span>分享</span>
          </el-button>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="comment-section" ref="commentSection">
        <div class="comment-header">
          <h3 class="comment-title">
            <el-icon><ChatDotRound /></el-icon>
            评论
            <span v-if="comments.length > 0" class="comment-count">{{ comments.length }}</span>
          </h3>
        </div>
        
        <!-- 发表评论 -->
        <div class="comment-form">
          <el-input 
            v-model="commentText" 
            type="textarea" 
            :rows="3" 
            placeholder="写下你的看法..." 
            resize="none"
            class="comment-input"
          />
          <div class="form-actions">
            <el-button 
              type="primary" 
              @click="submitComment"
              :disabled="!commentText.trim()"
              class="submit-btn"
            >
              发表评论
            </el-button>
          </div>
        </div>

        <!-- 评论列表 -->
        <div class="comment-list">
          <div v-if="comments.length === 0" class="no-comments">
            <el-empty description="还没有评论，快来抢沙发吧～" :image-size="100" />
          </div>
          
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <div class="comment-avatar">
              <el-avatar :size="36" :src="c._avatar" />
            </div>
            <div class="comment-content">
              <div class="comment-header">
                <span class="comment-author">{{ c._username || '匿名用户' }}</span>
                <span class="comment-time">{{ formatCommentTime(c.createTime) }}</span>
              </div>
              <div class="comment-body">{{ c.content }}</div>
              <div class="comment-actions">
                <el-button 
                  text 
                  size="small" 
                  @click="onLikeComment(c)"
                  :class="{ 'liked': c._isLiked }"
                >
                  <el-icon><Star /></el-icon>
                  <span>{{ c._likeCount || 0 }}</span>
                </el-button>
                <el-button text size="small">回复</el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { 
  ArrowLeft, 
  Location, 
  View, 
  ChatDotRound, 
  ChatLineRound, 
  Share, 
  Star,
  VideoPlay
} from '@element-plus/icons-vue'
import { 
  getPost, 
  listMediaByPost, 
  postStats, 
  likePost, 
  listComments, 
  addComment, 
  likeComment, 
  viewPost 
} from '../api/content'
import { userSimple } from '../api/user'

const route = useRoute()
const post = ref({})
const media = ref([])
const author = ref('匿名用户')
const authorAvatar = ref('')
const like = ref(0)
const viewCount = ref(0)
const commentCount = ref(0)
const comments = ref([])
const commentText = ref('')
const isLiked = ref(false)
const commentSection = ref(null)

function fullUrl(u) {
  if (!u) return ''
  if (u.startsWith('http')) return u
  return `http://localhost:8080${u}`
}

function formatCommentTime(time) {
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 30) return `${days}天前`
  return date.toLocaleDateString()
}

function openMediaViewer(index) {
  // TODO: 实现图片查看器
  console.log('打开图片查看器:', index)
}

function scrollToComments() {
  if (commentSection.value) {
    commentSection.value.scrollIntoView({ behavior: 'smooth' })
  }
}

onMounted(async () => {
  const id = route.params.id
  
  // 增加浏览量
  try { 
    await viewPost(id) 
  } catch {}

  const { data } = await getPost(id)
  post.value = data
  
  // 组装媒体列表
  const list = []
  if (post.value.video) {
    list.push({
      id: post.value.video.id,
      type: 'video',
      url: post.value.video.videoUrl,
      cover: post.value.video.coverUrl
    })
  }
  if (post.value.images && post.value.images.length) {
    post.value.images.forEach(img => {
      list.push({
        id: img.id,
        type: 'image',
        url: img.imageUrl,
        desc: img.description
      })
    })
  }
  
  // 兼容旧数据
  if (list.length === 0) {
    try {
      const { data: oldMedia } = await listMediaByPost(id)
      if (oldMedia) list.push(...oldMedia)
    } catch {}
  }
  
  media.value = list

  // 加载统计数据
  try {
    const s = await postStats(id)
    like.value = s.data.likeCount || 0
    viewCount.value = s.data.viewCount || 0
    commentCount.value = s.data.commentCount || 0
  } catch {}
  
  // 加载作者信息
  try {
    if (post.value.userId === 0) {
      author.value = '匿名用户'
    } else {
      const u = await userSimple(post.value.userId)
      author.value = u.data?.username || '匿名用户'
      authorAvatar.value = u.data?.avatar || ''
    }
  } catch {}
  
  await loadComments()
})

async function loadComments() {
  const id = route.params.id
  const { data } = await listComments(id)
  comments.value = data || []
  for (const c of comments.value) {
    // 设置点赞状态 (从后端返回的 isLiked 和 likeCount)
    c._isLiked = !!c.isLiked
    c._likeCount = c.likeCount || 0
    
    try {
      if (c.userId === 0) {
        c._username = '匿名用户'
      } else {
        const u = await userSimple(c.userId)
        c._username = u.data?.username || '匿名用户'
        c._avatar = u.data?.avatar || ''
      }
    } catch { 
      c._username = '匿名用户'
      c._avatar = ''
    }
  }
}

async function onLike() {
  const id = route.params.id
  try {
    const r = await likePost(id)
    like.value = r.data.likeCount || like.value
    isLiked.value = !isLiked.value
  } catch {}
}

async function submitComment() {
  const id = route.params.id
  if (!commentText.value.trim()) return
  try {
    await addComment({ postId: Number(id), content: commentText.value })
    commentText.value = ''
    await loadComments()
  } catch {}
}

async function onLikeComment(c) {
  try {
    const r = await likeComment(c.id)
    if (r.data) {
      c._likeCount = r.data.likeCount
      c._isLiked = r.data.liked
    }
  } catch {}
}
</script>

<style scoped>
.back-btn {
  color: #27ae60;
  font-weight: 500;
}

.post-card {
  border-radius: 12px;
  border: 1px solid #e8f5e9;
}

.post-content {
  padding: 20px;
}

.post-header {
  margin-bottom: 20px;
}

.post-title {
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 8px;
  line-height: 1.4;
}

.post-location {
  display: flex;
  align-items: center;
  color: #666;
  font-size: 14px;
}

.location-icon {
  margin-right: 4px;
  color: #27ae60;
}

.author-info {
  display: flex;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.author-details {
  flex: 1;
  margin-left: 12px;
}

.author-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.post-time {
  font-size: 12px;
  color: #999;
}

.post-stats {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  color: #666;
  font-size: 13px;
}

.post-body {
  margin-bottom: 24px;
}

.markdown-content {
  font-size: 16px;
  line-height: 1.8;
  color: #333;
  white-space: pre-wrap;
  margin-bottom: 20px;
}

.topic-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 16px;
}

.topic-tag {
  background: #e8f5e9;
  border-color: #27ae60;
  color: #27ae60;
  cursor: pointer;
  transition: all 0.2s ease;
}

.topic-tag:hover {
  background: #27ae60;
  color: white;
}

.media-gallery {
  margin: 24px 0;
}

.media-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.media-item:hover {
  transform: translateY(-2px);
}

.single-media .media-image,
.single-media .media-video {
  max-height: 500px;
}

.media-image {
  width: 100%;
  height: auto;
  max-height: 300px;
  object-fit: cover;
  cursor: zoom-in;
}

.media-video {
  width: 100%;
  height: auto;
  max-height: 300px;
  object-fit: cover;
  background: #000;
}

.video-indicator {
  position: absolute;
  top: 12px;
  right: 12px;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  padding: 6px 10px;
  border-radius: 20px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
}

.post-actions {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-top: 1px solid #f0f0f0;
  margin-top: 20px;
}

.post-actions .el-button {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  border-radius: 20px;
}

.post-actions .el-button.liked {
  border-color: #27ae60;
  color: #27ae60;
}

.count {
  margin-left: 4px;
  font-weight: 600;
}

.comment-section {
  margin-top: 30px;
  border-top: 1px solid #f0f0f0;
  padding-top: 24px;
}

.comment-header {
  margin-bottom: 20px;
}

.comment-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  color: #2c3e50;
  margin: 0;
}

.comment-count {
  background: #27ae60;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
}

.comment-form {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 16px;
  margin-bottom: 24px;
}

.comment-input :deep(.el-textarea__inner) {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: white;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.submit-btn {
  border-radius: 20px;
  padding: 8px 24px;
}

.comment-list {
  min-height: 200px;
}

.no-comments {
  padding: 40px 0;
}

.comment-item {
  display: flex;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-avatar {
  margin-right: 12px;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.comment-author {
  font-weight: 600;
  color: #2c3e50;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.comment-body {
  line-height: 1.6;
  color: #333;
  margin-bottom: 8px;
}

.comment-actions {
  display: flex;
  gap: 12px;
}

.comment-actions .el-button {
  padding: 4px 8px;
  font-size: 12px;
  color: #666;
}

.comment-actions .el-button.liked {
  color: #27ae60;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .post-title {
    font-size: 20px;
  }
  
  .author-info {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .author-details {
    margin: 12px 0;
  }
  
  .post-stats {
    width: 100%;
    justify-content: space-between;
  }
  
  .media-gallery {
    margin: 16px 0;
  }
  
  .post-actions {
    flex-direction: column;
  }
}
</style>