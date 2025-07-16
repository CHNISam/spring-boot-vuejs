import axios, { AxiosResponse } from 'axios';

const axiosApi = axios.create({
  baseURL: `/api`,
  timeout: 1000,
  headers: { 'Content-Type': 'application/json' }
});

// —— 现有的 User 类型
export interface User {
  id: number;
  firstName: string;
  lastName: string;
}

// —— 新增的 Post 类型
export interface Post {
  id: number;
  title: string;
  content: string;
}

export default {
  // —— 现有接口，不动
  hello(): Promise<AxiosResponse<string>> {
    return axiosApi.get(`/hello`);
  },

  getUser(userId: number): Promise<AxiosResponse<User>> {
    return axiosApi.get(`/user/${userId}`);
  },

  createUser(firstName: string, lastName: string): Promise<AxiosResponse<number>> {
    return axiosApi.post(`/user/${firstName}/${lastName}`);
  },

  getSecured(user: string, password: string): Promise<AxiosResponse<string>> {
    return axiosApi.get(`/secured/`, {
      auth: { username: user, password: password }
    });
  },

  // —— 新增：发表帖子
  createPost(title: string, content: string): Promise<AxiosResponse<number>> {
    // POST /api/posts  body: { title, content }
    return axiosApi.post<number>('/posts', { title, content });
  },

  // —— 新增：搜索帖子
  searchPosts(q: string): Promise<AxiosResponse<Post[]>> {
    // GET /api/posts/search?q=xxx
    return axiosApi.get<Post[]>('/posts/search', { params: { q } });
  }
}
