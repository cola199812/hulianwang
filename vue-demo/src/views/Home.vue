<template>
  <div class="p-4 space-y-6">
    <div class="bg-gradient-to-r from-green-400 to-blue-500 text-white rounded-lg p-6">
      <h1 class="text-2xl font-bold">欢迎回来，探索家！</h1>
      <p class="mt-2">为你准备了今日路线与活动推荐</p>
    </div>

    <div class="grid grid-cols-2 gap-4">
      <div class="relative">
        <el-button
          type="default"
          class="w-full h-full flex flex-col items-center justify-center p-6 rounded-lg border"
          @click="$router.push('/discover')"
        >
          <div class="text-green-600 text-2xl mb-2">🗺️</div>
          <div class="font-semibold">附近路线</div>
        </el-button>
        <div class="description-text absolute bottom-0 left-0 right-0 bg-white bg-opacity-95 text-xs p-2 rounded-b-lg shadow-sm transition-all duration-500 transform -translate-y-1 scale-95">
          发现周边热门路线
        </div>
      </div>
      <div class="relative">
        <el-button
          type="default"
          class="w-full h-full flex flex-col items-center justify-center p-6 rounded-lg border"
          @click="$router.push('/social')"
        >
          <div class="text-blue-600 text-2xl mb-2">🎉</div>
          <div class="font-semibold">同城活动</div>
        </el-button>
        <div class="description-text absolute bottom-0 left-0 right-0 bg-white bg-opacity-95 text-xs p-2 rounded-b-lg shadow-sm transition-all duration-500 transform -translate-y-1 scale-95">
          加入附近精彩活动
        </div>
      </div>
      <div class="relative">
        <el-button
          type="default"
          class="w-full h-full flex flex-col items-center justify-center p-6 rounded-lg border"
          @click="$router.push('/creation')"
        >
          <div class="text-purple-600 text-2xl mb-2">✍️</div>
          <div class="font-semibold">写游记</div>
        </el-button>
        <div class="description-text absolute bottom-0 left-0 right-0 bg-white bg-opacity-95 text-xs p-2 rounded-b-lg shadow-sm transition-all duration-500 transform -translate-y-1 scale-95">
          记录你的探险故事
        </div>
      </div>
      <div class="relative">
        <el-button
          type="default"
          class="w-full h-full flex flex-col items-center justify-center p-6 rounded-lg border"
          @click="$router.push('/tools')"
        >
          <div class="text-orange-600 text-2xl mb-2">🧭</div>
          <div class="font-semibold">工具箱</div>
        </el-button>
        <div class="description-text absolute bottom-0 left-0 right-0 bg-white bg-opacity-95 text-xs p-2 rounded-b-lg shadow-sm transition-all duration-500 transform -translate-y-1 scale-95">
          离线地图 & 轨迹 & 天气
        </div>
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
    
    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="text-lg font-bold">📝 最新游记</h2>
        <el-link type="primary" @click="$router.push('/creation')">更多</el-link>
      </div>
      <div class="masonry">
        <div v-for="p in posts.slice(0,6)" :key="p.id" class="brick" @click="$router.push(`/post/${p.id}`)">
          <PostCard :post="p" brief />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import PostCard from '../components/PostCard.vue'
import { listRoutes } from '../api/route'
import { listActivities } from '../api/activity'
import { listPosts, listMediaByPost } from '../api/content'

const routes = ref([])
const activities = ref([])
const posts = ref([])

async function load() {
  try {
    const [r, a] = await Promise.all([listRoutes(), listActivities()])
    routes.value = r.data || []
    activities.value = a.data || []
    const { data } = await listPosts()
    posts.value = data || []
    for (const p of posts.value) {
      const res = await listMediaByPost(p.id).catch(()=>null)
      p._media = res?.data || []
    }
  } catch {}
}

onMounted(load)
</script>

<style scoped>
.masonry { column-count: 3; column-gap: 16px; }
.brick { break-inside: avoid; margin-bottom: 16px; cursor: pointer; }

/* 描述文字默认隐藏，悬停显示 */
.description-text {
  opacity: 0;
  font-size: 12px;
  color: #33373d;
}
.relative:hover .description-text {
  opacity: 100;
  transform: translateY(-0.25rem) scale(1);
}

@media (max-width: 768px) { .masonry { column-count: 2; } }
@media (max-width: 480px) { .masonry { column-count: 1; } }
</style>
