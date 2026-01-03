<template>
  <div class="p-4" style="max-width: 800px; margin: 0 auto;">
    <h1 class="text-xl font-bold mb-4">我的</h1>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>个人信息</span>
          <el-button type="primary" size="small" @click="toggleEdit" style="float: right;">
            {{ isEditing ? '取消' : '编辑' }}
          </el-button>
        </div>
      </template>
      
      <!-- 查看模式 -->
      <div v-if="!isEditing">
        <div class="flex items-center mb-4">
          <el-avatar :size="80" :src="info.avatarUrl || ''">{{ info.username?.charAt(0) || 'U' }}</el-avatar>
          <div class="ml-4">
            <div class="text-xl font-bold">{{ info.nickname || info.username || '-' }}</div>
            <div class="text-gray-500 text-sm">{{ info.email || '-' }}</div>
          </div>
        </div>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="用户ID">{{ info.id || '-' }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ info.username || '-' }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ info.nickname || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ info.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="性别">{{ info.gender || '-' }}</el-descriptions-item>
          <el-descriptions-item label="生日">{{ info.birthday || '-' }}</el-descriptions-item>
          <el-descriptions-item label="个人简介" :span="2">{{ info.bio || '-' }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ formatDate(info.createTime) }}</el-descriptions-item>
        </el-descriptions>
      </div>
      
      <!-- 编辑模式 -->
      <el-form v-else :model="formData" label-width="80px">
        <el-form-item label="头像">
          <el-avatar :size="80" :src="formData.avatarUrl || ''">{{ formData.username?.charAt(0) || 'U' }}</el-avatar>
          <el-input v-model="formData.avatarUrl" placeholder="头像URL" style="margin-top: 10px;"></el-input>
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="formData.nickname" placeholder="请输入昵称"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="formData.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="formData.gender" placeholder="请选择性别">
            <el-option label="男" value="male"></el-option>
            <el-option label="女" value="female"></el-option>
            <el-option label="保密" value="secret"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="生日">
          <el-date-picker
            v-model="formData.birthday"
            type="date"
            placeholder="选择生日"
            style="width: 100%;"
          ></el-date-picker>
        </el-form-item>
        <el-form-item label="个人简介">
          <el-input
            v-model="formData.bio"
            type="textarea"
            rows="4"
            placeholder="请输入个人简介"
          ></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="saveProfile">保存</el-button>
          <el-button @click="cancelEdit">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <div class="mt-4 grid grid-cols-3 gap-3">
      <el-card class="nav-card" @click="$router.push('/my-posts')">
        <div class="nav-title">我的发布</div>
        <div class="nav-desc">查看与管理你的游记</div>
      </el-card>
      <el-card class="nav-card" @click="$router.push('/creation')">
        <div class="nav-title">发布新内容</div>
        <div class="nav-desc">记录最新行程与见闻</div>
      </el-card>
      <el-card class="nav-card" @click="$router.push('/notification')">
        <div class="nav-title">通知中心</div>
        <div class="nav-desc">查看系统与活动通知</div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { userInfo, updateUserProfile } from '../api/user'
import { ElMessage } from 'element-plus'

const info = ref({})
const isEditing = ref(false)
const formData = ref({})

// 加载用户信息
async function load() {
  try {
    const { data } = await userInfo()
    info.value = data || {}
    // 初始化表单数据
    formData.value = {
      nickname: info.value.nickname,
      avatarUrl: info.value.avatarUrl,
      phone: info.value.phone,
      gender: info.value.gender,
      birthday: info.value.birthday,
      bio: info.value.bio
    }
  } catch (error) {
    ElMessage.error('加载用户信息失败')
  }
}

// 格式化日期
function formatDate(dateStr) {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString()
}

// 切换编辑模式
function toggleEdit() {
  isEditing.value = !isEditing.value
  if (isEditing.value) {
    // 进入编辑模式时，复制当前信息到表单
    formData.value = {
      nickname: info.value.nickname,
      avatarUrl: info.value.avatarUrl,
      phone: info.value.phone,
      gender: info.value.gender,
      birthday: info.value.birthday,
      bio: info.value.bio
    }
  }
}

// 取消编辑
function cancelEdit() {
  isEditing.value = false
}

// 保存个人信息
async function saveProfile() {
  try {
    const { data } = await updateUserProfile(formData.value)
    // 更新用户信息
    info.value = data.user || {}
    isEditing.value = false
    ElMessage.success('个人信息更新成功')
  } catch (error) {
    ElMessage.error('更新失败：' + (error.response?.data?.message || '未知错误'))
  }
}

onMounted(load)
</script>

<style scoped>
.nav-title { font-weight:600; }
.nav-desc { color:#666; font-size:12px; margin-top:4px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.nav-card { cursor: pointer; transition: all 0.3s; }
.nav-card:hover { box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15); transform: translateY(-2px); }
</style>
