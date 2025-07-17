<template>
  <div class="card" @click="emit('click')">
    <div class="card-header">
      <div class="avatar" :style="avatarStyle"></div>
      <div class="meta">
        <div class="author">{{ post.authorName }}</div>
        <div class="time">{{ relativeTime }}</div>
      </div>
    </div>

    <div class="card-body">
      <h3 class="post-title" v-html="highlightedTitle"></h3>
      <p class="excerpt" v-html="highlightedExcerpt"></p>
    </div>

    <img
      v-if="post.thumbnail"
      class="thumb"
      :src="post.thumbnail"
      alt="缩略图"
    />

    <div class="card-footer">
      <span class="views">👁 {{ post.views }}</span>
      <span class="comments">💬 {{ post.comments }}</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed, defineEmits, defineProps } from 'vue'

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

const props = defineProps<{
  post: Post
  query?: string
}>()

// 数组形式声明 emits，TS 正确推断
const emit = defineEmits(['click'] as const)

const highlightedTitle = computed(() => {
  if (!props.query) return props.post.title
  const re = new RegExp(`(${props.query})`, 'gi')
  return props.post.title.replace(re, '<mark>$1</mark>')
})

const rawExcerpt = computed(() => {
  const txt = props.post.content.slice(0, 100)
  return props.post.content.length > 100 ? txt + '…' : txt
})

const highlightedExcerpt = computed(() => {
  if (!props.query) return rawExcerpt.value
  const re = new RegExp(`(${props.query})`, 'gi')
  return rawExcerpt.value.replace(re, '<mark>$1</mark>')
})

const avatarStyle = computed(() => ({
  background: props.post.authorAvatar
    ? `url(${props.post.authorAvatar}) center/cover`
    : '#444',
}))

const relativeTime = computed(() => {
  const then = new Date(props.post.createdAt).getTime()
  const diff = Date.now() - then
  const m = 60 * 1000, h = 60 * m, d = 24 * h
  if (diff < m) return '刚刚'
  if (diff < h) return `${Math.floor(diff / m)} 分钟前`
  if (diff < d) return `${Math.floor(diff / h)} 小时前`
  const dt = new Date(props.post.createdAt)
  return `${String(dt.getMonth() + 1).padStart(2, '0')}-${String(
    dt.getDate()
  ).padStart(2, '0')}`
})
</script>

<style scoped>
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
  display: flex; align-items: center; padding: 10px;
}
.avatar {
  width: 36px; height: 36px; border-radius: 50%;
}
.meta {
  margin-left: 10px; display: flex; flex-direction: column;
}
.author {
  font-size: .85rem; font-weight: 500; color: var(--text-title);
}
.time {
  font-size: .75rem; color: var(--text-muted); margin-top: 2px;
}

.card-body {
  padding: 0 10px 10px; flex: 1;
}
.post-title {
  font-size: .95rem; font-weight: 600; margin: 0 0 6px; color: var(--text-title);
}
.excerpt {
  font-size: .8rem; line-height: 1.4; color: var(--text-main);
  display: -webkit-box; -webkit-line-clamp: 1; -webkit-box-orient: vertical; overflow: hidden;
}
mark {
  background: rgba(255,215,0,0.4);
}

.thumb {
  width: 100%; height: 140px; object-fit: cover;
}

.card-footer {
  display: flex; align-items: center; padding: 6px 10px; border-top: 1px solid var(--border);
}
.views, .comments {
  font-size: .8rem; color: var(--text-muted); margin-right: 12px;
}
</style>
