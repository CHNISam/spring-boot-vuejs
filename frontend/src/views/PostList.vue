<template>
  <div class="post-list">
    <h2 class="title">帖子列表</h2>

    <div v-if="loading" class="status">加载中…</div>
    <div v-else-if="error" class="status error">请求出错：{{ error }}</div>
    <div v-else-if="posts.length === 0" class="status">暂无帖子。</div>

    <div v-else class="cards">
      <div
        v-for="post in posts"
        :key="post.id"
        class="card"
        @click="goToDetail(post.id)"
      >
        <!-- 头像 + 作者 + 相对时间 -->
        <div class="card-header">
          <div class="avatar" :style="avatarStyle(post.authorAvatar)"></div>
          <div class="meta">
            <div class="author">{{ post.authorName }}</div>
            <div class="time">{{ relativeTime(post.createdAt) }}</div>
          </div>
        </div>

        <!-- 标题 + 摘要 -->
        <div class="card-body">
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="excerpt">{{ excerpt(post.content) }}</p>
        </div>

        <!-- 缩略图（可选） -->
        <div v-if="post.thumbnail" class="thumb-wrapper">
          <img class="thumb" :src="post.thumbnail" />
        </div>

        <!-- 浏览／评论数 -->
        <div class="card-footer">
          <div class="foot-item">
            <svg viewBox="0 0 24 24"><path d="M12 5c-7 0-10 7-10 7s3 7 10 7 10-7 10-7-3-7-10-7z"/><circle cx="12" cy="12" r="3"/></svg>
            <span>{{ post.views }}</span>
          </div>
          <div class="foot-item">
            <svg viewBox="0 0 24 24"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2z"/></svg>
            <span>{{ post.comments }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

interface Post {
  id: number;
  title: string;
  content: string;
  authorName: string;
  authorAvatar?: string;
  createdAt: string;  // ISO 字符串
  views: number;
  comments: number;
  thumbnail?: string;
}

const router  = useRouter();
const posts   = ref<Post[]>([]);
const loading = ref(true);
const error   = ref<string|null>(null);

async function fetchPosts() {
  loading.value = true;
  error.value   = null;
  try {
    const res = await axios.get<Post[]>('/api/posts');
    posts.value = res.data;
  } catch (e: any) {
    error.value = e.response?.data?.message || e.message || '未知错误';
  } finally {
    loading.value = false;
  }
}

function goToDetail(id: number) {
  router.push({ name: 'PostDetail', params: { id } });
}

function excerpt(text: string): string {
  const max = 100;
  return text.length > max ? text.slice(0, max) + '…' : text;
}

/** 相对时间，1 小时内显示“XX分钟前”，1 天内显示“XX小时前”，否则显示 MM‑DD */
function relativeTime(iso: string): string {
  const then = new Date(iso).getTime();
  const diff = Date.now() - then;
  const m = 60*1000, h = 60*m, d = 24*h;
  if (diff < m)  return '刚刚';
  if (diff < h)  return `${Math.floor(diff/m)}分钟前`;
  if (diff < d)  return `${Math.floor(diff/h)}小时前`;
  const dt = new Date(iso);
  const mm = String(dt.getMonth()+1).padStart(2,'0');
  const dd = String(dt.getDate()).padStart(2,'0');
  return `${mm}-${dd}`;
}

function avatarStyle(url?: string) {
  return { background: url ? `url(${url}) center/cover` : '#444' };
}

onMounted(fetchPosts);
</script>

<style scoped>
.post-list {
  padding: 24px;
  background: #121212;
  color: #ddd;
  min-height: 100vh;
}
.title {
  font-size: 24px;
  margin-bottom: 16px;
  color: #fff;
}
.status {
  text-align: center;
  margin: 40px 0;
}
.status.error {
  color: #f56c6c;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px,1fr));
  gap: 16px;
}

.card {
  background: #1e1e1f;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: .2s;
}
.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.6);
}

.card-header {
  display: flex;
  align-items: center;
  padding: 12px;
}
.avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
}
.meta {
  margin-left: 12px;
}
.author {
  color: #fff;
  font-weight: 600;
}
.time {
  color: #999;
  font-size: .85em;
}

.card-body {
  padding: 0 12px 12px;
  flex: 1;
}
.post-title {
  font-size: 18px;
  color: #fff;
  margin: 0 0 8px;
}
.excerpt {
  color: #ccc;
  font-size: 14px;
  line-height: 1.4;
  margin: 0;
}

.thumb-wrapper {
  max-height: 160px;
  overflow: hidden;
}
.thumb {
  width: 100%;
  display: block;
  object-fit: cover;
}

.card-footer {
  display: flex;
  padding: 8px 12px;
  border-top: 1px solid #2a2a2a;
  background: #18181a;
}
.foot-item {
  display: flex;
  align-items: center;
  color: #aaa;
  margin-right: 16px;
  font-size: .9em;
}
.foot-item svg {
  width: 18px;
  height: 18px;
  margin-right: 4px;
  stroke: currentColor;
  fill: none;
}
</style>
