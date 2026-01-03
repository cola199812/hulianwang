<template>
  <div class="xh-card">
    <!-- 封面 -->
    <div class="xh-hero" v-if="heroUrl || heroCover">
      <img v-if="heroUrl" :src="fullUrl(heroUrl)" class="xh-hero-img" />
      <img v-else-if="heroCover" :src="fullUrl(heroCover)" class="xh-hero-img" />
      <div class="xh-hero-mask"></div>
      <div class="xh-title">
        {{ post.title || '无标题' }}
      </div>
      
      <!-- 视频标识 -->
      <div v-if="hasVideo" class="video-badge">
        <span>▶️</span>
      </div>
      
      <!-- 标签显示 -->
      <div v-if="post.topics && post.topics.length > 0" class="topic-tags">
        <span 
          v-for="topic in post.topics.slice(0, 3)" 
          :key="topic.id"
          class="topic-tag"
        >
          #{{ topic.name }}
        </span>
        <span v-if="post.topics.length > 3" class="more-tags">+{{ post.topics.length - 3 }}</span>
      </div>
    </div>

    <!-- 内容 -->
    <div class="xh-body">
      <div class="xh-meta">
        <div class="author-section">
          <el-avatar :size="32" :src="authorAvatar" class="author-avatar" />
          <div class="author-info">
            <span class="xh-author">{{ author }}</span>
            <span class="post-time">{{ time }}</span>
          </div>
        </div>
        
        <div class="stats-section">
          <div class="stat-item">
            <el-icon><View /></el-icon>
            <span>{{ viewCount }}</span>
          </div>
          <div class="stat-item">
            <el-icon><ChatDotRound /></el-icon>
            <span>{{ comment }}</span>
          </div>
        </div>
      </div>
      
      <div v-if="post.locationName" class="xh-location">
        <el-icon><Location /></el-icon>
        <span class="location-text">{{ post.locationName }}</span>
        <span v-if="post.distance != null" class="distance-text">
          (距您 {{ (post.distance / 1000).toFixed(1) }} km)
        </span>
      </div>

      <!-- 标签展示 -->
      <div v-if="post.topics && post.topics.length" class="topic-tags-body">
        <span 
          v-for="topic in post.topics" 
          :key="topic.id" 
          class="topic-tag-item"
          @click.stop="$emit('tag-click', topic.name)"
        >
          #{{ topic.name }}
        </span>
      </div>

      <div v-if="!brief" class="xh-text">
        {{ excerpt }}
        <span v-if="excerpt.length < (post.markdown || '').length" class="read-more" @click.stop="goToDetail">
          ...展开
        </span>
      </div>

      <!-- 操作区 -->
      <div v-if="!brief" class="xh-actions">
        <div class="xh-action" :class="{ 'is-liked': isLiked }" @click.stop="onLike">
          <el-icon v-if="isLiked" color="#e74c3c"><StarFilled /></el-icon>
          <el-icon v-else><Star /></el-icon>
          <span class="action-text">{{ like }}</span>
        </div>
        <div class="xh-action" @click.stop="onComment">
          <el-icon><ChatLineRound /></el-icon>
          <span class="action-text">{{ comment }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { postStats, likePost } from '../api/content'
import { userSimple } from '../api/user'
import { 
  View, 
  ChatDotRound, 
  ChatLineRound, 
  Share, 
  Star, 
  StarFilled,
  Location
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const router = useRouter()
const props = defineProps({
  post: { type: Object, required: true },
  brief: { type: Boolean, default: false }
})

const like = ref(0)
const comment = ref(0)
const viewCount = ref(0)
const author = ref('匿名用户')
const authorAvatar = ref('')
const isLiked = ref(false)

const time = computed(() => {
  const date = new Date(props.post.createTime || '')
  if (isNaN(date.getTime())) return ''
  
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 30) return `${days}天前`
  return date.toLocaleDateString()
})

const excerpt = computed(() => {
  const t = props.post.markdown || ''
  return t.length > 100 ? t.slice(0, 100) : t
})

const hasVideo = computed(() => {
  return props.post.video || (props.post._media && props.post._media.some(m => m.type === 'video'))
})

const heroCover = computed(() => {
  if (props.post.video && props.post.video.coverUrl) {
    return props.post.video.coverUrl
  }
  return ''
})

// 只保留封面图，不显示内容区的媒体列表
const mediaList = computed(() => {
  return [] // 返回空数组，不显示内容区的图片
})

const heroUrl = computed(() => {
  if (props.post.coverUrl) return props.post.coverUrl
  
  if (props.post.images && props.post.images.length > 0) {
    return props.post.images[0].imageUrl
  }
  
  const img = (props.post._media || []).find(m => m.type === 'image')
  return img ? img.url : ''
})

function fullUrl(u) {
  if (!u) return ''
  if (u.startsWith('http')) return u
  return `http://localhost:8080${u}`
}

async function loadMeta() {
  try {
    if (props.post.likeCount !== undefined) like.value = props.post.likeCount
    if (props.post.commentCount !== undefined) comment.value = props.post.commentCount
    if (props.post.viewCount !== undefined) viewCount.value = props.post.viewCount
    if (props.post.isLiked !== undefined) isLiked.value = props.post.isLiked

    const s = await postStats(props.post.id)
    like.value = s.data.likeCount || 0
    comment.value = s.data.commentCount || 0
    viewCount.value = s.data.viewCount || 0
  } catch {}

  try {
    if (props.post.userId === 0) {
      author.value = '匿名用户'
    } else {
      const u = await userSimple(props.post.userId)
      author.value = u.data?.username || '匿名用户'
      authorAvatar.value = u.data?.avatar || ''
    }
  } catch {}
}

async function onLike() {
  try {
    isLiked.value = !isLiked.value
    like.value += isLiked.value ? 1 : -1
    
    const r = await likePost(props.post.id)
    if (r.data) {
      like.value = r.data.likeCount
      isLiked.value = r.data.liked
    }
  } catch {
    isLiked.value = !isLiked.value
    like.value += isLiked.value ? 1 : -1
    ElMessage.error('点赞失败')
  }
}

function onComment() {
  router.push(`/post/${props.post.id}#comments`)
}

function goToDetail() {
  router.push(`/post/${props.post.id}`)
}

function onShare() {
  const shareUrl = `${window.location.origin}/post/${props.post.id}`
  
  if (navigator.share) {
    navigator.share({
      title: props.post.title || '分享帖子',
      text: excerpt.value,
      url: shareUrl
    }).catch(console.error)
  } else {
    navigator.clipboard.writeText(shareUrl)
      .then(() => ElMessage.success('链接已复制到剪贴板'))
      .catch(() => ElMessage.error('复制失败'))
  }
}

onMounted(loadMeta)
</script>

<style scoped>
/* 卡片整体 */
.xh-card {
  background: #fff;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(39, 174, 96, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid #e8f5e9;
  cursor: pointer;
}

.xh-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(39, 174, 96, 0.15);
}

/* 封面 */
.xh-hero {
  position: relative;
  background: #f8fdf9;
}

.xh-hero-img {
  width: 100%;
  height: 240px;
  object-fit: cover;
  display: block;
}

.xh-hero-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.7),
    rgba(0, 0, 0, 0.3),
    rgba(0, 0, 0, 0)
  );
}

.xh-title {
  position: absolute;
  left: 20px;
  right: 20px;
  bottom: 20px;
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  line-height: 1.4;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* 视频标识 */
.video-badge {
  position: absolute;
  top: 16px;
  right: 16px;
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 6px;
  backdrop-filter: blur(4px);
}

/* 标签显示 */
.topic-tags {
  position: absolute;
  top: 16px;
  left: 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  max-width: 70%;
}

.topic-tag {
  background: rgba(39, 174, 96, 0.9);
  color: white;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
  backdrop-filter: blur(4px);
  max-width: 80px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.more-tags {
  background: rgba(255, 255, 255, 0.9);
  color: #666;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 11px;
}

/* 内容区域 */
.xh-body {
  padding: 16px 20px;
}

/* 元信息 */
.xh-meta {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.author-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-avatar {
  border: 2px solid #e8f5e9;
}

.author-info {
  display: flex;
  flex-direction: column;
}

.xh-author {
  font-weight: 600;
  color: #2c3e50;
  font-size: 14px;
}

.post-time {
  font-size: 12px;
  color: #95a5a6;
  margin-top: 2px;
}

.stats-section {
  display: flex;
  gap: 16px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #7f8c8d;
}

.stat-item .el-icon {
  font-size: 14px;
}

/* 位置信息 */
.topic-tags-body {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin: 8px 0;
}

.topic-tag-item {
  font-size: 12px;
  color: #27ae60;
  background: #e8f5e9;
  padding: 2px 8px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.topic-tag-item:hover {
  background: #27ae60;
  color: white;
}

.xh-location {
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 8px 0 12px;
  font-size: 13px;
  color: #666;
}

.xh-location .el-icon {
  font-size: 14px;
  color: #27ae60;
}

.location-text {
  font-weight: 500;
}

.distance-text {
  color: #95a5a6;
  font-size: 12px;
}

/* 文本内容 */
.xh-text {
  color: #2c3e50;
  font-size: 14px;
  line-height: 1.6;
  margin: 12px 0;
  white-space: pre-wrap;
  cursor: text;
}

.read-more {
  color: #27ae60;
  font-weight: 500;
  cursor: pointer;
  display: inline-block;
  margin-left: 4px;
  padding: 2px 6px;
  border-radius: 4px;
}

.read-more:hover {
  background: #f0f9f0;
  text-decoration: none;
}

/* 操作区 */
.xh-actions {
  display: flex;
  gap: 24px;
  padding: 12px 0 4px;
  border-top: 1px solid #f0f0f0;
  margin-top: 16px;
}

.xh-action {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s ease;
  color: #7f8c8d;
  padding: 4px 8px;
  border-radius: 6px;
}

.xh-action:hover {
  background: #f8f9fa;
  color: #27ae60;
}

.xh-action .el-icon {
  font-size: 18px;
}

.xh-action.is-liked {
  color: #e74c3c;
}

.xh-action.is-liked:hover {
  color: #e74c3c;
  background: #fef0f0;
}

.action-text {
  font-size: 13px;
  font-weight: 500;
}

/* 移动端优化 */
@media (max-width: 768px) {
  .xh-card {
    border-radius: 12px;
  }
  
  .xh-hero-img {
    height: 200px;
  }
  
  .xh-title {
    font-size: 16px;
    left: 16px;
    right: 16px;
    bottom: 16px;
  }
  
  .xh-body {
    padding: 14px 16px;
  }
  
  .xh-actions {
    gap: 20px;
  }
  
  .topic-tags {
    top: 12px;
    left: 12px;
  }
  
  .topic-tag {
    padding: 3px 6px;
    font-size: 10px;
  }
}
</style>