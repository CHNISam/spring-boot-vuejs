import axios, { AxiosResponse } from "axios";

export const axiosApi = axios.create({
  baseURL: `/api`,
  timeout: 1000,
  headers: { "Content-Type": "application/json" },
});

export interface User {
  id: number;
  firstName: string;
  lastName: string;
}

export interface Post {
  id: number;
  title: string;
  content: string;
}

const api = {
  hello(): Promise<AxiosResponse<string>> {
    return axiosApi.get(`/hello`);
  },

  getUser(userId: number): Promise<AxiosResponse<User>> {
    return axiosApi.get(`/user/${userId}`);
  },

  createUser(
    firstName: string,
    lastName: string
  ): Promise<AxiosResponse<number>> {
    return axiosApi.post(`/user/${firstName}/${lastName}`);
  },

  getSecured(user: string, password: string): Promise<AxiosResponse<string>> {
    return axiosApi.get(`/secured/`, {
      auth: { username: user, password },
    });
  },

  createPost(title: string, content: string): Promise<AxiosResponse<number>> {
    return axiosApi.post<number>("/posts", { title, content });
  },

  searchPosts(q: string): Promise<AxiosResponse<Post[]>> {
    return axiosApi.get<Post[]>("/posts/search", { params: { q } });
  },

  // —— 新增：获取当前登录用户信息
  getCurrentUser(): Promise<AxiosResponse<User>> {
    return axiosApi.get(`/user/me`);
  },
  /**
   * 获取指定用户的所有帖子
   */
  getPostsByUser(userId: number): Promise<AxiosResponse<Post[]>> {
    return axiosApi.get<Post[]>(`/user/${userId}/posts`);
  },
  /**
   * 获取所有帖子列表
   */
  getAllPosts(): Promise<AxiosResponse<Post[]>> {
    return axiosApi.get<Post[]>(`/posts`);
  },
};

export default api;
