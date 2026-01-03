<template>
  <div class="p-4" style="max-width: 1000px; margin: 0 auto;">
    <div style="display:flex; justify-content:space-between; align-items:center;">
      <h1 class="text-xl font-bold mb-4">活动社交</h1>
      <div style="display:flex; gap:8px;">
        <el-button :loading="loading" @click="load">刷新</el-button>
        <el-button @click="$router.push('/activities')">活动列表</el-button>
      </div>
    </div>

    <div class="grid">
      <div class="left">
        <div class="section-head">
          <div class="title">热门活动</div>
          <el-link type="primary" @click="$router.push('/activities')">全部</el-link>
        </div>
        <el-skeleton v-if="loading" :rows="6" animated />
        <div v-else-if="list.length === 0" style="margin: 16px 0;">
          <el-empty description="暂无活动" />
        </div>
        <div v-else class="activity-grid">
          <div v-for="a in list.slice(0,6)" :key="a.id" class="activity-card">
            <div class="activity-cover">{{ (a.name || '').slice(0,1) }}</div>
            <div class="activity-title">{{ a.name }}</div>
            <div class="activity-meta">{{ formatTime(a.time) }} · {{ a.currentPeople }}/{{ a.maxPeople }} 人</div>
            <el-button
              v-if="!isJoined(a.id)"
              size="small"
              type="success"
              :disabled="Number(a.currentPeople) >= Number(a.maxPeople)"
              @click="join(a.id)"
            >
              {{ Number(a.currentPeople) >= Number(a.maxPeople) ? '已满' : '报名' }}
            </el-button>
            <el-button
              v-else
              size="small"
              type="warning"
              @click="cancelJoin(a.id)"
            >
              取消报名
            </el-button>
          </div>
        </div>
        <div class="section-head">
          <div class="title">最新游记</div>
          <el-link type="primary" @click="$router.push('/creation')">更多</el-link>
        </div>
        <el-skeleton v-if="loading" :rows="6" animated />
        <div v-else-if="posts.length === 0" style="margin: 16px 0;">
          <el-empty description="暂无游记" />
        </div>
        <div v-else class="masonry">
          <div v-for="p in posts.slice(0,6)" :key="p.id" class="brick" @click="$router.push(`/post/${p.id}`)">
            <PostCard :post="p" brief />
          </div>
        </div>
      </div>
      <div class="right">
        <el-card>
          <div class="title">附近路线</div>
          <el-skeleton v-if="loading" :rows="4" animated />
          <div v-else-if="routes.length === 0" style="margin: 12px 0;">
            <el-empty description="暂无路线" :image-size="80" />
          </div>
          <div v-else class="sidebar-list">
            <div v-for="r in routes.slice(0,5)" :key="r.id" class="sidebar-item" @click="$router.push('/discover')">
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
import { listActivities, joinActivity, cancelJoin as cancelJoinActivity, listMyActivities, checkIfJoined } from '../api/activity'
import { ElMessage } from 'element-plus'
import { listRoutes } from '../api/route'
import { listPosts, listMediaByPost } from '../api/content'
import PostCard from '../components/PostCard.vue'

const list = ref([])
const routes = ref([])
const posts = ref([])
const loading = ref(false)
const joinedActivities = ref(new Set()) // 记录已报名的活动ID

async function load() {
  loading.value = true
  try {
    const [aRes, rRes, pRes] = await Promise.allSettled([listActivities(), listRoutes(), listPosts()])
    list.value = aRes.status === 'fulfilled' ? (aRes.value.data || []) : []
    routes.value = rRes.status === 'fulfilled' ? (rRes.value.data || []) : []
    posts.value = pRes.status === 'fulfilled' ? (pRes.value.data || []) : []

    const needMedia = posts.value.slice(0, 6).filter(p => !p.images && !p.video)
    if (needMedia.length) {
      await Promise.allSettled(
        needMedia.map(async (p) => {
          const res = await listMediaByPost(p.id)
          p._media = res?.data || []
        })
      )
    }
    
    // 更新已报名活动状态
    await updateJoinedActivities()
  } finally {
    loading.value = false
  }
}

async function join(id) {
  try {
    await joinActivity(id)
    await load()
    ElMessage.success('报名成功')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '报名失败，可能未登录或人数已满')
  }
}

async function cancelJoin(id) {
  try {
    await cancelJoinActivity(id)
    await load()
    ElMessage.success('取消报名成功')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '取消报名失败')
  }
}

function isJoined(activityId) {
  return joinedActivities.value.has(activityId)
}

async function updateJoinedActivities() {
  // 检查每个活动用户是否已报名
  const joinedIds = new Set()
  
  for (const activity of list.value) {
    try {
      const isJoined = await checkIfJoined(activity.id)
      if (isJoined) {
        joinedIds.add(activity.id)
      }
    } catch (error) {
      console.error(`检查活动 ${activity.id} 报名状态失败:`, error)
    }
  }
  
  joinedActivities.value = joinedIds
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  if (isNaN(date.getTime())) return String(time)
  return date.toLocaleString()
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
