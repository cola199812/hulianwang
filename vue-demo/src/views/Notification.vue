<template>
  <div class="p-4" style="max-width: 900px; margin: 0 auto;">
    <div style="display:flex; justify-content:space-between; align-items:center;">
      <h1 class="text-xl font-bold mb-4">通知中心</h1>
      <div class="flex gap-2">
        <el-button type="primary" @click="markAllAsRead">全部已读</el-button>
        <el-button @click="$router.push('/')">返回首页</el-button>
      </div>
    </div>
    <el-tabs v-model="tab" @tab-change="loadNotifications">
      <el-tab-pane label="全部" name="all">
        <el-timeline v-if="notifications.length > 0">
          <el-timeline-item v-for="n in notifications" :key="n.id" :timestamp="n.createTime">
            <div class="note-item" :class="{ 'read': n.isRead }">
              <div class="note-header">
                <div class="note-title">{{ n.title }}</div>
                <div class="note-actions">
                  <el-button type="text" size="small" @click="markAsRead(n.id)">
                    {{ n.isRead ? '已读' : '标记已读' }}
                  </el-button>
                  <el-button type="text" size="small" @click="deleteNotification(n.id)">
                    删除
                  </el-button>
                </div>
              </div>
              <div class="note-desc">{{ n.content }}</div>
            </div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无通知" />
      </el-tab-pane>
      <el-tab-pane label="活动" name="activity">
        <el-timeline v-if="notifications.length > 0">
          <el-timeline-item v-for="n in notifications" :key="n.id" :timestamp="n.createTime">
            <div class="note-item" :class="{ 'read': n.isRead }">
              <div class="note-header">
                <div class="note-title">{{ n.title }}</div>
                <div class="note-actions">
                  <el-button type="text" size="small" @click="markAsRead(n.id)">
                    {{ n.isRead ? '已读' : '标记已读' }}
                  </el-button>
                  <el-button type="text" size="small" @click="deleteNotification(n.id)">
                    删除
                  </el-button>
                </div>
              </div>
              <div class="note-desc">{{ n.content }}</div>
            </div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无活动通知" />
      </el-tab-pane>
      <el-tab-pane label="系统" name="system">
        <el-timeline v-if="notifications.length > 0">
          <el-timeline-item v-for="n in notifications" :key="n.id" :timestamp="n.createTime">
            <div class="note-item" :class="{ 'read': n.isRead }">
              <div class="note-header">
                <div class="note-title">{{ n.title }}</div>
                <div class="note-actions">
                  <el-button type="text" size="small" @click="markAsRead(n.id)">
                    {{ n.isRead ? '已读' : '标记已读' }}
                  </el-button>
                  <el-button type="text" size="small" @click="deleteNotification(n.id)">
                    删除
                  </el-button>
                </div>
              </div>
              <div class="note-desc">{{ n.content }}</div>
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
import { ElMessage } from 'element-plus'
import { getNotifications, markNotificationAsRead, deleteNotification as apiDeleteNotification, markAllNotificationsAsRead } from '../api/notification'

const tab = ref('all')
const notifications = ref([])

async function loadNotifications() {
  try {
    const type = tab.value === 'all' ? null : tab.value
    const { data } = await getNotifications(type)
    notifications.value = data || []
  } catch (error) {
    ElMessage.error('获取通知失败')
    console.error('加载通知失败:', error)
  }
}

async function markAsRead(id) {
  try {
    await markNotificationAsRead(id)
    const notification = notifications.value.find(n => n.id === id)
    if (notification) {
      notification.isRead = true
    }
    ElMessage.success('标记已读成功')
  } catch (error) {
    ElMessage.error('标记已读失败')
    console.error('标记已读失败:', error)
  }
}

async function deleteNotification(id) {
  try {
    await apiDeleteNotification(id)
    notifications.value = notifications.value.filter(n => n.id !== id)
    ElMessage.success('删除通知成功')
  } catch (error) {
    ElMessage.error('删除通知失败')
    console.error('删除通知失败:', error)
  }
}

async function markAllAsRead() {
  try {
    await markAllNotificationsAsRead()
    notifications.value.forEach(n => n.isRead = true)
    ElMessage.success('全部标记已读成功')
  } catch (error) {
    ElMessage.error('全部标记已读失败')
    console.error('全部标记已读失败:', error)
  }
}

onMounted(loadNotifications)
</script>

<style scoped>
.note-title { font-weight:600; }
.note-desc { color:#666; font-size:12px; margin-top:4px; }
.note-item {
  padding: 12px;
  border-radius: 8px;
  background-color: #f9f9f9;
  margin-bottom: 8px;
  transition: all 0.3s;
}
.note-item:hover {
  background-color: #f0f0f0;
}
.note-item.read {
  opacity: 0.7;
}
.note-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}
.note-actions {
  display: flex;
  gap: 8px;
}
</style>
