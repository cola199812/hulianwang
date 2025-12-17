<template>
  <div class="p-4" style="max-width: 1000px; margin: 0 auto;">
    <!-- 瀑布流 -->
    <div class="masonry">
      <div v-for="p in posts" :key="p.id" class="brick" @click="goDetail(p.id)">
        <PostCard :post="p" />
      </div>
    </div>

    <!-- 悬浮加号 -->
    <div class="fab" @click="publishVisible=true">
      <el-icon size="24"><Plus /></el-icon>
    </div>

    <!-- 发布弹窗 -->
    <el-dialog v-model="publishVisible" title="发布笔记" width="500">
      <el-form :model="form" label-width="80">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="给笔记起个标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.markdown" type="textarea" :rows="4" placeholder="写点什么..." />
        </el-form-item>
        <el-form-item label="位置">
          <el-input v-model="form.locationName" placeholder="地点名称" />
        </el-form-item>
        <el-form-item label="图片/视频">
          <el-upload
            multiple
            accept="image/*,video/*"
            :auto-upload="false"
            :file-list="fileList"
            :on-change="onFilesChange">
            <el-button>选择文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="publishVisible=false">取消</el-button>
        <el-button type="primary" @click="submit">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Plus } from '@element-plus/icons-vue'
import PostCard from '../components/PostCard.vue'
import { createPost, listPosts, uploadMedia, listMediaByPost } from '../api/content'
import { ElMessage } from 'element-plus'

const router = useRouter()
const posts = ref([])
const publishVisible = ref(false)
const fileList = ref([])
const form = reactive({ title:'', markdown:'', locationName:'', lat:null, lng:null })

function fullUrl(u) {
  if (!u) return ''
  if (u.startsWith('http')) return u
  return `http://localhost:8080${u}`
}

function goDetail(id) {
  router.push(`/post/${id}`)
}

async function load() {
  try {
    const { data } = await listPosts()
    posts.value = data || []
    for (const p of posts.value) {
      const res = await listMediaByPost(p.id).catch(()=>null)
      p._media = res?.data || []
    }
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '加载内容失败')
  }
}

async function submit() {
  try {
    const hasText = !!(form.title || form.markdown)
    const hasFiles = fileList.value.length > 0
    if (!hasText && !hasFiles) {
      ElMessage.error('请填写内容或选择文件')
      return
    }
    const { data } = await createPost(form)
    const postId = data?.id
    if (!postId) {
      ElMessage.error('发布失败')
      return
    }
    if (hasFiles) {
      for (const f of fileList.value) {
        const fd = new FormData()
        fd.append('file', f.raw)
        fd.append('type', (f.raw?.type || '').startsWith('video') ? 'video' : 'image')
        fd.append('postId', postId)
        await uploadMedia(fd)
      }
    }
    publishVisible.value = false
    form.title = ''
    form.markdown = ''
    form.locationName = ''
    fileList.value = []
    await load()
    ElMessage.success('发布成功')
    router.push('/my-posts')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '发布失败，请先登录或稍后重试')
  }
}

onMounted(load)

function onFilesChange(file, fs) {
  fileList.value = fs || []
}
</script>

<style scoped>
.masonry {
  column-count: 3;
  column-gap: 16px;
}
.brick {
  break-inside: avoid;
  margin-bottom: 16px;
  cursor: pointer;
}
@media (max-width: 768px) {
  .masonry { column-count: 2; }
}
@media (max-width: 480px) {
  .masonry { column-count: 1; }
}
.fab {
  position: fixed;
  right: 24px;
  bottom: 24px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: #ff2442;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0,0,0,.2);
  cursor: pointer;
}
</style>
