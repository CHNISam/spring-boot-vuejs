import PostDetail from '@/views/PostDetail.vue';
import PostEditor from '@/views/PostEditor.vue';
import PostList from '@/views/PostList.vue';
import Search from "@/views/Search.vue";
import { createRouter, createWebHistory, RouteRecordRaw } from "vue-router";
import Bootstrap from "../views/Bootstrap.vue";
import Home from "../views/Home.vue";
import Login from "../views/Login.vue";
import Protected from "../views/Protected.vue";
import Service from "../views/Service.vue";
import User from "../views/User.vue";

import store from "../store";

const routes: Array<RouteRecordRaw> = [
  { path: "/", component: Home },
  { path: "/callservice", component: Service },
  { path: "/bootstrap", component: Bootstrap },
  { path: "/user", component: User },
  { path: "/login", component: Login },
  {
    path: "/protected",
    component: Protected,
    meta: {
      requiresAuth: true,
    },
  },
  { path: "/", name: "Home", component: Home },
    // 发表新帖（投稿）页面
  { path: '/editor',     name: 'PostEditor', component: PostEditor },
  // 帖子列表页面
  { path: '/posts',       name: 'PostList',   component: PostList },
  { path: "/search", name: "Search", component: Search },
  { path: '/posts/:id', name: 'PostDetail', component: PostDetail, props: true },

  // otherwise redirect to home
  { path: "/:pathMatch(.*)*", redirect: "/" },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL), // uris without hashes #, see https://router.vuejs.org/guide/essentials/history-mode.html#html5-history-mode
  routes,
});

router.beforeEach((to, from, next) => {
  if (to.matched.some((record) => record.meta.requiresAuth)) {
    // this route requires auth, check if logged in
    // if not, redirect to login page.
    if (!store.getters.isLoggedIn) {
      next({
        path: "/login",
      });
    } else {
      next();
    }
  } else {
    next(); // make sure to always call next()!
  }
});

export default router;
