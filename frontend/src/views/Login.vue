<template>
  <div class="login-page">
    <LoginForm
      :loginError="loginError"
      :error="errorMsg"
      @submit="handleSubmit"
    />
  </div>
</template>

<script lang="ts">
import LoginForm from '@/components/login/LoginForm.vue'
import axios, { AxiosError } from 'axios'
import { defineComponent, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useStore } from 'vuex'

export default defineComponent({
  name: 'LoginView',
  components: { LoginForm },
  setup() {
    const store = useStore()
    const router = useRouter()

    // 登录失败开关和错误信息
    const loginError = ref(false)
    const errorMsg = ref<string | boolean>(false)

    // 处理 LoginForm 的 submit 事件
    async function handleSubmit(payload: {
      user: string
      password: string
      done: () => void
    }) {
      loginError.value = false
      errorMsg.value = false

      try {
        await store.dispatch('login', {
          user: payload.user,
          password: payload.password
        })
        // 登录成功后跳转
        router.push('/Protected')
      } catch (err: unknown) {
        loginError.value = true

        // 如果是 AxiosError，就尝试取后端返回的 message
        if (axios.isAxiosError(err)) {
          const axiosErr = err as AxiosError<{ message?: string }>
          errorMsg.value =
            axiosErr.response?.data?.message ||
            'Bad login information'
        } else if (err instanceof Error) {
          // 其它 Error，使用它的 message
          errorMsg.value = err.message
        } else {
          // 万一都不是，用通用提示
          errorMsg.value = 'An unexpected error occurred'
        }
      } finally {
        // 停止按钮加载状态
        payload.done()
      }
    }

    return {
      loginError,
      errorMsg,
      handleSubmit
    }
  }
})
</script>

<style scoped>
.login-page {
  /* 让 LoginForm 组件全屏铺满 */
  height: 100vh;
  margin: 0;
  padding: 0;
}
</style>
