<template>
  <div class="profile-page p-4">
    <!-- 用户信息 -->
    <div class="user-info mb-6">
      <h1>Welcome, {{ user.firstName }} {{ user.lastName }}</h1>
      <p><strong>ID:</strong> {{ user.id }}</p>
    </div>

    <!-- 帖子列表 -->
    <div class="user-posts">
      <h2>Your Posts</h2>
      <div v-if="loading" class="text-gray-500">Loading posts…</div>
      <div v-else-if="posts.length === 0" class="text-gray-500">You haven't posted anything yet.</div>
      <ul v-else class="space-y-4">
        <li v-for="post in posts" :key="post.id" class="p-4 border rounded-lg">
          <h3 class="text-lg font-semibold">{{ post.title }}</h3>
          <p class="mt-2">{{ post.content }}</p>
        </li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, onMounted, ref } from 'vue';
import { useStore } from 'vuex';
import api, { Post } from '../api/backend-api';

export default defineComponent({
  name: 'Profile',
  setup() {
    const store = useStore();
    // 从 Vuex 里拿到 profile
    const user = computed(() => store.getters.currentUser!);

    const posts = ref<Post[]>([]);
    const loading = ref(false);
    const error = ref<string | null>(null);

    // 拉取当前用户的帖子
    async function loadPosts() {
      if (!user.value) return;
      loading.value = true;
      error.value = null;
      try {
        const resp = await api.getPostsByUser(user.value.id);
        posts.value = resp.data;
      } catch (e: any) {
        error.value = e.message || 'Failed to load posts';
      } finally {
        loading.value = false;
      }
    }

    onMounted(loadPosts);

    return { user, posts, loading, error };
  }
});
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
}
.user-info h1 {
  font-size: 2rem;
}
.user-posts h2 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}
</style>
