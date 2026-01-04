<template>
  <div class="activities-page">
    <el-card>
      <div class="page-header">
        <div style="display:flex; align-items:center; gap:12px;">
          <el-button icon="el-icon-back" @click="$router.push('/social')">返回</el-button>
          <h2 class="page-title">活动列表</h2>
        </div>
        <div>
          <el-button @click="$router.push('/routes')">路线列表</el-button>
        </div>
      </div>
      <el-form :model="form" inline label-width="100px" style="margin-bottom: 16px;">
        <el-form-item label="活动名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="选择路线">
          <el-select v-model="form.routeId" placeholder="请选择路线" style="width: 300px;">
            <el-option
              v-for="route in routes"
              :key="route.id"
              :label="`${route.name} (ID: ${route.id})`"
              :value="route.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="活动时间">
          <el-date-picker
            v-model="form.time"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择时间" />
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input v-model.number="form.maxPeople" type="number" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCreate">创建活动</el-button>
        </el-form-item>
      </el-form>

    <el-tabs v-model="activeTab" type="border-card" class="custom-tabs" @tab-change="handleTabChange">
      <el-tab-pane label="活动发现" name="discover">
        <el-table :data="list" style="width: 100%;">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="活动名称" />
          <el-table-column label="关联路线" width="300">
            <template #default="{ row }">
              <span v-if="getRouteName(row.routeId)">
                {{ getRouteName(row.routeId) }}
              </span>
              <span v-else style="color: #999;">路线已删除 (ID: {{ row.routeId }})</span>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="时间" />
          <el-table-column prop="maxPeople" label="最大人数" />
          <el-table-column prop="currentPeople" label="当前人数" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <el-button
                v-if="!joinedSet.has(row.id)"
                size="small"
                type="success"
                :disabled="Number(row.currentPeople) >= Number(row.maxPeople)"
                @click="join(row.id)"
              >{{ Number(row.currentPeople) >= Number(row.maxPeople) ? '已满' : '报名' }}</el-button>
              <el-button
                v-else
                size="small"
                type="warning"
                @click="cancelJoin(row.id)"
              >取消报名</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="我发布的活动" name="my-activities">
        <div v-if="myActivities.length > 0">
          <el-table :data="myActivities" style="width: 100%;">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="活动名称" />
            <el-table-column label="关联路线" width="300">
              <template #default="{ row }">
                <span v-if="getRouteName(row.routeId)">
                  {{ getRouteName(row.routeId) }}
                </span>
                <span v-else style="color: #999;">路线已删除 (ID: {{ row.routeId }})</span>
              </template>
            </el-table-column>
            <el-table-column prop="time" label="时间" />
            <el-table-column prop="maxPeople" label="最大人数" />
            <el-table-column prop="currentPeople" label="当前人数" />
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button size="small" type="success" @click="join(row.id)">报名</el-button>
                <el-button size="small" type="primary" @click="editActivity(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="confirmDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <el-empty v-else description="您还没有发布任何活动" />
      </el-tab-pane>
    </el-tabs>
    
    <!-- 编辑活动对话框 -->
    <el-dialog v-model="editVisible" title="编辑活动" width="600">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="活动名称">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="选择路线">
          <el-select v-model="editForm.routeId" placeholder="请选择路线" style="width: 300px;">
            <el-option
              v-for="route in routes"
              :key="route.id"
              :label="`${route.name} (ID: ${route.id})`"
              :value="route.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="活动时间">
          <el-date-picker
            v-model="editForm.time"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择时间" />
        </el-form-item>
        <el-form-item label="最大人数">
          <el-input v-model.number="editForm.maxPeople" type="number" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">保存</el-button>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog v-model="deleteVisible" title="确认删除" width="400">
      <p>确定要删除活动"{{ currentActivity.name }}"吗？此操作不可撤销。</p>
      <template #footer>
        <el-button @click="deleteVisible = false">取消</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
    </el-dialog>
    
    </el-card>
    <div style="margin-top:16px;">
      <h3 style="font-weight:600; margin-bottom:8px;">精选活动</h3>
      <div style="display:grid; grid-template-columns:repeat(3,1fr); gap:12px;">
        <el-card v-for="a in list.slice(0,3)" :key="a.id">
          <div style="font-weight:600;">{{ a.name }}</div>
          <div style="color:#666; font-size:12px; margin-top:4px;">{{ a.time }} · {{ a.currentPeople }}/{{ a.maxPeople }} 人</div>
          <div style="margin-top:8px;">
            <el-button size="small" type="success" @click="join(a.id)">报名</el-button>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { createActivity, listActivities, joinActivity, cancelJoin as cancelJoinActivity, listJoinedActivities, listMyActivities, updateActivity, deleteActivity } from '../api/activity'
import { listRoutes } from '../api/route'
import { ElMessage } from 'element-plus'

const form = reactive({
  name: '',
  routeId: null,
  time: '',
  maxPeople: 10
})

const list = ref([])
const joinedSet = ref(new Set())
const routes = ref([])
const activeTab = ref('discover')
const myActivities = ref([])
const editVisible = ref(false)
const deleteVisible = ref(false)
const currentActivity = ref({})
const editForm = reactive({
  id: null,
  name: '',
  routeId: null,
  time: '',
  maxPeople: 10
})

function getRouteName(routeId) {
  if (!routeId) return ''
  const route = routes.value.find(r => r.id === routeId)
  return route ? route.name : ''
}

async function loadRoutes() {
  try {
    const { data } = await listRoutes()
    routes.value = data || []
  } catch (error) {
    console.error('获取路线列表失败:', error)
    routes.value = [] // 确保即使失败也不会为null
    // 不显示错误消息，避免在页面加载时显示太多错误
  }
}

async function loadActivities() {
  try {
    const { data } = await listActivities()
    list.value = data || []
    await refreshJoinedSet()
  } catch (error) {
    console.error('获取活动列表失败:', error)
    list.value = [] // 确保即使失败也不会为null
    // 不显示错误消息，避免在页面加载时显示太多错误
  }
}

async function loadMyActivities() {
  try {
    const { data } = await listMyActivities()
    myActivities.value = data || []
  } catch (error) {
    console.error('获取我发布的活动失败:', error)
    myActivities.value = [] // 确保即使失败也不会为null
  }
}

async function load() {
  try {
    // 使用Promise.allSettled确保即使一个失败另一个也能正常执行
    const results = await Promise.allSettled([loadRoutes(), loadActivities()])
    results.forEach(result => {
      if (result.status === 'rejected') {
        console.error('Load failed:', result.reason)
      }
    })
  } catch (error) {
    console.error('Load function failed:', error)
  }
}

async function refreshJoinedSet() {
  try {
    const res = await listJoinedActivities()
    const ids = Array.isArray(res.data) ? res.data : []
    joinedSet.value = new Set(ids)
  } catch (e) {
    joinedSet.value = new Set()
  }
}

async function handleTabChange(tabName) {
  activeTab.value = tabName
  if (tabName === 'my-activities') {
    await loadMyActivities()
  }
}

function editActivity(activity) {
  editForm.id = activity.id
  editForm.name = activity.name
  editForm.routeId = activity.routeId
  editForm.time = activity.time
  editForm.maxPeople = activity.maxPeople
  editVisible.value = true
}

function confirmDelete(activity) {
  currentActivity.value = activity
  deleteVisible.value = true
}

async function handleUpdate() {
  if (!editForm.routeId) {
    ElMessage.warning('请选择路线')
    return
  }
  
  try {
    await updateActivity(editForm.id, {
      name: editForm.name,
      routeId: editForm.routeId,
      time: editForm.time,
      maxPeople: editForm.maxPeople
    })
    
    ElMessage.success('更新成功')
    editVisible.value = false
    await loadMyActivities()
  } catch (error) {
    console.error('更新活动失败:', error)
    ElMessage.error('更新失败: ' + (error.message || '网络错误'))
  }
}

async function handleDelete() {
  try {
    await deleteActivity(currentActivity.value.id)
    
    ElMessage.success('删除成功')
    deleteVisible.value = false
    await loadMyActivities()
  } catch (error) {
    console.error('删除活动失败:', error)
    ElMessage.error('删除失败: ' + (error.message || '网络错误'))
  }
}

async function handleCreate() {
  if (!form.routeId) {
    ElMessage.warning('请选择路线')
    return
  }
  
  try {
    // 添加超时控制
    const timeoutPromise = new Promise((_, reject) => 
      setTimeout(() => reject(new Error('Request timeout')), 10000)
    )
    await Promise.race([createActivity(form), timeoutPromise])
    
    ElMessage.success('创建成功')
    // 重置表单
    form.name = ''
    form.routeId = null
    form.time = ''
    form.maxPeople = 10
    await loadActivities()
  } catch (error) {
    console.error('创建活动失败:', error)
    ElMessage.error('创建失败: ' + (error.message || '网络错误'))
  }
}

async function join(id) {
  try {
    // 添加超时控制
    const timeoutPromise = new Promise((_, reject) => 
      setTimeout(() => reject(new Error('Request timeout')), 10000)
    )
    await Promise.race([joinActivity(id), timeoutPromise])
    
    ElMessage.success('报名成功')
    await loadActivities()
  } catch (error) {
    console.error('报名失败:', error)
    ElMessage.error('报名失败: ' + (error.message || '网络错误或人数已满'))
  }
}

async function cancelJoin(id) {
  try {
    const timeoutPromise = new Promise((_, reject) => 
      setTimeout(() => reject(new Error('Request timeout')), 10000)
    )
    await Promise.race([cancelJoinActivity(id), timeoutPromise])
    ElMessage.success('取消报名成功')
    await loadActivities()
  } catch (error) {
    console.error('取消报名失败:', error)
    ElMessage.error('取消报名失败: ' + (error.message || '网络错误'))
  }
}

onMounted(() => {
  load()
})
</script>

<style scoped>
.activities-page { max-width: 900px; margin: 40px auto; }
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; padding: 12px 16px; background: rgba(255,255,255,0.7); backdrop-filter: blur(10px); border-radius: 12px; box-shadow: 0 4px 16px rgba(39,174,96,0.08); border: 1px solid #c8e6c9; }
.page-title { color: #27ae60; font-weight: 700; margin: 0; }
.custom-tabs { border-radius: 12px; overflow: hidden; box-shadow: 0 8px 24px rgba(39,174,96,0.08); background: rgba(255,255,255,0.7); backdrop-filter: blur(10px); border: 1px solid #c8e6c9; }
:deep(.el-tabs__header) { background: rgba(255,255,255,0.5); margin:0; border-radius: 12px 12px 0 0; border-bottom: 1px solid #c8e6c9; }
:deep(.el-tabs__item.is-active) { color: #27ae60; font-weight: 600; background: rgba(39,174,96,0.08); }
:deep(.el-tabs__active-bar) { background-color: #27ae60; height: 3px; }
:deep(.el-tabs__nav-wrap::after) { background-color: #e8f5e9; }
</style>
