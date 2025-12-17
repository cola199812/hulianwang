<template>
  <div class="login-container">
    <el-card class="login-card">
      <div class="header">
        <h2>青年户外社交平台</h2>
      </div>
      
      <el-tabs v-model="activeTab" stretch>
        <el-tab-pane label="账号登录" name="login">
          <el-form :model="form" label-width="0">
            <el-form-item>
              <el-input v-model="form.username" placeholder="用户名 / 邮箱" prefix-icon="User" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password />
            </el-form-item>
            <el-button type="primary" class="full-btn" :loading="loading" @click="handleLogin">登 录</el-button>
            <div style="text-align: right; margin-top: 10px;">
              <router-link to="/reset-password" style="color: #409eff; text-decoration: none; font-size: 14px;">忘记密码?</router-link>
            </div>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="邮箱注册" name="register">
          <el-form :model="regForm" label-width="0">
            <el-form-item>
              <el-input v-model="regForm.username" placeholder="用户名" :prefix-icon="User" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.email" placeholder="邮箱" :prefix-icon="Message" />
            </el-form-item>
            <el-form-item>
              <div style="display: flex; width: 100%;">
                <el-input v-model="regForm.verificationCode" placeholder="验证码" :prefix-icon="Key" style="flex: 1; margin-right: 10px;" />
                <el-button type="primary" :disabled="countdown > 0" @click="sendVerificationCode">
                  {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
                </el-button>
              </div>
            </el-form-item>
            <el-form-item>
              <el-input v-model="regForm.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password />
            </el-form-item>
            <el-button type="success" class="full-btn" :loading="loading" @click="handleRegister">注 册</el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { login, register, sendCode } from '../api/user'
import { ElMessage } from 'element-plus'
import { User, Lock, Message, Key } from '@element-plus/icons-vue'

const router = useRouter()
const activeTab = ref('login')
const loading = ref(false)
const countdown = ref(0)

const form = reactive({
  username: '',
  password: ''
})

const regForm = reactive({
  username: '',
  email: '',
  verificationCode: '',
  password: ''
})

async function handleLogin() {
  if (!form.username || !form.password) return ElMessage.warning('请输入账号和密码')
  loading.value = true
  try {
    await login(form.username, form.password)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '登录失败，请检查账号密码')
  } finally {
    loading.value = false
  }
}

async function sendVerificationCode() {
  if (!regForm.email) return ElMessage.warning('请输入邮箱')
  // Simple email regex validation
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(regForm.email)) return ElMessage.warning('请输入有效的邮箱格式')

  try {
    await sendCode(regForm.email)
    ElMessage.success('验证码已发送（请查看后端控制台）')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '发送失败')
  }
}

async function handleRegister() {
  if (!regForm.username || !regForm.password || !regForm.email || !regForm.verificationCode) {
    return ElMessage.warning('请填写完整信息')
  }
  loading.value = true
  try {
    await register(regForm.username, regForm.password, regForm.email, regForm.verificationCode)
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    form.username = regForm.username
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #e0f7fa 0%, #a5d6a7 100%);
}
.login-card {
  width: 400px;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0,0,0,0.1);
  padding: 20px;
}
.header {
  text-align: center;
  margin-bottom: 24px;
}
.full-btn {
  width: 100%;
  font-weight: bold;
}
</style>
