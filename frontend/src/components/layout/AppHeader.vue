<template>
  <header class="app-header">
    <div class="header-left">
      <button class="icon-btn" @click="$emit('toggle-sidebar')" aria-label="切换侧栏">
        <svg viewBox="0 0 24 24" stroke="currentColor" fill="none" stroke-width="2">
          <path stroke-linecap="round" stroke-linejoin="round" d="M4 6h16M4 12h16M4 18h16" />
        </svg>
      </button>
      <router-link to="/" class="logo" @click="$emit('refresh')">Captured Moments</router-link>
    </div>

    <form class="header-center" @submit.prevent="onSearch">
      <input
        v-model="searchQuery"
        class="search-input"
        type="text"
        placeholder="Search…"
        aria-label="搜索"
      />
      <button type="submit" class="search-btn" aria-label="搜索">
        <svg viewBox="0 0 24 24" stroke="currentColor" fill="none" stroke-width="2">
          <circle cx="11" cy="11" r="8" />
          <line x1="21" y1="21" x2="16.65" y2="16.65" />
        </svg>
      </button>
    </form>

    <nav class="header-right">
      <router-link to="/">Home</router-link>
      <router-link to="/callservice">Service</router-link>
      <router-link to="/bootstrap">Bootstrap</router-link>
      <router-link to="/user">User</router-link>
      <router-link to="/login">Login</router-link>
      <router-link to="/protected">Protected</router-link>
    </nav>
  </header>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'

export default defineComponent({
  name: 'AppHeader',
  emits: ['toggle-sidebar', 'refresh', 'search'],
  setup(_, { emit }) {
    const searchQuery = ref('')
    function onSearch() {
      const q = searchQuery.value.trim()
      if (q) emit('search', q)
    }
    return { searchQuery, onSearch }
  }
})
</script>

<style lang="scss" scoped>
/* —— 变量 —— */
$color-bg-header:    #181818;
$color-bg-input:     #282828;
$color-bg-btn:       #444;
$color-bg-btn-hover: #555;
$color-border:       #303030;
$color-text:         #ddd;
$color-placeholder:  #777;
$color-accent:       #42b983;
$border-radius:      4px;

/* —— 样式 —— */
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 16px;
  background: $color-bg-header;
  border-bottom: 1px solid $color-border;
  color: $color-text;

  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;

    .icon-btn {
      background: none;
      border: none;
      padding: 6px;
      border-radius: $border-radius;
      cursor: pointer;
      transition: background 0.2s;
      &:hover { background: $color-bg-btn-hover; }
      svg { width: 24px; height: 24px; color: $color-text; }
    }

    .logo {
      font-size: 18px;
      font-weight: bold;
      color: $color-text;
      text-decoration: none;
      &:hover { color: $color-accent; }
    }
  }

  .header-center {
    flex: 1;
    max-width: 400px;
    display: flex;
    margin: 0 24px;

    .search-input {
      flex: 1;
      padding: 6px 12px;
      background: $color-bg-input;
      color: $color-text;
      border: 1px solid $color-border;
      border-radius: $border-radius 0 0 $border-radius;
      transition: border-color 0.2s;
      &::placeholder { color: $color-placeholder; }
      &:focus {
        outline: none;
        border-color: $color-accent;
      }
    }

    .search-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      background: $color-bg-btn;
      border: 1px solid $color-border;
      border-radius: 0 $border-radius $border-radius 0;
      padding: 6px;
      margin-left: 4px;  // 按钮与输入框间距
      cursor: pointer;
      transition: background 0.2s;
      svg { width: 20px; height: 20px; color: $color-text; }
      &:hover { background: $color-bg-btn-hover; }
    }
  }

  .header-right {
    display: flex;
    gap: 12px;
    white-space: nowrap;

    a {
      color: $color-text;
      font-weight: bold;
      text-decoration: none;
      transition: color 0.2s;
      &:hover,
      &.router-link-exact-active { color: $color-accent; }
    }
  }
}
</style>
