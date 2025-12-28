<template>
  <div class="p-4" style="max-width: 680px;margin:0 auto;">
    <el-button text @click="$router.back()" style="margin-bottom:12px;">
      <el-icon><ArrowLeft /></el-icon>返回
    </el-button>

    <el-card shadow="never">
      <!-- 头图 -->
      <img v-if="cover" :src="fullUrl(cover)" style="width:100%;max-height:320px;object-fit:cover;border-radius:8px;" />

      <div class="mt-4">
        <h2 class="mb-2">{{ post.title || '无标题' }}</h2>
        <div class="text-sm text-gray-500 mb-3">{{ post.locationName || '' }}</div>
        <div class="text-base leading-7 whitespace-pre-wrap">{{ post.markdown }}</div>
        
        <!-- 话题标签 -->
        <div v-if="post.topics && post.topics.length" class="mt-4 flex flex-wrap gap-2">
          <span v-for="t in post.topics" :key="t.id" class="text-blue-500 cursor-pointer hover:underline mr-2">
            #{{ t.name }}
          </span>
        </div>
      </div>

      <!-- 附件网格 -->
      <div v-if="media.length" class="mt-6 grid grid-cols-3 gap-2">
        <template v-for="m in media" :key="m.id">
          <img v-if="m.type==='image'" :src="fullUrl(m.url)" class="media-img" />
          <video v-else controls :src="fullUrl(m.url)" class="media-video" />
        </template>
      </div>

      <!-- 操作区 -->
      <div class="mt-6 flex items-center gap-4 text-sm text-gray-500">
        <span>{{ author }}</span>
        <span>·</span>
        <span>{{ new Date(post.createTime).toLocaleString() }}</span>
        <el-button text type="primary" @click="onLike">👍 {{ like }}</el-button>
      </div>

      <!-- 评论区 -->
      <div class="mt-6">
        <h3 class="mb-2">评论</h3>
        <el-form @submit.prevent="submitComment">
          <el-input v-model="commentText" type="textarea" :rows="3" placeholder="写下你的看法..." />
          <div style="margin-top:8px;">
            <el-button type="primary" @click="submitComment">发表评论</el-button>
          </div>
        </el-form>
        <div class="mt-4 space-y-3">
          <div v-for="c in comments" :key="c.id" class="comment">
            <div class="comment-head">
              <span class="comment-user">{{ c._username || '匿名用户' }}</span>
              <span class="comment-time">{{ new Date(c.createTime).toLocaleString() }}</span>
            </div>
            <div class="comment-body">{{ c.content }}</div>
            <div class="comment-actions">
              <el-button text @click="onLikeComment(c)">👍 {{ c._likeCount || 0 }}</el-button>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getPost, listMediaByPost, postStats, likePost, listComments, addComment, likeComment, viewPost } from '../api/content'
import { userSimple } from '../api/user'

const route = useRoute()
const post = ref({})
const media = ref([])
const cover = ref('')
const author = ref('匿名用户')
const like = ref(0)
const comments = ref([])
const commentText = ref('')

function fullUrl(u) {
  if (!u) return ''
  if (u.startsWith('http')) return u
  return `http://localhost:8080${u}`
}

onMounted(async () => {
  const id = route.params.id
  
  // 增加浏览量
  try { await viewPost(id) } catch {}

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

  // 封面优先用 post.coverUrl，否则取第一张图
  if (post.value.coverUrl) {
    cover.value = post.value.coverUrl
  } else {
    const firstImg = media.value.find(m => m.type === 'image')
    cover.value = firstImg ? firstImg.url : ''
  }

  try {
    const s = await postStats(id)
    like.value = s.data.likeCount || 0
  } catch {}
  try {
    if (post.value.userId === 0) {
      author.value = '匿名用户'
    } else {
      const u = await userSimple(post.value.userId)
      author.value = u.data?.username || '匿名用户'
    }
  } catch {}
  await loadComments()
})

async function loadComments() {
  const id = route.params.id
  const { data } = await listComments(id)
  comments.value = data || []
  for (const c of comments.value) {
    try {
      if (c.userId === 0) {
        c._username = '匿名用户'
      } else {
        const u = await userSimple(c.userId)
        c._username = u.data?.username || '匿名用户'
      }
    } catch { c._username = '匿名用户' }
    try {
      const r = await commentStats(c.id)
      c._likeCount = r.data.likeCount || 0
    } catch {}
  }
}

async function onLike() {
  const id = route.params.id
  const r = await likePost(id)
  like.value = r.data.likeCount || like.value
}

async function submitComment() {
  const id = route.params.id
  if (!commentText.value) return
  await addComment({ postId: Number(id), content: commentText.value })
  commentText.value = ''
  await loadComments()
}

async function onLikeComment(c) {
  const r = await likeComment(c.id)
  c._likeCount = r.data.likeCount || 0
}
</script>

<style scoped>
.media-img { width:100%; height:auto; display:block; border-radius:8px; }
.media-video { width:100%; height:auto; display:block; border-radius:8px; }
.comment { border-top:1px solid #eee; padding-top:8px; }
.comment-head { display:flex; gap:8px; color:#666; font-size:12px; }
.comment-user { font-weight:600; }
.comment-body { margin-top:6px; white-space:pre-wrap; }
.comment-actions { margin-top:6px; }
</style>
