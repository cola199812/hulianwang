<template>
  <div style="max-width: 900px; margin: 40px auto;">
    <el-card>
      <div style="display:flex; justify-content:space-between; align-items:center;">
        <h2>路线列表</h2>
        <div>
          <el-button type="primary" @click="$router.push('/routes/create')">创建路线</el-button>
          <el-button @click="$router.push('/activities')">活动列表</el-button>
        </div>
      </div>
      <el-form :model="filters" inline label-width="80px" style="margin:12px 0;">
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
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { listRoutes, getRoute } from '../api/route'

const routes = ref([])
const detailVisible = ref(false)
const detail = ref(null)
const filters = ref({ keyword:'', level:'' })

async function load() {
  const { data } = await listRoutes()
  routes.value = data
}

async function viewDetail(id) {
  const { data } = await getRoute(id)
  detail.value = data
  detailVisible.value = true
}

onMounted(load)

const filtered = computed(() => {
  return routes.value.filter(r => {
    const byKeyword = !filters.value.keyword || (r.name || '').toLowerCase().includes(filters.value.keyword.toLowerCase())
    const byLevel = !filters.value.level || r.level === filters.value.level
    return byKeyword && byLevel
  })
})

function reset() {
  filters.value.keyword = ''
  filters.value.level = ''
}
</script>
