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
  /* 背景色系 */
  --bg: #181818;
  --card-bg: rgba(40, 40, 42, 0.95);
  --surface: #2C2C2C;
  --border: #525252;

  /* 文本色系 */
  --text-main: #EAEAEA;
  --text-muted: #C0C0C0;

  /* 科技蓝色系强调色 */
  --accent: #2196F3;        /* 主强调 —— 现代科技蓝 */
  --accent-hover: #1E88E5;  /* 悬停态 —— 稍微深一点的蓝 */
  --error: #CF6679;

  /* 通用圆角 & 间距 */
  --radius: 8px;
  --padding: 24px;
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
