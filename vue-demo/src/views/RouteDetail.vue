<template>
  <div class="route-detail" style="max-width: 1000px; margin: 20px auto; padding: 0 20px;">
    <!-- 页面头部 -->
    <div class="header" style="display:flex; justify-content:space-between; align-items:center; margin-bottom: 20px;">
      <div style="display:flex; align-items:center; gap: 12px;">
        <el-button icon="el-icon-back" @click="$router.go(-1)">返回</el-button>
        <h1 style="margin: 0; font-size: 24px; font-weight: bold;">路线详情</h1>
      </div>
      <div style="display:flex; gap: 8px;">
        <el-button type="primary" @click="startRoute" :disabled="!routeInfo">
          <el-icon><Location /></el-icon>
          开始导航
        </el-button>
        <el-button @click="toggleFavorite" :disabled="!routeInfo">
          <el-icon><Star :class="{ favorited: isFavorited }" /></el-icon>
          {{ isFavorited ? '已收藏' : '收藏' }}
        </el-button>
        <el-button @click="shareRoute" :disabled="!routeInfo">
          <el-icon><Share /></el-icon>
          分享
        </el-button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" style="text-align:center; padding: 40px;">
      <el-icon class="is-loading" style="font-size: 32px;"><Loading /></el-icon>
      <p>加载中...</p>
    </div>

    <!-- 路线详情内容 -->
    <div v-else-if="routeInfo" class="route-content">
      <!-- 基本信息卡片 -->
      <el-card style="margin-bottom: 20px;">
        <div style="display:flex; justify-content:space-between; align-items:flex-start;">
          <div style="flex: 1;">
            <h2 style="margin: 0 0 16px 0; font-size: 20px; color: #333;">{{ routeInfo.name }}</h2>
            
            <!-- 路线统计信息 -->
            <div style="display:flex; gap: 24px; margin-bottom: 16px; flex-wrap: wrap;">
              <div class="stat-item">
                <div class="stat-label">距离</div>
                <div class="stat-value">{{ routeInfo.distance }} km</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">难度</div>
                <div class="stat-value">
                  <el-tag :type="getDifficultyType(routeInfo.level)">{{ routeInfo.level }}</el-tag>
                </div>
              </div>
              <div class="stat-item">
                <div class="stat-label">预计时间</div>
                <div class="stat-value">{{ estimatedTime }} 小时</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">热度</div>
                <div class="stat-value">{{ routeInfo.hotScore || 0 }}</div>
              </div>
            </div>

            <!-- 路线描述 -->
            <div v-if="routeInfo.description" style="margin-bottom: 16px;">
              <h3 style="margin: 0 0 8px 0; font-size: 16px; color: #666;">路线描述</h3>
              <p style="line-height: 1.6; color: #555; margin: 0;">{{ routeInfo.description }}</p>
            </div>

            <!-- 路线标签 -->
            <div v-if="routeInfo.tags && routeInfo.tags.length > 0" style="margin-bottom: 16px;">
              <h3 style="margin: 0 0 8px 0; font-size: 16px; color: #666;">路线标签</h3>
              <div style="display:flex; gap: 8px; flex-wrap: wrap;">
                <el-tag v-for="tag in routeInfo.tags" :key="tag" type="info">{{ tag }}</el-tag>
              </div>
            </div>
          </div>

          <!-- 路线图片 -->
          <div v-if="routeInfo.images && routeInfo.images.length > 0" style="margin-left: 20px;">
            <el-image 
              :src="routeInfo.images[0]" 
              style="width: 200px; height: 150px; border-radius: 8px;"
              fit="cover"
              @click="showImageViewer = true"
            />
          </div>
        </div>
      </el-card>

      <!-- 地图区域 -->
      <el-card style="margin-bottom: 20px;">
        <template #header>
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <span>路线地图</span>
          </div>
        </template>
        
        <RouteMap 
          :route-id="route.params.id"
          :route-info="routeInfo"
          height="400px"
        />
      </el-card>

      <!-- 路线评价和评论 -->
      <el-card>
        <template #header>
          <div style="display:flex; justify-content:space-between; align-items:center;">
            <span>路线评价 ({{ totalComments }}条评论)</span>
            <el-button type="primary" size="small" @click="showCommentDialog = true">
              写评价
            </el-button>
          </div>
        </template>

        <!-- 总体评分 -->
        <div style="display:flex; align-items:center; gap: 20px; margin-bottom: 20px; padding: 16px; background: #f9f9f9; border-radius: 8px;">
          <div style="text-align:center;">
            <div style="font-size: 32px; font-weight: bold; color: #409EFF;">{{ averageRating }}</div>
            <div style="color: #666; font-size: 14px;">平均评分</div>
          </div>
          <div style="flex: 1;">
            <div v-for="i in 5" :key="i" style="display:flex; align-items:center; gap: 8px; margin-bottom: 4px;">
              <span style="width: 20px; text-align:right;">{{ i }}</span>
              <el-rate 
                :model-value="i" 
                disabled 
                show-score
                text-color="#99A9BF"
                score-template="{value}"
                style="margin-right: 8px;"
              />
              <div style="flex: 1; height: 8px; background: #eee; border-radius: 4px; overflow: hidden;">
                <div 
                  :style="{ 
                    width: `${(ratingCounts[i] / totalComments * 100) || 0}%`, 
                    height: '100%', 
                    background: '#409EFF' 
                  }"
                ></div>
              </div>
              <span style="width: 40px; text-align:left; color: #666; font-size: 14px;">{{ ratingCounts[i] || 0 }}</span>
            </div>
          </div>
        </div>

        <!-- 评论列表 -->
        <div v-if="comments.length > 0">
          <div v-for="comment in comments" :key="comment.id" class="comment-item" style="padding: 16px; border-bottom: 1px solid #eee;">
            <div style="display:flex; justify-content:space-between; align-items:flex-start; margin-bottom: 8px;">
              <div style="display:flex; align-items:center; gap: 12px;">
                <el-avatar :size="32" :src="comment.userAvatar">{{ comment.username?.[0] }}</el-avatar>
                <div>
                  <div style="font-weight: 500; color: #333;">{{ comment.username || '匿名用户' }}</div>
                  <div style="font-size: 12px; color: #999;">{{ formatDate(comment.createTime) }}</div>
                </div>
              </div>
              <el-rate :model-value="comment.rating" disabled size="small" />
            </div>
            <p style="margin: 8px 0; line-height: 1.6; color: #555;">{{ comment.content }}</p>
            <div style="display:flex; gap: 16px; align-items:center;">
              <el-button size="small" text @click="likeComment(comment.id)">
                <el-icon><Upload /></el-icon>
                点赞 ({{ comment.likes || 0 }})
              </el-button>
              <el-button size="small" text @click="replyComment(comment.id)">
                <el-icon><ChatLineRound /></el-icon>
                回复
              </el-button>
            </div>
          </div>
        </div>
        <div v-else style="text-align:center; padding: 40px; color: #999;">
          暂无评价，成为第一个评价的用户吧！
        </div>
      </el-card>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" style="text-align:center; padding: 40px;">
      <el-icon style="font-size: 48px; color: #F56C6C; margin-bottom: 16px;"><Warning /></el-icon>
      <p style="color: #F56C6C; margin-bottom: 16px;">{{ error }}</p>
      <el-button @click="loadRoute">重试</el-button>
    </div>

    <!-- 评价对话框 -->
    <el-dialog v-model="showCommentDialog" title="写评价" width="500">
      <el-form :model="commentForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="commentForm.rating" :max="5" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input 
            v-model="commentForm.content" 
            type="textarea" 
            :rows="4" 
            placeholder="分享您的路线体验..."
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showCommentDialog = false">取消</el-button>
        <el-button type="primary" @click="submitComment">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getRoute, getRouteComments, getRouteRatingStats, addRouteComment, likeRouteComment, toggleRouteFavorite, checkRouteFavorite } from '../api/route'
import { userInfo } from '../api/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Location, Star, Share, Loading, Warning, 
  Upload, ChatLineRound 
} from '@element-plus/icons-vue'
import RouteMap from '../components/RouteMap.vue'

const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(true)
  const error = ref(null)
  const isFavorited = ref(false)
  const showImageViewer = ref(false)
  const showCommentDialog = ref(false)

// 路线数据
const routeData = ref(null)
const comments = ref([])
const ratingCounts = ref({ 1: 0, 2: 0, 3: 0, 4: 0, 5: 0 })

// 表单数据
const commentForm = ref({
  rating: 5,
  content: ''
})

// 计算属性
const routeInfo = computed(() => routeData.value)
const totalComments = computed(() => {
  return Object.values(ratingCounts.value).reduce((sum, count) => sum + count, 0)
})

const averageRating = computed(() => {
  const total = totalComments.value
  if (total === 0) return 0
  
  const weightedSum = Object.entries(ratingCounts.value)
    .reduce((sum, [rating, count]) => sum + (parseInt(rating) * count), 0)
  
  return (weightedSum / total).toFixed(1)
})

const estimatedTime = computed(() => {
  if (!routeInfo.value?.distance) return 0
  // 假设平均步行速度 4km/h，考虑难度调整
  const baseSpeed = 4 // km/h
  const difficultyMultiplier = {
    '简单': 1.0,
    '中等': 0.8,
    '困难': 0.6
  }
  const multiplier = difficultyMultiplier[routeInfo.value.level] || 0.8
  return Math.round((routeInfo.value.distance / (baseSpeed * multiplier)) * 10) / 10
})

// 工具函数
const getDifficultyType = (level) => {
  const types = {
    '简单': 'success',
    '中等': 'warning', 
    '困难': 'danger'
  }
  return types[level] || 'info'
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 方法
const loadRoute = async () => {
  loading.value = true
  error.value = null
  
  try {
    const routeId = route.params.id
    const { data } = await getRoute(routeId)
    routeData.value = data
    
    // 检查收藏状态
    try {
      const favoriteRes = await checkRouteFavorite(routeId)
      isFavorited.value = favoriteRes.data?.favorited || false
    } catch (err) {
      console.error('检查收藏状态失败:', err)
      isFavorited.value = false
    }
    
    // 加载评论数据
    await loadComments()
    
  } catch (err) {
    console.error('加载路线详情失败:', err)
    error.value = '路线不存在或加载失败'
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  try {
    const routeId = route.params.id
    const [commentsRes, ratingStatsRes] = await Promise.all([
      getRouteComments(routeId),
      getRouteRatingStats(routeId)
    ])
    
    comments.value = commentsRes.data || []
    ratingCounts.value = {
      1: ratingStatsRes.data['1'] || 0,
      2: ratingStatsRes.data['2'] || 0,
      3: ratingStatsRes.data['3'] || 0,
      4: ratingStatsRes.data['4'] || 0,
      5: ratingStatsRes.data['5'] || 0
    }
  } catch (err) {
    console.error('加载评论失败:', err)
  }
}

const startRoute = () => {
  // TODO: 集成地图导航功能
  ElMessage.info('导航功能待开发')
}

const toggleFavorite = async () => {
  try {
    await toggleRouteFavorite(route.params.id)
    isFavorited.value = !isFavorited.value
    ElMessage.success(isFavorited.value ? '收藏成功' : '取消收藏')
  } catch (err) {
    console.error('收藏操作失败:', err)
    ElMessage.error('操作失败，请重试')
  }
}

const shareRoute = async () => {
  try {
    const shareData = {
      title: routeInfo.value?.name || '路线详情',
      text: routeInfo.value ? `发现一条${routeInfo.value.level}难度的路线，距离${routeInfo.value.distance}km` : '分享一条精彩路线',
      url: window.location.href
    }
    
    if (navigator.share) {
      await navigator.share(shareData)
    } else {
      // 复制链接到剪贴板
      await navigator.clipboard.writeText(window.location.href)
      ElMessage.success('链接已复制到剪贴板')
    }
  } catch (err) {
    console.error('分享失败:', err)
    ElMessage.error('分享失败')
  }
}



const submitComment = async () => {
  if (!commentForm.value.content.trim()) {
    ElMessage.warning('请输入评价内容')
    return
  }
  
  try {
    // 获取当前用户信息
    const userRes = await userInfo()
    const user = userRes.data
    
    const commentData = {
      userName: user.username,
      userAvatar: user.avatar || '/default-avatar.png',
      content: commentForm.value.content,
      rating: commentForm.value.rating
    }
    
    await addRouteComment(route.params.id, commentData)
    
    ElMessage.success('评价提交成功')
    showCommentDialog.value = false
    commentForm.value = { rating: 5, content: '' }
    
    // 重新加载评论
    await loadComments()
    
  } catch (err) {
    console.error('提交评价失败:', err)
    ElMessage.error('提交失败，请重试')
  }
}

const likeComment = async (commentId) => {
  try {
    await likeRouteComment(commentId)
    ElMessage.success('点赞成功')
    
    // 更新评论的点赞数
    const commentIndex = comments.value.findIndex(c => c.id === commentId)
    if (commentIndex !== -1) {
      comments.value[commentIndex].likeCount = (comments.value[commentIndex].likeCount || 0) + 1
    }
  } catch (err) {
    console.error('点赞失败:', err)
    ElMessage.error('点赞失败，请重试')
  }
}

const replyComment = (commentId) => {
  // TODO: 实现回复评论功能
  ElMessage.info('回复功能待开发')
}

// 生命周期
onMounted(() => {
  loadRoute()
})

// 监听路由变化
watch(() => route.params.id, () => {
  if (route.params.id) {
    loadRoute()
  }
})
</script>

<style scoped>
.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  min-width: 80px;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 16px;
  font-weight: 500;
  color: #333;
}

.favorited {
  color: #ff9800;
}

.comment-item:last-child {
  border-bottom: none;
}

.map-container {
  cursor: pointer;
  transition: all 0.3s ease;
}

.map-container:hover {
  background: #f0f0f0;
}
</style>