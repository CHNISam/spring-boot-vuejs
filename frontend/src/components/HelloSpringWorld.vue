<!-- src/components/HelloSpringWorld.vue -->
<template>
  <div class="hello">
    <h1>{{ hellomsg }}</h1>

    <h2>查看源码：</h2>
    <ul>
      <li>
        <a href="https://github.com/CHNISam/spring-boot-vuejs" target="_blank">
          github.com/CHNISam/spring-boot-vuejs
        </a>
      </li>
    </ul>

    <h3>功能演示：</h3>
    <ul>
      <!-- 保留“登录到受保护部分” -->
      <li>登录到受保护部分：<router-link to="/login">/login</router-link></li>
      <!-- 新增动态列表 -->
      <li>查看动态列表：<router-link to="/posts">/posts</router-link></li>
      <!-- 新增发表新帖（投稿） -->
      <li>发表新帖（投稿）：<router-link to="/editor">/editor</router-link></li>
      <!-- 新增搜索功能 -->
      <li>帖子搜索：<router-link to="/search">/search</router-link></li>
      <!-- 保留示例受保护页面 -->
      <li>受保护页面示例：<router-link to="/protected">/protected</router-link></li>
    </ul>
  </div>
</template>

<script lang="ts">
import { defineComponent, onMounted } from 'vue'
import { useStore } from 'vuex'
import api from '../api/backend-api'

export default defineComponent({
  name: 'HelloSpringWorld',
  props: {
    hellomsg: {
      type: String,
      required: true,
    },
  },
  setup() {
    const store = useStore()

    onMounted(() => {
      const isLoggedIn = store.getters.isLoggedIn
      const currentUser = store.getters.currentUser
      const credentials = (store.state as any).credentials

      if (isLoggedIn) {
        console.log('✅ 用户已登录 - 用户信息:', currentUser)
        console.log('🆔 用户ID:', currentUser ? currentUser.id : '无')
        console.log('🔐 凭证状态:', credentials !== null ? '已设置' : '未设置')
        console.log('🌐 尝试访问 /api/user/me...')
        api.getCurrentUser()
          .then(response => {
            console.log('🔄 /api/user/me 响应:', response.data)
          })
          .catch(error => {
            console.error('❌ /api/user/me 请求失败:', error.message)
          })
      } else {
        console.log('🔒 用户未登录')
        console.log('ℹ️ 访问 /login 进行登录')
      }
    })

    return {}
  }
})
</script>


<style scoped lang="scss">
h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  margin: 8px 0;
}

a {
  color: #42b983;
}
</style>
