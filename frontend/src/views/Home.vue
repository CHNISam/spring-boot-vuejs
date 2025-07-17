<!-- src/views/Home.vue -->
<template>
  <div class="home">
    <img alt="Vue with Spring logo" src="../assets/spring-boot-vuejs-logo.png">
    <!-- 改成你的项目介绍文案 -->
    <HelloSpringWorld hellomsg="欢迎来到基于大模型和 SpringBoot 的社交论坛系统" />
  </div>
</template>

<script lang="ts">
import HelloSpringWorld from '@/components/HelloSpringWorld.vue';
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'Home',
  components: {
    HelloSpringWorld,
  },
});
</script>
