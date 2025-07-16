<template>
    <div class="post-view">
        <h1>Create Post</h1>
        <input v-model="title" placeholder="Title" />
        <textarea v-model="content" placeholder="Content"></textarea>
        <button @click="createNewPost">Publish</button>
        <p v-if="postId">New post id: {{ postId }}</p>
    </div>
</template>

<script lang="ts">
import api from '@/api/backend-api'
import { defineComponent, ref } from 'vue'

export default defineComponent({
    name: 'PostEditor',
    setup() {
        const title = ref('')
        const content = ref('')
        const postId = ref<number | null>(null)

        async function createNewPost() {
            console.log('📝 Creating post:', title.value, content.value);
            try {
                const res = await api.createPost(title.value, content.value);
                console.log('✅ createPost response.id =', res.data);
                postId.value = res.data;
            } catch (e) {
                console.error('Failed to create post:', e);
            }
        }


        return { title, content, postId, createNewPost }
    }
})
</script>

<style scoped>
.post-view {
    max-width: 600px;
    margin: 0 auto;
    padding: 24px;
    color: #ddd;
    overflow: auto; /* 允许滚动 */
}

/* 文本框也启用滚动条（当内容过长时） */
.post-view textarea {
    resize: vertical;
    overflow: auto;
}

/* 滚动条宽度/高度 */
.post-view::-webkit-scrollbar,
.post-view textarea::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

/* 轨道 */
.post-view::-webkit-scrollbar-track,
.post-view textarea::-webkit-scrollbar-track {
  background: #202020;
  border-radius: 4px;
}

/* 滑块 */
.post-view::-webkit-scrollbar-thumb,
.post-view textarea::-webkit-scrollbar-thumb {
  background-color: #555;
  border-radius: 4px;
  border: 2px solid #202020;
}

/* 滑块 hover */
.post-view::-webkit-scrollbar-thumb:hover,
.post-view textarea::-webkit-scrollbar-thumb:hover {
  background-color: #777;
}

.post-view input,
.post-view textarea {
    width: 100%;
    margin: 8px 0;
    padding: 8px;
    background: #282828;
    border: 1px solid #303030;
    color: #e0e0e0;
    border-radius: 4px;
}

.post-view button {
    margin-top: 12px;
    padding: 8px 16px;
    background: #42b983;
    border: none;
    color: #fff;
    border-radius: 4px;
    cursor: pointer;
}

.post-view button:hover {
    background: #369f6c;
}
</style>
