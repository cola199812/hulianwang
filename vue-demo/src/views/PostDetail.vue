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
      </div>

      <!-- 附件网格 -->
      <div v-if="media.length" class="mt-6 grid grid-cols-3 gap-2">
        <template v-for="m in media" :key="m.id">
          <img v-if="m.type==='image'" :src="fullUrl(m.url)" class="rounded-md" />
          <video v-else controls :src="fullUrl(m.url)" class="rounded-md" />
        </template>
      </div>

      <!-- 操作区 -->
      <div class="mt-6 flex items-center gap-4 text-sm text-gray-500">
        <span>{{ new Date(post.createTime).toLocaleString() }}</span>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ArrowLeft } from '@element-plus/icons-vue'
import { getPost, listMediaByPost } from '../api/content'

const route = useRoute()
const post = ref({})
const media = ref([])
const cover = ref('')

function fullUrl(u) {
  if (!u) return ''
  if (u.startsWith('http')) return u
  return `http://localhost:8080${u}`
}

onMounted(async () => {
  const id = route.params.id
  const { data } = await getPost(id)
  post.value = data
  const { data: list } = await listMediaByPost(id)
  media.value = list || []
  // 封面优先用 post.coverUrl，否则取第一张图
  cover.value = post.value.coverUrl || (media.value.find(m => m.type === 'image')?.url || '')
})
</script>