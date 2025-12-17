<template>
  <div class="p-4" style="max-width: 900px; margin: 0 auto;">
    <div style="display:flex; justify-content:space-between; align-items:center;">
      <h1 class="text-xl font-bold mb-4">通知中心</h1>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
    <el-tabs v-model="tab">
      <el-tab-pane label="全部" name="all">
        <el-timeline>
          <el-timeline-item v-for="n in notes" :key="n.id" :timestamp="n.time">
            <div class="note-title">{{ n.title }}</div>
            <div class="note-desc">{{ n.desc }}</div>
          </el-timeline-item>
        </el-timeline>
      </el-tab-pane>
      <el-tab-pane label="活动" name="activity">
        <el-empty description="暂无活动通知" />
      </el-tab-pane>
      <el-tab-pane label="系统" name="system">
        <el-empty description="暂无系统公告" />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const tab = ref('all')
const notes = ref([
  { id:1, title:'你报名的“山地徒步”活动时间提醒', time:'2025-12-18 09:00', desc:'集合点：市体育馆南门' },
  { id:2, title:'路线“湖畔环线”更新了描述', time:'2025-12-17 20:10', desc:'难度由“中等”调整为“简单”' },
  { id:3, title:'系统维护公告', time:'2025-12-16 23:00', desc:'凌晨 1:00-2:00 进行短时维护' }
])
</script>

<style scoped>
.note-title { font-weight:600; }
.note-desc { color:#666; font-size:12px; margin-top:4px; }
</style>
