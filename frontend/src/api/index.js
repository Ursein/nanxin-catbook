import axios from 'axios'
import { getMockCatList, getMockCatDetail, getMockRecommend, getMockLogin, getMockRegister, getMockMe, getMockUpdateMe, getMockFollow, getMockUnfollow, getMockFollowStatus, getMockMyFollows, getMockRatingStats, getMockRatingSubmit, getMockCommentCreate, getMockCommentRemove, getMockPhotoList, getMockPhotoUpload, getMockPhotoLike, getMockPhotoUnlike, getMockPhotoApprove, getMockPhotoReject, getMockPhotoRemove, getMockMyCats, getMockMyRatings } from '@/mock/data'

// 设置为 true 时完全使用 mock 数据（不尝试请求后端）
// 设置为 false 时先尝试后端，失败后降级到 mock
const ALWAYS_MOCK = false

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

// 辅助函数：API 失败时降级到 mock
function withMockFallback(realFn, mockFn) {
  return async (...args) => {
    if (ALWAYS_MOCK) return mockFn(...args)
    try {
      return await realFn(...args)
    } catch {
      console.warn('API 不可用，使用 mock 数据')
      return mockFn(...args)
    }
  }
}

// 空操作（写操作在后端不可用时静默忽略）
function noop() {
  return Promise.resolve({ data: null })
}

// ===== API 接口 =====

// 猫咪（支持 mock 降级）
export const catApi = {
  list: withMockFallback(
    (params) => api.get('/cats', { params }),
    getMockCatList
  ),
  detail: withMockFallback(
    (id) => api.get(`/cats/${id}`),
    getMockCatDetail
  ),
  create: noop,
  update: noop,
  remove: noop,
  recommend: withMockFallback(
    (id) => api.get(`/cats/${id}/recommend`),
    getMockRecommend
  ),
}

// 照片（支持 mock）
export const photoApi = {
  list: withMockFallback(
    (catId) => api.get(`/cats/${catId}/photos`),
    getMockPhotoList
  ),
  upload: withMockFallback(
    (catId, formData) => api.post(`/cats/${catId}/photos`, formData),
    getMockPhotoUpload
  ),
  approve: withMockFallback(
    (id) => api.put(`/photos/${id}/approve`),
    getMockPhotoApprove
  ),
  reject: withMockFallback(
    (id, reason) => api.put(`/photos/${id}/reject`, { reason }),
    getMockPhotoReject
  ),
  remove: withMockFallback(
    (id) => api.delete(`/photos/${id}`),
    getMockPhotoRemove
  ),
  like: withMockFallback(
    (id) => api.post(`/photos/${id}/like`),
    getMockPhotoLike
  ),
  unlike: withMockFallback(
    (id) => api.delete(`/photos/${id}/like`),
    getMockPhotoUnlike
  ),
}

// 评论（支持 mock）
export const commentApi = {
  list: (catId) => api.get(`/cats/${catId}/comments`),
  create: withMockFallback(
    (catId, content) => api.post(`/cats/${catId}/comments`, { content }),
    getMockCommentCreate
  ),
  remove: withMockFallback(
    (id) => api.delete(`/comments/${id}`),
    getMockCommentRemove
  ),
}

// 关注（支持 mock）
export const followApi = {
  follow: withMockFallback(
    (catId) => api.post(`/cats/${catId}/follow`),
    getMockFollow
  ),
  unfollow: withMockFallback(
    (catId) => api.delete(`/cats/${catId}/follow`),
    getMockUnfollow
  ),
  status: withMockFallback(
    (catId) => api.get(`/cats/${catId}/follow/status`),
    getMockFollowStatus
  ),
  myFollows: withMockFallback(
    () => api.get('/user/follows'),
    getMockMyFollows
  ),
}

// 评分（支持 mock）
export const ratingApi = {
  submit: withMockFallback(
    (catId, ratings) => api.post(`/cats/${catId}/rating`, ratings),
    getMockRatingSubmit
  ),
  stats: withMockFallback(
    (catId) => api.get(`/cats/${catId}/rating`),
    getMockRatingStats
  ),
}

// 认证（支持 mock）
export const authApi = {
  login: withMockFallback(
    (username, password) => api.post('/auth/login', { username, password }),
    getMockLogin
  ),
  register: withMockFallback(
    (username, password, email) => api.post('/auth/register', { username, password, email }),
    getMockRegister
  ),
  me: withMockFallback(
    () => api.get('/auth/me'),
    getMockMe
  ),
  updateMe: withMockFallback(
    (data) => api.put('/auth/me', data),
    getMockUpdateMe
  ),
}

// 用户（支持 mock）
export const userApi = {
  myCats: withMockFallback(
    () => api.get('/user/cats'),
    getMockMyCats
  ),
  myRatings: withMockFallback(
    () => api.get('/user/ratings'),
    getMockMyRatings
  ),
}