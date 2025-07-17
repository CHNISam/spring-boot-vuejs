<!-- src/components/HelloSpringWorld.vue -->
<template>
  <div class="hello">
    <h1>{{ hellomsg }}</h1>

    <h2>Source Code:</h2>
    <ul>
      <li>
        <a href="https://github.com/CHNISam/spring-boot-vuejs" target="_blank">
          github.com/CHNISam/spring-boot-vuejs
        </a>
      </li>
    </ul>

    <h3>Features:</h3>
    <ul>
      <!-- keep “Log in to protected area” -->
      <li>Log in to protected area: <router-link to="/login">/login</router-link></li>
      <!-- new post list -->
      <li>View post list: <router-link to="/posts">/posts</router-link></li>
      <!-- new create-post (editor) -->
      <li>Create new post (Editor): <router-link to="/editor">/editor</router-link></li>
      <!-- new search -->
      <li>Search posts: <router-link to="/search">/search</router-link></li>
      <!-- keep example protected page -->
      <li>Protected page example: <router-link to="/protected">/protected</router-link></li>
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
        console.log('✅ User is logged in – User info:', currentUser)
        console.log('🆔 User ID:', currentUser ? currentUser.id : 'None')
        console.log('🔐 Credentials status:', credentials !== null ? 'Set' : 'Not set')
        console.log('🌐 Attempting to GET /api/user/me…')
        api.getCurrentUser()
          .then(response => {
            console.log('🔄 /api/user/me response:', response.data)
          })
          .catch(error => {
            console.error('❌ Failed to GET /api/user/me:', error.message)
          })
      } else {
        console.log('🔒 User is not logged in')
        console.log('ℹ️ Please navigate to /login to sign in')
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
