import { createStore } from 'vuex';
import api, { axiosApi, User } from '../api/backend-api';

export interface State {
  credentials: { username: string; password: string } | null;
  profile: User | null;
  loginError: boolean;
}

export default createStore<State>({
  state: {
    credentials: null,
    profile: null,
    loginError: false
  },
  mutations: {
    setCredentials(state, creds: { username: string; password: string }) {
      state.credentials = creds;
      // 让后续所有请求自动带上 Basic Auth
      axiosApi.defaults.auth = creds;
    },
    setProfile(state, profile: User) {
      state.profile = profile;
    },
    setLoginError(state, err: boolean) {
      state.loginError = err;
    }
  },
  actions: {
    async login({ commit }, { user, password }) {
      try {
        // 1️⃣ 校验凭证
        await api.getSecured(user, password);

        // 2️⃣ 缓存凭证并挂到 axios
        commit('setCredentials', { username: user, password });

        // 3️⃣ 拉当前用户信息
        const { data: me } = await api.getCurrentUser();
        commit('setProfile', me);

        commit('setLoginError', false);
        return me;
      } catch (e) {
        commit('setLoginError', true);
        throw e;
      }
    }
  },
  getters: {
    isLoggedIn: state => !!state.profile,
    currentUser: state => state.profile
  }
});
