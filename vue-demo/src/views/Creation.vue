<template>
  <div class="page">
    <div class="tabs-header">
      <div 
        class="tab-item" 
        :class="{ active: activeTab === 'discover' }"
        @click="activeTab = 'discover'; handleTabChange('discover')"
      >
        <el-icon><Location /></el-icon> 发现
      </div>
      <div 
        class="tab-item" 
        :class="{ active: activeTab === 'nearby' }"
        @click="activeTab = 'nearby'; handleTabChange('nearby')"
      >
        <el-icon><MapLocation /></el-icon> 附近
      </div>
    </div>

    <!-- Search & Filter -->
    <div class="filter-section">
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索笔记..."
          prefix-icon="Search"
          clearable
          class="search-input"
        />
      </div>
      <div class="tag-scroll" v-if="suggestedTags.length > 0">
        <div 
          class="tag-pill" 
          :class="{ active: filterTag === '' }"
          @click="filterTag = ''"
        >
          全部
        </div>
        <div 
          v-for="tag in suggestedTags" 
          :key="tag"
          class="tag-pill" 
          :class="{ active: filterTag === tag }"
          @click="filterTag = tag"
        >
          #{{ tag }}
        </div>
      </div>
    </div>

    <!-- Content List -->
    <div class="content-list" v-loading="loading">
      <div v-if="filteredPosts.length === 0" class="empty-state">
        <el-empty description="暂无内容" />
      </div>
      
      <div class="masonry-layout" v-else>
        <PostCard 
          v-for="post in filteredPosts" 
          :key="post.id" 
          :post="post"
          @click="goDetail(post.id)"
          @tag-click="filterTag = $event"
        />
      </div>
    </div>

    <!-- Floating Action Button -->
    <div class="fab" @click="openPublish">
      <el-icon size="26"><Plus /></el-icon>
    </div>

    <!-- 发布弹窗 -->
    <el-dialog
      v-model="publishVisible"
      title="🎒 发布探险笔记"
      width="560px"
      class="publish-dialog"
      :close-on-click-modal="false"
    >
      <div class="publish-content">
        <!-- 头部引导 -->
        <div class="publish-guide">
          <el-icon><InfoFilled /></el-icon>
          <span>分享你的徒步路线、露营经历或美景瞬间</span>
        </div>

        <!-- 表单内容 -->
        <el-form :model="form" label-width="80px" label-position="top">
          <!-- 标题 -->
          <el-form-item label="标题" required>
            <el-input
              v-model="form.title"
              placeholder="给你的探险起个响亮的名字"
              maxlength="30"
              show-word-limit
              class="title-input"
            >
              <template #prefix>
                <el-icon><EditPen /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <!-- 内容 -->
          <el-form-item label="探险故事">
            <el-input
              v-model="form.markdown"
              type="textarea"
              :rows="5"
              placeholder="写下你的经历、感受和实用建议..."
              maxlength="500"
              show-word-limit
              resize="none"
              class="content-input"
            />
          </el-form-item>

          <!-- 位置选择 -->
          <el-form-item label="位置信息" class="location-item">
            <div class="location-container">
              <el-input
                v-model="form.locationName"
                placeholder="选择你的探险地点"
                readonly
                class="location-input"
              >
                <template #prefix>
                  <el-icon><LocationFilled /></el-icon>
                </template>
                <template #append>
                  <el-button 
                    @click="openMapSelect" 
                    type="primary" 
                    plain 
                    size="small"
                    class="location-btn"
                  >
                    <el-icon><MapLocation /></el-icon> 选择位置
                  </el-button>
                </template>
              </el-input>
              <div v-if="form.locationName" class="selected-location">
                <el-tag type="success" size="small" closable @close="clearLocation">
                  <el-icon><Location /></el-icon>
                  {{ form.locationName }}
                </el-tag>
              </div>
            </div>
          </el-form-item>

          <!-- 多媒体上传 -->
          <el-form-item label="图片/视频" class="media-item">
            <div class="upload-area">
              <!-- 拖拽上传 -->
              <el-upload
                ref="uploadRef"
                multiple
                accept="image/*,video/*"
                :auto-upload="false"
                :file-list="fileList"
                :on-change="onFilesChange"
                :before-remove="handleFileRemove"
                :show-file-list="false"
                drag
                class="upload-drag"
              >
                <div class="upload-inner">
                  <el-icon class="upload-icon"><UploadFilled /></el-icon>
                  <div class="upload-text">
                    <div>点击或拖拽文件到此处</div>
                    <div class="upload-hint">支持图片和视频，最多9个文件</div>
                  </div>
                </div>
              </el-upload>
              
              <!-- 文件预览 -->
              <div v-if="fileList.length > 0" class="file-preview">
                <div class="preview-header">
                  <span class="preview-title">已选择 {{ fileList.length }} 个文件</span>
                  <el-button 
                    type="danger" 
                    text 
                    size="small" 
                    @click="clearAllFiles"
                    class="clear-btn"
                  >
                    清空
                  </el-button>
                </div>
                <div class="preview-grid">
                  <div 
                    v-for="(file, index) in fileList" 
                    :key="index" 
                    class="preview-item"
                  >
                    <div class="preview-card">
                      <!-- 图片预览 -->
                      <img 
                        v-if="file.raw.type.startsWith('image')"
                        :src="getFilePreview(file.raw)"
                        class="preview-image"
                        alt="预览"
                      />
                      <!-- 视频预览 -->
                      <div v-else class="preview-video">
                        <el-icon><VideoPlay /></el-icon>
                        <span class="video-text">{{ file.name }}</span>
                      </div>
                      
                      <!-- 文件信息 -->
                      <div class="file-info">
                        <span class="file-name" :title="file.name">{{ getShortName(file.name) }}</span>
                        <span class="file-size">{{ formatFileSize(file.size) }}</span>
                      </div>
                      
                      <!-- 删除按钮 -->
                      <el-icon 
                        class="delete-icon" 
                        @click="removeFile(index)"
                      >
                        <CloseBold />
                      </el-icon>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-form-item>

          <!-- 标签选择（可选项） -->
          <el-form-item label="添加标签" class="tags-item">
            <div class="tags-container">
              <el-tag
                v-for="tag in suggestedTags"
                :key="tag"
                class="tag-option"
                :type="selectedTags.includes(tag) ? 'success' : 'info'"
                @click="toggleTag(tag)"
              >
                #{{ tag }}
              </el-tag>
              <el-input
                v-model="customTag"
                placeholder="自定义标签"
                size="small"
                class="custom-tag-input"
                @keyup.enter="addCustomTag"
              >
                <template #append>
                  <el-button @click="addCustomTag">
                    <el-icon><Plus /></el-icon>
                  </el-button>
                </template>
              </el-input>
            </div>
          </el-form-item>
        </el-form>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="publishVisible = false" class="cancel-btn">
            取消
          </el-button>
          <el-button 
            type="success" 
            @click="submit" 
            :loading="submitting"
            :disabled="!canSubmit"
            class="submit-btn"
          >
            <el-icon><Promotion /></el-icon>
            发布探险笔记
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 地图选点弹窗 -->
    <el-dialog
      v-model="mapVisible"
      title="📍 选择位置"
      width="620px"
      append-to-body
      @opened="initMap"
    >
      <div id="map-container" style="height: 420px; width: 100%; border-radius: 8px; overflow: hidden;"></div>
      <div class="map-footer">
        <div v-if="tempLocation.name" class="selected-location-info">
          <el-icon color="#27ae60"><SuccessFilled /></el-icon>
          <span>已选择：{{ tempLocation.name }}</span>
        </div>
        <div class="map-actions">
          <el-button @click="mapVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="confirmLocation" 
            :disabled="!tempLocation.lat"
            class="confirm-location-btn"
          >
            确定选择
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, shallowRef } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { 
  Plus, 
  Location, 
  EditPen, 
  MapLocation, 
  UploadFilled, 
  VideoPlay, 
  CloseBold,
  Promotion,
  InfoFilled,
  LocationFilled,
  SuccessFilled,
  Search
} from '@element-plus/icons-vue'
import PostCard from '../components/PostCard.vue'
import { createPost, listPosts, listMediaByPost, listNearbyPosts, getPresignedUrl, savePostImages, savePostVideo, listTopics } from '../api/content'
import { ElMessage, ElMessageBox } from 'element-plus'
import AMapLoader from '@amap/amap-jsapi-loader'
import axios from 'axios'

// 请替换为你的高德地图 Key 和 Security Code
const AMAP_KEY = '0783e3e3a68f001b6e7be3cde16e28bd' 
const AMAP_SECURITY_CODE = '7e0643f833d109fb8586e047c36150d2'

// 设置安全密钥
window._AMapSecurityConfig = {
  securityJsCode: AMAP_SECURITY_CODE,
}

const router = useRouter()
const route = useRoute()
const posts = ref([])
const nearbyPosts = ref([])
const activeTab = ref('discover')
const loading = ref(false)
const submitting = ref(false)

const publishVisible = ref(false)
const uploadRef = ref()
const fileList = ref([])
const customTag = ref('')
const selectedTags = ref([])
const allTopics = ref([])
const searchKeyword = ref('')
const filterTag = ref('')

const suggestedTags = computed(() => {
  // Use fetched topics or fallback
  if (allTopics.value.length > 0) {
    return allTopics.value.map(t => t.name).slice(0, 10)
  }
  return ['徒步', '登山', '露营', '风景', '户外装备', '路线分享', '日出日落', '星空']
})

const form = reactive({
  title: '',
  markdown: '',
  locationName: '',
  lat: null,
  lng: null
})

// 地图相关
const mapVisible = ref(false)
const map = shallowRef(null)
const marker = shallowRef(null)
const geocoder = shallowRef(null)
const tempLocation = reactive({
  lat: null,
  lng: null,
  name: ''
})

// 计算属性
const currentPosts = computed(() => {
  return activeTab.value === 'discover' ? posts.value : nearbyPosts.value
})

const filteredPosts = computed(() => {
  let res = currentPosts.value
  if (searchKeyword.value) {
    const k = searchKeyword.value.toLowerCase()
    res = res.filter(p => 
      (p.title && p.title.toLowerCase().includes(k)) || 
      (p.markdown && p.markdown.toLowerCase().includes(k)) ||
      (p.topics && p.topics.some(t => t.name.toLowerCase().includes(k)))
    )
  }
  if (filterTag.value) {
    res = res.filter(p => p.topics && p.topics.some(t => t.name === filterTag.value))
  }
  return res
})

const canSubmit = computed(() => {
  return form.title.trim().length > 0
})

// 方法
function goDetail(id) {
  router.push(`/post/${id}`)
}

function getFilePreview(file) {
  return URL.createObjectURL(file)
}

function getShortName(name) {
  return name.length > 15 ? name.substring(0, 12) + '...' : name
}

function formatFileSize(bytes) {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + ' ' + sizes[i]
}

function toggleTag(tag) {
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    if (selectedTags.value.length < 5) {
      selectedTags.value.push(tag)
    } else {
      ElMessage.warning('最多只能选择5个标签')
    }
  }
}

function addCustomTag() {
  if (customTag.value.trim() && !selectedTags.value.includes(customTag.value.trim())) {
    if (selectedTags.value.length < 5) {
      selectedTags.value.push(customTag.value.trim())
      customTag.value = ''
    } else {
      ElMessage.warning('最多只能选择5个标签')
    }
  }
}

function clearAllFiles() {
  if (fileList.value.length > 0) {
    ElMessageBox.confirm('确定要清空所有已选择的文件吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(() => {
      fileList.value = []
      uploadRef.value?.clearFiles()
    })
  }
}

function removeFile(index) {
  fileList.value.splice(index, 1)
}

function handleFileRemove() {
  return true
}

function clearLocation() {
  form.locationName = ''
  form.lat = null
  form.lng = null
}

async function handleTabChange(tab) {
  if (tab === 'discover') {
    if (posts.value.length === 0) loadPosts()
  } else if (tab === 'nearby') {
    loadNearby()
  }
}

async function loadPosts() {
  loading.value = true
  try {
    const { data } = await listPosts()
    posts.value = data || []
    await loadMediaForPosts(posts.value)
  } catch {
    ElMessage.error('加载发现内容失败')
  } finally {
    loading.value = false
  }
}

async function loadNearby() {
  loading.value = true
  // 获取当前位置
  if (!navigator.geolocation) {
    ElMessage.error('浏览器不支持地理定位')
    loading.value = false
    return
  }

  navigator.geolocation.getCurrentPosition(
    async (position) => {
      const { latitude, longitude } = position.coords
      try {
        const { data } = await listNearbyPosts(latitude, longitude)
        nearbyPosts.value = data || []
        await loadMediaForPosts(nearbyPosts.value)
      } catch {
        ElMessage.error('加载附近动态失败')
      } finally {
        loading.value = false
      }
    },
    (err) => {
      console.error(err)
      ElMessage.error('无法获取您的位置，请检查权限')
      loading.value = false
    }
  )
}

async function loadMediaForPosts(postList) {
  for (const p of postList) {
    if (!p._media) { // 避免重复加载
      const res = await listMediaByPost(p.id).catch(() => null)
      p._media = res?.data || []
    }
  }
}

function openPublish() {
  publishVisible.value = true
}

function openMapSelect() {
  mapVisible.value = true
}

function initMap() {
  AMapLoader.load({
    key: AMAP_KEY,
    version: '2.0',
    plugins: ['AMap.Geocoder', 'AMap.Geolocation']
  }).then((AMap) => {
    if (!map.value) {
      map.value = new AMap.Map('map-container', {
        zoom: 13,
        center: [116.397428, 39.90923] // 默认北京
      })

      // 点击地图选点
      map.value.on('click', (e) => {
        const { lng, lat } = e.lnglat
        updateMarker(AMap, lng, lat)
        getAddress(lng, lat)
      })

      // 尝试定位当前位置
      const geolocation = new AMap.Geolocation({
        enableHighAccuracy: true,
        timeout: 10000,
        zoomToAccuracy: true,
      })
      map.value.addControl(geolocation)
      geolocation.getCurrentPosition((status, result) => {
        if (status === 'complete') {
          updateMarker(AMap, result.position.lng, result.position.lat)
          getAddress(result.position.lng, result.position.lat)
        }
      })
    }
    
    // 初始化 Geocoder
    if (!geocoder.value) {
      geocoder.value = new AMap.Geocoder()
    }
  }).catch(e => {
    console.error(e)
    ElMessage.error('地图加载失败，请检查 Key 配置')
  })
}

function updateMarker(AMap, lng, lat) {
  if (!marker.value) {
    marker.value = new AMap.Marker({
      position: [lng, lat]
    })
    map.value.add(marker.value)
  } else {
    marker.value.setPosition([lng, lat])
  }
  tempLocation.lng = lng
  tempLocation.lat = lat
}

function getAddress(lng, lat) {
  geocoder.value.getAddress([lng, lat], (status, result) => {
    if (status === 'complete' && result.regeocode) {
      tempLocation.name = result.regeocode.formattedAddress
    }
  })
}

function confirmLocation() {
  form.lat = tempLocation.lat
  form.lng = tempLocation.lng
  form.locationName = tempLocation.name
  mapVisible.value = false
}

async function submit() {
  if (!canSubmit.value) {
    ElMessage.warning('请填写标题/内容或上传文件')
    return
  }

  submitting.value = true
  try {
    // 1. Create Post
    const postData = {
      ...form,
      topics: selectedTags.value.map(tag => ({ name: tag }))
    }
    
    const { data } = await createPost(postData)
    const postId = data?.id
    if (!postId) {
      ElMessage.error('创建帖子失败')
      return
    }

    // 2. Upload Files
    const images = []
    const videos = []

    const uploadPromises = fileList.value.map(async (f, index) => {
      const file = f.raw
      if (!file) return

      const isVideo = file.type.startsWith('video')
      const ext = file.name.substring(file.name.lastIndexOf('.'))
      const objectName = `uploads/${new Date().getFullYear()}/${Date.now()}_${Math.random().toString(36).substring(7)}${ext}`

      // Get presigned URL
      const res = await getPresignedUrl(objectName)
      const presignedUrl = res.data.presignedUrl

      // Upload to MinIO
      await axios.put(presignedUrl, file, {
        headers: { 'Content-Type': file.type }
      })

      // Construct URL (remove query params)
      const url = presignedUrl.split('?')[0]

      if (isVideo) {
        videos.push({
          postId: postId,
          videoUrl: url,
          coverUrl: '', // Backend will generate
          duration: 0
        })
      } else {
        images.push({
          postId: postId,
          imageUrl: url,
          description: '',
          sortOrder: index,
          tag: ''
        })
      }
    })

    await Promise.all(uploadPromises)

    // 3. Save Metadata
    if (images.length > 0) {
      await savePostImages(postId, images)
    }
    for (const v of videos) {
      await savePostVideo(postId, v)
    }

    // 4. Cleanup
    publishVisible.value = false
    fileList.value = []
    selectedTags.value = []
    customTag.value = ''
    form.title = ''
    form.markdown = ''
    form.locationName = ''
    form.lat = null
    form.lng = null

    // 5. Refresh
    if (activeTab.value === 'discover') {
      await loadPosts()
    } else {
      await loadNearby()
    }
    
    ElMessage.success({
      message: '发布成功！',
      duration: 2000,
      showClose: true
    })
  } catch (e) {
    console.error(e)
    ElMessage.error('发布失败：' + (e.message || '未知错误'))
  } finally {
    submitting.value = false
  }
}

function onFilesChange(_, fs) {
  const newFiles = fs || []
  if (fileList.value.length + newFiles.length > 9) {
    ElMessage.warning('最多只能上传9个文件')
    return
  }
  fileList.value = [...fileList.value, ...newFiles.slice(0, 9 - fileList.value.length)]
}

async function fetchTopics() {
  try {
    const { data } = await listTopics()
    if (data && data.length > 0) {
      allTopics.value = data
    }
  } catch (e) {
    console.error('Failed to load topics', e)
  }
}

onMounted(() => {
  loadPosts()
  fetchTopics()
  if (route.query.tag) {
    filterTag.value = route.query.tag
  }
})
</script>

<style scoped>
/* 统一绿色主题 */
:root {
  --primary-green: #2e7d32;
  --primary-green-dark: #1b5e20;
  --primary-green-light: #388e3c;
  --secondary-green: #4caf50;
  --accent-green: #66bb6a;
  --light-green: #e8f5e9;
  --lighter-green: #c8e6c9;
}

/* 页面整体 */
.page {
  padding: 20px 24px 40px;
  max-width: 1200px;
  margin: 0 auto;
  min-height: calc(100vh - 100px);
  background: linear-gradient(135deg, var(--light-green) 0%, var(--lighter-green) 100%);
  border-radius: 20px;
  margin-top: 20px;
  box-shadow: 0 8px 32px rgba(46, 125, 50, 0.1);
}

/* 顶部标签页 (Custom) */
.tabs-header {
  display: flex;
  gap: 32px;
  margin-bottom: 24px;
  padding: 0 12px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 16px rgba(46, 125, 50, 0.1);
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 12px 20px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.5);
}

.tab-item.active {
  color: var(--primary-green);
  font-weight: 600;
  background: var(--light-green);
  box-shadow: 0 4px 12px rgba(46, 125, 50, 0.15);
}

.tab-item:hover {
  color: var(--primary-green-light);
  background: rgba(46, 125, 50, 0.1);
}

/* 筛选区 */
.filter-section {
  margin-bottom: 24px;
  padding: 0 12px;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 16px rgba(46, 125, 50, 0.1);
}

.search-bar {
  margin-bottom: 16px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
  padding-left: 16px;
  box-shadow: 0 2px 8px rgba(46, 125, 50, 0.1);
  border: 1px solid #c8e6c9;
}
.search-input :deep(.el-input__wrapper):hover {
  border-color: #2e7d32;
}
.search-input :deep(.el-input__wrapper.is-focus) {
  border-color: #2e7d32;
  box-shadow: 0 0 0 1px #2e7d32;
}

.tag-scroll {
  display: flex;
  gap: 12px;
  overflow-x: auto;
  padding-bottom: 8px;
  scrollbar-width: none; /* Firefox */
}

.tag-scroll::-webkit-scrollbar {
  display: none; /* Chrome/Safari */
}

.tag-pill {
  flex-shrink: 0;
  padding: 6px 16px;
  background: #f0f2f5;
  border-radius: 16px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}

.tag-pill:hover {
  background: #e6e8eb;
}

.tag-pill.active {
  background: #e8f5e9;
  color: #27ae60;
  font-weight: 500;
}

/* 瀑布流 */
.masonry-layout {
  column-count: 3;
  column-gap: 20px;
}

/* Responsive columns */
@media (max-width: 992px) {
  .masonry-layout { column-count: 2; }
}
@media (max-width: 600px) {
  .masonry-layout { column-count: 1; }
}

.masonry-layout :deep(.post-card) {
  break-inside: avoid;
  margin-bottom: 20px;
}

/* 空状态 */
.empty-state {
  padding: 60px 0;
  text-align: center;
}

/* 悬浮按钮 */
.fab {
  position: fixed;
  right: 32px;
  bottom: 32px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #2e7d32, #388e3c);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 20px rgba(46, 125, 50, 0.4);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 100;
}

.fab:hover {
  transform: scale(1.1) rotate(90deg);
  box-shadow: 0 8px 25px rgba(46, 125, 50, 0.6);
}

/* 发布弹窗 */
.publish-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

.publish-dialog :deep(.el-dialog__header) {
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  margin-right: 0;
  padding: 20px 24px;
}

.publish-dialog :deep(.el-dialog__title) {
  color: white;
  font-weight: 600;
  font-size: 18px;
}

.publish-dialog :deep(.el-dialog__headerbtn) {
  color: white;
}

.publish-dialog :deep(.el-dialog__headerbtn:hover .el-dialog__close) {
  color: #f0f0f0;
}

.publish-content {
  padding: 0 8px;
}

/* 发布引导 */
.publish-guide {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 8px;
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}

.publish-guide .el-icon {
  color: #2e7d32;
}

/* 表单样式 */
.title-input :deep(.el-input__inner) {
  font-size: 16px;
  font-weight: 500;
}

.content-input :deep(.el-textarea__inner) {
  font-size: 14px;
  line-height: 1.6;
}

.location-input :deep(.el-input__inner) {
  cursor: pointer;
}

/* Upload & Tags */
.upload-container {
  margin-top: 8px;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 8px;
}

.tag-item {
  cursor: pointer;
  user-select: none;
  transition: all 0.2s;
}

.custom-tag-input {
  width: 120px;
}

/* Map Footer */
.map-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.selected-location-info {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #333;
  font-size: 14px;
  font-weight: 500;
}

.location-btn {
  background: #e8f5e9;
  border-color: #27ae60;
  color: #27ae60;
}

.location-btn:hover {
  background: #27ae60;
  color: white;
}

.selected-location {
  margin-top: 8px;
}

.selected-location .el-tag {
  padding: 4px 10px;
}

/* 上传区域 */
.upload-drag {
  width: 100%;
}

.upload-drag :deep(.el-upload) {
  width: 100%;
}

.upload-drag :deep(.el-upload-dragger) {
  width: 100%;
  height: 180px;
  border: 2px dashed #c8e6c9;
  background: #f9fdf9;
  border-radius: 12px;
  padding: 30px;
}

.upload-drag :deep(.el-upload-dragger:hover) {
  border-color: #27ae60;
  background: #f0f9f0;
}

.upload-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.upload-icon {
  font-size: 48px;
  color: #27ae60;
}

.upload-text {
  text-align: center;
  color: #666;
}

.upload-hint {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

/* 文件预览 */
.file-preview {
  margin-top: 20px;
}

.preview-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.preview-title {
  font-weight: 500;
  color: #333;
}

.clear-btn {
  padding: 4px 8px;
}

.preview-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.preview-item {
  position: relative;
}

.preview-card {
  position: relative;
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  transition: all 0.2s ease;
}

.preview-card:hover {
  border-color: #27ae60;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.preview-image {
  width: 100%;
  height: 100px;
  object-fit: cover;
  display: block;
}

.preview-video {
  width: 100%;
  height: 100px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
}

.preview-video .el-icon {
  font-size: 28px;
  margin-bottom: 8px;
}

.video-text {
  font-size: 12px;
  padding: 0 8px;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  width: 100%;
}

.file-info {
  padding: 8px;
  background: rgba(255, 255, 255, 0.95);
}

.file-name {
  display: block;
  font-size: 12px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  display: block;
  font-size: 11px;
  color: #999;
  margin-top: 2px;
}

.delete-icon {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 20px;
  height: 20px;
  background: rgba(0, 0, 0, 0.5);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s ease;
}

.preview-item:hover .delete-icon {
  opacity: 1;
}

/* 标签选择 */
.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}

.tag-option {
  cursor: pointer;
  transition: all 0.2s ease;
  user-select: none;
}

.tag-option:hover {
  transform: translateY(-2px);
}

.custom-tag-input {
  width: 180px;
}

/* 对话框底部 */
.dialog-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0 0;
}

.cancel-btn {
  border-radius: 20px;
  padding: 10px 24px;
}

.submit-btn {
  border-radius: 20px;
  padding: 10px 32px;
  background: linear-gradient(135deg, #27ae60, #2ecc71);
  border: none;
  font-weight: 500;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #219653, #27ae60);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(39, 174, 96, 0.3);
}

.submit-btn:disabled {
  background: #cccccc;
  transform: none;
  box-shadow: none;
}

/* 地图弹窗 */
.map-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e8e8e8;
}

.selected-location-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #27ae60;
  font-weight: 500;
}

.map-actions {
  display: flex;
  gap: 12px;
}

.confirm-location-btn {
  border-radius: 20px;
  padding: 8px 24px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .masonry {
    column-count: 2;
  }
}

@media (max-width: 768px) {
  .page {
    padding: 16px;
  }
  
  .masonry {
    column-count: 1;
  }
  
  .fab {
    right: 20px;
    bottom: 20px;
    width: 50px;
    height: 50px;
  }
  
  .preview-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .preview-grid {
    grid-template-columns: 1fr;
  }
}
</style>
