<template>
  <div class="p-4" style="max-width: 1000px; margin: 0 auto;">
    <div style="display:flex; justify-content:space-between; align-items:center;">
      <h1 class="text-xl font-bold mb-4">路线发现</h1>
      <div style="display:flex; gap:8px;">
        <el-button type="primary" @click="$router.push('/routes/create')">创建路线</el-button>
        <el-button @click="$router.push('/routes')">路线列表</el-button>
      </div>
    </div>

    <el-form :model="filters" inline label-width="80px" class="mb-4">
      <el-form-item label="关键词">
        <el-input v-model="filters.keyword" placeholder="按名称搜索" clearable />
      </el-form-item>
      <el-form-item label="难度">
        <el-select v-model="filters.level" placeholder="全部" clearable style="width: 120px;">
          <el-option label="简单" value="简单" />
          <el-option label="中等" value="中等" />
          <el-option label="困难" value="困难" />
        </el-select>
      </el-form-item>
      <el-form-item label="距离(km)">
        <div style="display:flex; gap:8px; align-items:center;">
          <el-input v-model.number="filters.min" type="number" placeholder="最小" style="width: 100px;" />
          <span>-</span>
          <el-input v-model.number="filters.max" type="number" placeholder="最大" style="width: 100px;" />
        </div>
      </el-form-item>
      <el-form-item>
        <el-button @click="reset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="filtered" style="width: 100%;">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="路线名" />
      <el-table-column prop="distance" label="距离" />
      <el-table-column prop="level" label="难度" />
      <el-table-column prop="description" label="描述" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button size="small" @click="viewDetail(row.id)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="detailVisible" title="路线详情" width="600">
      <div v-if="detail">
        <p><b>ID：</b>{{ detail.id }}</p>
        <p><b>路线名：</b>{{ detail.name }}</p>
        <p><b>距离：</b>{{ detail.distance }}</p>
        <p><b>难度：</b>{{ detail.level }}</p>
        <p><b>描述：</b>{{ detail.description }}</p>
      </div>
    </el-dialog>
    
    <div style="margin-top:16px;">
      <h2 class="text-lg font-bold mb-3">精选路线</h2>
      <div class="route-grid">
        <div v-for="r in filtered.slice(0,6)" :key="r.id" class="route-card">
          <div class="route-cover">{{ (r.name || '').slice(0,1) }}</div>
          <div class="route-title">{{ r.name }}</div>
          <div class="route-meta">{{ r.distance }} km · {{ r.level }}</div>
          <el-button size="small" @click="viewDetail(r.id)">查看</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { listRoutes, getRoute } from '../api/route'

const routes = ref([])
const detailVisible = ref(false)
const detail = ref(null)

const filters = reactive({
  keyword: '',
  level: '',
  min: null,
  max: null
})

const filtered = computed(() => {
  return routes.value.filter(r => {
    const byKeyword = !filters.keyword || (r.name || '').toLowerCase().includes(filters.keyword.toLowerCase())
    const byLevel = !filters.level || r.level === filters.level
    const byMin = filters.min == null || Number(r.distance) >= Number(filters.min)
    const byMax = filters.max == null || Number(r.distance) <= Number(filters.max)
    return byKeyword && byLevel && byMin && byMax
  })
})

function reset() {
  filters.keyword = ''
  filters.level = ''
  filters.min = null
  filters.max = null
}

async function load() {
  const { data } = await listRoutes()
  routes.value = data || []
}

async function viewDetail(id) {
  const { data } = await getRoute(id)
  detail.value = data
  detailVisible.value = true
}

onMounted(load)
</script>

<style scoped>
.route-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:12px; }
.route-card { border:1px solid #eee; border-radius:10px; padding:12px; display:flex; flex-direction:column; gap:6px; }
.route-cover { width:100%; height:120px; border-radius:8px; background:linear-gradient(135deg,#8bd,#5ac); color:#fff; display:flex; align-items:center; justify-content:center; font-size:28px; font-weight:700; }
.route-title { font-weight:600; }
.route-meta { color:#666; font-size:12px; }
@media (max-width: 768px) { .route-grid { grid-template-columns:repeat(2,1fr); } }
@media (max-width: 480px) { .route-grid { grid-template-columns:repeat(1,1fr); } }
</style>
