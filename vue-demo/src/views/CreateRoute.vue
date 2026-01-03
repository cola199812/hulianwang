<template>
  <div style="max-width: 600px; margin: 40px auto;">
    <el-card>
      <h2>创建路线</h2>
      <el-form :model="form" label-width="90px">
        <el-form-item label="路线名">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="距离(km)">
          <el-input v-model.number="form.distance" type="number" />
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="form.level" placeholder="选择难度">
            <el-option label="简单" value="简单" />
            <el-option label="中等" value="中等" />
            <el-option label="困难" value="困难" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleCreate">提交</el-button>
          <el-button @click="$router.push('/routes')">返回列表</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { createRoute } from '../api/route'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()

const form = reactive({
  name: '',
  distance: null,
  level: '',
  description: ''
})

async function handleCreate() {
  if (!form.name || !form.distance || !form.level || !form.description) {
    ElMessage.warning('请填写所有必要信息')
    return
  }

  try {
    // 添加超时控制
    const timeoutPromise = new Promise((_, reject) => 
      setTimeout(() => reject(new Error('Request timeout')), 10000)
    )
    await Promise.race([createRoute(form), timeoutPromise])
    ElMessage.success('创建成功')
    
    // 跳转到路线发现页面，并自动切换到"我发布的路线"标签页
    router.push({
      path: '/routes',
      query: { tab: 'my-routes' }
    })
  } catch (error) {
    console.error('创建路线失败:', error)
    ElMessage.error('创建失败: ' + (error.message || '网络错误'))
  }
}
</script>

