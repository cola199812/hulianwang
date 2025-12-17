<template>
  <div class="reset-container">
    <el-card class="reset-card">
      <div class="header">
        <h2>重置密码</h2>
      </div>
      
      <el-form :model="form" label-width="0">
        <el-form-item>
          <el-input v-model="form.email" placeholder="请输入注册邮箱" :prefix-icon="Message" />
        </el-form-item>
        <el-form-item>
          <div style="display: flex; width: 100%;">
            <el-input v-model="form.verificationCode" placeholder="验证码" :prefix-icon="Key" style="flex: 1; margin-right: 10px;" />
            <el-button type="primary" :disabled="countdown > 0" @click="handleSendCode">
              {{ countdown > 0 ? `${countdown}s` : '发送验证码' }}
            </el-button>
          </div>
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码" :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-button type="primary" class="full-btn" :loading="loading" @click="handleReset">确认重置</el-button>
        <div class="links">
          <router-link to="/login">返回登录</router-link>
        </div>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { sendCode, resetPassword } from '../api/user'
import { ElMessage } from 'element-plus'
import { Message, Key, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const loading = ref(false)
const countdown = ref(0)

const form = reactive({
  email: '',
  verificationCode: '',
  newPassword: ''
})

async function handleSendCode() {
  if (!form.email) return ElMessage.warning('请输入邮箱')
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  if (!emailRegex.test(form.email)) return ElMessage.warning('请输入有效的邮箱格式')

  try {
    await sendCode(form.email)
    ElMessage.success('验证码已发送')
    countdown.value = 60
    const timer = setInterval(() => {
      countdown.value--
      if (countdown.value <= 0) clearInterval(timer)
    }, 1000)
  } catch (e) {
    ElMessage.error(e.response?.data?.message || '发送失败')
  }
}

async function handleReset() {
  if (!form.email || !form.verificationCode || !form.newPassword) {
    return ElMessage.warning('请填写完整信息')
  }
  loading.value = true
  try {
    await resetPassword(form.email, form.verificationCode, form.newPassword)
    ElMessage.success('密码重置成功，请重新登录')
    router.push('/login')
  } catch (e) {
    console.error(e)
    const msg = e.response?.data?.message || e.message || '重置失败'
    ElMessage.error(msg)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.reset-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f0f2f5;
}
.reset-card {
  width: 400px;
}
.header {
  text-align: center;
  margin-bottom: 20px;
}
.full-btn {
  width: 100%;
  margin-top: 10px;
}
.links {
  margin-top: 15px;
  text-align: right;
}
.links a {
  color: #409eff;
  text-decoration: none;
  font-size: 14px;
}
</style>
