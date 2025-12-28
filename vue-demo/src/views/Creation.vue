<template>
  <div class="home-container">
    <!-- 顶部导航 -->
    <div class="top-nav">
      <div class="nav-left">
        <h1 class="logo">📒 灵感笔记</h1>
      </div>
      <div class="nav-right">
        <el-button type="primary" @click="publishVisible = true" class="publish-btn">
          <el-icon><Plus /></el-icon>
          发布笔记
        </el-button>
      </div>
    </div>

    <!-- 瀑布流容器 -->
    <div class="masonry-container">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>
      
      <div v-else class="masonry">
        <div 
          v-for="(p, index) in posts" 
          :key="p.id" 
          class="brick" 
          @click="goDetail(p.id)"
        >
          <PostCard :post="p" :index="index" brief />
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="!loading && posts.length === 0" class="empty-state">
        <div class="empty-content">
          <el-icon size="100" color="#dcdfe6"><Notebook /></el-icon>
          <h3>还没有任何笔记</h3>
          <p>发布你的第一条笔记，记录生活中的美好瞬间</p>
          <el-button type="primary" size="large" @click="publishVisible = true">
            <el-icon><Plus /></el-icon>
            立即发布
          </el-button>
        </div>
      </div>
    </div>

    <!-- 简化的悬浮发布按钮（备用） -->
    <div class="fab-simple" @click="publishVisible = true">
      <el-icon size="20"><Edit /></el-icon>
      <span>发布</span>
    </div>

    <!-- 发布弹窗 -->
    <el-dialog 
      v-model="publishVisible" 
      title="发布笔记" 
      width="520px"
      class="publish-dialog"
      :close-on-click-modal="false"
      :destroy-on-close="true"
      @closed="resetForm"
    >
      <div class="publish-content">
        <el-form :model="form" label-width="auto" ref="formRef">
          <!-- 媒体上传区域 - 放在最前面 -->
          <div class="form-section">
            <div class="section-label">
              <el-icon><Picture /></el-icon>
              <span>上传图片/视频</span>
              <span class="section-hint">（至少上传一张图片）</span>
            </div>
            <el-form-item prop="files">
              <div class="upload-area">
                <el-upload
                  v-model:file-list="fileList"
                  multiple
                  accept="image/*,video/*"
                  :auto-upload="false"
                  :on-change="onFilesChange"
                  :show-file-list="false"
                  :limit="9"
                  class="media-upload"
                >
                  <div class="upload-trigger">
                    <el-icon size="28"><Plus /></el-icon>
                    <div class="upload-hint">点击上传</div>
                    <div class="upload-subhint">支持 JPG、PNG、MP4 格式</div>
                    <div class="upload-subhint">最多 9 个文件</div>
                  </div>
                </el-upload>
                
                <!-- 预览区域 -->
                <div v-if="fileList.length > 0" class="preview-container">
                  <div 
                    v-for="(file, index) in fileList" 
                    :key="index"
                    class="preview-item"
                    :class="{ 'is-video': file.raw?.type?.startsWith('video') }"
                  >
                    <div class="preview-image">
                      <img 
                        v-if="file.raw?.type?.startsWith('image')" 
                        :src="URL.createObjectURL(file.raw)"
                        alt="预览"
                        @load="URL.revokeObjectURL(file.url)"
                      />
                      <div 
                        v-else-if="file.raw?.type?.startsWith('video')"
                        class="video-preview"
                      >
                        <el-icon size="24"><VideoPlay /></el-icon>
                        <span>视频</span>
                      </div>
                    </div>
                    <div class="preview-actions">
                      <div class="preview-info">
                        <div class="file-name">{{ getFileName(file.name) }}</div>
                        <div class="file-size">{{ formatFileSize(file.size) }}</div>
                      </div>
                      <el-icon 
                        class="remove-btn" 
                        @click.stop="removeFile(index)"
                      >
                        <Close />
                      </el-icon>
                    </div>
                  </div>
                </div>
              </div>
              <div v-if="fileList.length > 0" class="upload-tips">
                已选择 {{ fileList.length }} 个文件，可拖拽调整顺序
              </div>
            </el-form-item>
          </div>

          <!-- 标题区域 -->
          <div class="form-section">
            <div class="section-label">
              <el-icon><Edit /></el-icon>
              <span>笔记内容</span>
            </div>
            <el-form-item prop="title">
              <el-input 
                v-model="form.title" 
                placeholder="给笔记起个吸引人的标题吧..."
                size="large"
                class="title-input"
                maxlength="50"
                show-word-limit
              />
            </el-form-item>
            <el-form-item prop="markdown">
              <el-input
                v-model="form.markdown"
                type="textarea"
                :rows="5"
                placeholder="分享你的故事、经验或想法..."
                resize="none"
                class="content-textarea"
                maxlength="1000"
                show-word-limit
              />
            </el-form-item>
          </div>

          <!-- 位置区域（简化版） -->
          <div class="form-section">
            <div class="section-label">
              <el-icon><Location /></el-icon>
              <span>添加地点（可选）</span>
            </div>
            <el-form-item>
              <el-input 
                v-model="form.locationName" 
                placeholder="在哪里发布的这篇笔记？"
                :prefix-icon="MapLocation"
              />
            </el-form-item>
          </div>
        </el-form>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="publishVisible = false" class="cancel-btn">
            取消
          </el-button>
          <el-button 
            type="primary" 
            @click="submit" 
            class="submit-btn"
            :loading="submitting"
            :disabled="fileList.length === 0"
          >
            {{ submitting ? '发布中...' : '立即发布' }}
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { 
  Plus, 
  Close, 
  Location, 
  Picture, 
  Edit, 
  MapLocation, 
  VideoPlay,
  Notebook
} from '@element-plus/icons-vue'
import PostCard from '../components/PostCard.vue'
import { createPost, listPosts, uploadMedia, listMediaByPost } from '../api/content'
import { ElMessage } from 'element-plus'

const router = useRouter()
const posts = ref([])
const loading = ref(true)
const publishVisible = ref(false)
const submitting = ref(false)
const fileList = ref([])
const formRef = ref()

const form = reactive({ 
  title: '', 
  markdown: '', 
  locationName: ''
})

// 加载数据
async function load() {
  try {
    loading.value = true
    const { data } = await listPosts()
    posts.value = data || []
    
    // 确保媒体数据正确加载，避免重复显示
    for (const p of posts.value) {
      try {
        const res = await listMediaByPost(p.id)
        // 确保_media是数组且去重
        p._media = Array.isArray(res?.data) ? [...new Map(res.data.map(item => 
          [item.id, item]
        )).values()] : []
      } catch (e) {
        p._media = []
      }
    }
  } catch (e) {
    console.error('加载失败:', e)
    ElMessage.error(e.response?.data?.message || '加载内容失败')
  } finally {
    loading.value = false
  }
}

// 提交表单
async function submit() {
  try {
    // 基础验证
    if (fileList.value.length === 0) {
      ElMessage.error('请至少上传一张图片')
      return
    }
    
    if (!form.title.trim() && !form.markdown.trim()) {
      ElMessage.error('请填写标题或内容')
      return
    }
    
    submitting.value = true
    
    // 创建帖子
    const { data } = await createPost(form)
    const postId = data?.id
    
    if (!postId) {
      ElMessage.error('发布失败')
      return
    }
    
    // 上传文件
    const uploadPromises = fileList.value.map(async (f, index) => {
      const fd = new FormData()
      fd.append('file', f.raw)
      fd.append('type', (f.raw?.type || '').startsWith('video') ? 'video' : 'image')
      fd.append('postId', postId)
      fd.append('order', index) // 添加排序字段
      return uploadMedia(fd)
    })
    
    await Promise.all(uploadPromises)
    
    // 成功处理
    publishVisible.value = false
    ElMessage.success({
      message: '发布成功！',
      type: 'success',
      duration: 2000,
      showClose: true
    })
    
    // 重新加载数据
    await load()
    
  } catch (e) {
    console.error('发布失败:', e)
    ElMessage.error(e.response?.data?.message || '发布失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

// 文件处理
function onFilesChange(file, fs) {
  // 限制文件数量
  if (fs.length > 9) {
    ElMessage.warning('最多只能上传9个文件')
    fileList.value = fs.slice(0, 9)
    return
  }
  fileList.value = fs || []
}

function removeFile(index) {
  fileList.value.splice(index, 1)
}

function formatFileSize(bytes) {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

function getFileName(name) {
  if (!name) return '未命名'
  if (name.length <= 15) return name
  return name.substring(0, 12) + '...'
}

// 重置表单
function resetForm() {
  form.title = ''
  form.markdown = ''
  form.locationName = ''
  fileList.value = []
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

function goDetail(id) {
  router.push(`/post/${id}`)
}

onMounted(() => {
  load()
})
</script>

<style scoped>
.home-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 0 24px 40px;
}

/* 顶部导航 */
.top-nav {
  position: sticky;
  top: 0;
  z-index: 100;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 16px 0;
  margin-bottom: 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  background: linear-gradient(135deg, #ff2442 0%, #ff6b9d 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.publish-btn {
  padding: 10px 24px;
  border-radius: 24px;
  font-weight: 500;
  background: linear-gradient(135deg, #ff2442 0%, #ff6b9d 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(255, 36, 66, 0.2);
  transition: all 0.3s ease;
}

.publish-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 36, 66, 0.3);
}

/* 瀑布流容器 */
.masonry-container {
  position: relative;
  min-height: 400px;
}

.loading-container {
  max-width: 1200px;
  margin: 0 auto;
}

.masonry {
  column-count: 4;
  column-gap: 20px;
  animation: fadeIn 0.6s ease-out;
  max-width: 1400px;
  margin: 0 auto;
}

.brick {
  break-inside: avoid;
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border-radius: 12px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: relative;
}

.brick:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.brick:hover::after {
  content: '查看详情';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 500;
  z-index: 2;
}

/* 空状态 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
  padding: 40px;
}

.empty-content {
  text-align: center;
  max-width: 400px;
}

.empty-content h3 {
  margin: 20px 0 12px;
  font-size: 24px;
  font-weight: 600;
  color: #333;
}

.empty-content p {
  color: #666;
  margin-bottom: 24px;
  line-height: 1.6;
}

/* 简化的悬浮按钮 */
.fab-simple {
  position: fixed;
  right: 32px;
  bottom: 32px;
  width: 60px;
  height: 60px;
  border-radius: 30px;
  background: linear-gradient(135deg, #ff2442 0%, #ff6b9d 100%);
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 6px 24px rgba(255, 36, 66, 0.3);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  z-index: 99;
  font-size: 12px;
}

.fab-simple:hover {
  transform: scale(1.1);
  box-shadow: 0 12px 32px rgba(255, 36, 66, 0.4);
}

/* 发布弹窗样式 */
.publish-dialog :deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
}

.publish-dialog :deep(.el-dialog__header) {
  padding: 20px 24px;
  border-bottom: 1px solid #f0f0f0;
  margin: 0;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
}

.publish-dialog :deep(.el-dialog__title) {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.publish-dialog :deep(.el-dialog__body) {
  padding: 0;
}

.publish-content {
  padding: 24px;
  max-height: 70vh;
  overflow-y: auto;
}

.form-section {
  margin-bottom: 28px;
}

.form-section:last-child {
  margin-bottom: 0;
}

.section-label {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 16px;
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.section-label .el-icon {
  color: #ff2442;
}

.section-hint {
  font-size: 13px;
  color: #999;
  font-weight: normal;
  margin-left: 4px;
}

.title-input :deep(.el-input__wrapper) {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 12px 16px;
  border: 1px solid #e9ecef;
}

.title-input :deep(.el-input__wrapper.is-focus) {
  border-color: #ff2442;
  box-shadow: 0 0 0 1px rgba(255, 36, 66, 0.2);
}

.content-textarea :deep(.el-textarea__inner) {
  border-radius: 12px;
  border: 1px solid #e9ecef;
  padding: 16px;
  font-size: 14px;
  line-height: 1.6;
  transition: all 0.3s ease;
  background: #f8f9fa;
}

.content-textarea :deep(.el-textarea__inner):focus {
  border-color: #ff2442;
  box-shadow: 0 0 0 1px rgba(255, 36, 66, 0.2);
  background: white;
}

/* 上传区域 */
.upload-area {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.media-upload :deep(.el-upload) {
  width: 100%;
}

.upload-trigger {
  width: 100%;
  height: 140px;
  border: 2px dashed #dee2e6;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f8f9fa;
}

.upload-trigger:hover {
  border-color: #ff2442;
  background: #fff5f7;
}

.upload-hint {
  margin-top: 8px;
  font-weight: 500;
  color: #495057;
}

.upload-subhint {
  font-size: 12px;
  color: #6c757d;
  margin-top: 2px;
}

.upload-tips {
  font-size: 12px;
  color: #6c757d;
  margin-top: 8px;
  text-align: center;
}

/* 预览区域 */
.preview-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}

.preview-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.preview-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.preview-image {
  width: 100%;
  height: 140px;
  overflow: hidden;
  background: #f8f9fa;
}

.preview-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.preview-item:hover .preview-image img {
  transform: scale(1.05);
}

.video-preview {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #ff2442 0%, #ff6b9d 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
}

.preview-actions {
  padding: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: white;
}

.preview-info {
  flex: 1;
  min-width: 0;
}

.file-name {
  font-size: 11px;
  color: #495057;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-weight: 500;
}

.file-size {
  font-size: 10px;
  color: #6c757d;
  margin-top: 2px;
}

.remove-btn {
  color: #6c757d;
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
  transition: all 0.3s ease;
  flex-shrink: 0;
  margin-left: 8px;
}

.remove-btn:hover {
  color: #ff2442;
  background: #ffeef0;
}

/* 弹窗底部 */
.dialog-footer {
  padding: 16px 24px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
}

.cancel-btn {
  padding: 10px 24px;
  border-radius: 12px;
  font-weight: 500;
  border-color: #e9ecef;
}

.submit-btn {
  padding: 10px 24px;
  border-radius: 12px;
  font-weight: 500;
  background: linear-gradient(135deg, #ff2442 0%, #ff6b9d 100%);
  border: none;
  transition: all 0.3s ease;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 36, 66, 0.3);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 响应式设计 */
@media (max-width: 1400px) {
  .masonry { column-count: 3; }
}

@media (max-width: 1024px) {
  .masonry { column-count: 2; }
  .home-container { padding: 0 16px 32px; }
  .top-nav { padding: 12px 0; }
}

@media (max-width: 768px) {
  .masonry { column-count: 1; max-width: 500px; }
  .logo { font-size: 20px; }
  .publish-dialog { width: 90% !important; }
  .preview-container { grid-template-columns: repeat(2, 1fr); }
  .fab-simple { right: 16px; bottom: 16px; }
}

@media (max-width: 480px) {
  .home-container { padding: 0 12px 24px; }
  .publish-content { padding: 16px; }
  .dialog-footer { padding: 12px 16px; }
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 滚动条美化 */
.publish-content::-webkit-scrollbar {
  width: 8px;
}

.publish-content::-webkit-scrollbar-track {
  background: #f1f3f5;
  border-radius: 4px;
}

.publish-content::-webkit-scrollbar-thumb {
  background: #ced4da;
  border-radius: 4px;
}

.publish-content::-webkit-scrollbar-thumb:hover {
  background: #adb5bd;
}
</style>