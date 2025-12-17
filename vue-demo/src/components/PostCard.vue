<template>
  <div class="xh-card">
    <div class="xh-hero" v-if="heroUrl">
      <img :src="fullUrl(heroUrl)" class="xh-hero-img" />
      <div class="xh-title">
        <div class="xh-title-text">{{ post.title || '无标题' }}</div>
      </div>
    </div>
    <div class="xh-body">
      <div class="xh-meta">
        <span class="xh-author">{{ author }}</span>
        <span> · </span>
        <span>{{ time }}</span>
      </div>
      <div v-if="!brief" class="xh-text">{{ excerpt }}</div>
      <div v-if="!brief && mediaList.length" class="xh-media-grid">
        <template v-for="m in mediaList" :key="m.id">
          <img v-if="m.type==='image'" :src="fullUrl(m.url)" class="xh-media" />
          <video v-else controls muted class="xh-media">
            <source :src="fullUrl(m.url)" />
          </video>
        </template>
      </div>
    </div>
    <div v-if="!brief" class="xh-actions">
      <div class="xh-action" @click="onLike">👍 {{ like }}</div>
      <div class="xh-action">💬 {{ comment }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import { postStats, likePost } from '../api/content'
import { userSimple } from '../api/user'
const props = defineProps({ post: { type: Object, required: true }, brief: { type: Boolean, default: false } })
const like = ref(0)
const comment = ref(0)
const time = computed(() => (props.post.createTime || '').toString().replace('T', ' '))
const excerpt = computed(() => {
  const t = props.post.markdown || ''
  return t.length > 120 ? t.slice(0, 120) + '...' : t
})
const author = ref('匿名用户')
const mediaList = computed(() => (props.post._media || []).slice(0, 6))
const heroUrl = computed(() => {
  if (props.post.coverUrl) return props.post.coverUrl
  const firstImage = (props.post._media || []).find(m => m.type === 'image')
  return firstImage ? firstImage.url : ''
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
.xh-card { background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 6px 20px rgba(0,0,0,0.08); }
.xh-hero { position:relative; }
.xh-hero-img { width:100%; height:220px; object-fit:cover; display:block; }
.xh-title { position:absolute; left:0; right:0; bottom:0; padding:10px 12px; background:linear-gradient(to top, rgba(0,0,0,0.5), rgba(0,0,0,0)); }
.xh-title-text { color:#fff; font-weight:600; font-size:16px; }
.xh-body { padding:12px; }
.xh-meta { color:#666; font-size:12px; margin-bottom:6px; }
.xh-author { font-weight:600; }
.xh-text { white-space:pre-wrap; color:#333; font-size:14px; line-height:1.6; }
.xh-media-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:6px; margin-top:8px; }
.xh-media { width:100%; height:110px; object-fit:cover; border-radius:8px; }
.xh-actions { display:flex; gap:14px; padding:10px 12px; border-top:1px solid #f0f0f0; color:#444; font-size:13px; }
.xh-action { cursor:pointer; }
</style>
