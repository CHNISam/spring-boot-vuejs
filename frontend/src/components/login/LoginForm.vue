<!-- src/components/login/LoginForm.vue -->
<template>
  <div class="fullpage-login" tabindex="0" @keyup.enter="onSubmit">
    <div class="login-box">
      <!-- 动画标题，白色文字 -->
      <AnimatedTitle :text="title" />

      <!-- 副标题 -->
      <p class="subtitle">{{ subtitle }}</p>

      <form @submit.prevent="onSubmit" autocomplete="on">
        <input
          v-model="user"
          type="text"
          placeholder="Username"
          :class="{ 'has-error': error && !loginError }"
        />
        <input
          v-model="password"
          type="password"
          placeholder="Password"
          :class="{ 'has-error': error && !loginError }"
        />

        <p v-if="error" class="error-message">
          {{ loginError ? 'Bad login information' : error }}
        </p>

        <button type="submit" class="btn-login" :disabled="loading">
          <span v-if="!loading">{{ loginError ? 'Retry Login' : 'Login' }}</span>
          <span v-else>…</span>
        </button>
      </form>
    </div>
  </div>
</template>

<script lang="ts">
import AnimatedTitle from '@/components/login/AnimatedTitle.vue'
import { computed, defineComponent, ref } from 'vue'

export default defineComponent({
  name: 'LoginForm',
  components: { AnimatedTitle },
  props: {
    loginError: { type: Boolean, required: true },
    error:      { type: [Boolean, String], required: true }
  },
  emits: ['submit'],
  setup(_, { emit }) {
    const user = ref('')
    const password = ref('')
    const loading = ref(false)

    const title = computed(() =>
      _.loginError
        ? "You don't have rights here, mate :D"
        : 'Login to Captured Moments'
    )
    const subtitle = computed(() =>
      _.loginError
        ? 'Seems that you don’t have access rights…'
        : "You're not logged in yet. Please enter your credentials."
    )

    function onSubmit() {
      if (loading.value) return
      loading.value = true
      emit('submit', { user: user.value, password: password.value, done: () => (loading.value = false) })
    }

    return { user, password, loading, title, subtitle, onSubmit }
  }
})
</script>

<style scoped>
.fullpage-login {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  /* 银灰色暗背景 */
  background: #1f1f1f;
}

.login-box {
  width: 100%;
  max-width: 400px;
  padding: 24px;
  backdrop-filter: blur(8px);
  /* 半透明银灰 */
  background: rgba(220, 220, 220, 0.08);
  border-radius: 12px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.6);
  text-align: center;
}

.subtitle {
  margin-bottom: 24px;
  color: #ddd;
  font-size: 14px;
}

/* 输入框：银灰白暗主题 */
input {
  width: 100%;
  padding: 10px 12px;
  margin-bottom: 16px;
  background: #2b2b2b;
  border: 1px solid #555;
  border-radius: 6px;
  color: #f5f5f5;
  font-size: 14px;
  transition: border-color 0.2s, background 0.2s;
}
input::placeholder {
  color: #999;
}
input:focus {
  outline: none;
  border-color: #bbb;
  background: #333;
}
.has-error {
  border-color: #e57373 !important;
}

/* 错误提示 */
.error-message {
  margin-bottom: 16px;
  color: #f28b82;
  font-size: 13px;
}

.btn-login {
  width: 100%;
  padding: 12px;
  /* 深银灰背景 */
  background: #6e6e6e;
  color: #f5f5f5;
  font-size: 15px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}

.btn-login:hover:not(:disabled) {
  /* 略微提亮 */
  background: #8a8a8a;
}

.btn-login:disabled {
  /* 禁用态更暗 */
  background: #444;
  color: #888;
  cursor: not-allowed;
}

</style>
