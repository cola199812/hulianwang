<template>
  <div class="p-4" style="max-width: 1000px; margin: 0 auto;">
    <div style="display:flex; justify-content:space-between; align-items:center;">
      <h1 class="text-xl font-bold mb-4">活动社交</h1>
      <el-button @click="$router.push('/activities')">活动列表</el-button>
    </div>

    <div class="grid">
      <div class="left">
        <div class="section-head">
          <div class="title">热门活动</div>
          <el-link type="primary" @click="$router.push('/activities')">全部</el-link>
        </div>
        <div class="activity-grid">
          <div v-for="a in list.slice(0,6)" :key="a.id" class="activity-card">
            <div class="activity-cover">{{ (a.name || '').slice(0,1) }}</div>
            <div class="activity-title">{{ a.name }}</div>
            <div class="activity-meta">{{ a.time }} · {{ a.currentPeople }}/{{ a.maxPeople }} 人</div>
            <el-button size="small" type="success" @click="join(a.id)">报名</el-button>
          </div>
        </div>
        <div class="section-head">
          <div class="title">最新游记</div>
          <el-link type="primary" @click="$router.push('/creation')">更多</el-link>
        </div>
        <div class="masonry">
          <div v-for="p in posts.slice(0,6)" :key="p.id" class="brick" @click="$router.push(`/post/${p.id}`)">
            <PostCard :post="p" brief />
          </div>
        </div>
      </div>
      <div class="right">
        <el-card>
          <div class="title">附近路线</div>
          <div class="sidebar-list">
            <div v-for="r in routes.slice(0,5)" :key="r.id" class="sidebar-item">
              <div class="name">{{ r.name }}</div>
              <div class="meta">{{ r.distance }} km · {{ r.level }}</div>
            </div>
          </div>
          <el-button size="small" @click="$router.push('/routes')">查看更多</el-button>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listActivities, joinActivity } from '../api/activity'
import { ElMessage } from 'element-plus'
import { listRoutes } from '../api/route'
import { listPosts, listMediaByPost } from '../api/content'
import PostCard from '../components/PostCard.vue'

const list = ref([])
const routes = ref([])
const posts = ref([])

async function load() {
  try {
    const { data } = await listActivities()
    list.value = data || []
    const { data: r } = await listRoutes()
    routes.value = r || []
    const { data: p } = await listPosts()
    posts.value = p || []
    for (const x of posts.value) {
      const res = await listMediaByPost(x.id).catch(()=>null)
      x._media = res?.data || []
    }
  } catch {}
}

async function join(id) {
  try {
    await joinActivity(id)
    await load()
    ElMessage.success('报名成功')
  } catch (e) {
    ElMessage.error('报名失败，可能未登录或人数已满')
  }
}

onMounted(load)
</script>

<style scoped>
.grid { display:grid; grid-template-columns:2fr 1fr; gap:16px; }
.section-head { display:flex; align-items:center; justify-content:space-between; margin:12px 0; }
.title { font-weight:600; }
.activity-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:12px; }
.activity-card { border:1px solid #eee; border-radius:10px; padding:12px; display:flex; flex-direction:column; gap:6px; }
.activity-cover { width:100%; height:120px; border-radius:8px; background:linear-gradient(135deg,#f9b,#f67); color:#fff; display:flex; align-items:center; justify-content:center; font-size:28px; font-weight:700; }
.activity-title { font-weight:600; }
.activity-meta { color:#666; font-size:12px; }
.masonry { column-count: 2; column-gap: 16px; }
.brick { break-inside: avoid; margin-bottom: 16px; cursor: pointer; }
.sidebar-list { display:flex; flex-direction:column; gap:8px; margin:8px 0; }
.sidebar-item .name { font-weight:600; }
.sidebar-item .meta { color:#666; font-size:12px; }
@media (max-width: 768px) { .grid { grid-template-columns:1fr; } .activity-grid { grid-template-columns:repeat(2,1fr); } .masonry { column-count: 1; } }
</style>
