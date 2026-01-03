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

.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, var(--primary-green) 0%, var(--secondary-green) 100%);
}
.login-card {
  width: 420px;
  border-radius: 20px;
  box-shadow: 0 12px 40px rgba(46,125,50,0.3);
  padding: 32px;
  background: #ffffff;
  border: 1px solid var(--lighter-green);
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}
.header h2 {
  color: var(--primary-green);
  font-size: 28px;
  font-weight: 700;
  margin: 0 0 8px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}
.header {
  text-align: center;
  margin-bottom: 32px;
}
.full-btn {
  width: 100%;
  font-weight: 600;
  border-radius: 12px;
  padding: 14px 0;
  font-size: 16px;
  background: var(--primary-green);
  border-color: var(--primary-green);
  box-shadow: 0 4px 12px rgba(46, 125, 50, 0.3);
  transition: all 0.3s ease;
}
.full-btn:hover {
  background: var(--primary-green-dark);
  border-color: var(--primary-green-dark);
  box-shadow: 0 6px 16px rgba(46, 125, 50, 0.4);
  transform: translateY(-2px);
}
:deep(.el-tabs__item.is-active) {
  color: var(--primary-green);
}
:deep(.el-tabs__active-bar) {
  background-color: var(--primary-green);
  height: 3px;
}
:deep(.el-input__inner) {
  border-radius: 8px;
  border: 2px solid var(--lighter-green);
  transition: all 0.3s ease;
}
:deep(.el-input__inner:focus) {
  border-color: var(--primary-green);
  box-shadow: 0 0 0 3px rgba(46, 125, 50, 0.1);
}
:deep(.el-button--primary) {
  background: var(--primary-green);
  border-color: var(--primary-green);
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(46, 125, 50, 0.2);
}
:deep(.el-button--primary:hover) {
  background: var(--primary-green-dark);
  border-color: var(--primary-green-dark);
  box-shadow: 0 4px 12px rgba(46, 125, 50, 0.3);
}
:deep(.el-button--success) {
  background: var(--primary-green-light);
  border-color: var(--primary-green-light);
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(56, 142, 60, 0.2);
}
:deep(.el-button--success:hover) {
  background: var(--primary-green);
  border-color: var(--primary-green);
  box-shadow: 0 4px 12px rgba(46, 125, 50, 0.3);
}
</style>
