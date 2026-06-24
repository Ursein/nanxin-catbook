<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { authApi, userApi, catApi, followApi } from '@/api'

const router = useRouter()

// Redirect if not logged in
const token = localStorage.getItem('token')
if (!token) {
  router.push('/login')
}

// User info
const user = ref(null)
const loading = ref(true)

// Tabs
const activeTab = ref('cats')
const tabs = [
  { key: 'cats', label: 'My Cats' },
  { key: 'follows', label: 'Following' },
  { key: 'ratings', label: 'My Ratings' },
]

// Tab data
const myCats = ref([])
const myFollows = ref([])
const myRatings = ref([])
const tabLoading = ref({ cats: true, follows: true, ratings: true })

// Edit modal
const showEditModal = ref(false)
const editForm = ref({ nickname: '', email: '' })
const editSaving = ref(false)
const editError = ref('')

// Computed stats
const stats = computed(() => ({
  cats: myCats.value.length,
  follows: myFollows.value.length,
  ratings: myRatings.value.length,
}))

const roleLabel = (role) => {
  const map = { USER: 'User', VERIFIED: 'Verified', ADMIN: 'Admin' }
  return map[role] || role
}

const statusLabel = (status) => {
  const map = { ACTIVE: 'On Campus', SEEKING_ADOPT: 'Adoptable', MISSING: 'Missing', DECEASED: 'Deceased' }
  return map[status] || status
}

// Load user info
const loadUser = async () => {
  try {
    const res = await authApi.me()
    user.value = res.data
    localStorage.setItem('user', JSON.stringify(res.data))
  } catch {
    router.push('/login')
  }
}

// Load tab data
const loadTabData = async (tab) => {
  tabLoading.value[tab] = true
  try {
    if (tab === 'cats') {
      const res = await userApi.myCats()
      myCats.value = res.data
    } else if (tab === 'follows') {
      const idsRes = await followApi.myFollows()
      const ids = idsRes.data
      if (ids.length > 0) {
        const details = await Promise.all(
          ids.map(id => catApi.detail(id).then(r => r.data))
        )
        myFollows.value = details
      } else {
        myFollows.value = []
      }
    } else if (tab === 'ratings') {
      const res = await userApi.myRatings()
      myRatings.value = res.data
    }
  } catch (err) {
    console.error('Failed to load tab data:', err)
  } finally {
    tabLoading.value[tab] = false
  }
}

// Switch tab
const switchTab = (tab) => {
  activeTab.value = tab
  if (tab === 'cats' && myCats.value.length === 0 && !tabLoading.value.cats) {
    loadTabData('cats')
  } else if (tab === 'follows' && myFollows.value.length === 0 && !tabLoading.value.follows) {
    loadTabData('follows')
  } else if (tab === 'ratings' && myRatings.value.length === 0 && !tabLoading.value.ratings) {
    loadTabData('ratings')
  }
}

// Edit profile
const openEdit = () => {
  editForm.value = {
    nickname: user.value?.nickname || '',
    email: user.value?.email || '',
  }
  editError.value = ''
  showEditModal.value = true
}

const saveProfile = async () => {
  editSaving.value = true
  editError.value = ''
  try {
    const res = await authApi.updateMe(editForm.value)
    user.value = res.data
    localStorage.setItem('user', JSON.stringify(res.data))
    showEditModal.value = false
  } catch (err) {
    editError.value = err.response?.data?.message || 'Save failed'
  } finally {
    editSaving.value = false
  }
}

// Navigation
const goToDetail = (id) => router.push(`/cat/${id}`)
const goToAdd = () => router.push('/add')

onMounted(async () => {
  loading.value = true
  await loadUser()
  loading.value = false
  loadTabData('cats')
  loadTabData('follows')
  loadTabData('ratings')
})
</script>

<template>
  <div class="profile-page">
    <!-- Loading -->
    <div v-if="loading" class="empty-state">
      <p class="empty-text">Loading...</p>
    </div>

    <template v-else-if="user">
      <!-- Hero -->
      <section class="profile-hero">
        <div class="container">
          <span class="eyebrow">My Profile</span>
          <div class="profile-card">
            <div class="profile-avatar">
              <span class="avatar-placeholder">🐱</span>
            </div>
            <div class="profile-info">
              <h2 class="profile-name">{{ user.username }}</h2>
              <p v-if="user.nickname" class="profile-nickname">{{ user.nickname }}</p>
              <div class="profile-meta">
                <span class="role-badge" :class="user.role?.toLowerCase()">
                  {{ roleLabel(user.role) }}
                </span>
                <span v-if="user.email" class="profile-email">{{ user.email }}</span>
              </div>
            </div>
            <div class="profile-actions">
              <button class="btn-pill outline" @click="openEdit">Edit Profile</button>
            </div>
          </div>
        </div>
      </section>

      <!-- Stats Bar -->
      <section class="stats-bar">
        <div class="container">
          <div class="stats-row">
            <div class="stat-item">
              <span class="stat-num">{{ stats.cats }}</span>
              <span class="stat-label">Cats Added</span>
            </div>
            <div class="stat-divider" />
            <div class="stat-item">
              <span class="stat-num">{{ stats.follows }}</span>
              <span class="stat-label">Following</span>
            </div>
            <div class="stat-divider" />
            <div class="stat-item">
              <span class="stat-num">{{ stats.ratings }}</span>
              <span class="stat-label">Ratings</span>
            </div>
            <div class="stat-spacer" />
            <button class="btn-pill accent" @click="goToAdd">+ Add Cat</button>
          </div>
        </div>
      </section>

      <!-- Tabs -->
      <section class="profile-tabs">
        <div class="container">
          <div class="tab-bar">
            <button
              v-for="tab in tabs"
              :key="tab.key"
              class="tab-btn"
              :class="{ active: activeTab === tab.key }"
              @click="switchTab(tab.key)"
            >
              {{ tab.label }}
            </button>
          </div>
        </div>
      </section>

      <!-- Tab Content -->
      <section class="tab-content">
        <div class="container">
          <!-- Loading -->
          <div v-if="tabLoading[activeTab]" class="empty-state">
            <p class="empty-text">Loading...</p>
          </div>

          <!-- My Cats -->
          <template v-else-if="activeTab === 'cats'">
            <div v-if="myCats.length === 0" class="empty-state">
              <span class="empty-icon">🐱</span>
              <p class="empty-text">No cats added yet</p>
              <button class="btn-pill accent" @click="goToAdd">Add one now</button>
            </div>
            <div v-else class="card-grid">
              <button
                v-for="cat in myCats"
                :key="cat.id"
                class="cat-card"
                @click="goToDetail(cat.id)"
              >
                <div class="cat-card-img">
                  <img v-if="cat.coverPhotoUrl" :src="cat.coverPhotoUrl" alt="" />
                  <span v-else class="cat-card-placeholder">🐱</span>
                </div>
                <div class="cat-card-body">
                  <h3 class="cat-card-name">{{ cat.name }}</h3>
                  <p class="cat-card-meta">
                    <span class="status-tag" :class="cat.status?.toLowerCase()">
                      {{ statusLabel(cat.status) }}
                    </span>
                    <span v-if="cat.locationArea">· {{ cat.locationArea }}</span>
                  </p>
                </div>
              </button>
            </div>
          </template>

          <!-- My Follows -->
          <template v-else-if="activeTab === 'follows'">
            <div v-if="myFollows.length === 0" class="empty-state">
              <span class="empty-icon">💛</span>
              <p class="empty-text">Not following any cats yet</p>
              <button class="btn-pill accent" @click="router.push('/')">Discover cats</button>
            </div>
            <div v-else class="card-grid">
              <button
                v-for="cat in myFollows"
                :key="cat.id"
                class="follow-card"
                @click="goToDetail(cat.id)"
              >
                <div class="follow-avatar">
                  <img v-if="cat.coverPhotoUrl" :src="cat.coverPhotoUrl" alt="" />
                  <span v-else>🐱</span>
                </div>
                <div class="follow-info">
                  <h3 class="follow-name">{{ cat.name }}</h3>
                  <p v-if="cat.nickname" class="follow-nickname">{{ cat.nickname }}</p>
                  <p class="follow-meta">{{ cat.locationArea }} · {{ cat.colourTags }}</p>
                </div>
              </button>
            </div>
          </template>

          <!-- My Ratings -->
          <template v-else-if="activeTab === 'ratings'">
            <div v-if="myRatings.length === 0" class="empty-state">
              <span class="empty-icon">⭐</span>
              <p class="empty-text">No ratings yet</p>
              <button class="btn-pill accent" @click="router.push('/')">Explore cats</button>
            </div>
            <div v-else class="rating-list">
              <div
                v-for="r in myRatings"
                :key="r.catId"
                class="rating-card"
                @click="goToDetail(r.catId)"
              >
                <div class="rating-header">
                  <h3 class="rating-cat-name">{{ r.catName || 'Cat #' + r.catId }}</h3>
                  <span class="rating-date">{{ r.createdAt?.slice(0, 10) }}</span>
                </div>
                <div class="rating-stars">
                  <div class="star-row">
                    <span class="star-label">Temper</span>
                    <span class="star-bars">
                      <span v-for="i in 5" :key="i" class="star-dot" :class="{ filled: i <= r.r1 }" />
                    </span>
                  </div>
                  <div class="star-row">
                    <span class="star-label">Looks</span>
                    <span class="star-bars">
                      <span v-for="i in 5" :key="i" class="star-dot" :class="{ filled: i <= r.r2 }" />
                    </span>
                  </div>
                  <div class="star-row">
                    <span class="star-label">Social</span>
                    <span class="star-bars">
                      <span v-for="i in 5" :key="i" class="star-dot" :class="{ filled: i <= r.r3 }" />
                    </span>
                  </div>
                  <div class="star-row">
                    <span class="star-label">Appetite</span>
                    <span class="star-bars">
                      <span v-for="i in 5" :key="i" class="star-dot" :class="{ filled: i <= r.r4 }" />
                    </span>
                  </div>
                  <div class="star-row">
                    <span class="star-label">Energy</span>
                    <span class="star-bars">
                      <span v-for="i in 5" :key="i" class="star-dot" :class="{ filled: i <= r.r5 }" />
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </div>
      </section>
    </template>
  </div>

  <!-- Edit Profile Modal -->
  <Teleport to="body">
    <transition name="modal">
      <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
        <div class="modal-panel">
          <h2 class="modal-title">Edit Profile</h2>
          <div class="modal-body">
            <label class="form-label">Nickname</label>
            <input
              v-model="editForm.nickname"
              class="form-input"
              placeholder="Enter nickname"
              maxlength="50"
            />
            <label class="form-label">Email</label>
            <input
              v-model="editForm.email"
              class="form-input"
              type="email"
              placeholder="Enter email"
              maxlength="100"
            />
            <p v-if="editError" class="form-error">{{ editError }}</p>
          </div>
          <div class="modal-footer">
            <button class="btn-pill outline" @click="showEditModal = false">Cancel</button>
            <button class="btn-pill accent" :disabled="editSaving" @click="saveProfile">
              {{ editSaving ? 'Saving...' : 'Save' }}
            </button>
          </div>
        </div>
      </div>
    </transition>
  </Teleport>
</template>

<style scoped>
.profile-page {
  padding: var(--section-py) 0;
}

.eyebrow {
  font-size: 0.8125rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.12em;
  color: var(--text-tertiary);
}

/* --- Profile Card --- */
.profile-hero {
  margin-bottom: 1.5rem;
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  margin-top: 1.5rem;
  padding: 2rem;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
}

.profile-avatar {
  width: 5rem;
  height: 5rem;
  border-radius: 50%;
  background: var(--glass-bg);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.avatar-placeholder {
  font-size: 2.5rem;
}

.profile-info {
  flex: 1;
  min-width: 0;
}

.profile-name {
  font-family: var(--font-display);
  font-size: 1.5rem;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.profile-nickname {
  font-size: 0.9375rem;
  color: var(--text-secondary);
  margin-top: 0.25rem;
}

.profile-meta {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-top: 0.5rem;
  flex-wrap: wrap;
}

.role-badge {
  font-size: 0.75rem;
  font-weight: 600;
  padding: 0.2rem 0.625rem;
  border-radius: 999px;
  background: var(--glass-bg);
  color: var(--text-secondary);
  border: 1px solid var(--border-subtle);
}

.role-badge.admin {
  background: rgba(255, 149, 0, 0.1);
  color: #e8870a;
  border-color: rgba(255, 149, 0, 0.2);
}

.profile-email {
  font-size: 0.8125rem;
  color: var(--text-tertiary);
}

.profile-actions {
  flex-shrink: 0;
}

.btn-pill {
  padding: 0.625rem 1.5rem;
  border-radius: 999px;
  font-size: 0.875rem;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.3s var(--ease-smooth);
}

.btn-pill.accent {
  background: var(--text-primary);
  color: #fff;
}

.btn-pill.accent:hover {
  opacity: 0.85;
}

.btn-pill.accent:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.btn-pill.outline {
  background: none;
  border: 1px solid var(--border-subtle);
  color: var(--text-secondary);
}

.btn-pill.outline:hover {
  border-color: var(--border-hover);
  color: var(--text-primary);
}

/* --- Stats Bar --- */
.stats-bar {
  margin-bottom: 2rem;
}

.stats-row {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  padding: 1rem 1.5rem;
  background: var(--glass-bg);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
}

.stat-num {
  font-family: var(--font-display);
  font-size: 1.5rem;
  font-weight: 700;
}

.stat-label {
  font-size: 0.75rem;
  color: var(--text-tertiary);
}

.stat-divider {
  width: 1px;
  height: 2rem;
  background: var(--border-subtle);
}

.stat-spacer {
  flex: 1;
}

/* --- Tabs --- */
.tab-bar {
  display: flex;
  gap: 0.5rem;
  border-bottom: 1px solid var(--border-subtle);
  padding-bottom: 0;
}

.tab-btn {
  padding: 0.75rem 1.5rem;
  border: none;
  background: none;
  font-size: 0.9375rem;
  font-weight: 500;
  color: var(--text-tertiary);
  cursor: pointer;
  border-bottom: 2px solid transparent;
  margin-bottom: -1px;
  transition: all 0.3s var(--ease-smooth);
}

.tab-btn:hover {
  color: var(--text-secondary);
}

.tab-btn.active {
  color: var(--text-primary);
  font-weight: 600;
  border-bottom-color: var(--text-primary);
}

/* --- Tab Content --- */
.tab-content {
  padding-top: 2rem;
}

/* Card Grid */
.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1rem;
}

/* Cat Card */
.cat-card {
  background: var(--glass-bg);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s var(--ease-smooth);
  text-align: left;
  color: var(--text-primary);
  width: 100%;
}

.cat-card:hover {
  background: var(--bg-card);
  border-color: var(--border-hover);
}

.cat-card-img {
  width: 100%;
  aspect-ratio: 4 / 3;
  background: var(--bg-card-hover);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.cat-card-img img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cat-card-placeholder {
  font-size: 3rem;
  opacity: 0.5;
}

.cat-card-body {
  padding: 0.875rem 1rem;
}

.cat-card-name {
  font-size: 1rem;
  font-weight: 600;
}

.cat-card-meta {
  font-size: 0.75rem;
  color: var(--text-tertiary);
  margin-top: 0.25rem;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.status-tag {
  font-size: 0.6875rem;
  font-weight: 600;
  padding: 0.125rem 0.5rem;
  border-radius: 999px;
  background: var(--glass-bg);
}

.status-tag.active {
  background: rgba(52, 199, 89, 0.1);
  color: #34c759;
}

.status-tag.seeking_adopt {
  background: rgba(255, 149, 0, 0.1);
  color: #e8870a;
}

.status-tag.missing {
  background: rgba(255, 59, 48, 0.1);
  color: #ff3b30;
}

.status-tag.deceased {
  background: rgba(142, 142, 147, 0.15);
  color: #8e8e93;
}

/* Follow Card */
.follow-card {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  background: var(--glass-bg);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.3s var(--ease-smooth);
  text-align: left;
  color: var(--text-primary);
  width: 100%;
}

.follow-card:hover {
  background: var(--bg-card);
  border-color: var(--border-hover);
}

.follow-avatar {
  width: 4rem;
  height: 4rem;
  border-radius: var(--radius-sm);
  background: var(--bg-card-hover);
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  font-size: 2rem;
}

.follow-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.follow-info {
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
  justify-content: center;
}

.follow-name {
  font-size: 1rem;
  font-weight: 600;
}

.follow-nickname {
  font-size: 0.8125rem;
  color: var(--text-tertiary);
}

.follow-meta {
  font-size: 0.75rem;
  color: var(--text-tertiary);
  margin-top: 0.25rem;
}

/* Rating List */
.rating-list {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.rating-card {
  padding: 1.25rem;
  background: var(--glass-bg);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.3s var(--ease-smooth);
}

.rating-card:hover {
  background: var(--bg-card);
  border-color: var(--border-hover);
}

.rating-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 0.75rem;
}

.rating-cat-name {
  font-size: 1rem;
  font-weight: 600;
}

.rating-date {
  font-size: 0.75rem;
  color: var(--text-tertiary);
}

.rating-stars {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.star-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.star-label {
  font-size: 0.8125rem;
  color: var(--text-secondary);
  width: 2.5rem;
  flex-shrink: 0;
}

.star-bars {
  display: flex;
  gap: 0.25rem;
}

.star-dot {
  width: 0.625rem;
  height: 0.625rem;
  border-radius: 50%;
  background: var(--border-subtle);
  transition: background 0.3s;
}

.star-dot.filled {
  background: #ffb800;
}

/* Empty State */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 4rem 2rem;
  text-align: center;
  gap: 1rem;
}

.empty-icon {
  font-size: 3rem;
  opacity: 0.5;
}

.empty-text {
  color: var(--text-tertiary);
  font-size: 0.9375rem;
}

/* --- Modal --- */
.modal-overlay {
  position: fixed;
  inset: 0;
  z-index: 200;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 1rem;
}

.modal-panel {
  width: 100%;
  max-width: 400px;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-subtle);
  padding: 2rem;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
}

.modal-title {
  font-family: var(--font-display);
  font-size: 1.25rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
}

.modal-body {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.form-label {
  font-size: 0.8125rem;
  color: var(--text-secondary);
  font-weight: 500;
}

.form-input {
  padding: 0.75rem 1rem;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-subtle);
  background: var(--glass-bg);
  color: var(--text-primary);
  font-family: var(--font-body);
  font-size: 0.9375rem;
  outline: none;
  transition: border-color 0.3s;
}

.form-input:focus {
  border-color: var(--border-hover);
}

.form-input::placeholder {
  color: var(--text-tertiary);
}

.form-error {
  font-size: 0.8125rem;
  color: #ff3b30;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 0.75rem;
  margin-top: 1.5rem;
}

/* Modal Transition */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.4s var(--ease-spring);
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .modal-panel,
.modal-leave-to .modal-panel {
  transform: scale(0.95) translateY(0.5rem);
}

/* --- Responsive --- */
@media (max-width: 768px) {
  .profile-card {
    flex-direction: column;
    text-align: center;
    padding: 1.5rem;
  }

  .profile-meta {
    justify-content: center;
  }

  .stats-row {
    flex-wrap: wrap;
    gap: 0.75rem;
    justify-content: center;
  }

  .stat-divider {
    display: none;
  }

  .stat-spacer {
    display: none;
  }

  .tab-bar {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }

  .card-grid {
    grid-template-columns: 1fr;
  }
}
</style>