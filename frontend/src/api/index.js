import axios from 'axios'

const api = axios.create({
  baseURL: '/api/v1',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor: attach JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    if (config.data instanceof FormData) {
      delete config.headers['Content-Type']
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor: handle errors
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

// ===== API =====

export const catApi = {
  list: (params) => api.get('/cats', { params }),
  detail: (id) => api.get(`/cats/${id}`),
  create: (data) => api.post('/cats', data),
  update: (id, data) => api.put(`/cats/${id}`, data),
  remove: (id) => api.delete(`/cats/${id}`),
  recommend: (id) => api.get(`/cats/${id}/recommend`),
}

export const photoApi = {
  list: (catId) => api.get(`/cats/${catId}/photos`),
  upload: (catId, formData) => api.post(`/cats/${catId}/photos`, formData),
  approve: (id) => api.put(`/photos/${id}/approve`),
  reject: (id, reason) => api.put(`/photos/${id}/reject`, { reason }),
  remove: (id) => api.delete(`/photos/${id}`),
  like: (id) => api.post(`/photos/${id}/like`),
  unlike: (id) => api.delete(`/photos/${id}/like`),
}

export const commentApi = {
  list: (catId) => api.get(`/cats/${catId}/comments`),
  create: (catId, content) => api.post(`/cats/${catId}/comments`, { content }),
  remove: (id) => api.delete(`/comments/${id}`),
}

export const followApi = {
  follow: (catId) => api.post(`/cats/${catId}/follow`),
  unfollow: (catId) => api.delete(`/cats/${catId}/follow`),
  status: (catId) => api.get(`/cats/${catId}/follow/status`),
  myFollows: () => api.get('/user/follows'),
}

export const ratingApi = {
  submit: (catId, ratings) => api.post(`/cats/${catId}/rating`, ratings),
  stats: (catId) => api.get(`/cats/${catId}/rating`),
}

export const authApi = {
  login: (username, password) => api.post('/auth/login', { username, password }),
  register: (username, password, email) => api.post('/auth/register', { username, password, email }),
  me: () => api.get('/auth/me'),
  updateMe: (data) => api.put('/auth/me', data),
  uploadAvatar: (formData) => api.post('/auth/me/avatar', formData),
}

export const userApi = {
  myCats: () => api.get('/user/cats'),
  myRatings: () => api.get('/user/ratings'),
}