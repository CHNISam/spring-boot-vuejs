<template>
  <div id="app">
    <AppHeader
      @toggle-sidebar="collapsed = !collapsed"
      @search="onSearch"
      @refresh="goHome"
    />
    <div class="app-layout">
      <AppSidebar :collapsed="collapsed" />
      <main
        class="app-main"
        :style="{ marginLeft: collapsed ? '0' : expandedWidth + 'px' }"
      >
        <router-view />
      </main>
    </div>
  </div>
</template>

<script lang="ts">
import AppHeader from '@/components/layout/AppHeader.vue'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import { defineComponent, ref } from 'vue'
import { useRouter } from 'vue-router'

export default defineComponent({
  name: 'App',
  components: { AppHeader, AppSidebar },
  setup() {
    const collapsed = ref(false)
    const expandedWidth = 160  // 必须与 Sidebar 展开宽度保持一致
    const router = useRouter()

    function onSearch(q: string) {
      if (q?.trim()) {
        router.push({ name: 'Search', query: { q: q.trim() } })
      }
    }
    function goHome() {
      router.push({ name: 'Home' })
    }

    return { collapsed, expandedWidth, onSearch, goHome }
  }
})
</script>
<style>
:root {
  /* 整体背景和卡片背景 */
  --bg: #121212;
  --card-bg: rgba(30, 30, 31, 0.9);
  --border: #2a2a2a;
  --radius: 8px;
  --padding: 24px;

  /* 文本配色 */
  --text-title: #FFFFFF;   /* 标题 */
  --text-main: #E0E0E0;    /* 正文 */
  --text-muted: #A0A0A0;   /* 次要信息 */

  /* 强调色 */
  --accent: #4A90E2;
  --accent-hover: #6AAFFF;

  /* 错误色 */
  --error: #CF6679;
}

/* 全局标题风格 */
.title, h1, h2 {
  color: var(--text-title);
}
</style>

<style lang="scss" scoped>
#app {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-layout {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.app-main {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  background: #121212;
  color: #ddd;
  transition: margin-left 0.2s ease;
}
</style>
