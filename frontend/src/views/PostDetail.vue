<template>
  <div class="post-detail">
    <!-- Back button -->
    <button class="back-btn" @click="goBack">← Back</button>

    <!-- Loading / Error -->
    <div v-if="loading" class="status">Loading…</div>
    <div v-else-if="error" class="status error">Error loading: {{ error }}</div>

    <!-- Content -->
    <div v-else>
      <div v-if="post" class="card">
        <!-- Title -->
        <h1 class="title">{{ post.title }}</h1>

        <!-- Meta info -->
        <div class="meta">
          <span class="pub-date">{{ formatDate(post.createdAt) }}</span>
          <div class="stats">
            <button
              class="stat-item views"
              @click="scrollToComments"
              aria-label="View views"
            >
              <svg viewBox="0 0 24 24">
                <path d="M12 5c-7 0-10 7-10 7s3 7 10 7 10-7 10-7-3-7-10-7z"/>
                <circle cx="12" cy="12" r="3"/>
              </svg>
              <span>{{ post.views }}</span>
            </button>
            <button
              class="stat-item comments"
              @click="scrollToComments"
              aria-label="View comments"
            >
              <svg viewBox="0 0 24 24">
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2z"/>
              </svg>
              <span>{{ post.comments }}</span>
            </button>
          </div>
        </div>

        <!-- Body -->
        <div v-if="post.content" class="content" v-html="post.content"></div>
        <div v-else class="content">No content available for this post.</div>

        <!-- Comments Section -->
        <div class="comments-section" ref="commentsSection" id="comments">
          <h2 class="comments-title">Comments ({{ comments.length }})</h2>

          <div v-if="comments.length === 0" class="no-comments">
            No comments yet. Be the first to comment!
          </div>

          <!-- Flattened comments & replies -->
          <div
            v-for="c in comments"
            :key="c.id"
            class="comment"
            :class="{ reply: c.depth > 0 }"
            :style="{ marginLeft: c.depth * 24 + 'px' }"
          >
            <div class="comment-avatar"></div>
            <div class="comment-body">
              <div class="comment-meta">
                <span class="comment-user">{{ c.username }}</span>
                <span class="sep">·</span>
                <span class="comment-time">{{ timeAgo(c.createdAt) }}</span>
              </div>

              <div class="comment-text">
                <template v-if="c.replyToId">
                  <span class="mention">@{{ replyName(c.replyToId) }}</span>
                </template>
                {{ c.text }}
              </div>

              <div class="comment-actions-row">
                <button class="action-btn reply" @click="startReply(c)">Reply</button>
                <template v-if="currentUserId !== null && c.userId === currentUserId">
                  <button class="action-btn edit" @click="editComment(c)">Edit</button>
                  <button class="action-btn delete" @click="deleteComment(c)">Delete</button>
                </template>
              </div>
            </div>
          </div>

          <!-- Reply banner -->
          <transition name="fade">
            <div v-if="replyTarget" class="reply-banner">
              Replying to @{{ replyTarget.username }}
              <button class="reply-cancel-btn" @click="cancelReply" aria-label="Cancel reply">×</button>
            </div>
          </transition>

          <!-- New comment input -->
          <div class="comment-input">
            <textarea
              v-model="newComment"
              placeholder="Write a comment…"
              @keydown.enter.ctrl.exact.prevent="postComment"
              @keydown.meta.enter.exact.prevent="postComment"
            ></textarea>
            <button
              class="btn-submit"
              @click="postComment"
              :disabled="!newComment.trim()"
            >
              Post
            </button>
          </div>
        </div>
      </div>

      <div v-else class="status">Post not found or has been deleted.</div>
    </div>
  </div>
</template>


<script lang="ts" setup>
import axios from 'axios'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

/* ------------ Types ------------ */
interface Post {
  id: number
  title: string
  content: string
  createdAt: string
  views: number
  comments: number
}

interface CommentRaw {
  id: number
  userId: number
  username: string
  replyToId?: number | null
  text: string
  createdAt: string
}

interface CommentDisplay extends CommentRaw {
  depth: number
}

/* ------------ Refs ------------ */
const route = useRoute()
const router = useRouter()

const post = ref<Post|null>(null)
const comments = ref<CommentDisplay[]>([])
const commentRaw = ref<CommentRaw[]>([])
const userMap = ref<Record<number,string>>({})
const loading = ref(true)
const error = ref<string|null>(null)
const newComment = ref('')
const replyTarget = ref<CommentRaw|null>(null)
const commentsSection = ref<HTMLElement|null>(null)
const currentUserId = ref<number|null>(null)

/* ------------ Init ------------ */
onMounted(async () => {
  await fetchCurrentUser()
  await fetchPost()
})

/* ------------ API ------------ */
async function fetchCurrentUser() {
  try {
    const res = await axios.get<{id:number, username?:string}>('/api/me')
    currentUserId.value = res.data.id
  } catch {
    currentUserId.value = null
  }
}

async function fetchPost() {
  loading.value = true
  error.value = null
  try {
    const res = await axios.get<Post>(`/api/posts/${route.params.id}`)
    post.value = res.data
    await fetchComments()
  } catch (e: any) {
    error.value = e.response?.data?.message || e.message || 'Unknown error'
  } finally {
    loading.value = false
  }
}

async function fetchComments() {
  if (!post.value) return
  const res = await axios.get<CommentRaw[]>(`/api/posts/${post.value.id}/comments`)
  commentRaw.value = res.data
  buildUserMap()
  buildThread()
  post.value.comments = commentRaw.value.length
}

/* ------------ Helpers ------------ */
function buildUserMap() {
  const map: Record<number,string> = {}
  commentRaw.value.forEach(c => { map[c.id] = c.username })
  userMap.value = map
}

function buildThread() {
  const sorted = [...commentRaw.value].sort(
    (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()
  )
  const children: Record<number, CommentRaw[]> = {}
  sorted.forEach(c => {
    if (c.replyToId) (children[c.replyToId] ||= []).push(c)
  })
  const flat: CommentDisplay[] = []
  sorted.filter(c => !c.replyToId).forEach(root => {
    flat.push({ ...root, depth: 0 })
    pushChildren(root, 1, flat, children)
  })
  comments.value = flat
}

function pushChildren(
  parent: CommentRaw,
  depth: number,
  out: CommentDisplay[],
  children: Record<number, CommentRaw[]>
) {
  const kids = children[parent.id]
  if (!kids) return
  kids.forEach(k => {
    out.push({ ...k, depth })
    pushChildren(k, depth + 1, out, children)
  })
}

function replyName(id?: number | null) {
  if (!id) return ''
  return userMap.value[id] ?? 'Unknown'
}

function goBack() {
  router.back()
}

function formatDate(iso: string) {
  const dt = new Date(iso)
  return `${dt.getFullYear()}-${String(dt.getMonth()+1).padStart(2,'0')}-${String(dt.getDate()).padStart(2,'0')}`
}

function timeAgo(iso: string) {
  const now = Date.now(),
        past = new Date(iso).getTime(),
        diff = now - past
  const m = 60e3, h = 60*m, d = 24*h
  if (diff < m) return 'just now'
  if (diff < h) return `${Math.floor(diff/m)} minutes ago`
  if (diff < d) return `${Math.floor(diff/h)} hours ago`
  return formatDate(iso)
}

function scrollToComments() {
  commentsSection.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

/* ------------ Reply Flow ------------ */
function startReply(c: CommentRaw | CommentDisplay) {
  replyTarget.value = {
    id: c.id,
    userId: c.userId,
    username: c.username,
    replyToId: c.replyToId,
    text: c.text,
    createdAt: c.createdAt
  }
}

function cancelReply() {
  replyTarget.value = null
}

/* ------------ CRUD ------------ */
async function postComment() {
  const txt = newComment.value.trim()
  if (!txt || !post.value) return
  const payload: any = { text: txt }
  if (replyTarget.value) payload.replyToId = replyTarget.value.id

  await axios.post<CommentRaw>(
    `/api/posts/${post.value.id}/comments`,
    payload
  )

  newComment.value = ''
  replyTarget.value = null
  await fetchComments()
}

async function deleteComment(c: CommentDisplay) {
  if (!post.value) return
  if (!confirm('Are you sure you want to delete this comment?')) return
  await axios.delete(`/api/posts/${post.value.id}/comments/${c.id}`)
  await fetchComments()
}

async function editComment(c: CommentDisplay) {
  if (!post.value) return
  const txt = prompt('Edit comment:', c.text)?.trim()
  if (txt == null || txt === c.text) return
  const payload: any = { text: txt, replyToId: c.replyToId ?? null }
  await axios.put(`/api/posts/${post.value.id}/comments/${c.id}`, payload)
  await fetchComments()
}
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

/* 返回 */
.back-btn {
  background: none;
  border: none;
  color: var(--accent);
  margin-bottom: 16px;
  cursor: pointer;
  transition: color .2s;
}
.back-btn:hover { color: var(--accent-hover); }

/* 状态 */
.status {
  text-align: center;
  padding: 24px 0;
  color: var(--text-main);
}
.status.error { color: var(--error); }

/* 卡片 */
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
  background: var(--surface);
  border-radius: var(--radius);
  margin-bottom: 24px;
}
.pub-date {
  font-size: .875rem;
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
  font-size: .875rem;
  color: var(--text-muted);
  cursor: pointer;
  transition: color .2s;
}
.stat-item:hover { color: var(--accent); }
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
.no-comments {
  padding: 24px;
  text-align: center;
  color: var(--text-muted);
}

/* 评论列表 */
.comment {
  display: flex;
  margin-bottom: 20px;
  position: relative;
}
.comment.reply::before {
  content: '';
  position: absolute;
  left: -12px;
  top: 20px;
  bottom: -8px;
  width: 2px;
  background: var(--border);
}
.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: var(--surface);
  flex-shrink: 0;
}
.comment-body {
  margin-left: 12px;
  flex: 1;
}
.comment-meta {
  font-size: .875rem;
  color: var(--text-muted);
}
.comment-user {
  color: var(--text-title);
  font-weight: 500;
}
.comment-text {
  margin: 6px 0;
  font-size: .95rem;
  color: var(--text-main);
  line-height: 1.6;
}
.mention {
  color: var(--accent);
  margin-right: 4px;
}

/* 行为区 */
.comment-actions-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-top: 4px;
}
.action-btn {
  background: none;
  border: none;
  padding: 0;
  font-size: .85rem;
  cursor: pointer;
  transition: color .15s;
}
.action-btn.reply { color: var(--accent); }
.action-btn.edit { color: var(--accent); }
.action-btn.delete { color: var(--error); }
.action-btn:hover { text-decoration: underline; }

/* 正在回复提示条 */
.reply-banner {
  margin-top: 16px;
  margin-bottom: 4px;
  padding: 6px 12px;
  font-size: .875rem;
  color: var(--accent);
  background: rgba(0,0,0,.3);
  border: 1px solid var(--accent);
  border-radius: var(--radius);
  display: inline-flex;
  align-items: center;
  gap: 8px;
}
.reply-cancel-btn {
  background: none;
  border: none;
  color: var(--accent-hover);
  cursor: pointer;
  font-size: 1.1em;
  line-height: 1;
}

/* 输入 */
.comment-input {
  display: flex;
  align-items: flex-start;
  margin-top: 20px;
}
.comment-input textarea {
  flex: 1;
  padding: 10px;
  background: var(--card-bg);
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: .95rem;
  color: var(--text-main);
  min-height: 100px;
  resize: vertical;
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
  font-size: .95rem;
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

/* 小过渡 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity .15s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
