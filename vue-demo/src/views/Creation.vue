<template>
  <div class="page">
    <!-- 顶部 Tabs -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="mb-4">
      <el-tab-pane label="发现" name="discover" />
      <el-tab-pane label="附近动态" name="nearby" />
    </el-tabs>

    <!-- 瀑布流 -->
    <div v-loading="loading" class="masonry">
      <div
        v-for="p in currentPosts"
        :key="p.id"
        class="brick"
        @click="goDetail(p.id)"
      >
        <PostCard :post="p" brief />
      </div>
    </div>

    <!-- 空状态 -->
    <el-empty v-if="!loading && currentPosts.length === 0" description="暂无内容" />

    <!-- 悬浮发布按钮 -->
    <div class="fab" @click="openPublish">
      <el-icon size="26"><Plus /></el-icon>
    </div>

    <!-- 发布弹窗 -->
    <el-dialog
      v-model="publishVisible"
      title="🌿 发布探险笔记"
      width="520"
      class="publish-dialog"
    >
      <el-form :model="form" label-width="80">
        <el-form-item label="标题">
          <el-input
            v-model="form.title"
            placeholder="给这次探险起个名字"
          />
        </el-form-item>

        <el-form-item label="内容">
          <el-input
            v-model="form.markdown"
            type="textarea"
            :rows="4"
            placeholder="记录你的路线、风景和感受..."
          />
        </el-form-item>

        <el-form-item label="位置">
          <el-input
            v-model="form.locationName"
            placeholder="点击右侧按钮选择位置"
            readonly
          >
            <template #append>
              <el-button @click="openMapSelect">
                <el-icon><Location /></el-icon> 选择
              </el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item label="图片/视频">
          <el-upload
            multiple
            accept="image/*,video/*"
            :auto-upload="false"
            :file-list="fileList"
            :on-change="onFilesChange"
          >
            <el-button type="success" plain>选择文件</el-button>
          </el-upload>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="publishVisible=false">取消</el-button>
        <el-button type="success" @click="submit">发布</el-button>
      </template>
    </el-dialog>

    <!-- 地图选点弹窗 -->
    <el-dialog
      v-model="mapVisible"
      title="选择位置"
      width="600px"
      append-to-body
      @opened="initMap"
    >
      <div id="map-container" style="height: 400px; width: 100%;"></div>
      <div style="margin-top: 10px; text-align: right;">
        <span v-if="tempLocation.name" style="margin-right: 10px; color: #666;">
          已选: {{ tempLocation.name }}
        </span>
        <el-button @click="mapVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmLocation" :disabled="!tempLocation.lat">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import { Plus, Location } from '@element-plus/icons-vue'
import PostCard from '../components/PostCard.vue'
import { createPost, listPosts, listMediaByPost, listNearbyPosts, getPresignedUrl, savePostImages, savePostVideo } from '../api/content'
import { ElMessage } from 'element-plus'
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
const posts = ref([])
const nearbyPosts = ref([])
const activeTab = ref('discover')
const loading = ref(false)

const publishVisible = ref(false)
const fileList = ref([])
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

const currentPosts = computed(() => {
  return activeTab.value === 'discover' ? posts.value : nearbyPosts.value
})

function goDetail(id) {
  router.push(`/post/${id}`)
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
  const hasText = !!(form.title || form.markdown)
  const hasFiles = fileList.value.length > 0
  if (!hasText && !hasFiles) {
    ElMessage.error('请填写内容或选择文件')
    return
  }

  try {
    // 1. Create Post
    const { data } = await createPost(form)
    const postId = data?.id
    if (!postId) return

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
    ElMessage.success('发布成功')
    router.push('/my-posts')
  } catch (e) {
    console.error(e)
    ElMessage.error('发布失败：' + (e.message || '未知错误'))
  }
}

function onFilesChange(_, fs) {
  fileList.value = fs || []
}

onMounted(loadPosts)
</script>

<style scoped>
/* 页面整体 */
.page {
  padding: 24px;
  max-width: 1000px;
  margin: 0 auto;
  background: linear-gradient(180deg, #f2fbf5, #ffffff);
  border-radius: 12px;
  min-height: 80vh;
}

.mb-4 {
  margin-bottom: 16px;
}

/* 瀑布流 */
.masonry {
  column-count: 3;
  column-gap: 16px;
}

.brick {
  break-inside: avoid;
  margin-bottom: 16px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.brick:hover {
  transform: translateY(-4px);
}

/* 自适应 */
@media (max-width: 768px) {
  .masonry {
    column-count: 2;
  }
}
@media (max-width: 480px) {
  .masonry {
    column-count: 1;
  }
}

/* 悬浮按钮 */
.fab {
  position: fixed;
  right: 28px;
  bottom: 28px;
  width: 58px;
  height: 58px;
  border-radius: 50%;
  background: #39c5bb;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(57, 197, 187, 0.4);
  cursor: pointer;
  transition: all 0.3s;
  z-index: 100;
}
.fab:hover {
  transform: scale(1.1);
  background: #2ea89f;
}
</style>
