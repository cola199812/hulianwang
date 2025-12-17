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

const form = reactive({
  name: '',
  distance: null,
  level: '',
  description: ''
})

async function handleCreate() {
  try {
    await createRoute(form)
    alert('创建成功')
  } catch (e) {
    alert('创建失败，可能未登录')
  }
}
</script>

