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
        <!-- 头部：头像 + 作者 + 时间 -->
        <div class="card-header">
          <div
            class="avatar"
            :style="avatarStyle(post.authorAvatar)"
          ></div>
          <div class="meta">
            <div class="author">{{ post.authorName }}</div>
            <div class="time">{{ relativeTime(post.createdAt) }}</div>
          </div>
        </div>

        <!-- 正文：标题 + 摘要 -->
        <div class="card-body">
          <h3 class="post-title">{{ post.title }}</h3>
          <p class="excerpt">{{ excerpt(post.content) }}</p>
        </div>

        <!-- 缩略图（可选）-->
        <div v-if="post.thumbnail" class="thumb-wrapper">
          <img class="thumb" :src="post.thumbnail" alt="缩略图" />
        </div>

        <!-- 底部：浏览量／评论量 -->
        <div class="card-footer">
          <div class="foot-item">
            <!-- 眼睛图标 -->
            <svg viewBox="0 0 24 24"><path d="M12 5c-7 0-10 7-10 7s3 7 10 7 10-7 10-7-3-7-10-7z"/><circle cx="12" cy="12" r="3"/></svg>
            <span>{{ post.views }}</span>
          </div>
          <div class="foot-item">
            <!-- 评论图标 -->
            <svg viewBox="0 0 24 24"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2z"/></svg>
            <span>{{ post.comments }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import axios from 'axios'
import { defineComponent, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

interface Post {
  id: number
  title: string
  content: string
  authorName: string
  authorAvatar?: string
  createdAt: string  // ISO 时间
  views: number
  comments: number
  thumbnail?: string
}

export default defineComponent({
  name: 'PostList',
  setup() {
    const router = useRouter()
    const posts = ref<Post[]>([])
    const loading = ref(true)
    const error = ref<string | null>(null)

    async function fetchPosts() {
      loading.value = true
      error.value = null
      try {
        const res = await axios.get<Post[]>('/api/posts')
        posts.value = res.data
      } catch (e: any) {
        error.value = e.response?.data?.message || e.message || '未知错误'
      } finally {
        loading.value = false
      }
    }

    function goToDetail(id: number) {
      router.push(`/posts/${id}`)
    }

    /** 截取正文摘要，最长 100 字 */
    function excerpt(text: string): string {
      const max = 100
      return text.length > max ? text.slice(0, max) + '…' : text
    }

    /** 相对时间显示 */
    function relativeTime(iso: string): string {
      const then = new Date(iso).getTime()
      const now = Date.now()
      const diff = now - then
      const minute = 60 * 1000
      const hour = 60 * minute
      const day = 24 * hour

      if (diff < minute) {
        return '刚刚'
      } else if (diff < hour) {
        const m = Math.floor(diff / minute)
        return `${m}分钟前`
      } else if (diff < day) {
        const h = Math.floor(diff / hour)
        return `${h}小时前`
      } else {
        const d = new Date(iso)
        // 只显示月-日
        return `${String(d.getMonth() + 1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
      }
    }

    /** 头像样式 */
    function avatarStyle(url?: string) {
      return {
        backgroundImage: url ? `url(${url})` : '',
      }
    }

    onMounted(fetchPosts)

    return {
      posts,
      loading,
      error,
      goToDetail,
      excerpt,
      relativeTime,
      avatarStyle,
    }
  },
})
</script>

<style scoped>
.post-list {
  padding: 24px;
  background: #121212;
  color: #ddd;
  min-height: 100vh;
}
.title {
  margin-bottom: 16px;
  font-size: 24px;
  color: #fff;
}
.status {
  text-align: center;
  margin: 40px 0;
}
.status.error {
  color: #f56363;
}

.cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.card {
  background: #1e1e1f;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  cursor: pointer;
  transition: transform .2s, box-shadow .2s;
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
  width: 40px; height: 40px;
  border-radius: 50%;
  background: #333 center/cover no-repeat;
  flex-shrink: 0;
}
.meta {
  margin-left: 12px;
  line-height: 1.2;
}
.author {
  font-weight: 600;
  color: #fff;
}
.time {
  font-size: 0.85em;
  color: #999;
}

.card-body {
  padding: 0 12px 12px;
  flex: 1;
}
.post-title {
  margin: 0 0 8px;
  font-size: 18px;
  color: #fff;
}
.excerpt {
  margin: 0;
  font-size: 14px;
  color: #ccc;
  line-height: 1.4;
}

.thumb-wrapper {
  width: 100%; 
  max-height: 160px;
  overflow: hidden;
}
.thumb {
  width: 100%; height: 100%;
  object-fit: cover;
  display: block;
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
  margin-right: 16px;
  color: #aaa;
  font-size: 0.9em;
}
.foot-item svg {
  width: 18px; height: 18px;
  margin-right: 4px;
  fill: none;
  stroke: currentColor;
}
</style>
