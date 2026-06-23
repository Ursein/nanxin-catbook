import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器：自动附加 JWT Token，并处理 FormData 的 Content-Type
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    // FormData 需要浏览器自动设置 Content-Type（含 boundary），不能手动指定
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理错误
api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api

// ===== API 接口 =====

// 猫咪
export const catApi = {
  list: (params) => api.get('/cats', { params }),
  detail: (id) => api.get(`/cats/${id}`),
  create: (data) => api.post('/cats', data),
  update: (id, data) => api.put(`/cats/${id}`, data),
  remove: (id) => api.delete(`/cats/${id}`),
  recommend: (id) => api.get(`/cats/${id}/recommend`),
}

// 照片
export const photoApi = {
  list: (catId) => api.get(`/cats/${catId}/photos`),
  upload: (catId, formData) =>
    api.post(`/cats/${catId}/photos`, formData),
  approve: (id) => api.put(`/photos/${id}/approve`),
  reject: (id, reason) => api.put(`/photos/${id}/reject`, { reason }),
  remove: (id) => api.delete(`/photos/${id}`),
  like: (id) => api.post(`/photos/${id}/like`),
  unlike: (id) => api.delete(`/photos/${id}/like`),
}

// 评论
export const commentApi = {
  list: (catId) => api.get(`/cats/${catId}/comments`),
  create: (catId, content) => api.post(`/cats/${catId}/comments`, { content }),
  remove: (id) => api.delete(`/comments/${id}`),
}

// 关注
export const followApi = {
  follow: (catId) => api.post(`/cats/${catId}/follow`),
  unfollow: (catId) => api.delete(`/cats/${catId}/follow`),
  status: (catId) => api.get(`/cats/${catId}/follow/status`),
  myFollows: () => api.get('/user/follows'),
}

// 评分
export const ratingApi = {
  submit: (catId, ratings) => api.post(`/cats/${catId}/rating`, ratings),
  stats: (catId) => api.get(`/cats/${catId}/rating`),
}

// 认证
export const authApi = {
  login: (username, password) => api.post('/auth/login', { username, password }),
  register: (username, password, email) =>
    api.post('/auth/register', { username, password, email }),
  me: () => api.get('/auth/me'),
  updateMe: (data) => api.put('/auth/me', data),
}