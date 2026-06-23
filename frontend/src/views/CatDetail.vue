<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { catApi, commentApi, followApi, ratingApi, photoApi } from '@/api'

const route = useRoute()
const router = useRouter()
const cat = ref(null)
const loading = ref(true)
const activePhoto = ref(0)
const isFollowed = ref(false)
const isAdmin = ref(false)
const isLoggedIn = ref(!!localStorage.getItem('token'))
const commentText = ref('')
const uploading = ref(false)
const fileInput = ref(null)

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await catApi.detail(route.params.id)
    cat.value = res.data
    isFollowed.value = res.data.isFollowed || false
  } catch (err) {
    console.error('Failed to load cat:', err)
  } finally {
    loading.value = false
  }
}

const submitComment = async () => {
  if (!commentText.value.trim()) return
  try {
    await commentApi.create(cat.value.id, commentText.value)
    commentText.value = ''
    loadDetail() // refresh
  } catch (err) {
    console.error('Failed to submit comment:', err)
  }
}

const toggleFollow = async () => {
  try {
    if (isFollowed.value) {
      await followApi.unfollow(cat.value.id)
    } else {
      await followApi.follow(cat.value.id)
    }
    isFollowed.value = !isFollowed.value
    cat.value.followCount += isFollowed.value ? 1 : -1
  } catch (err) {
    console.error('Follow failed:', err)
  }
}

const ratingDims = [
  { key: 'r1', label: '猫德' },
  { key: 'r2', label: '颜值' },
  { key: 'r3', label: '社交' },
  { key: 'r4', label: '干饭' },
  { key: 'r5', label: '活力' },
]

const setRating = async (dim, val) => {
  try {
    const ratings = {}
    for (const d of ratingDims) {
      if (dim === d.key) {
        ratings[d.key] = val
      } else {
        const prev = cat.value['my' + d.key.toUpperCase()]
        if (prev && prev > 0) ratings[d.key] = prev
      }
    }
    await ratingApi.submit(cat.value.id, ratings)
    loadDetail()
  } catch (err) {
    console.error('Rating failed:', err)
  }
}

const goToCat = (id) => router.push(`/cat/${id}`)

const prevPhoto = () => {
  if (cat.value?.photos) {
    activePhoto.value = activePhoto.value === 0
      ? cat.value.photos.length - 1 : activePhoto.value - 1
  }
}
const nextPhoto = () => {
  if (cat.value?.photos) {
    activePhoto.value = cat.value.photos.length - 1 === activePhoto.value
      ? 0 : activePhoto.value + 1
  }
}

const statusLabel = (s) => {
  const map = { ACTIVE: '在校', SEEKING_ADOPT: '待领养', MISSING: '失踪', DECEASED: '离世' }
  return map[s] || s
}

const photoGradient = (i) => {
  const gradients = [
    'linear-gradient(135deg, #e8f4f8, #d4e6f1)',
    'linear-gradient(135deg, #fce4ec, #f8bbd0)',
    'linear-gradient(135deg, #e8f5e9, #c8e6c9)',
  ]
  return gradients[i % gradients.length]
}

const checkAdmin = () => {
  const stored = localStorage.getItem('user')
  if (stored) {
    try {
      const u = JSON.parse(stored)
      isAdmin.value = u.role === 'ADMIN'
    } catch {}
  }
}

const deleteCat = async () => {
  if (!confirm('确定要删除这只猫咪吗？')) return
  try {
    await catApi.remove(cat.value.id)
    router.push('/')
  } catch (err) {
    alert('删除失败')
  }
}

const triggerUpload = () => {
  fileInput.value?.click()
}

const handleFileUpload = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    await photoApi.upload(cat.value.id, formData)
    loadDetail()
  } catch (err) {
    alert('上传失败: ' + (err.response?.data?.message || '未知错误'))
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}

const togglePhotoLike = async (photoId) => {
  try {
    await photoApi.like(photoId)
    loadDetail()
  } catch (err) {
    if (err.response?.status === 401) {
      router.push('/login')
    }
  }
}

const deleteComment = async (commentId) => {
  if (!confirm('确定删除这条留言？')) return
  try {
    await commentApi.remove(commentId)
    loadDetail()
  } catch (err) {
    alert('删除失败')
  }
}

onMounted(() => {
  checkAdmin()
  loadDetail()
})
</script>

<template>
  <div class="detail">
    <!-- Loading -->
    <div v-if="loading" class="detail-loading">
      <div class="loading-photo" />
      <div class="loading-info">
        <div class="skeleton-line w-48" />
        <div class="skeleton-line w-64" />
        <div class="skeleton-line w-32" />
      </div>
    </div>

    <template v-if="cat">
      <!-- Photo Carousel -->
      <section v-if="cat.photos && cat.photos.length > 0" class="photo-section">
        <div class="photo-carousel">
          <div
            class="photo-track"
            :style="{ transform: `translateX(-${activePhoto * 100}%)` }"
          >
            <div
              v-for="(photo, i) in cat.photos"
              :key="photo.id"
              class="photo-slide"
            >
              <img
                v-if="photo.url"
                :src="photo.compressedUrl || photo.url"
                :alt="photo.description || '猫咪照片'"
                class="photo-img"
              />
              <div v-else class="photo-placeholder" :style="{ background: photoGradient(i) }">
                <span class="photo-emoji">🐱</span>
                <p class="photo-desc">{{ photo.description }}</p>
              </div>
            </div>
          </div>

          <!-- Photo Controls -->
          <button class="photo-nav photo-prev" @click="prevPhoto">
            ←
          </button>
          <button class="photo-nav photo-next" @click="nextPhoto">
            →
          </button>

          <!-- Like button -->
          <button
            v-if="cat.photos[activePhoto]"
            class="photo-like-btn"
            :class="{ liked: cat.photos[activePhoto].isLiked }"
            @click="togglePhotoLike(cat.photos[activePhoto].id)"
          >
            ❤ {{ cat.photos[activePhoto].likeCount || 0 }}
          </button>

          <!-- Dots -->
          <div class="photo-dots">
            <button
              v-for="(_, i) in cat.photos"
              :key="i"
              class="dot"
              :class="{ active: activePhoto === i }"
              @click="activePhoto = i"
            />
          </div>
        </div>
      </section>

      <!-- Upload Photo -->
      <section v-if="isLoggedIn" class="upload-section">
        <div class="container">
          <input
            ref="fileInput"
            type="file"
            accept="image/*"
            style="display: none"
            @change="handleFileUpload"
          />
          <button class="btn-pill" :disabled="uploading" @click="triggerUpload">
            {{ uploading ? '上传中...' : '📸 上传照片' }}
          </button>
        </div>
      </section>

      <!-- Info Section -->
      <section class="info-section">
        <div class="container">
          <div class="info-layout">
            <!-- Left: Main Info -->
            <div class="info-main">
              <!-- Header -->
              <div class="info-header">
                <div class="info-title-row">
                  <h1 class="cat-name">{{ cat.name }}</h1>
                  <span v-if="cat.nickname" class="cat-nickname">{{ cat.nickname }}</span>
                  <span class="status-tag" :class="cat.status">
                    {{ statusLabel(cat.status) }}
                  </span>
                  <template v-if="isAdmin">
                    <button class="admin-btn" @click="router.push('/edit/' + cat.id)">编辑</button>
                    <button class="admin-btn danger" @click="deleteCat">删除</button>
                  </template>
                </div>

                <!-- Stats -->
                <div class="cat-stats">
                  <div class="stat-item">
                    <span class="stat-value">{{ cat.likeCount }}</span>
                    <span class="stat-label">点赞</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-value">{{ cat.followCount }}</span>
                    <span class="stat-label">关注</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-value">{{ cat.avgRating }}</span>
                    <span class="stat-label">评分</span>
                  </div>
                </div>
              </div>

              <!-- Attributes -->
              <div class="cat-attributes">
                <div class="attr-row">
                  <div class="attr-item">
                    <span class="attr-label">性别</span>
                    <span class="attr-value">{{ cat.gender === 'MALE' ? '男孩' : '女孩' }}</span>
                  </div>
                  <div class="attr-item">
                    <span class="attr-label">出生</span>
                    <span class="attr-value">{{ cat.birthYear || '未知' }}</span>
                  </div>
                  <div class="attr-item">
                    <span class="attr-label">体重</span>
                    <span class="attr-value">{{ cat.weight ? cat.weight + 'kg' : '未知' }}</span>
                  </div>
                </div>
                <div class="attr-row">
                  <div class="attr-item">
                    <span class="attr-label">毛色</span>
                    <span class="attr-value">{{ cat.colourTags || '未知' }}</span>
                  </div>
                  <div class="attr-item">
                    <span class="attr-label">绝育</span>
                    <span class="attr-value" :class="{ 'text-accent': cat.sterilized }">
                      {{ cat.sterilized ? '已绝育' : '未绝育' }}
                    </span>
                  </div>
                  <div class="attr-item">
                    <span class="attr-label">位置</span>
                    <span class="attr-value">{{ cat.locationArea }}</span>
                  </div>
                </div>
              </div>

              <!-- Personality -->
              <div class="cat-section">
                <h2 class="section-title">性格</h2>
                <div class="tag-group">
                  <span
                    v-for="tag in (cat.personalityTags || '').split(';').filter(t => t)"
                    :key="tag"
                    class="personality-tag"
                  >
                    {{ tag }}
                  </span>
                </div>
                <p class="section-text">{{ cat.personalityDesc }}</p>
              </div>

              <!-- Actions: Follow + Rating -->
              <div class="cat-actions">
                <div class="actions-top">
                  <button
                    class="btn-pill"
                    :class="{ accent: isFollowed }"
                    @click="toggleFollow"
                  >
                    {{ isFollowed ? '已关注 ✓' : '🐾 关注' }}
                  </button>
                  <div class="overall-rating">
                    <span class="overall-score">{{ cat.avgRating || '0' }}</span>
                    <span class="overall-label">综合评分</span>
                  </div>
                </div>

                <div class="rating-group">
                  <div v-for="dim in ratingDims" :key="dim.key" class="rating-dim">
                    <span class="rating-label">{{ dim.label }}</span>
                    <div class="dim-stars">
                      <button
                        v-for="star in 5"
                        :key="star"
                        class="star-btn"
                        :class="{ active: star <= (cat['my' + dim.key.toUpperCase()] || 0) }"
                        @click="setRating(dim.key, star)"
                      >★</button>
                    </div>
                    <span class="dim-avg">{{ cat['avg' + dim.key.toUpperCase()] || '0' }}</span>
                  </div>
                </div>
              </div>

              <!-- Comments -->
              <div class="cat-section">
                <h2 class="section-title">留言</h2>

                <div class="comment-input-wrapper">
                  <input
                    v-model="commentText"
                    type="text"
                    class="comment-input"
                    placeholder="说点什么..."
                    @keyup.enter="submitComment"
                  />
                  <button class="btn-pill" @click="submitComment">发送</button>
                </div>

                <div class="comment-list">
                  <div
                    v-for="comment in cat.comments"
                    :key="comment.id"
                    class="comment-item"
                  >
                    <div class="comment-header">
                      <span class="comment-user">{{ comment.username }}</span>
                      <div class="comment-header-right">
                        <span class="comment-time">{{ comment.createdAt }}</span>
                        <button
                          v-if="isAdmin"
                          class="comment-delete-btn"
                          @click="deleteComment(comment.id)"
                          title="删除"
                        >✕</button>
                      </div>
                    </div>
                    <p class="comment-content">{{ comment.content }}</p>
                  </div>
                </div>
              </div>
            </div>

            <!-- Right: Recommended -->
            <aside class="info-sidebar">
              <div class="double-bezel">
                <div class="inner">
                  <h3 class="sidebar-title">相似猫咪</h3>
                  <div class="recommend-list">
                    <button
                      v-for="rc in cat.recommendCats"
                      :key="rc.catId"
                      class="recommend-item"
                      @click="goToCat(rc.catId)"
                    >
                      <div class="recommend-avatar" />
                      <div class="recommend-info">
                        <span class="recommend-name">{{ rc.name }}</span>
                        <span v-if="rc.nickname" class="recommend-nickname">{{ rc.nickname }}</span>
                      </div>
                    </button>
                  </div>
                </div>
              </div>
            </aside>
          </div>
        </div>
      </section>
    </template>
  </div>
</template>

<style scoped>
/* --- Loading --- */
.detail-loading {
  padding: 2rem var(--container-px);
}
.loading-photo {
  width: 100%;
  height: 50vh;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  animation: pulse 1.5s ease-in-out infinite;
  margin-bottom: 2rem;
}
.loading-info {
  max-width: var(--container-max);
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.skeleton-line {
  height: 1rem;
  background: var(--bg-card);
  border-radius: 0.5rem;
  animation: pulse 1.5s ease-in-out infinite;
}
.w-48 { width: 12rem; }
.w-64 { width: 16rem; }
.w-32 { width: 8rem; }

/* --- Photo Section --- */
.photo-section {
  width: 100%;
  max-height: 50vh;
  overflow: hidden;
}

.photo-carousel {
  position: relative;
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  overflow: hidden;
}

.photo-track {
  display: flex;
  transition: transform 0.6s var(--ease-spring);
}

.photo-slide {
  min-width: 100%;
  height: 50vh;
  max-height: 500px;
}

.photo-img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  display: block;
  background: #0d0d0d;
}

.photo-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.photo-emoji {
  font-size: 4rem;
  opacity: 0.4;
}

.photo-desc {
  color: var(--text-tertiary);
  font-size: 0.875rem;
}

/* Photo Nav */
.photo-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(12px);
  color: var(--text-primary);
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  z-index: 2;
}

.photo-nav:hover {
  background: rgba(255, 255, 255, 0.9);
  border-color: var(--border-hover);
}

.photo-prev { left: 1rem; }
.photo-next { right: 1rem; }

/* Photo Like Button */
.photo-like-btn {
  position: absolute;
  bottom: 1.5rem;
  right: 1.5rem;
  z-index: 2;
  padding: 0.5rem 1rem;
  border-radius: 999px;
  border: 1px solid rgba(0, 0, 0, 0.08);
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(12px);
  color: var(--text-tertiary);
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.3s ease;
}
.photo-like-btn:hover {
  color: #ef4444;
  border-color: rgba(239, 68, 68, 0.3);
}
.photo-like-btn.liked {
  color: #ef4444;
  border-color: rgba(239, 68, 68, 0.3);
  background: rgba(239, 68, 68, 0.08);
}

/* Upload Section */
.upload-section {
  padding: 1rem 0;
}
.upload-section .container {
  display: flex;
  justify-content: center;
}

/* Dots */
.photo-dots {
  position: absolute;
  bottom: 1.5rem;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 0.5rem;
  z-index: 2;
}

.dot {
  width: 0.5rem;
  height: 0.5rem;
  border-radius: 50%;
  border: none;
  background: rgba(0, 0, 0, 0.2);
  cursor: pointer;
  transition: all 0.3s ease;
}

.dot.active {
  background: var(--text-primary);
  width: 1.25rem;
  border-radius: 0.25rem;
}

/* --- Info Section --- */
.info-section {
  padding: var(--section-py) 0;
}

.info-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 3rem;
}

/* --- Main Info --- */
.info-header {
  margin-bottom: 2rem;
}

.info-title-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.cat-name {
  font-size: 2.5rem;
  font-weight: 800;
  letter-spacing: -0.03em;
}

.cat-nickname {
  font-size: 1rem;
  color: var(--text-tertiary);
  font-weight: 400;
}

.admin-btn {
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.75rem;
  font-weight: 500;
  border: 1px solid var(--border-subtle);
  background: var(--bg-card);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s;
}
.admin-btn:hover {
  border-color: var(--border-hover);
  color: var(--text-primary);
}
.admin-btn.danger {
  color: #ef4444;
  border-color: rgba(239, 68, 68, 0.2);
}
.admin-btn.danger:hover {
  background: rgba(239, 68, 68, 0.08);
  border-color: rgba(239, 68, 68, 0.4);
}

/* Stats */
.cat-stats {
  display: flex;
  gap: 2rem;
  margin-top: 1.5rem;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
}

.stat-value {
  font-family: var(--font-display);
  font-size: 1.5rem;
  font-weight: 700;
}

.stat-label {
  font-size: 0.75rem;
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

/* Attributes */
.cat-attributes {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  padding: 1.5rem;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-md);
  margin-bottom: 2rem;
}

.attr-row {
  display: flex;
  gap: 2rem;
}

.attr-item {
  flex: 1;
}

.attr-label {
  display: block;
  font-size: 0.6875rem;
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.08em;
  margin-bottom: 0.25rem;
}

.attr-value {
  font-size: 0.9375rem;
  font-weight: 500;
}

.text-accent { color: var(--accent); }

/* Sections */
.cat-section {
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  margin-bottom: 0.75rem;
  color: var(--text-secondary);
}

.tag-group {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 0.75rem;
}

.personality-tag {
  padding: 0.25rem 0.75rem;
  border-radius: 999px;
  font-size: 0.8125rem;
  background: rgba(245, 158, 11, 0.1);
  color: var(--accent);
  border: 1px solid rgba(245, 158, 11, 0.15);
}

.section-text {
  font-size: 0.9375rem;
  color: var(--text-secondary);
  line-height: 1.7;
}

/* Actions */
.cat-actions {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  padding: 1.25rem 0;
  border-top: 1px solid var(--border-subtle);
  border-bottom: 1px solid var(--border-subtle);
  margin-bottom: 2rem;
}

.actions-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.overall-rating {
  display: flex;
  align-items: baseline;
  gap: 0.375rem;
}

.overall-score {
  font-family: var(--font-display);
  font-size: 1.5rem;
  font-weight: 800;
  color: var(--accent);
}

.overall-label {
  font-size: 0.75rem;
  color: var(--text-tertiary);
}

.rating-group {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.rating-dim {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.rating-label {
  font-size: 0.75rem;
  color: var(--text-tertiary);
  min-width: 2.5rem;
}

.dim-stars {
  display: flex;
  gap: 0.125rem;
}

.dim-avg {
  font-size: 0.6875rem;
  color: var(--text-tertiary);
  min-width: 1.5rem;
  text-align: right;
}

.star-btn {
  background: none;
  border: none;
  font-size: 1rem;
  color: var(--text-tertiary);
  cursor: pointer;
  transition: all 0.2s ease;
  padding: 0;
  line-height: 1;
}

.star-btn.active {
  color: var(--accent);
  text-shadow: 0 0 8px rgba(245, 158, 11, 0.4);
}

.star-btn:hover {
  color: var(--accent);
}

/* Comments */
.comment-input-wrapper {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.comment-input {
  flex: 1;
  padding: 0.625rem 1rem;
  border-radius: 999px;
  border: 1px solid var(--border-subtle);
  background: var(--glass-bg);
  color: var(--text-primary);
  font-family: var(--font-body);
  font-size: 0.875rem;
  outline: none;
  transition: border-color 0.3s ease;
}
.comment-input:focus {
  border-color: var(--border-hover);
}
.comment-input::placeholder {
  color: var(--text-tertiary);
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.comment-item {
  padding: 1rem;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-md);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.375rem;
}

.comment-header-right {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.comment-delete-btn {
  background: none;
  border: none;
  font-size: 0.75rem;
  color: var(--text-tertiary);
  cursor: pointer;
  padding: 0.125rem 0.375rem;
  border-radius: 0.25rem;
  transition: all 0.2s;
}
.comment-delete-btn:hover {
  color: #ef4444;
  background: rgba(239, 68, 68, 0.08);
}

.comment-user {
  font-size: 0.8125rem;
  font-weight: 600;
}

.comment-time {
  font-size: 0.6875rem;
  color: var(--text-tertiary);
}

.comment-content {
  font-size: 0.875rem;
  color: var(--text-secondary);
  line-height: 1.6;
}

/* --- Sidebar --- */
.info-sidebar {
  position: sticky;
  top: 6rem;
  align-self: start;
}

.sidebar-title {
  font-size: 0.875rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  margin-bottom: 1rem;
  color: var(--text-secondary);
}

.recommend-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.recommend-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem;
  border-radius: var(--radius-md);
  background: none;
  border: none;
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.3s ease;
  width: 100%;
  text-align: left;
}

.recommend-item:hover {
  background: var(--bg-card-hover);
}

.recommend-avatar {
  width: 2.5rem;
  height: 2.5rem;
  border-radius: 50%;
  background: var(--bg-card-hover);
  flex-shrink: 0;
}

.recommend-info {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
}

.recommend-name {
  font-size: 0.875rem;
  font-weight: 600;
}

.recommend-nickname {
  font-size: 0.75rem;
  color: var(--text-tertiary);
}

/* Animations */
@keyframes pulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.6; }
}

/* --- Responsive --- */
@media (max-width: 1024px) {
  .info-layout {
    grid-template-columns: 1fr;
  }

  .info-sidebar {
    position: static;
  }
}

@media (max-width: 768px) {
  .photo-slide,
  .photo-placeholder {
    height: 40vh;
    max-height: 350px;
  }

  .cat-name {
    font-size: 1.75rem;
  }

  .cat-stats {
    gap: 1.5rem;
  }

  .cat-attributes {
    padding: 1rem;
  }

  .attr-row {
    gap: 1rem;
    flex-wrap: wrap;
  }

  .attr-item {
    min-width: calc(50% - 0.5rem);
  }

  .cat-actions {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
  }

  .photo-nav {
    width: 2rem;
    height: 2rem;
    font-size: 0.8125rem;
  }

  .photo-prev { left: 0.5rem; }
  .photo-next { right: 0.5rem; }
}
</style>