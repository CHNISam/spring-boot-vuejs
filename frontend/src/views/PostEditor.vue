<template>
  <div class="post-view">
    <h1>Create Post</h1>

    <label>
      Title ({{ title.length }} / 30)
      <input
        v-model="title"
        placeholder="Title"
        maxlength="30"
      />
    </label>

    <label>
      Content ({{ content.length }} / 30000)
      <textarea
        v-model="content"
        placeholder="Content"
        maxlength="30000"
        rows="6"
      ></textarea>
    </label>

    <button
      @click="createNewPost"
      :disabled="!canPublish"
    >
      Publish
    </button>

    <p v-if="postId">New post id: {{ postId }}</p>
  </div>
</template>

<script lang="ts">
import api from '@/api/backend-api'
import { computed, defineComponent, ref } from 'vue'

export default defineComponent({
  name: 'PostEditor',
  setup() {
    const title = ref('')
    const content = ref('')
    const postId = ref<number | null>(null)

    // 是否可发布：标题非空且不超30，内容长度不超30000
    const canPublish = computed(() => {
      return (
        title.value.trim().length > 0 &&
        title.value.length <= 30 &&
        content.value.length <= 30000
      )
    })

    async function createNewPost() {
      console.log('📝 Creating post:', title.value, content.value)
      try {
        const res = await api.createPost(title.value, content.value)
        console.log('✅ createPost response.id =', res.data)
        postId.value = res.data
      } catch (e) {
        console.error('Failed to create post:', e)
      }
    }

    return {
      title,
      content,
      postId,
      canPublish,
      createNewPost,
    }
  },
})
</script>

<style scoped>
.post-view {
  max-width: 600px;
  margin: 0 auto;
  padding: 24px;
  color: #ddd;
  overflow: auto;
}

.post-view label {
  display: block;
  margin-bottom: 16px;
  font-size: 14px;
  color: #aaa;
}

.post-view input,
.post-view textarea {
  width: 100%;
  margin-top: 4px;
  padding: 8px;
  background: #282828;
  border: 1px solid #303030;
  color: #e0e0e0;
  border-radius: 4px;
}

.post-view textarea {
  resize: vertical;
  overflow: auto;
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

.post-view button:disabled {
  background: #555;
  cursor: not-allowed;
}

/* 自定义滚动条 */
.post-view::-webkit-scrollbar,
.post-view textarea::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

.post-view::-webkit-scrollbar-track,
.post-view textarea::-webkit-scrollbar-track {
  background: #202020;
  border-radius: 4px;
}

.post-view::-webkit-scrollbar-thumb,
.post-view textarea::-webkit-scrollbar-thumb {
  background-color: #555;
  border-radius: 4px;
  border: 2px solid #202020;
}

.post-view::-webkit-scrollbar-thumb:hover,
.post-view textarea::-webkit-scrollbar-thumb:hover {
  background-color: #777;
}
</style>
