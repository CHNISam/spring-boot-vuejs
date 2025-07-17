<template>
  <div class="min-h-screen bg-gray-900 text-gray-100 p-12">
    <!-- Header -->
    <header class="mb-10">
      <h1 class="text-5xl font-bold text-blue-400">管理后台首页</h1>
    </header>

    <!-- Dashboard Grid -->
    <div class="grid grid-cols-1 lg:grid-cols-2 gap-8">
      <!-- Admin Info -->
      <div class="bg-gray-800 p-8 rounded-lg shadow-xl">
        <h2 class="text-2xl font-semibold mb-4 text-blue-300">当前管理员</h2>
        <p v-if="user" class="text-lg">
          姓名：<span class="font-medium">{{ user.firstName }} {{ user.lastName }}</span>
        </p>
        <p v-if="user" class="text-lg">
          ID：<span class="font-medium">{{ user.id }}</span>
        </p>
        <p v-else class="text-red-500">未检测到登录用户</p>
      </div>

      <!-- Service Status -->
      <div class="bg-gray-800 p-8 rounded-lg shadow-xl flex flex-col justify-between">
        <h2 class="text-2xl font-semibold mb-6 text-blue-300">系统服务状态</h2>
        <div class="space-y-4">
          <div>
            <button @click="testHello" class="btn-blue w-full">测试 /api/hello</button>
            <p v-if="helloResp" class="mt-2 text-blue-300">{{ helloResp }}</p>
            <p v-else-if="helloError" class="mt-2 text-red-500">{{ helloError }}</p>
          </div>
          <div>
            <button @click="loadPostsCount" class="btn-blue w-full">获取帖子总数</button>
            <p v-if="postsCount !== null" class="mt-2 text-blue-300">共 {{ postsCount }} 条</p>
            <p v-else-if="postsError" class="mt-2 text-red-500">{{ postsError }}</p>
          </div>
          <div>
            <button @click="loadUserInfo" class="btn-blue w-full">刷新用户信息</button>
            <p v-if="userInfoError" class="mt-2 text-red-500">{{ userInfoError }}</p>
          </div>
        </div>
      </div>

      <!-- AI Module & Quick Actions (full width) -->
      <div class="lg:col-span-2 grid grid-cols-1 md:grid-cols-2 gap-8">
        <!-- AI Status -->
        <div class="bg-gray-800 p-8 rounded-lg shadow-xl">
          <h2 class="text-2xl font-semibold mb-4 text-blue-300">AI 模块状态</h2>
          <ul class="list-disc list-inside space-y-2 text-gray-400">
            <li>摘要服务：未接入</li>
            <li>向量检索：未接入</li>
          </ul>
        </div>

        <!-- Quick Actions -->
        <div class="bg-gray-800 p-8 rounded-lg shadow-xl">
          <h2 class="text-2xl font-semibold mb-4 text-blue-300">快捷操作</h2>
          <div class="space-y-4">
            <button @click="noop" class="btn-blue w-full">重建索引</button>
            <button @click="noop" class="btn-blue w-full">清理审核队列</button>
          </div>
        </div>
      </div>

      <!-- Integration: REST API Test (Bootstrap Style) -->
      <div class="lg:col-span-2 bg-gray-800 p-8 rounded-lg shadow-xl bootstrap">
        <h2 class="text-2xl font-semibold mb-4 text-blue-300">REST 接口测试</h2>
        <h6><span class="badge bg-primary">Let’s go!</span> 点击下方按钮测试后端接口：</h6>
        <button class="btn btn-success mt-4" @click="callHelloApi" id="btnCallHello">/hello (GET)</button>

        <h4 class="mt-6">
          响应结果：
          <span
            class="alert alert-primary"
            role="alert"
            v-show="showResponse"
            dismissible
            @dismissed="showResponse=false"
          >{{ backendResponse }}</span>
        </h4>

        <button class="btn btn-secondary mt-4" data-bs-toggle="collapse" data-bs-target="#collapseOuter">查看详情</button>
        <div class="collapse mt-2" id="collapseOuter">
          <div class="card card-body bg-gray-700">
            <button class="btn btn-primary mr-2" data-bs-toggle="collapse" data-bs-target="#collapseInnerStatusCode">HTTP 状态</button>
            <div class="collapse" id="collapseInnerStatusCode">
              <div class="card card-body bg-gray-600">Status: {{ httpStatusCode }}</div>
              <div class="card card-body bg-gray-600">StatusText: {{ httpStatusText }}</div>
            </div>

            <button class="btn btn-warning mr-2 mt-2" data-bs-toggle="collapse" data-bs-target="#collapseInnerHeaders">HTTP Headers</button>
            <div class="collapse" id="collapseInnerHeaders">
              <ul v-if="headers.length" class="mt-2">
                <li v-for="(header, idx) in headers" :key="idx">
                  <div class="card card-body bg-gray-600">Header: {{ header }}</div>
                </li>
              </ul>
            </div>

            <button class="btn btn-danger mt-2" data-bs-toggle="collapse" data-bs-target="#collapseInnerResponseConfig">请求配置</button>
            <div class="collapse" id="collapseInnerResponseConfig">
              <div class="card card-body bg-gray-600">Config: {{ responseConfig }}</div>
            </div>
          </div>
        </div>
      </div>

    </div>
  </div>
</template>

<script lang="ts">
import api from '@/api/backend-api';
import { useStore } from '@/store/useStore';
import { AxiosError } from 'axios';
import { computed, defineComponent, ref } from 'vue';

export default defineComponent({
  name: 'AdminDashboardWithTest',
  setup() {
    const store = useStore();
    const user = computed(() => store.getters.currentUser);

    // Dashboard states
    const helloResp = ref('');
    const helloError = ref('');
    const postsCount = ref<number | null>(null);
    const postsError = ref('');
    const userInfoError = ref('');

    // REST test states
    const msg = ref('Nice Bootstrap candy!');
    const showResponse = ref(false);
    const backendResponse = ref('');
    const responseConfig = ref<any>('');
    const httpStatusCode = ref<number>(0);
    const httpStatusText = ref<string>('');
    const headers = ref<string[]>(['Nothing here atm. Did you call the Service?']);
    const errors = ref<AxiosError[]>([]);

    // Dashboard methods
    const testHello = async () => {
      helloResp.value = '';
      helloError.value = '';
      try {
        const resp = await api.hello();
        helloResp.value = resp.data;
      } catch (e: any) {
        helloError.value = e.message || '请求失败';
      }
    };

    const loadPostsCount = async () => {
      postsCount.value = null;
      postsError.value = '';
      try {
        const resp = await api.getAllPosts();
        postsCount.value = resp.data.length;
      } catch (e: any) {
        postsError.value = e.message || '请求失败';
      }
    };

    const loadUserInfo = async () => {
      userInfoError.value = '';
      try {
        const resp = await api.getCurrentUser();
        store.commit('setProfile', resp.data);
      } catch (e: any) {
        userInfoError.value = e.message || '请求失败';
      }
    };

    const noop = () => alert('功能未实现，请添加后端接口');

    // REST API test method
    const callHelloApi = () => {
      api.hello()
        .then(response => {
          backendResponse.value = response.data;
          httpStatusCode.value = response.status;
          httpStatusText.value = response.statusText;
          headers.value = Object.entries(response.headers).map(([k, v]) => `${k}: ${v}`);
          responseConfig.value = response.config;
          showResponse.value = true;
        })
        .catch((error: AxiosError) => {
          errors.value.push(error);
        });
    };

    return {
      user,
      helloResp,
      helloError,
      postsCount,
      postsError,
      userInfoError,
      testHello,
      loadPostsCount,
      loadUserInfo,
      noop,
      // REST test
      msg,
      showResponse,
      backendResponse,
      responseConfig,
      httpStatusCode,
      httpStatusText,
      headers,
      errors,
      callHelloApi,
    };
  }
});
</script>

<style scoped>
.btn-blue {
  background-color: #0ea5e9;
  color: #ffffff;
  padding: 0.75rem;
  border-radius: 0.5rem;
  font-weight: 500;
  transition: background-color 0.2s;
}
.btn-blue:hover {
  background-color: #0284c7;
}
.bootstrap .btn {
  margin-right: 0.5rem;
}
.bootstrap .card-body {
  background-color: #2c2c2c;
}
</style>