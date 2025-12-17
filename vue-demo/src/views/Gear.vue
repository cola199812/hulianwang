<template>
  <div class="p-4" style="max-width: 1000px; margin: 0 auto;">
    <div style="display:flex; justify-content:space-between; align-items:center;">
      <h1 class="text-xl font-bold mb-4">装备服务</h1>
      <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
    </div>
    <div class="grid">
      <div class="left">
        <div class="section-head">
          <div class="title">热门装备</div>
        </div>
        <div class="gear-grid">
          <div v-for="g in gears" :key="g.id" class="gear-card">
            <div class="gear-cover">{{ (g.name || '').slice(0,1) }}</div>
            <div class="gear-title">{{ g.name }}</div>
            <div class="gear-meta">{{ g.type }} · {{ g.weight }} g</div>
            <el-button size="small">加入清单</el-button>
          </div>
        </div>
      </div>
      <div class="right">
        <el-card>
          <div class="title">我的清单</div>
          <div class="sidebar-list">
            <div v-for="i in gears.slice(0,5)" :key="'m'+i.id" class="sidebar-item">
              <div class="name">{{ i.name }}</div>
              <div class="meta">{{ i.type }}</div>
            </div>
          </div>
          <el-button size="small">导出清单</el-button>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
const gears = ref([
  { id:1, name:'轻量帐篷', type:'帐篷', weight:980 },
  { id:2, name:'羽绒睡袋', type:'睡袋', weight:720 },
  { id:3, name:'登山杖', type:'器材', weight:420 },
  { id:4, name:'保温水壶', type:'器材', weight:350 },
  { id:5, name:'头灯', type:'照明', weight:90 },
  { id:6, name:'徒步鞋', type:'鞋靴', weight:1100 }
])
</script>

<style scoped>
.grid { display:grid; grid-template-columns:2fr 1fr; gap:16px; }
.section-head { display:flex; align-items:center; justify-content:space-between; margin:12px 0; }
.title { font-weight:600; }
.gear-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:12px; }
.gear-card { border:1px solid #eee; border-radius:10px; padding:12px; display:flex; flex-direction:column; gap:6px; }
.gear-cover { width:100%; height:120px; border-radius:8px; background:linear-gradient(135deg,#bdf,#7ac); color:#fff; display:flex; align-items:center; justify-content:center; font-size:28px; font-weight:700; }
.gear-title { font-weight:600; }
.gear-meta { color:#666; font-size:12px; }
.sidebar-list { display:flex; flex-direction:column; gap:8px; margin:8px 0; }
.sidebar-item .name { font-weight:600; }
.sidebar-item .meta { color:#666; font-size:12px; }
@media (max-width: 768px) { .grid { grid-template-columns:1fr; } .gear-grid { grid-template-columns:repeat(2,1fr); } }
</style>
