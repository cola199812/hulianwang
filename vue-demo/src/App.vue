<template>
  <div class="layout">
    <!-- 顶部导航 -->
    <el-menu
      v-if="showNav"
      mode="horizontal"
      :default-active="active"
      router
      class="top-nav"
    >
      <el-menu-item index="/home">🏠 首页</el-menu-item>
      <el-menu-item index="/discover">🧭 路线发现</el-menu-item>
      <el-menu-item index="/social">🤝 活动社交</el-menu-item>
      <el-menu-item index="/creation">✍️ 内容创作</el-menu-item>
      <el-menu-item index="/gear">🎒 装备服务</el-menu-item>
      <el-menu-item index="/tools">🛠️ 工具服务</el-menu-item>
      <el-menu-item index="/notification">🔔 通知</el-menu-item>
      <el-menu-item index="/profile">👤 我的</el-menu-item>
    </el-menu>

    <!-- 页面内容 -->
    <div class="page-container">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const active = computed(() => route.path)
const showNav = computed(
  () => !['/login', '/', '/reset-password'].includes(route.path)
)
</script>

<style scoped>
/* 整体布局 */
.layout {
  min-height: 100vh;
  background: linear-gradient(180deg, #f2fbf5, #ffffff);
}

/* 顶部导航栏 */
.top-nav {
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: none;
  padding: 0 24px;
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(10px);
  box-shadow: 0 4px 12px rgba(39, 174, 96, 0.08);
}

/* 菜单项 */
.top-nav :deep(.el-menu-item) {
  font-weight: 600;
  color: #2c3e50;
  border-radius: 20px;
  margin: 8px 6px;
  transition: all 0.2s ease;
}

/* hover 效果 */
.top-nav :deep(.el-menu-item:hover) {
  background-color: rgba(39, 174, 96, 0.12);
  color: #27ae60;
}

/* 激活状态 */
.top-nav :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, #2ecc71, #27ae60);
  color: #fff;
  box-shadow: 0 4px 10px rgba(39, 174, 96, 0.35);
}

/* 页面内容区域 */
.page-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 移动端优化 */
@media (max-width: 768px) {
  .top-nav {
    padding: 0 8px;
  }
  .top-nav :deep(.el-menu-item) {
    margin: 6px 4px;
    font-size: 14px;
  }
}
</style>
