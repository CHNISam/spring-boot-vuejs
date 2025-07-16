<!-- src/components/layout/AppSidebar.vue -->
<template>
  <aside class="app-sidebar" :class="{ collapsed }">
    <ul class="menu-list">
      <li>
        <router-link to="/" title="首页" exact>
          <span class="link-text">首页</span>
        </router-link>
      </li>
      <li>
        <!-- 改成 /posts 列表页 -->
        <router-link to="/posts" title="动态" exact>
          <span class="link-text">动态</span>
        </router-link>
      </li>
      <li>
        <router-link to="/albums" title="相册">
          <span class="link-text">相册</span>
        </router-link>
      </li>
      <li>
        <router-link to="/editor" title="投稿">
          <span class="link-text">投稿</span>
        </router-link>
      </li>
      <!-- 去掉原来的“帖子”项 -->
    </ul>
  </aside>
</template>

<script lang="ts">
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'AppSidebar',
  props: {
    collapsed: { type: Boolean, default: false }
  }
});
</script>

<style lang="scss" scoped>
$color-bg-sider: #202020;
$color-border: #303030;
$color-text: #ddd;
$color-accent: #42b983;
$color-hover: #303030;
$border-radius: 4px;
$width-expanded: 160px; // 展开后宽度

.app-sidebar {
    position: fixed;
    top: 56px;
    left: 0;
    bottom: 0;
    width: $width-expanded;
    background: $color-bg-sider;
    border-right: 1px solid $color-border;

    /* 默认在视口内 */
    transform: translateX(0);
    transition: transform 0.2s ease;

    &.collapsed {
        /* 整个侧栏滑到左边屏幕外 */
        transform: translateX(-100%);
    }

    .menu-list {
        list-style: none;
        padding: 16px 0;
        margin: 0;

        li {
            margin: 8px 0;

            a {
                display: flex;
                align-items: center;
                padding: 8px 12px;
                border-radius: $border-radius;
                color: $color-text;
                text-decoration: none;
                transition: background 0.2s, color 0.2s;

                &:hover {
                    background: $color-hover;
                }

                &.router-link-exact-active {
                    background: $color-accent;
                    color: #fff;
                }

                .link-text {
                    margin-left: 8px;
                }
            }
        }
    }
}
</style>
