<template>
  <div style="max-width: 900px; margin: 40px auto;">
    <el-card>
      <div style="display:flex; justify-content:space-between; align-items:center;">
        <h2>活动列表</h2>
        <div>
          <el-button @click="$router.push('/routes')">路线列表</el-button>
        </div>
      </div>
      <el-form :model="form" inline label-width="100px" style="margin-bottom: 16px;">
        <el-form-item label="活动名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="路线 ID">
          <el-input v-model.number="form.routeId" type="number" />
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
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { createActivity, listActivities, joinActivity } from '../api/activity'

const form = reactive({
  name: '',
  routeId: null,
  time: '',
  maxPeople: 10
})
const list = ref([])

async function load() {
  const { data } = await listActivities()
  list.value = data
}

async function handleCreate() {
  try {
    await createActivity(form)
    await load()
    alert('创建成功')
  } catch (e) {
    alert('创建失败')
  }
}

async function join(id) {
  try {
    await joinActivity(id)
    await load()
    alert('报名成功')
  } catch (e) {
    alert('报名失败，可能未登录或人数已满')
  }
}

onMounted(load)
</script>

