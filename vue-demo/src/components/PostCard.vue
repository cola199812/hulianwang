<template>
  <div class="xh-card">
    <div class="xh-hero" v-if="heroUrl">
      <img :src="fullUrl(heroUrl)" class="xh-hero-img" />
      <div class="xh-title">
        <div class="xh-title-text">{{ post.title || '无标题' }}</div>
      </div>
    </div>
    <div class="xh-body">
      <div class="xh-meta">{{ post.locationName || '未知地点' }}</div>
      <div class="xh-text">{{ post.markdown }}</div>
      <div v-if="mediaList.length" class="xh-media-grid">
        <template v-for="m in mediaList" :key="m.id">
          <img v-if="m.type==='image'" :src="fullUrl(m.url)" class="xh-media" />
          <video v-else controls muted class="xh-media">
            <source :src="fullUrl(m.url)" />
          </video>
        </template>
      </div>
    </div>
    <div class="xh-actions">
      <div class="xh-action" @click="like++">👍 {{ like }}</div>
      <div class="xh-action" @click="comment++">💬 {{ comment }}</div>
      <div class="xh-action">{{ time }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
const props = defineProps({ post: { type: Object, required: true } })
const like = ref(0)
const comment = ref(0)
const time = computed(() => (props.post.createTime || '').toString().replace('T', ' '))
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
</script>

<style scoped>
.xh-card { background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 6px 20px rgba(0,0,0,0.08); }
.xh-hero { position:relative; }
.xh-hero-img { width:100%; height:220px; object-fit:cover; display:block; }
.xh-title { position:absolute; left:0; right:0; bottom:0; padding:10px 12px; background:linear-gradient(to top, rgba(0,0,0,0.5), rgba(0,0,0,0)); }
.xh-title-text { color:#fff; font-weight:600; font-size:16px; }
.xh-body { padding:12px; }
.xh-meta { color:#888; font-size:12px; margin-bottom:6px; }
.xh-text { white-space:pre-wrap; color:#333; font-size:14px; line-height:1.6; }
.xh-media-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:6px; margin-top:8px; }
.xh-media { width:100%; height:110px; object-fit:cover; border-radius:8px; }
.xh-actions { display:flex; gap:14px; padding:10px 12px; border-top:1px solid #f0f0f0; color:#666; font-size:13px; }
.xh-action { cursor:pointer; }
</style>
