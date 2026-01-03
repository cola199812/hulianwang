<template>
  <div class="p-4" style="max-width: 900px; margin: 0 auto;">
    <div style="display:flex; justify-content:space-between; align-items:center;">
      <h1 class="text-xl font-bold mb-4">通知中心</h1>
      <el-button type="primary" @click="$router.push('/home')">返回首页</el-button>
    </div>
    <el-tabs v-model="tab" @tab-change="loadNotifications">
      <el-tab-pane label="全部" name="all">
        <el-timeline v-if="notifications.length > 0">
          <el-timeline-item 
            v-for="n in notifications" 
            :key="n.id" 
            :timestamp="formatTime(n.createdAt)"
          >
            <div class="note-title">{{ n.title }}</div>
            <div class="note-desc">{{ n.content }}</div>
            <div style="margin-top: 8px;">
              <el-tag v-if="n.type" :type="getTagType(n.type)" size="small">{{ n.type }}</el-tag>
            </div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无通知" />
      </el-tab-pane>
      <el-tab-pane label="活动" name="activity">
        <el-timeline v-if="activityNotifications.length > 0">
          <el-timeline-item 
            v-for="n in activityNotifications" 
            :key="n.id" 
            :timestamp="formatTime(n.createdAt)"
          >
            <div class="note-title">{{ n.title }}</div>
            <div class="note-desc">{{ n.content }}</div>
            <div style="margin-top: 8px;">
              <el-tag type="success" size="small">活动通知</el-tag>
            </div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无活动通知" />
      </el-tab-pane>
      <el-tab-pane label="系统" name="system">
        <el-timeline v-if="systemNotifications.length > 0">
          <el-timeline-item 
            v-for="n in systemNotifications" 
            :key="n.id" 
            :timestamp="formatTime(n.createdAt)"
          >
            <div class="note-title">{{ n.title }}</div>
            <div class="note-desc">{{ n.content }}</div>
            <div style="margin-top: 8px;">
              <el-tag type="warning" size="small">系统通知</el-tag>
            </div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无系统公告" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const tab = ref('all')
const notifications = ref([])
const activityNotifications = ref([])
const systemNotifications = ref([])

async function loadNotifications() {
  const localNotifications = [
    { id: 1, title: '你报名的活动时间提醒', content: '集合点：市体育馆南门', type: 'activity', createdAt: '2025-12-18 09:00:00' },
    { id: 2, title: '路线更新提醒', content: '路线“湖畔环线”更新了描述', type: 'system', createdAt: '2025-12-17 20:10:00' },
    { id: 3, title: '系统维护公告', content: '凌晨 1:00-2:00 进行短时维护', type: 'system', createdAt: '2025-12-16 23:00:00' }
  ]

  notifications.value = localNotifications
  activityNotifications.value = localNotifications.filter(n => n.type === 'activity')
  systemNotifications.value = localNotifications.filter(n => n.type === 'system')
}

function formatTime(timeString) {
  if (!timeString) return ''
  const date = new Date(timeString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

function getTagType(type) {
  switch (type) {
    case 'activity':
    case '活动':
      return 'success'
    case 'system':
    case '系统':
      return 'warning'
    default:
      return 'info'
  }
}



onMounted(() => {
  loadNotifications()
})
</script>

<style scoped>
.note-title { font-weight:600; }
.note-desc { color:#666; font-size:12px; margin-top:4px; }
</style>
