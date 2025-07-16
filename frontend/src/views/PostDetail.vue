<template>
  <div class="post-detail">
    <button class="back-btn" @click="goBack">← 返回</button>

    <div v-if="loading" class="status">加载中…</div>
    <div v-else-if="error" class="status error">加载失败：{{ error }}</div>

    <!-- 只有 post 拿到数据后才渲染下面内容 -->
    <div v-else-if="post" class="card">
      <h1 class="title">{{ post.title }}</h1>
      <div class="meta">
        <span class="author">{{ post.authorName }}</span>
        <span class="sep">·</span>
        <span class="time">{{ timeAgo(post.createdAt) }}</span>
        <span class="sep">·</span>
        <span class="views">{{ post.views }} 次浏览</span>
      </div>
      <div class="content" v-html="post.content"></div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import axios from 'axios'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

interface Post {
  id: number
  title: string
  content: string
  authorName: string
  createdAt: string   // ISO 字符串
  views: number
}

const route = useRoute()
const router = useRouter()

const post = ref<Post | null>(null)
const loading = ref(true)
const error = ref<string | null>(null)

async function fetchPost() {
  loading.value = true
  error.value = null
  try {
    const id = route.params.id
    const res = await axios.get<Post>(`/api/posts/${id}`)
    post.value = res.data
  } catch (e: any) {
    error.value = e.response?.data?.message || e.message || '未知错误'
  } finally {
    loading.value = false
  }
}

onMounted(fetchPost)

function goBack() {
  router.back()
}

/** 生成相对时间字符串 */
function timeAgo(iso: string): string {
  const now = Date.now()
  const past = new Date(iso).getTime()
  const diff = now - past
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour

  if (diff < hour) {
    const m = Math.floor(diff / minute)
    return m <= 1 ? '刚刚' : `${m} 分钟前`
  } else if (diff < day) {
    const h = Math.floor(diff / hour)
    return `${h} 小时前`
  } else {
    return new Date(iso).toLocaleDateString()
  }
}
</script>

<style scoped>
.post-detail {
  max-width: 720px;
  margin: 0 auto;
  padding: 24px;
  color: var(--text-dark);
}
.back-btn {
  background: none;
  border: none;
  color: var(--accent);
  font-size: 1rem;
  cursor: pointer;
  margin-bottom: 16px;
}
.status {
  text-align: center;
  padding: 24px 0;
  color: var(--text-dark);
}
.status.error {
  color: #e74c3c;
}

/* 卡片样式 */
.card {
  background: var(--card-light);
  border: var(--glass-border);
  backdrop-filter: blur(calc(var(--blur)/2));
  border-radius: var(--radius);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
  padding: 24px;
}
body.dark .card {
  background: var(--card-dark);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.45);
}

.title {
  margin: 0 0 12px;
  font-size: 28px;
}
.meta {
  font-size: 14px;
  color: #888;
  margin-bottom: 24px;
}
.meta .sep {
  margin: 0 6px;
}
.content {
  line-height: 1.8;
  color: var(--text-dark);
}

/* 深色主题变量示例 */
:root {
  --text-dark: #333;
  --card-light: rgba(255,255,255,0.8);
  --glass-border: 1px solid rgba(0,0,0,0.1);
  --radius: 8px;
  --blur: 12px;
  --accent: #4a90e2;
}
body.dark {
  --text-dark: #ddd;
  --card-light: rgba(30,30,31,0.6);
}
</style>
