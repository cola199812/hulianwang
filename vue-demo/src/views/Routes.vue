<template>
  <div class="routes-page">
    <div class="page-header">
      <h1 class="page-title">🧭 路线发现</h1>
      <div class="header-actions">
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

    <el-tabs v-model="activeTab" type="border-card" class="custom-tabs" @tab-change="handleTabChange">
      <el-tab-pane label="路线发现" name="discover">
        <el-table :data="discoverPaged" v-loading="loadingRoutes" style="width: 100%;">
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
        <div v-if="!loadingRoutes && filtered.length === 0" style="margin-top: 16px;">
          <el-empty description="暂无路线" />
        </div>
        <div v-if="filtered.length > pageSize" style="margin-top: 12px; display:flex; justify-content:flex-end;">
          <el-pagination
            v-model:current-page="discoverPage"
            :page-size="pageSize"
            :total="filtered.length"
            layout="prev, pager, next"
          />
        </div>
      </el-tab-pane>
      
      <el-tab-pane label="我发布的路线" name="my-routes">
        <div v-if="myRoutes.length > 0">
          <el-table :data="myPaged" v-loading="loadingMyRoutes" style="width: 100%;">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="name" label="路线名" />
            <el-table-column prop="distance" label="距离" />
            <el-table-column prop="level" label="难度" />
            <el-table-column prop="description" label="描述" />
            <el-table-column label="操作" width="200">
              <template #default="{ row }">
                <el-button size="small" @click="viewDetail(row.id)">详情</el-button>
                <el-button size="small" type="primary" @click="editRoute(row)">编辑</el-button>
                <el-button size="small" type="danger" @click="confirmDelete(row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="myRoutes.length > pageSize" style="margin-top: 12px; display:flex; justify-content:flex-end;">
            <el-pagination
              v-model:current-page="myPage"
              :page-size="pageSize"
              :total="myRoutes.length"
              layout="prev, pager, next"
            />
          </div>
        </div>
        <div v-else-if="loadingMyRoutes" style="margin-top: 16px;">
          <el-skeleton :rows="6" animated />
        </div>
        <el-empty v-else description="您还没有发布任何路线" />
      </el-tab-pane>
    </el-tabs>

    <el-dialog v-model="detailVisible" title="路线详情" width="720">
      <el-skeleton v-if="detailLoading" :rows="8" animated />
      <div v-else-if="detail">
        <div style="display:flex; justify-content:space-between; align-items:flex-start; gap:12px;">
          <div>
            <div style="font-size:18px; font-weight:600;">{{ detail.name }}</div>
            <div style="color:#666; font-size:12px; margin-top:4px;">
              {{ detail.distance }} km · {{ detail.level }}
            </div>
          </div>
          <el-button :loading="favoriteLoading" :type="favorited ? 'success' : 'primary'" plain @click="toggleFavorite">
            {{ favorited ? '已收藏' : '收藏' }}
          </el-button>
        </div>
        <div style="margin-top:12px; color:#333; line-height:1.6;">
          {{ detail.description }}
        </div>

        <div style="margin-top:16px;">
          <div style="display:flex; align-items:center; gap:8px;">
            <div style="font-weight:600;">评分</div>
            <el-rate :model-value="avgRating" disabled allow-half />
            <div style="color:#666; font-size:12px;">{{ avgRating }} ({{ totalRatings }})</div>
          </div>
          <div style="display:flex; flex-direction:column; gap:6px; margin-top:10px;">
            <div v-for="n in [5,4,3,2,1]" :key="n" style="display:flex; align-items:center; gap:8px;">
              <div style="width:32px; text-align:right; font-size:12px; color:#666;">{{ n }}星</div>
              <el-progress
                :percentage="totalRatings ? Math.round((Number(ratingStats[String(n)] || 0) / totalRatings) * 100) : 0"
                :stroke-width="10"
              />
              <div style="width:40px; text-align:right; font-size:12px; color:#666;">
                {{ ratingStats[String(n)] || 0 }}
              </div>
            </div>
          </div>
        </div>

        <div style="margin-top:16px;">
          <div style="font-weight:600; margin-bottom:8px;">写点评</div>
          <div style="display:flex; align-items:center; justify-content:space-between; gap:12px;">
            <el-rate v-model="commentForm.rating" />
            <el-button type="primary" :loading="commentSubmitting" @click="submitComment">发布</el-button>
          </div>
          <el-input
            v-model="commentForm.content"
            type="textarea"
            :rows="3"
            placeholder="说说这条路线怎么样…"
            style="margin-top:8px;"
          />
        </div>

        <div style="margin-top:16px;">
          <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:8px;">
            <div style="font-weight:600;">评论 ({{ comments.length }})</div>
            <el-button text :loading="commentsLoading" @click="viewDetail(detail.id)">刷新</el-button>
          </div>
          <el-skeleton v-if="commentsLoading" :rows="6" animated />
          <el-empty v-else-if="comments.length === 0" description="暂无评论" :image-size="80" />
          <div v-else style="display:flex; flex-direction:column; gap:10px;">
            <div v-for="c in comments" :key="c.id" style="border:1px solid #eee; border-radius:8px; padding:10px;">
              <div style="display:flex; justify-content:space-between; align-items:flex-start; gap:12px;">
                <div style="display:flex; align-items:center; gap:10px;">
                  <el-avatar :size="32" :src="c.userAvatar || ''">{{ (c.userName || 'U').slice(0,1) }}</el-avatar>
                  <div>
                    <div style="font-weight:600;">{{ c.userName || '用户' }}</div>
                    <div style="color:#999; font-size:12px;">{{ formatTime(c.createTime) }}</div>
                  </div>
                </div>
                <div style="display:flex; align-items:center; gap:8px;">
                  <el-rate :model-value="Number(c.rating || 0)" disabled />
                  <el-button size="small" @click="likeComment(c)">👍 {{ c.likeCount || 0 }}</el-button>
                </div>
              </div>
              <div style="margin-top:8px; color:#333; line-height:1.6;">
                {{ c.content }}
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else>
        <el-empty description="未找到该路线" />
      </div>
    </el-dialog>

    <!-- 编辑路线对话框 -->
    <el-dialog v-model="editVisible" title="编辑路线" width="600">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="路线名">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="距离(km)">
          <el-input v-model.number="editForm.distance" type="number" />
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="editForm.level" style="width: 200px;">
            <el-option label="简单" value="简单" />
            <el-option label="中等" value="中等" />
            <el-option label="困难" value="困难" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" type="textarea" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">保存</el-button>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog v-model="deleteVisible" title="确认删除" width="400">
      <p>确定要删除路线"{{ currentRoute.name }}"吗？此操作不可撤销。</p>
      <template #footer>
        <el-button @click="deleteVisible = false">取消</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
    </el-dialog>
    
    <!-- 精选路线部分 - 只在路线发现标签页显示 -->
    <div v-if="activeTab === 'discover' && !loadingRoutes && filtered.length > 0" class="featured-section">
      <h2 class="section-title">精选路线</h2>
      <div class="route-grid">
        <div v-for="r in filtered.slice(0,6)" :key="r.id" class="route-card">
          <div class="route-cover">{{ (r.name || '').slice(0,1) }}</div>
          <div class="route-title">{{ r.name }}</div>
          <div class="route-meta">{{ r.distance }} km · {{ r.level }}</div>
          <el-button size="small" @click="viewDetail(r.id)">查看</el-button>
        </div>
      </div>
    </div>

    <!-- 我的路线统计 -->
    <div v-if="activeTab === 'my-routes'" class="stats-section">
      <el-card class="stats-card">
        <div style="display:flex; justify-content:space-between; align-items:center;">
          <div>
            <h3 class="stats-title">我的路线统计</h3>
            <p class="stats-desc">您已发布 {{ myRoutes.length }} 条路线</p>
          </div>
          <el-button type="primary" @click="$router.push('/routes/create')">
            <el-icon><Plus /></el-icon>
            发布新路线
          </el-button>
        </div>
        
        <div v-if="myRoutes.length > 0" class="stats-list">
          <div class="route-grid">
            <div v-for="r in myRoutes.slice(0,6)" :key="r.id" class="route-card">
              <div class="route-cover">{{ (r.name || '').slice(0,1) }}</div>
              <div class="route-title">{{ r.name }}</div>
              <div class="route-meta">{{ r.distance }} km · {{ r.level }}</div>
              <div class="card-actions">
                <el-button size="small" @click="viewDetail(r.id)">查看</el-button>
                <el-button size="small" type="primary" @click="editRoute(r)">编辑</el-button>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import {
  listRoutes,
  getRoute,
  listMyRoutes,
  updateRoute,
  deleteRoute,
  getRouteComments,
  getRouteRatingStats,
  addRouteComment,
  likeRouteComment,
  toggleRouteFavorite,
  checkRouteFavorite
} from '../api/route'
import { userInfo } from '../api/user'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const route = useRoute()

const routes = ref([])
const myRoutes = ref([])
const loadingRoutes = ref(false)
const loadingMyRoutes = ref(false)

const activeTab = ref('discover')

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

const pageSize = ref(10)
const discoverPage = ref(1)
const myPage = ref(1)

watch(filtered, () => {
  discoverPage.value = 1
})
watch(myRoutes, () => {
  myPage.value = 1
})

const discoverPaged = computed(() => {
  const start = (discoverPage.value - 1) * pageSize.value
  return filtered.value.slice(start, start + pageSize.value)
})

const myPaged = computed(() => {
  const start = (myPage.value - 1) * pageSize.value
  return myRoutes.value.slice(start, start + pageSize.value)
})

function reset() {
  filters.keyword = ''
  filters.level = ''
  filters.min = null
  filters.max = null
}

function initializeActiveTab() {
  activeTab.value = route.query.tab === 'my-routes' ? 'my-routes' : 'discover'
}

watch(
  () => route.query.tab,
  () => {
    initializeActiveTab()
    if (activeTab.value === 'my-routes') loadMyRoutes()
  }
)

async function loadRoutes() {
  loadingRoutes.value = true
  try {
    const { data } = await listRoutes()
    routes.value = data || []
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '获取路线列表失败')
    routes.value = []
  } finally {
    loadingRoutes.value = false
  }
}

async function loadMyRoutes() {
  loadingMyRoutes.value = true
  try {
    const { data } = await listMyRoutes()
    myRoutes.value = data || []
  } catch (error) {
    myRoutes.value = []
    if (error.response?.status !== 401) {
      ElMessage.error(error.response?.data?.message || '获取我的路线失败')
    }
  } finally {
    loadingMyRoutes.value = false
  }
}

function handleTabChange(tabName) {
  if (tabName === 'my-routes') {
    loadMyRoutes()
  }
}

const detailVisible = ref(false)
const detailLoading = ref(false)
const detail = ref(null)
const ratingStats = ref({ '1': 0, '2': 0, '3': 0, '4': 0, '5': 0 })
const commentsLoading = ref(false)
const comments = ref([])
const favorited = ref(false)
const favoriteLoading = ref(false)

const currentUser = ref(null)
const commentSubmitting = ref(false)
const commentForm = reactive({
  rating: 5,
  content: ''
})

const totalRatings = computed(() => {
  return Object.values(ratingStats.value || {}).reduce((sum, x) => sum + Number(x || 0), 0)
})

const avgRating = computed(() => {
  const total = totalRatings.value
  if (!total) return 0
  const weighted =
    1 * Number(ratingStats.value['1'] || 0) +
    2 * Number(ratingStats.value['2'] || 0) +
    3 * Number(ratingStats.value['3'] || 0) +
    4 * Number(ratingStats.value['4'] || 0) +
    5 * Number(ratingStats.value['5'] || 0)
  return Math.round((weighted / total) * 10) / 10
})

async function ensureCurrentUser() {
  if (currentUser.value) return currentUser.value
  try {
    const { data } = await userInfo()
    currentUser.value = data || null
    return currentUser.value
  } catch {
    currentUser.value = null
    return null
  }
}

function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  if (isNaN(date.getTime())) return String(time)
  return date.toLocaleString()
}

async function viewDetail(id) {
  detailVisible.value = true
  detailLoading.value = true
  commentsLoading.value = true
  try {
    const [rRes, sRes, cRes, fRes] = await Promise.allSettled([
      getRoute(id),
      getRouteRatingStats(id),
      getRouteComments(id),
      checkRouteFavorite(id)
    ])

    detail.value = rRes.status === 'fulfilled' ? rRes.value.data : null
    ratingStats.value =
      sRes.status === 'fulfilled'
        ? (sRes.value.data || { '1': 0, '2': 0, '3': 0, '4': 0, '5': 0 })
        : { '1': 0, '2': 0, '3': 0, '4': 0, '5': 0 }
    comments.value = cRes.status === 'fulfilled' ? (cRes.value.data || []) : []
    favorited.value = fRes.status === 'fulfilled' ? !!fRes.value.data?.favorited : false
  } catch {
    ElMessage.error('获取路线详情失败')
    detail.value = null
  } finally {
    detailLoading.value = false
    commentsLoading.value = false
  }
}

async function toggleFavorite() {
  if (!detail.value?.id) return
  favoriteLoading.value = true
  try {
    await toggleRouteFavorite(detail.value.id)
    const { data } = await checkRouteFavorite(detail.value.id)
    favorited.value = !!data?.favorited
    ElMessage.success('操作成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '操作失败')
  } finally {
    favoriteLoading.value = false
  }
}

async function submitComment() {
  if (!detail.value?.id) return
  if (!commentForm.content.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  commentSubmitting.value = true
  try {
    const u = await ensureCurrentUser()
    await addRouteComment(detail.value.id, {
      userName: u?.nickname || u?.username || '用户',
      userAvatar: u?.avatarUrl || '',
      content: commentForm.content.trim(),
      rating: Number(commentForm.rating || 5)
    })
    commentForm.content = ''
    commentForm.rating = 5
    const [sRes, cRes] = await Promise.allSettled([getRouteRatingStats(detail.value.id), getRouteComments(detail.value.id)])
    ratingStats.value =
      sRes.status === 'fulfilled'
        ? (sRes.value.data || { '1': 0, '2': 0, '3': 0, '4': 0, '5': 0 })
        : ratingStats.value
    comments.value = cRes.status === 'fulfilled' ? (cRes.value.data || []) : comments.value
    ElMessage.success('评论成功')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '评论失败')
  } finally {
    commentSubmitting.value = false
  }
}

async function likeComment(item) {
  try {
    await likeRouteComment(item.id)
    item.likeCount = Number(item.likeCount || 0) + 1
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '点赞失败')
  }
}

const editVisible = ref(false)
const deleteVisible = ref(false)
const editForm = ref({})
const currentRoute = ref({})

function editRoute(item) {
  currentRoute.value = item
  editForm.value = { ...item }
  editVisible.value = true
}

async function handleUpdate() {
  if (!currentRoute.value?.id) return
  try {
    await updateRoute(currentRoute.value.id, editForm.value)
    ElMessage.success('更新成功')
    editVisible.value = false
    await Promise.allSettled([loadRoutes(), activeTab.value === 'my-routes' ? loadMyRoutes() : Promise.resolve()])
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '更新失败')
  }
}

function confirmDelete(item) {
  currentRoute.value = item
  deleteVisible.value = true
}

async function handleDelete() {
  if (!currentRoute.value?.id) return
  try {
    await deleteRoute(currentRoute.value.id)
    ElMessage.success('删除成功')
    deleteVisible.value = false
    routes.value = routes.value.filter(r => r.id !== currentRoute.value.id)
    myRoutes.value = myRoutes.value.filter(r => r.id !== currentRoute.value.id)
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '删除失败')
  }
}

onMounted(async () => {
  initializeActiveTab()
  await loadRoutes()
  if (activeTab.value === 'my-routes') {
    await loadMyRoutes()
  }
})
</script>

<style scoped>
.routes-page { padding: 20px; max-width: 1000px; margin: 0 auto; }
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; padding: 16px 20px; background: rgba(255,255,255,0.7); backdrop-filter: blur(10px); border-radius: 16px; box-shadow: 0 4px 16px rgba(39,174,96,0.08); border: 1px solid #c8e6c9; }
.page-title { color: #27ae60; font-weight: 700; margin: 0; }
.header-actions { display:flex; gap:8px; }
.custom-tabs { border-radius: 16px; overflow: hidden; box-shadow: 0 8px 24px rgba(39,174,96,0.08); background: rgba(255,255,255,0.7); backdrop-filter: blur(10px); border: 1px solid #c8e6c9; }
:deep(.el-tabs__header) { background: rgba(255,255,255,0.5); margin:0; border-radius: 16px 16px 0 0; border-bottom: 1px solid #c8e6c9; }
:deep(.el-tabs__item.is-active) { color: #27ae60; font-weight: 600; background: rgba(39,174,96,0.08); }
:deep(.el-tabs__active-bar) { background-color: #27ae60; height: 3px; }
:deep(.el-tabs__nav-wrap::after) { background-color: #e8f5e9; }
.featured-section { margin-top:16px; }
.section-title { font-weight:600; margin-bottom:8px; color:#27ae60; }
.route-grid { display:grid; grid-template-columns:repeat(3,1fr); gap:12px; }
.route-card { border: none; border-radius:10px; padding:12px; display:flex; flex-direction:column; gap:6px; box-shadow: 0 4px 16px rgba(39,174,96,0.08); background: #fff; }
.route-cover { width:100%; height:120px; border-radius:8px; background:linear-gradient(135deg,#27ae60,#2ecc71); color:#fff; display:flex; align-items:center; justify-content:center; font-size:28px; font-weight:700; }
.route-title { font-weight:600; }
.route-meta { color:#666; font-size:12px; }
.card-actions { display:flex; gap:4px; margin-top:8px; }
.stats-section { margin-top:16px; }
.stats-card { border: 1px solid #c8e6c9; }
.stats-title { font-weight:600; }
.stats-desc { color:#666; margin-top:4px; }
.stats-list { margin-top:16px; }
@media (max-width: 768px) { .route-grid { grid-template-columns:repeat(2,1fr); } }
@media (max-width: 480px) { .route-grid { grid-template-columns:repeat(1,1fr); } }
</style>
