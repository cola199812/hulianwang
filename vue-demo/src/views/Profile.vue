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
    <div class="mt-4 grid grid-cols-3 gap-3">
      <el-card class="nav-card" @click="$router.push('/my-posts')">
        <div class="nav-title">我的发布</div>
        <div class="nav-desc">查看与管理你的游记</div>
      </el-card>
      <el-card class="nav-card" @click="$router.push('/creation')">
        <div class="nav-title">发布新内容</div>
        <div class="nav-desc">记录最新行程与见闻</div>
      </el-card>
      <el-card class="nav-card" @click="$router.push('/notification')">
        <div class="nav-title">通知中心</div>
        <div class="nav-desc">查看系统与活动通知</div>
      </el-card>
    </div>
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

<style scoped>
.nav-title { font-weight:600; }
.nav-desc { color:#666; font-size:12px; margin-top:4px; }
</style>
