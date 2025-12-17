<template>
  <div class="p-4 space-y-6">
    <!-- 欢迎横幅 -->
    <div class="bg-gradient-to-r from-green-400 to-blue-500 text-white rounded-lg p-6">
      <h1 class="text-2xl font-bold">欢迎回来，探索家！</h1>
      <p class="mt-2">今日推荐已为你准备好，立即出发吧 ⛰️</p>
    </div>

    <!-- 快捷入口 -->
    <div class="grid grid-cols-2 gap-4">
      <div
        class="border rounded-lg p-4 hover:shadow cursor-pointer"
        @click="$router.push('/discover')"
      >
        <div class="text-green-600 text-2xl mb-2">🗺️</div>
        <div class="font-semibold">附近路线</div>
        <div class="text-sm text-gray-500">发现周边热门路线</div>
      </div>
      <div
        class="border rounded-lg p-4 hover:shadow cursor-pointer"
        @click="$router.push('/social')"
      >
        <div class="text-blue-600 text-2xl mb-2">🎉</div>
        <div class="font-semibold">同城活动</div>
        <div class="text-sm text-gray-500">加入附近精彩活动</div>
      </div>
      <div
        class="border rounded-lg p-4 hover:shadow cursor-pointer"
        @click="$router.push('/creation')"
      >
        <div class="text-purple-600 text-2xl mb-2">✍️</div>
        <div class="font-semibold">写游记</div>
        <div class="text-sm text-gray-500">记录你的探险故事</div>
      </div>
      <div
        class="border rounded-lg p-4 hover:shadow cursor-pointer"
        @click="$router.push('/tools')"
      >
        <div class="text-orange-600 text-2xl mb-2">🧭</div>
        <div class="font-semibold">工具箱</div>
        <div class="text-sm text-gray-500">离线地图 & 轨迹 & 天气</div>
      </div>
    </div>

    <!-- 推荐路线 -->
    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="text-lg font-bold">🔥 热门路线</h2>
        <el-link type="primary" @click="$router.push('/discover')">更多</el-link>
      </div>
      <div class="space-y-3">
        <div class="border rounded p-3 flex items-center gap-3">
          <img
            src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=mountain%20trail%20sunrise&image_size=landscape_16_9"
            class="w-20 h-14 object-cover rounded"
          />
          <div class="flex-1">
            <div class="font-semibold">梧桐山登顶线</div>
            <div class="text-sm text-gray-500">8.2 km · 难度 ★★★</div>
          </div>
          <el-button size="small" @click="$router.push('/discover')">查看</el-button>
        </div>
        <div class="border rounded p-3 flex items-center gap-3">
          <img
            src="https://trae-api-sg.mchost.guru/api/ide/v1/text_to_image?prompt=forest%20path%20river&image_size=landscape_16_9"
            class="w-20 h-14 object-cover rounded"
          />
          <div class="flex-1">
            <div class="font-semibold">梅林后山环线</div>
            <div class="text-sm text-gray-500">5.4 km · 难度 ★★</div>
          </div>
          <el-button size="small" @click="$router.push('/discover')">查看</el-button>
        </div>
      </div>
    </div>

    <!-- 推荐活动 -->
    <div>
      <div class="flex items-center justify-between mb-3">
        <h2 class="text-lg font-bold">🎊 近期活动</h2>
        <el-link type="primary" @click="$router.push('/social')">更多</el-link>
      </div>
      <div class="space-y-3">
        <div class="border rounded p-3">
          <div class="font-semibold">夜跑莲花山</div>
          <div class="text-sm text-gray-500">今晚 19:30 · 6/10 人已报名</div>
          <div class="mt-2 flex gap-2">
            <el-tag size="small">夜跑</el-tag>
            <el-tag size="small" type="success">新手友好</el-tag>
          </div>
        </div>
        <div class="border rounded p-3">
          <div class="font-semibold">大梅沙桨板体验</div>
          <div class="text-sm text-gray-500">本周六 10:00 · 4/8 人已报名</div>
          <div class="mt-2 flex gap-2">
            <el-tag size="small">水上运动</el-tag>
            <el-tag size="small" type="warning">需预约</el-tag>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup></script>