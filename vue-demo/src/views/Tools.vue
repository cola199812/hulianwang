<template>
  <div class="p-4" style="max-width: 1000px; margin: 0 auto;">
    <div style="display:flex; justify-content:space-between; align-items:center;">
      <h1 class="text-xl font-bold mb-4">工具服务</h1>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
    <div class="tool-grid">
      <el-card class="tool-card" @click="$router.push('/routes')">
        <div class="tool-icon">🗺️</div>
        <div class="tool-title">离线地图</div>
        <div class="tool-desc">保存常用区域地图，支持无信号导航</div>
      </el-card>
      <el-card class="tool-card" @click="$router.push('/activities')">
        <div class="tool-icon">📍</div>
        <div class="tool-title">轨迹记录</div>
        <div class="tool-desc">轨迹采样与导出 GPX/KML</div>
      </el-card>
      <el-card class="tool-card">
        <div class="tool-icon">☀️</div>
        <div class="tool-title">天气预报</div>
        <div class="tool-desc">多日天气与风险提示</div>
      </el-card>
      <el-card class="tool-card">
        <div class="tool-icon">🚨</div>
        <div class="tool-title">紧急求助</div>
        <div class="tool-desc">一键分享坐标与状态</div>
      </el-card>
      
    </div>
  </div>
</template>

<script setup></script>

<style scoped>
.tool-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:12px; }
.tool-card { cursor:pointer; }
.tool-icon { font-size:28px; }
.tool-title { font-weight:600; margin-top:6px; }
.tool-desc { color:#666; font-size:12px; margin-top:4px; }
@media (max-width: 768px) { .tool-grid { grid-template-columns:repeat(2,1fr); } }
@media (max-width: 480px) { .tool-grid { grid-template-columns:repeat(1,1fr); } }
</style>
