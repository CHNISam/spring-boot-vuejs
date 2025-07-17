<template>
  <div class="profile-post-list">
    <h2 class="title">TA 的帖子</h2>

    <!-- 状态 -->
    <div v-if="loading" class="status">加载中…</div>
    <div v-else-if="error" class="status error">请求出错：{{ error }}</div>
    <div v-else-if="!posts.length" class="status empty">
      <svg
        class="w-16 h-16 mb-2 text-gray-600"
        fill="none"
        viewBox="0 0 48 48"
      >
        <rect
          x="8"
          y="16"
          width="32"
          height="24"
          rx="2"
          fill="currentColor"
          opacity="0.15"
        />
        <rect
          x="12"
          y="20"
          width="24"
          height="16"
          rx="2"
          fill="currentColor"
          opacity="0.12"
        />
        <path
          d="M16 24h8m-8 4h16"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
        />
      </svg>
      暂无帖子。
    </div>

    <!-- 卡片 -->
    <div v-else class="cards">
      <div
        v-for="post in posts"
        :key="post.id"
        class="card"
        @click="emitGo(post.id)"
      >
        <div class="card-header">
          <div
            class="avatar"
            :style="avatarStyle(post.authorAvatar)"
            aria-hidden="true"
          ></div>
          <div class="meta">
            <div class="author">{{ post.authorName || '匿名' }}</div>
            <div class="time">{{ relativeTime(post.createdAt) }}</div>
          </div>
        </div>

        <div class="card-body">
          <h3 class="post-title">{{ post.title || '（无标题）' }}</h3>
          <p class="excerpt">{{ excerpt(post.content || '') }}</p>
        </div>

        <div class="thumb-wrapper" v-if="post.thumbnail">
          <img class="thumb" :src="post.thumbnail" alt="缩略图" />
        </div>

        <div class="card-footer">
          <button
            class="foot-item views"
            @click.stop="emitGo(post.id)"
            aria-label="查看帖子"
          >
            <svg class="icon" viewBox="0 0 24 24">
              <path
                d="M12 5c-7 0-10 7-10 7s3 7 10 7 10-7 10-7-3-7-10-7z"
              />
              <circle cx="12" cy="12" r="3" />
            </svg>
            <span>{{ post.views ?? 0 }}</span>
          </button>
          <button
            class="foot-item comments"
            @click.stop="emitGo(post.id)"
            aria-label="查看评论"
          >
            <svg class="icon" viewBox="0 0 24 24">
              <path
                d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2z"
              />
            </svg>
            <span>{{ post.comments ?? 0 }}</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
/**
 * 通用帖子展示网格，适用于 Profile、Search、分类页
 */
export interface ProfilePostItem {
  id: number
  title?: string
  content?: string
  createdAt?: string
  authorName?: string
  authorAvatar?: string
  views?: number
  comments?: number
  thumbnail?: string
}

const props = defineProps<{
  posts: ProfilePostItem[]
  loading?: boolean
  error?: string | null
}>()

const emit = defineEmits<{
  (e: 'go', id: number): void
}>()

const avatarStyle = (url?: string): Record<string, string> => ({
  background: url ? `url(${url}) center/cover` : 'var(--avatar-bg,#444)',
})

function emitGo(id: number) {
  emit('go', id)
}

function excerpt(text: string): string {
  const max = 100
  return text.length > max ? text.slice(0, max) + '…' : text
}

function relativeTime(iso?: string): string {
  if (!iso) return ''
  const then = new Date(iso).getTime()
  const diff = Date.now() - then
  const m = 60 * 1000,
    h = 60 * m,
    d = 24 * h
  if (diff < m) return '刚刚'
  if (diff < h) return `${Math.floor(diff / m)} 分钟前`
  if (diff < d) return `${Math.floor(diff / h)} 小时前`
  const dt = new Date(iso)
  return `${String(dt.getMonth() + 1).padStart(2, '0')}-${String(
    dt.getDate()
  ).padStart(2, '0')}`
}
</script>

<style scoped>
/* 避免与父级冲突，这里用局部变量，外部未定义时使用硬编码 */
:host,
.profile-post-list {
  --padding: 0;
  --bg: transparent;
  --text-main: #cbd5e1;
  --text-title: #e2e8f0;
  --text-muted: #94a3b8;
  --accent: #5ea6ff;
  --card-bg: #23262d;
  --border: #3b3f4a;
  --radius: 12px;
  --error: #f87171;
  --avatar-bg: #444;
}

.profile-post-list {
  width: 100%;
  color: var(--text-main);
}

.title {
  font-size: 1.125rem;
  font-weight: 600;
  color: var(--text-title);
  margin-bottom: 16px;
  text-align: left;
}

.status {
  text-align: center;
  margin: 40px 0;
}
.status.error {
  color: var(--error);
}
.status.empty {
  color: var(--text-muted);
  display: flex;
  flex-direction: column;
  align-items: center;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.card {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s, border-color 0.15s;
}
.card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.6);
  border-color: var(--accent);
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
  display: -webkit-box;
  -webkit-line-clamp: 2; /* 比搜索页更长一些 */
  -webkit-box-orient: vertical;
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
  transition: color 0.15s;
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
