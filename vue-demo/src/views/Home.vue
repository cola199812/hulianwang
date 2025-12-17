<template>
  <div class="p-4 space-y-6">
    <div class="bg-gradient-to-r from-green-400 to-blue-500 text-white rounded-lg p-6">
      <h1 class="text-2xl font-bold">欢迎回来，探索家！</h1>
      <p class="mt-2">为你准备了今日路线与活动推荐</p>
    </div>

    <div class="grid grid-cols-2 gap-4">
      <div class="border rounded-lg p-4 hover:shadow cursor-pointer" @click="$router.push('/discover')">
        <div class="text-green-600 text-2xl mb-2">🗺️</div>
        <div class="font-semibold">附近路线</div>
        <div class="text-sm text-gray-500">发现周边热门路线</div>
      </div>
      <div class="border rounded-lg p-4 hover:shadow cursor-pointer" @click="$router.push('/social')">
        <div class="text-blue-600 text-2xl mb-2">🎉</div>
        <div class="font-semibold">同城活动</div>
        <div class="text-sm text-gray-500">加入附近精彩活动</div>
      </div>
      <div class="border rounded-lg p-4 hover:shadow cursor-pointer" @click="$router.push('/creation')">
        <div class="text-purple-600 text-2xl mb-2">✍️</div>
        <div class="font-semibold">写游记</div>
        <div class="text-sm text-gray-500">记录你的探险故事</div>
      </div>
      <div class="border rounded-lg p-4 hover:shadow cursor-pointer" @click="$router.push('/tools')">
        <div class="text-orange-600 text-2xl mb-2">🧭</div>
        <div class="font-semibold">工具箱</div>
        <div class="text-sm text-gray-500">离线地图 & 轨迹 & 天气</div>
      </div>
    </div>

    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="text-lg font-bold">🔥 热门路线</h2>
        <el-link type="primary" @click="$router.push('/routes')">更多</el-link>
      </div>
      <el-empty v-if="routes.length === 0" description="暂无路线，去创建一条吧" />
      <div v-else class="space-y-3">
        <div v-for="r in routes.slice(0,3)" :key="r.id" class="border rounded p-3 flex items-center gap-3">
          <div class="flex-1">
            <div class="font-semibold">{{ r.name }}</div>
            <div class="text-sm text-gray-500">{{ r.distance }} km · 难度 {{ r.level }}</div>
          </div>
          <el-button size="small" @click="$router.push('/routes')">查看</el-button>
        </div>
      </div>
    </div>

    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="text-lg font-bold">🎊 近期活动</h2>
        <el-link type="primary" @click="$router.push('/activities')">更多</el-link>
      </div>
      <el-empty v-if="activities.length === 0" description="暂无活动，去发布一个吧" />
      <div v-else class="space-y-3">
        <div v-for="a in activities.slice(0,3)" :key="a.id" class="border rounded p-3">
          <div class="font-semibold">{{ a.name }}</div>
          <div class="text-sm text-gray-500">{{ a.time }} · {{ a.currentPeople }}/{{ a.maxPeople }} 人</div>
          <div class="mt-2">
            <el-button size="small" type="success" @click="$router.push('/activities')">报名</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listRoutes } from '../api/route'
import { listActivities } from '../api/activity'

const routes = ref([])
const activities = ref([])

async function load() {
  try {
    const [r, a] = await Promise.all([listRoutes(), listActivities()])
    routes.value = r.data || []
    activities.value = a.data || []
  } catch {}
}

onMounted(load)
</script>
