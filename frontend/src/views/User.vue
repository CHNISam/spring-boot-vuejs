<template>
  <div class="profile-page p-6 bg-dark">
    <!-- 用户信息 -->
    <div v-if="user" class="user-info bg-card rounded-lg shadow-lg p-6 mb-6">
      <!-- 头像 -->
      <div class="avatar-container mb-6 text-center">
        <div class="avatar-wrapper">
          <img :src="user.avatar || defaultAvatar" alt="Avatar" class="avatar" />
        </div>
        <!-- 隐藏的文件选择框 -->
        <input type="file" @change="handleAvatarChange" class="hidden" ref="avatarInput" />
        <!-- 点击按钮触发文件选择框 -->
        <button @click="triggerAvatarInput" class="btn-change-avatar">Change Avatar</button>
      </div>

      <!-- 用户名和ID -->
      <h1 class="text-center text-2xl font-semibold text-white">Welcome, {{ user.firstName }} {{ user.lastName }}</h1>
      <p class="text-center text-gray-400"><strong>ID:</strong> {{ user.id }}</p>

      <!-- 用户简介 -->
      <div class="mt-6">
        <h3 class="font-semibold text-xl text-white">Bio</h3>
        <p class="text-gray-300">{{ user.bio || 'No bio available.' }}</p>
        <button @click="isEditingBio = !isEditingBio" class="btn-edit mt-4">Edit Bio</button>
        <div v-if="isEditingBio" class="mt-2">
          <textarea v-model="user.bio" placeholder="Write something about yourself..." class="bio-textarea"></textarea>
          <button @click="saveBio" class="btn-save">Save</button>
        </div>
      </div>

      <!-- 用户联系信息 -->
      <div class="mt-6">
        <h3 class="font-semibold text-xl text-white">Contact Info</h3>
        <p class="text-gray-300"><strong>Email:</strong> {{ user.email }}</p>
        <button @click="isEditingEmail = !isEditingEmail" class="btn-edit mt-4">Edit Email</button>
        <div v-if="isEditingEmail" class="mt-2">
          <input type="email" v-model="user.email" placeholder="Enter new email" class="email-input" />
          <button @click="saveEmail" class="btn-save">Save</button>
        </div>
      </div>
    </div>

    <!-- 帖子列表 -->
    <div v-if="posts.length > 0" class="user-posts bg-card rounded-lg shadow-lg p-6">
      <h2 class="text-xl font-semibold text-white">Your Posts</h2>
      <div v-if="loading" class="text-gray-500">Loading posts…</div>
      <ul v-else class="space-y-4 mt-4">
        <li v-for="post in posts" :key="post.id" class="p-4 border rounded-lg shadow-sm hover:shadow-md transition-shadow bg-post hover:bg-post-hover">
          <h3 class="text-lg font-semibold text-white">{{ post.title }}</h3>
          <p class="mt-2 text-gray-300">{{ post.content }}</p>
        </li>
      </ul>
    </div>
    <div v-else class="text-gray-500">You haven't posted anything yet.</div>
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted, ref } from 'vue';

export default defineComponent({
  name: 'UserProfile',
  setup() {
    // 模拟用户数据
    const user = ref({
      id: 1,
      firstName: 'John',
      lastName: 'Doe',
      avatar: 'https://randomuser.me/api/portraits/men/10.jpg', // 示例头像
      bio: 'Hello, I am John. I am a software engineer with a passion for coding.',
      email: 'john.doe@example.com'
    });

    // 模拟帖子数据
    const posts = ref([
      { id: 1, title: 'My first post', content: 'This is the content of my first post.' },
      { id: 2, title: 'Vue.js Tips', content: 'Here are some useful Vue.js tips and tricks.' },
      { id: 3, title: 'Web Development Trends', content: 'Let\'s discuss the latest web development trends in 2025.' }
    ]);

    const loading = ref(false);
    const isEditingBio = ref(false);
    const isEditingEmail = ref(false);

    const defaultAvatar = 'https://example.com/default-avatar.png'; // 默认头像的URL

    // 上传头像
    const avatarInput = ref<HTMLInputElement | null>(null);

    const handleAvatarChange = (event: Event) => {
      const file = (event.target as HTMLInputElement).files?.[0];
      if (file) {
        // 处理头像文件上传
        const reader = new FileReader();
        reader.onload = () => {
          if (reader.result) {
            user.value.avatar = reader.result as string;
          }
        };
        reader.readAsDataURL(file);
      }
    };

    const triggerAvatarInput = () => {
      avatarInput.value?.click();
    };

    const saveBio = () => {
      alert('Bio saved');
      isEditingBio.value = false;
    };

    const saveEmail = () => {
      alert('Email saved');
      isEditingEmail.value = false;
    };

    onMounted(() => {
      loading.value = true;
      setTimeout(() => {
        loading.value = false;
      }, 1000); // 模拟 1 秒的延时加载
    });

    return { 
      user, posts, loading, 
      isEditingBio, isEditingEmail, 
      defaultAvatar, handleAvatarChange, triggerAvatarInput, saveBio, saveEmail 
    };
  }
});
</script>

<style scoped>
/* 深色主题 */
.bg-dark {
  background-color: #121212;
}

.bg-card {
  background-color: #1E1E1E;
}

.bg-post {
  background-color: #2A2A2A;
}

.bg-post-hover {
  background-color: #3A3A3A;
}

.text-gray-400 {
  color: #b0b0b0;
}

.text-gray-300 {
  color: #e0e0e0;
}

.text-white {
  color: white;
}

/* 头像和按钮样式 */
.avatar-wrapper {
  position: relative;
  display: inline-block;
  overflow: hidden;
  width: 120px;
  height: 120px;
  border-radius: 50%;
  background: linear-gradient(45deg, #6CC1FF, #3F7BFF);
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.btn-change-avatar {
  margin-top: 10px;
  padding: 8px 20px;
  background-color: #4CAF50;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 4px;
}

.btn-change-avatar:hover {
  background-color: #45a049;
}

h1, h3 {
  font-size: 1.5rem;
}

.text-center {
  text-align: center;
}

/* 编辑 bio 和 email 输入框样式 */
.bio-textarea, .email-input {
  width: 100%;
  padding: 10px;
  margin-top: 10px;
  border: 1px solid #444;
  border-radius: 8px;
  background-color: #333;
  color: white;
}

.bio-textarea {
  height: 100px;
}

.btn-save {
  margin-top: 10px;
  padding: 10px 25px;
  background-color: #2196F3;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 4px;
}

.btn-save:hover {
  background-color: #0b7dda;
}

.user-posts h2 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
}

.user-posts ul {
  list-style-type: none;
  padding-left: 0;
}

.user-posts li {
  padding: 12px;
  border: 1px solid #444;
  border-radius: 8px;
  background-color: #1E1E1E;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.user-posts li:hover {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.user-posts li h3 {
  font-size: 1.2rem;
  font-weight: bold;
}
</style>
