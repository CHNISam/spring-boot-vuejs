<template>
  <div class="post-detail">
    <!-- 返回按钮 -->
    <button class="back-btn" @click="goBack">← 返回</button>

    <!-- 加载 / 错误 -->
    <div v-if="loading" class="status">加载中…</div>
    <div v-else-if="error" class="status error">加载失败：{{ error }}</div>

    <!-- 内容 -->
    <div v-else>
      <div v-if="post" class="card">
        <!-- 标题 -->
        <h1 class="title">{{ post.title }}</h1>

        <!-- 元信息 -->
        <div class="meta">
          <span class="pub-date">{{ formatDate(post.createdAt) }}</span>
          <div class="stats">
            <button class="stat-item views" @click="scrollToComments" aria-label="查看阅读量">
              <svg viewBox="0 0 24 24"><path d="M12 5c-7 0-10 7-10 7s3 7 10 7 10-7 10-7-3-7-10-7z"/><circle cx="12" cy="12" r="3"/></svg>
              <span>{{ post.views }}</span>
            </button>
            <button class="stat-item comments" @click="scrollToComments" aria-label="查看评论">
              <svg viewBox="0 0 24 24"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2z"/></svg>
              <span>{{ post.comments }}</span>
            </button>
          </div>
        </div>

        <!-- 正文 -->
        <div class="content" v-html="post.content"></div>

        <!-- 评论区 -->
        <div class="comments-section" ref="commentsSection" id="comments">
          <h2 class="comments-title">评论（{{ comments.length }}）</h2>

          <div v-if="comments.length === 0" class="no-comments">
            还没有评论，快来抢沙发～
          </div>

          <div
            v-for="(c, idx) in comments"
            :key="c.id"
            class="comment"
          >
            <div class="comment-avatar"></div>
            <div class="comment-body">
              <div class="comment-meta">
                <span class="comment-user">{{ c.user }}</span>
                <span class="sep">·</span>
                <span class="comment-time">{{ timeAgo(c.createdAt) }}</span>
              </div>
              <div class="comment-text">{{ c.text }}</div>
              <div class="comment-actions">
                <button class="action-btn" @click="editComment(idx)">编辑</button>
                <button class="action-btn" @click="deleteComment(idx)">删除</button>
              </div>
            </div>
          </div>

          <!-- 发表评论 -->
          <div class="comment-input">
            <textarea
              v-model="newComment"
              placeholder="写下你的评论…"
              @keydown.enter.prevent="postComment"
            ></textarea>
            <button
              class="btn-submit"
              @click="postComment"
              :disabled="!newComment.trim()"
            >
              发送
            </button>
          </div>
        </div>
      </div>

      <div v-else class="status">帖子不存在或已被删除。</div>
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
  createdAt: string
  views: number
  comments: number
}

interface Comment {
  id: number
  user: string
  text: string
  createdAt: string
}

const route = useRoute()
const router = useRouter()

const post = ref<Post|null>(null)
const comments = ref<Comment[]>([])
const loading = ref(true)
const error = ref<string|null>(null)
const newComment = ref('')
const commentsSection = ref<HTMLElement|null>(null)

async function fetchComments() {
  if (!post.value) return
  const res = await axios.get<Comment[]>(`/api/posts/${post.value.id}/comments`)
  comments.value = res.data
  post.value.comments = comments.value.length
}

async function fetchPost() {
  loading.value = true
  error.value = null
  try {
    const res = await axios.get<Post>(`/api/posts/${route.params.id}`)
    post.value = res.data
    await fetchComments()
  } catch (e: any) {
    error.value = e.response?.data?.message || e.message || '未知错误'
  } finally {
    loading.value = false
  }
}

function goBack() {
  router.back()
}

function formatDate(iso: string) {
  const dt = new Date(iso)
  return `${dt.getFullYear()}-${String(dt.getMonth()+1).padStart(2,'0')}-${String(dt.getDate()).padStart(2,'0')}`
}

function timeAgo(iso: string) {
  const now = Date.now(), past = new Date(iso).getTime(), diff = now - past
  const m = 60e3, h = 60*m, d = 24*h
  if (diff < m) return '刚刚'
  if (diff < h) return `${Math.floor(diff/m)} 分钟前`
  if (diff < d) return `${Math.floor(diff/h)} 小时前`
  return formatDate(iso)
}

function scrollToComments() {
  if (commentsSection.value) {
    commentsSection.value.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

async function postComment() {
  const text = newComment.value.trim()
  if (!text || !post.value) return
  const res = await axios.post<Comment>(
    `/api/posts/${post.value.id}/comments`,
    { text }
  )
  comments.value.push(res.data)
  newComment.value = ''
  post.value.comments = comments.value.length
}

async function deleteComment(idx: number) {
  const c = comments.value[idx]
  if (!confirm('确定要删除这条评论吗？')) return
  await axios.delete(`/api/posts/${post.value!.id}/comments/${c.id}`)
  comments.value.splice(idx, 1)
  post.value!.comments = comments.value.length
}

async function editComment(idx: number) {
  const c = comments.value[idx]
  const newText = prompt('编辑评论内容：', c.text)?.trim()
  if (newText == null || newText === c.text) return
  await axios.put(`/api/posts/${post.value!.id}/comments/${c.id}`, { text: newText })
  comments.value[idx].text = newText
}

onMounted(fetchPost)
</script>

<style scoped>
.post-detail {
  max-width: 720px;
  margin: 0 auto;
  padding: var(--padding);
  background: var(--bg);
  color: var(--text-main);
  min-height: 100vh;
}

/* 返回按钮 */
.back-btn {
  background: none;
  border: none;
  color: var(--accent);
  margin-bottom: 16px;
  cursor: pointer;
  transition: color .2s;
}
.back-btn:hover {
  color: var(--accent-hover);
}

/* 状态提示 */
.status {
  text-align: center;
  padding: 24px 0;
  color: var(--text-main);
}
.status.error {
  color: var(--error);
}

/* 卡片 & 标题 */
.card {
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: var(--radius);
  padding: var(--padding);
}
.title {
  font-size: 1.75rem;
  line-height: 1.2;
  margin-bottom: 12px;
  color: var(--text-title);
}

/* 元信息 */
.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  background: var(--card-bg);
  border-radius: var(--radius);
  margin-bottom: 24px;
}
.pub-date {
  font-size: 0.875rem;
  color: var(--text-muted);
}
.stats {
  display: flex;
  gap: 16px;
}
.stat-item {
  display: flex;
  align-items: center;
  background: none;
  border: none;
  font-size: 0.875rem;
  color: var(--text-muted);
  cursor: pointer;
  transition: color .2s;
}
.stat-item:hover {
  color: var(--accent);
}
.stat-item svg {
  width: 18px;
  height: 18px;
  margin-right: 4px;
  stroke: currentColor;
  fill: none;
}

/* 正文 */
.content {
  font-size: 1rem;
  line-height: 1.8;
  color: var(--text-main);
  margin-bottom: 32px;
}

/* 评论区 */
.comments-section {
  border-top: 1px solid var(--border);
  padding-top: 24px;
}
.comments-title {
  font-size: 1.25rem;
  margin-bottom: 16px;
  color: var(--text-title);
}

/* 评论列表 */
.comment {
  display: flex;
  margin-bottom: 20px;
}
.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--surface);
}
.comment-body {
  margin-left: 12px;
  flex: 1;
}
.comment-meta {
  font-size: 0.875rem;
  color: var(--text-muted);
}
.comment-user {
  color: var(--text-title);
  font-weight: 500;
}
.comment-text {
  margin: 6px 0;
  font-size: 0.95rem;
  color: var(--text-main);
}
.comment-actions .action-btn {
  font-size: 0.85rem;
  color: var(--accent);
  background: none;
  border: none;
  margin-right: 8px;
  cursor: pointer;
  transition: color .2s;
}
.comment-actions .action-btn:hover {
  text-decoration: underline;
}

/* 发表评论 */
.comment-input {
  display: flex;
  align-items: flex-start;
  margin-top: 28px;
}
.comment-input textarea {
  flex: 1;
  padding: 10px;
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 0.95rem;
  color: var(--text-main);
  min-height: 100px;
}
.comment-input textarea:focus {
  outline: none;
  box-shadow: 0 0 0 2px var(--accent);
  border-color: var(--accent);
}
.comment-input textarea::placeholder {
  color: var(--text-muted);
}
.btn-submit {
  margin-left: 12px;
  padding: 10px 18px;
  background: var(--accent);
  border: none;
  border-radius: 6px;
  color: #fff;
  font-size: 0.95rem;
  cursor: pointer;
  transition: background .2s;
}
.btn-submit:disabled {
  background: var(--border);
  cursor: not-allowed;
}
.btn-submit:not(:disabled):hover {
  background: var(--accent-hover);
}
</style>
