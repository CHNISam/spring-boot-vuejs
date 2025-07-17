<template>
  <div class="search-container">
    <header class="search-header">
      <h2 class="search-title">Search Results</h2>
    </header>

    <!-- AI 智能摘要 -->
    <section v-if="posts.length" class="ai-summary">
      <h3 class="ai-title">🔍 AI Insight</h3>
      <p v-if="loadingSummary" class="ai-loading">Analyzing your query…</p>
      <p v-else-if="summary" class="ai-text">{{ summary }}</p>
      <p v-else class="ai-retry">
        <button @click="regenerateSummary" class="retry-btn">
          🔁 重试生成 AI 总结
        </button>
      </p>
    </section>

    <!-- 加载骨架屏 -->
    <div v-if="loading" class="cards-grid">
      <SkeletonCard v-for="n in 6" :key="n" />
    </div>
    <div v-else-if="error" class="status error">Error: {{ error }}</div>
    <div v-else-if="!loading && posts.length === 0" class="status empty">
      No results found.
    </div>

    <!-- 正常内容 -->
    <div v-else class="cards-grid">
      <article
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
          <h3 class="post-title" v-html="highlightText(post.title)"></h3>
          <p class="excerpt" v-html="highlightText(post.excerpt)"></p>
          <img
            v-if="post.thumbnail"
            class="thumbnail"
            :src="post.thumbnail"
            alt="Post thumbnail"
          />
        </div>

        <div class="card-footer">
          <button class="foot-item" @click.stop="goToDetail(post.id)" aria-label="Views">
            <svg class="icon" viewBox="0 0 24 24">
              <path d="M12 5c-7 0-10 7-10 7s3 7 10 7 10-7 10-7-3-7-10-7z" />
              <circle cx="12" cy="12" r="3" />
            </svg>
            <span>{{ post.views }}</span>
          </button>
          <button class="foot-item" @click.stop="goToDetail(post.id)" aria-label="Comments">
            <svg class="icon" viewBox="0 0 24 24">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2z" />
            </svg>
            <span>{{ post.comments }}</span>
          </button>
        </div>
      </article>
    </div>
  </div>
</template>

<script lang="ts" setup>
import api, { Post as APIPost } from '@/api/backend-api'
import SkeletonCard from '@/components/common/SkeletonCard.vue'
import { onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

interface FullPost extends APIPost {
  excerpt: string
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
const loading = ref(false)
const error = ref<string | null>(null)
const summary = ref<string | null>(null)
const loadingSummary = ref(false)
const summaryCache = new Map<string, string>()
let summaryTimer: ReturnType<typeof setTimeout> | null = null

const avatarStyle = (url?: string) => ({
  background: url ? `url(${url}) center/cover` : 'var(--avatar-bg)'
})
const relativeTime = (iso: string) => {
  const diff = Date.now() - new Date(iso).getTime()
  const m = 60e3, h = 60 * m, d = 24 * h
  if (diff < m) return 'just now'
  if (diff < h) return `${Math.floor(diff / m)}m ago`
  if (diff < d) return `${Math.floor(diff / h)}h ago`
  const dt = new Date(iso)
  return `${String(dt.getMonth() + 1).padStart(2, '0')}-${String(dt.getDate()).padStart(2, '0')}`
}
const makeExcerpt = (text: string) =>
  text.length > 80 ? text.slice(0, 80) + '…' : text
const goToDetail = (id: number) =>
  router.push({ name: 'PostDetail', params: { id } })
const escapeRegExp = (str: string) =>
  str.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
function highlightText(text: string): string {
  if (!q.value) return text
  const re = new RegExp(`(${escapeRegExp(q.value)})`, 'gi')
  return text.replace(re, `<span class="highlight">$1</span>`)
}
function scheduleAISummary(query: string, briefs: { title: string; excerpt: string }[]) {
  if (!query || briefs.length === 0) return
  if (summaryCache.has(query)) {
    summary.value = summaryCache.get(query)!
    return
  }
  if (summaryTimer) clearTimeout(summaryTimer)
  loadingSummary.value = true
  summaryTimer = setTimeout(async () => {
    try {
      const res = await api.getAISummary(query, briefs)
      summary.value = res.data.summary
      summaryCache.set(query, res.data.summary)
    } catch {
      summary.value = null
    } finally {
      loadingSummary.value = false
    }
  }, 1000)
}
function regenerateSummary() {
  const briefs = posts.value.map(p => ({ title: p.title, excerpt: p.excerpt }))
  scheduleAISummary(q.value, briefs)
}
async function doSearch() {
  loading.value = true
  error.value = null
  summary.value = null
  try {
    const res = await api.searchPosts(q.value)
    const full = res.data.map(p => ({
      ...p,
      excerpt: makeExcerpt(p.content),
      authorName: 'Anonymous',
      authorAvatar: undefined,
      createdAt: new Date().toISOString(),
      views: 0,
      comments: 0,
      thumbnail: undefined,
    }))
    posts.value = full
    scheduleAISummary(q.value, full.map(p => ({ title: p.title, excerpt: p.excerpt })))
  } catch (e: any) {
    error.value = e.response?.data?.message || e.message || 'Unknown error'
    posts.value = []
  } finally {
    loading.value = false
  }
}

watch(() => route.query.q, () => doSearch())
onMounted(doSearch)
</script>

<style>
.highlight {
  color: var(--accent);
}
</style>

<style scoped>
:root {
  --bg: #121417;
  --fg: #e0e0e0;
  --card-bg: #1e1f26;
  --border: #2a2c38;
  --accent: #3d7bfd;
  --text-main: #c0c0c0;
  --text-title: #ffffff;
  --text-muted: #888a9b;
  --avatar-bg: #32343f;
  --radius: 0.5rem;
  --gap: 1rem;
  --padding: 1.5rem;
}

.search-container {
  padding: var(--padding);
  background: var(--bg);
  color: var(--fg);
  min-height: 100vh;
}

.search-header {
  margin-bottom: var(--gap);
}

.search-title {
  font-size: 1.5rem;
  font-weight: bold;
  color: var(--text-title);
}

.ai-summary {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: var(--gap);
  margin-bottom: var(--gap);
}

.ai-title {
  margin: 0 0 0.5rem;
  font-size: 1rem;
  color: var(--accent);
}

.ai-text,
.ai-loading {
  font-size: 0.9rem;
  color: var(--text-main);
}

.ai-retry {
  text-align: right;
}

.retry-btn {
  padding: 0.3rem 0.8rem;
  background: var(--accent);
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.85rem;
}

.retry-btn:hover {
  opacity: 0.9;
}

.status {
  text-align: center;
  margin: 2rem 0;
  color: var(--text-main);
}

.status.error {
  color: #ff6b6b;
}

.status.empty {
  color: var(--text-muted);
}

.cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--gap);
}

.card {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.5);
}

.card-header {
  display: flex;
  align-items: center;
  padding: 0.75rem var(--gap);
}

.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
}

.meta {
  margin-left: 0.75rem;
}

.author {
  font-size: 0.9rem;
  font-weight: 500;
  color: var(--text-title);
}

.time {
  font-size: 0.75rem;
  color: var(--text-muted);
  margin-top: 0.25rem;
}

.card-body {
  padding: 0 var(--gap);
  flex: 1;
}

.post-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-title);
  margin: 0.5rem 0;
}

.excerpt {
  font-size: 0.85rem;
  line-height: 1.4;
  color: var(--text-main);
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  overflow: hidden;
}

.thumbnail {
  width: 100%;
  margin-top: 0.5rem;
  border-radius: var(--radius);
  object-fit: cover;
  height: 140px;
}

.card-footer {
  display: flex;
  align-items: center;
  padding: 0.5rem var(--gap);
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
</style>
<style>
.highlight {
  color: var(--accent);
}
</style>
