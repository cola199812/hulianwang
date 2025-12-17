<template>
  <div class="p-4" style="max-width: 800px; margin: 0 auto;">
    <h1 class="text-xl font-bold mb-4">我的</h1>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>账户信息</span>
        </div>
      </template>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="用户ID">{{ info.id || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ info.username || '-' }}</el-descriptions-item>
        <el-descriptions-item label="注册时间">{{ info.createTime || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { userInfo } from '../api/user'

const info = ref({})

async function load() {
  try {
    const { data } = await userInfo()
    info.value = data || {}
  } catch {}
}

onMounted(load)
</script>
