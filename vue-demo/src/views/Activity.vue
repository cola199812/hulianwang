<template>
  <div class="p-4" style="max-width: 1000px; margin: 0 auto;">
    <div style="display:flex; justify-content:space-between; align-items:center;">
      <h1 class="text-xl font-bold mb-4">活动社交</h1>
      <el-button @click="$router.push('/activities')">活动列表</el-button>
    </div>

    <el-table :data="list" style="width: 100%;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="活动名称" />
      <el-table-column prop="routeId" label="路线ID" />
      <el-table-column prop="time" label="时间" />
      <el-table-column prop="maxPeople" label="最大人数" />
      <el-table-column prop="currentPeople" label="当前人数" />
      <el-table-column label="操作" width="150">
        <template #default="{ row }">
          <el-button size="small" type="success" @click="join(row.id)">报名</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listActivities, joinActivity } from '../api/activity'
import { ElMessage } from 'element-plus'

const list = ref([])

async function load() {
  try {
    const { data } = await listActivities()
    list.value = data || []
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
