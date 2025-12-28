<template>
  <div class="xh-card">
    <!-- 封面 -->
    <div class="xh-hero" v-if="heroUrl">
      <img :src="fullUrl(heroUrl)" class="xh-hero-img" />
      <div class="xh-hero-mask"></div>
      <div class="xh-title">
        {{ post.title || '无标题' }}
      </div>
    </div>

    <!-- 内容 -->
    <div class="xh-body">
      <div class="xh-meta">
        <span class="xh-author">👤 {{ author }}</span>
        <span class="dot">·</span>
        <span>{{ time }}</span>
      </div>
      <div v-if="post.locationName" class="xh-location" style="font-size: 12px; color: #666; margin-top: 4px;">
         📍 {{ post.locationName }}
         <span v-if="post.distance != null"> (距您 {{ (post.distance / 1000).toFixed(1) }} km)</span>
      </div>

      <div v-if="!brief" class="xh-text">
        {{ excerpt }}
      </div>

      <div v-if="!brief && mediaList.length" class="xh-media-grid">
        <template v-for="m in mediaList" :key="m.id">
          <img
            v-if="m.type === 'image'"
            :src="fullUrl(m.url)"
            class="xh-media"
          />
          <video v-else muted controls class="xh-media">
            <source :src="fullUrl(m.url)" />
          </video>
        </template>
      </div>
    </div>

    <!-- 操作区 -->
    <div v-if="!brief" class="xh-actions">
      <div class="xh-action" @click="onLike">
        👍 <span>{{ like }}</span>
      </div>
      <div class="xh-action">
        💬 <span>{{ comment }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { postStats, likePost } from '../api/content'
import { userSimple } from '../api/user'

const props = defineProps({
  post: { type: Object, required: true },
  brief: { type: Boolean, default: false }
})

const like = ref(0)
const comment = ref(0)
const author = ref('匿名用户')

const time = computed(() =>
  (props.post.createTime || '').toString().replace('T', ' ')
)

const excerpt = computed(() => {
  const t = props.post.markdown || ''
  return t.length > 100 ? t.slice(0, 100) + '...' : t
})

const mediaList = computed(() =>
  (props.post._media || []).slice(0, 6)
)

const heroUrl = computed(() => {
  if (props.post.coverUrl) return props.post.coverUrl
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
    const s = await postStats(props.post.id)
    like.value = s.data.likeCount || 0
    comment.value = s.data.commentCount || 0
  } catch {}

  try {
    if (props.post.userId === 0) {
      author.value = '匿名用户'
    } else {
      const u = await userSimple(props.post.userId)
      author.value = u.data?.username || '匿名用户'
    }
  } catch {}
}

async function onLike() {
  try {
    const r = await likePost(props.post.id)
    like.value = r.data.likeCount || like.value
  } catch {}
}

onMounted(loadMeta)
</script>

<style scoped>
/* 卡片整体 */
.xh-card {
  background: #fff;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 6px 18px rgba(39, 174, 96, 0.12);
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.xh-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 28px rgba(39, 174, 96, 0.22);
}

/* 封面 */
.xh-hero {
  position: relative;
}

.xh-hero-img {
  width: 100%;
  height: 220px;
  object-fit: cover;
  display: block;
}

.xh-hero-mask {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to top,
    rgba(0, 0, 0, 0.55),
    rgba(0, 0, 0, 0)
  );
}

.xh-title {
  position: absolute;
  left: 12px;
  right: 12px;
  bottom: 10px;
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  line-height: 1.4;
}

/* 内容 */
.xh-body {
  padding: 12px 14px;
}

.xh-meta {
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 4px;
}

.xh-author {
  font-weight: 600;
  color: #2c7a4b;
}

.dot {
  margin: 0 2px;
}

.xh-text {
  margin-top: 6px;
  color: #333;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
}

/* 图片九宫格 */
.xh-media-grid {
  margin-top: 8px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 6px;
}

.xh-media {
  width: 100%;
  height: 110px;
  object-fit: cover;
  border-radius: 8px;
}

/* 操作区 */
.xh-actions {
  display: flex;
  gap: 16px;
  padding: 10px 14px;
  border-top: 1px solid #f0f0f0;
  font-size: 13px;
  color: #444;
}

.xh-action {
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s ease;
}

.xh-action:hover {
  color: #27ae60;
}
</style>
