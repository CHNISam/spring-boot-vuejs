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
import { defineComponent, ref } from 'vue'
import { useRouter } from 'vue-router'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppSidebar from '@/components/layout/AppSidebar.vue'

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
