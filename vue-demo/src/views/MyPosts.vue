<template>
  <div class="p-4" style="max-width: 1000px; margin: 0 auto;">
    <h2 class="text-xl font-bold mb-4">我的发布</h2>
    <div class="masonry">
      <div v-for="p in posts" :key="p.id" class="brick" @click="goDetail(p.id)">
        <PostCard :post="p" />
      </div>
    </div>
  </div>
  </template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import PostCard from '../components/PostCard.vue'
import { listMyPosts, listMediaByPost } from '../api/content'

const posts = ref([])
const router = useRouter()

function goDetail(id) {
  router.push(`/post/${id}`)
}

async function load() {
  const { data } = await listMyPosts()
  posts.value = data || []
  for (const p of posts.value) {
    const res = await listMediaByPost(p.id).catch(()=>null)
    p._media = res?.data || []
  }
}

onMounted(load)
</script>

<style scoped>
.masonry { column-count: 3; column-gap: 16px; }
.brick { break-inside: avoid; margin-bottom: 16px; cursor: pointer; }
@media (max-width: 768px) { .masonry { column-count: 2; } }
@media (max-width: 480px) { .masonry { column-count: 1; } }
</style>
