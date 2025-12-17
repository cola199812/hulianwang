<template>
  <div class="p-4" style="max-width: 1000px; margin: 0 auto;">
    <template v-if="loggedIn">
      <div class="user-head">
        <div class="user-name">{{ user.username }}</div>
        <div class="user-meta">发布数量：{{ posts.length }}</div>
        <div class="flex-gap"></div>
        <el-button size="small" @click="doLogout">退出登录</el-button>
      </div>
      <div class="masonry">
        <div v-for="p in posts" :key="p.id" class="brick">
          <div class="brick-actions">
            <el-button text type="danger" @click.stop="onDelete(p.id)">删除</el-button>
          </div>
          <div @click="goDetail(p.id)">
            <PostCard :post="p" brief />
          </div>
        </div>
      </div>
    </template>
    <template v-else>
      <div class="login-tip">
        <div>请登录后查看你的发布</div>
        <el-button type="primary" @click="goLogin" style="margin-right:8px;">去登录</el-button>
        <el-button @click="goRegister">去注册</el-button>
      </div>
    </template>
  </div>
  </template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PostCard from '../components/PostCard.vue'
import { listMyPosts, listMediaByPost, deletePost } from '../api/content'
import { userInfo, logout } from '../api/user'

const posts = ref([])
const user = ref({})
const loggedIn = ref(false)
const router = useRouter()

function goDetail(id) {
  router.push(`/post/${id}`)
}
function goLogin() {
  router.push('/login')
}
function goRegister() {
  router.push('/reset-password')
}
async function doLogout() {
  await logout()
  loggedIn.value = false
}

async function load() {
  try {
    const ui = await userInfo()
    user.value = ui.data || {}
    loggedIn.value = true
    const { data } = await listMyPosts()
    posts.value = data || []
    for (const p of posts.value) {
      const res = await listMediaByPost(p.id).catch(()=>null)
      p._media = res?.data || []
    }
  } catch {
    loggedIn.value = false
  }
}

onMounted(load)

async function onDelete(id) {
  await deletePost(id)
  await load()
}
</script>

<style scoped>
.masonry { column-count: 3; column-gap: 16px; }
.brick { break-inside: avoid; margin-bottom: 16px; cursor: pointer; }
@media (max-width: 768px) { .masonry { column-count: 2; } }
@media (max-width: 480px) { .masonry { column-count: 1; } }
.user-head { display:flex; align-items:baseline; gap:10px; margin-bottom:12px; }
.flex-gap { flex:1; }
.user-name { font-size:18px; font-weight:600; }
.user-meta { color:#666; font-size:13px; }
.login-tip { display:flex; align-items:center; gap:8px; }
.brick { position:relative; }
.brick-actions { position:absolute; right:0; top:0; z-index:2; }
</style>
