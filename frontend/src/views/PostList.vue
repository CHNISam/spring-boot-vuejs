<template>
  <div class="post-list">
    <h2 class="title">Post List</h2>

    <!-- 加载时显示骨架屏 -->
    <div v-if="loading" class="cards">
      <SkeletonCard v-for="n in 6" :key="n" />
    </div>

    <!-- 错误 / 空状态 -->
    <div v-else-if="error" class="status error">
      Error loading: {{ error }}
    </div>
    <div v-else-if="posts.length === 0" class="status">
      No posts yet.
    </div>

    <!-- 正常内容 -->
    <div v-else class="cards">
      <div
        v-for="post in posts"
        :key="post.id"
        class="card"
        @click="goToDetail(post.id)"
      >
        <div class="card-header">
          <div class="avatar" :style="avatarStyle(post.authorAvatar)"></div>
          <div class="meta">
            <div class="author">{{ post.authorName }}</div>
            <div class="time">{{ relativeTime(post.createdAt) }}</div>
          </div>
        </div>

        <div class="card-body">
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="excerpt">{{ excerpt(post.content) }}</p>
        </div>

        <div class="thumb-wrapper" v-if="post.thumbnail">
          <img class="thumb" :src="post.thumbnail" alt="Thumbnail" />
        </div>

        <div class="card-footer">
          <button class="foot-item views" @click.stop="goToDetail(post.id)" aria-label="Views">
            <svg class="icon" viewBox="0 0 24 24"><path d="M12 5c-7 0-10 7-10 7s3 7 10 7 10-7 10-7-3-7-10-7z"/><circle cx="12" cy="12" r="3"/></svg>
            <span>{{ post.views }}</span>
          </button>
          <button class="foot-item comments" @click.stop="goToDetail(post.id)" aria-label="Comments">
            <svg class="icon" viewBox="0 0 24 24"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2z"/></svg>
            <span>{{ post.comments }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import axios from 'axios'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

interface Post {
  id: number
  title: string
  content: string
  authorName: string
  authorAvatar?: string
  createdAt: string
  views: number
  comments: number
  thumbnail?: string
}

const router = useRouter()
const posts = ref<Post[]>([])
const loading = ref(true)
const error = ref<string | null>(null)

const avatarStyle = (url?: string) => ({
  background: url ? `url(${url}) center/cover` : '#666'
})
const goToDetail = (id: number) =>
  router.push({ name: 'PostDetail', params: { id } })
const excerpt = (text: string) =>
  text.length > 80 ? text.slice(0, 80) + '…' : text
const relativeTime = (iso: string) => {
  const diff = Date.now() - new Date(iso).getTime()
  const m = 60_000, h = 3_600_000, d = 86_400_000
  if (diff < m) return 'just now'
  if (diff < h) return `${Math.floor(diff / m)}m ago`
  if (diff < d) return `${Math.floor(diff / h)}h ago`
  const dt = new Date(iso)
  return `${String(dt.getMonth() + 1).padStart(2,'0')}-${String(dt.getDate()).padStart(2,'0')}`
}

async function fetchPosts() {
  loading.value = true
  error.value = null
  try {
    const res = await axios.get<Post[]>('/api/posts')
    posts.value = res.data.map(p => ({ ...p, comments: p.comments ?? 0 }))
    await Promise.all(
      posts.value.map(async p => {
        const cntRes = await axios.get<number>(`/api/posts/${p.id}/comments/count`)
        p.comments = cntRes.data
      })
    )
  } catch (e: any) {
    error.value = e.response?.data?.message || e.message || 'Unknown error'
  } finally {
    loading.value = false
  }
}

onMounted(fetchPosts)
</script>

<style scoped>
.post-list {
  padding: var(--padding);
  background: var(--bg);
  color: var(--text-main);
  min-height: 100vh;
}
.title {
  font-size: 1.25rem;
  font-weight: 700;
  margin-bottom: 1rem;
  color: var(--text-title);
}
.cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 1rem;
}
.card {
  display: flex;
  flex-direction: column;
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}
.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}
.card-header {
  display: flex;
  align-items: center;
  padding: 0.75rem 1rem;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--border);
}
.meta {
  margin-left: 0.75rem;
}
.author {
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-title);
}
.time {
  font-size: 0.75rem;
  color: var(--text-muted);
  margin-top: 0.25rem;
}
.card-body {
  flex: 1;
  padding: 0 1rem;
}
.post-title {
  font-size: 1rem;
  font-weight: 600;
  margin: 0.5rem 0;
  line-height: 1.2;
  color: var(--text-title);
}
.excerpt {
  font-size: 0.85rem;
  line-height: 1.4;
  color: var(--text-main);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.thumb-wrapper {
  position: relative;
  width: 100%;
  padding-top: 75%; /* 4:3 Aspect Ratio */
  overflow: hidden;
}
.thumb {
  position: absolute;
  top: 0; left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.card-footer {
  display: flex;
  align-items: center;
  padding: 0.5rem 1rem;
  background: var(--card-bg);
  border-top: 1px solid var(--border);
}
.foot-item {
  display: flex;
  align-items: center;
  margin-right: 1rem;
  font-size: 0.85rem;
  color: var(--text-muted);
  background: none;
  border: none;
  cursor: pointer;
  transition: color 0.2s;
}
.foot-item:hover {
  color: var(--accent);
}
.icon {
  width: 18px;
  height: 18px;
  margin-right: 0.25rem;
  stroke: currentColor;
  fill: none;
}
.status {
  text-align: center;
  margin: 2rem 0;
  color: var(--text-main);
}
.status.error {
  color: var(--error);
}
</style>
