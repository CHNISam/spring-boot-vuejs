<template>
  <div class="hello">
    <h1>{{ hellomsg }}</h1>
    <h2>See the sources here: </h2>
    <ul>
      <li><a href="https://github.com/jonashackt/spring-boot-vuejs" target="_blank">github.com/jonashackt/spring-boot-vuejs</a></li>
    </ul>
    <h3>This site contains more stuff :)</h3>
    <ul>
        <li>HowTo call REST-Services:</li>
        <li><router-link to="/callservice">/callservice</router-link></li>
        <li>HowTo to play around with Bootstrap UI components:</li>
        <li><router-link to="/bootstrap">/bootstrap</router-link></li>
        <li>HowTo to interact with the Spring Boot database backend:</li>
        <li><router-link to="/user">/user</router-link></li>
        <li>Login to the secured part of the application</li>
        <li><router-link to="/login">/login</router-link></li>
        <li>A secured part of this application:</li>
        <li><router-link to="/protected">/protected</router-link></li>
    </ul>
  </div>
</template>

<script>
// 正确导入api模块
import api from '../api/backend-api';

export default {
  name: 'HelloSpringWorld',
  props: {
    hellomsg: {
      type: String,
      required: true 
    }
  },
  mounted() {
    const store = this.$store;
    const isLoggedIn = store.getters.isLoggedIn;
    const currentUser = store.getters.currentUser;
    const credentials = store.state.credentials;
    
    if(isLoggedIn) {
      console.log("✅ 用户已登录 - 用户信息:", currentUser);
      console.log("🆔 用户ID:", currentUser ? currentUser.id : "无");
      console.log("🔐 凭证状态:", credentials !== null ? "已设置" : "未设置");
      
      // 修复API访问错误 - 使用直接导入的api，而不是this.$api
      console.log("🌐 尝试访问 /api/user/me...");
      api.getCurrentUser()
        .then(response => {
          console.log("🔄 /api/user/me 响应:", response.data);
        })
        .catch(error => {
          console.error("❌ /api/user/me 请求失败:", error.message);
        });
    } else {
      console.log("🔒 用户未登录");
      console.log("ℹ️ 访问 /login 进行登录");
    }
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="scss">
h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>