<template>
  <div>
    <el-menu v-if="showNav" mode="horizontal" :default-active="active" router>
      <el-menu-item index="/home">首页</el-menu-item>
      <el-menu-item index="/discover">路线发现</el-menu-item>
      <el-menu-item index="/social">活动社交</el-menu-item>
      <el-menu-item index="/creation">内容创作</el-menu-item>
      <el-menu-item index="/gear">装备服务</el-menu-item>
      <el-menu-item index="/tools">工具服务</el-menu-item>
      <el-menu-item index="/notification">通知中心</el-menu-item>
      <el-menu-item index="/profile">我的</el-menu-item>
    </el-menu>
    <div style="padding: 16px;">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
const route = useRoute()
const active = computed(() => route.path)
const showNav = computed(() => !['/login', '/', '/reset-password'].includes(route.path))
</script>

