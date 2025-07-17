<template>
  <div class="post-list">
    <h2 class="title">搜索结果</h2>

    <div v-if="loading" class="status">加载中…</div>
    <div v-else-if="error" class="status error">请求出错：{{ error }}</div>
    <div v-else-if="posts.length === 0" class="status">暂无结果。</div>

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
          <img class="thumb" :src="post.thumbnail" alt="缩略图" />
        </div>

        <div class="card-footer">
          <button class="foot-item views" @click.stop="goToDetail(post.id)">
            <svg class="icon" viewBox="0 0 24 24"><path d="M12 5c-7 0-10 7-10 7s3 7 10 7 10-7 10-7-3-7-10-7z"/><circle cx="12" cy="12" r="3"/></svg>
            <span>{{ post.views }}</span>
          </button>
          <button class="foot-item comments" @click.stop="goToDetail(post.id)">
            <svg class="icon" viewBox="0 0 24 24"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2z"/></svg>
            <span>{{ post.comments }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import api, { Post as APIPost } from '@/api/backend-api'
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 在 BasicPost 的基础上，扩展卡片需要的字段
interface FullPost extends APIPost {
  authorName: string
  authorAvatar?: string
  createdAt: string
  views: number
  comments: number
  thumbnail?: string
}

const route = useRoute()
const router = useRouter()
const q = ref<string>((route.query.q as string) || '')

const posts = ref<FullPost[]>([])
const loading = ref<boolean>(false)
const error = ref<string|null>(null)

const avatarStyle = (url?: string): Record<string,string> => ({
  background: url ? `url(${url}) center/cover` : '#444'
})

const goToDetail = (id: number) => {
  router.push({ name: 'PostDetail', params: { id } })
}

const excerpt = (text: string): string => {
  const max = 100
  return text.length > max ? text.slice(0, max) + '…' : text
}

const relativeTime = (iso: string): string => {
  const then = new Date(iso).getTime()
  const diff = Date.now() - then
  const m = 60*1000, h = 60*m, d = 24*h
  if (diff < m)  return '刚刚'
  if (diff < h)  return `${Math.floor(diff/m)} 分钟前`
  if (diff < d)  return `${Math.floor(diff/h)} 小时前`
  const dt = new Date(iso)
  return `${String(dt.getMonth()+1).padStart(2,'0')}-${String(dt.getDate()).padStart(2,'0')}`
}

async function doSearch() {
  loading.value = true
  error.value = null
  try {
    const res = await api.searchPosts(q.value)        // APIPost[]
    // 映射成 FullPost，加上默认值
    posts.value = res.data.map(p => ({
      ...p,
      authorName: '匿名',
      authorAvatar: undefined,
      createdAt: new Date().toISOString(),
      views: 0,
      comments: 0,
      thumbnail: undefined,
    }))
  } catch (e: any) {
    error.value = e.response?.data?.message || e.message || '未知错误'
    posts.value = []
  } finally {
    loading.value = false
  }
}

watch(() => route.query.q, v => {
  q.value = (v as string) || ''
  doSearch()
})

onMounted(doSearch)
</script>

<style scoped>
.post-list {
  padding: var(--padding);
  background: var(--bg);
  color: var(--text-main);
  min-height: 100vh;
}

.title {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-title);
  margin-bottom: 16px;
}

.status {
  text-align: center;
  margin: 40px 0;
  color: var(--text-main);
}
.status.error {
  color: var(--error);
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px,1fr));
  gap: 16px;
}

.card {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: transform .2s, box-shadow .2s;
}
.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 18px rgba(0,0,0,0.6);
}

.card-header {
  display: flex;
  align-items: center;
  padding: 10px;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  flex-shrink: 0;
}
.meta {
  margin-left: 10px;
  display: flex;
  flex-direction: column;
}
.author {
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--text-title);
}
.time {
  font-size: 0.75rem;
  color: var(--text-muted);
  margin-top: 2px;
}

.card-body {
  padding: 0 10px 10px;
  flex: 1;
}
.post-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: var(--text-title);
  margin: 0 0 6px;
}
.excerpt {
  font-size: 0.8rem;
  line-height: 1.4;
  color: var(--text-main);

  /* Safari/WebKit */
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;

  /* 标准属性 */
  display: box;
  line-clamp: 1;
  box-orient: vertical;

  overflow: hidden;
}

.thumb-wrapper {
  height: auto;
}
.thumb {
  width: 100%;
  height: 140px;
  object-fit: cover;
}

.card-footer {
  display: flex;
  align-items: center;
  padding: 6px 10px;
  border-top: 1px solid var(--border);
  background: var(--card-bg);
}
.foot-item {
  display: flex;
  align-items: center;
  margin-right: 12px;
  font-size: 0.8rem;
  color: var(--text-muted);
  background: none;
  border: none;
  cursor: pointer;
  transition: color .2s;
}
.foot-item:hover {
  color: var(--accent);
}
.icon {
  width: 16px;
  height: 16px;
  margin-right: 3px;
  stroke: currentColor;
  fill: none;
}
</style>
