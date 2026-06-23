<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { catApi } from '@/api'

const router = useRouter()

const activeTab = ref('ACTIVE')
const cats = ref([])
const loading = ref(true)
const page = ref(0)
const hasMore = ref(true)
const SIZE = 20

const tabs = [
  { key: 'ACTIVE', label: '在校', icon: '🐱' },
  { key: 'SEEKING_ADOPT', label: '待领养', icon: '🏠' },
  { key: 'MISSING', label: '失踪', icon: '🔍' },
  { key: 'DECEASED', label: '喵星', icon: '🌈' },
]

const switchTab = (key) => {
  activeTab.value = key
  cats.value = []
  page.value = 0
  hasMore.value = true
  loadCats()
}

const loadCats = async () => {
  loading.value = true
  try {
    const res = await catApi.list({ status: activeTab.value, page: page.value, size: SIZE })
    const newCats = res.data.content
    cats.value = page.value === 0 ? newCats : [...cats.value, ...newCats]
    page.value++
    hasMore.value = newCats.length >= SIZE
  } catch (err) {
    console.error('Failed to load cats:', err)
  } finally {
    loading.value = false
    nextTick(() => {
      document.querySelectorAll('.cat-card').forEach((el, i) => {
        setTimeout(() => el.classList.add('visible'), i * 80)
      })
    })
  }
}

const goToDetail = (id) => {
  router.push(`/cat/${id}`)
}

const statusLabel = (status) => {
  const map = { ACTIVE: '在校', SEEKING_ADOPT: '待领养', MISSING: '失踪', DECEASED: '离世' }
  return map[status] || status
}

onMounted(() => {
  loadCats()
})
</script>

<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="container">
        <div class="hero-content">
          <span class="eyebrow">南信大 · 校园猫咪图鉴</span>
          <h1 class="hero-title">校园里的<span class="highlight">猫邻居</span></h1>
          <p class="hero-sub">记录每一只在南信大生活过的猫咪，用温柔的方式认识它们</p>
        </div>
      </div>
    </section>

    <!-- Status Tabs -->
    <section class="tabs-section">
      <div class="container">
        <div class="tabs-wrapper">
          <button
            v-for="tab in tabs"
            :key="tab.key"
            class="tab-btn"
            :class="{ active: activeTab === tab.key }"
            @click="switchTab(tab.key)"
          >
            <span class="tab-icon">{{ tab.icon }}</span>
            <span class="tab-label">{{ tab.label }}</span>
          </button>
        </div>

        <!-- Active tab indicator -->
        <div class="tab-count">
          <span v-if="!loading">{{ cats.length }} 只猫咪</span>
        </div>
      </div>
    </section>

    <!-- Bento Grid -->
    <section class="grid-section">
      <div class="container">
        <!-- Loading State -->
        <div v-if="loading && cats.length === 0" class="loading-state">
          <div class="loading-grid">
            <div v-for="i in 6" :key="i" class="skeleton-card" :class="`skeleton-${i}`" />
          </div>
        </div>

        <!-- Empty State -->
        <div v-else-if="cats.length === 0" class="empty-state">
          <span class="empty-icon">🐱</span>
          <p class="empty-text">这个分区暂时还没有猫咪</p>
        </div>

        <!-- Bento Grid -->
        <div v-else class="bento-grid">
          <div
            v-for="(cat, index) in cats"
            :key="cat.id"
            class="cat-card fade-up"
            :class="[`cell-${(index % 6) + 1}`]"
            @click="goToDetail(cat.id)"
          >
            <!-- Cover Photo Placeholder -->
            <div class="card-bg" :style="cat.coverPhotoUrl ? { backgroundImage: `url(${cat.coverPhotoUrl})`, backgroundSize: 'cover', backgroundPosition: 'center' } : {}">
              <div class="card-overlay" />
              <div class="card-content">
                <!-- Status Tag -->
                <span class="status-tag" :class="cat.status">
                  {{ statusLabel(cat.status) }}
                </span>

                <div class="card-info">
                  <h3 class="card-name">{{ cat.name }}</h3>
                  <p v-if="cat.nickname" class="card-nickname">{{ cat.nickname }}</p>
                  <div class="card-meta">
                    <span>{{ cat.locationArea }}</span>
                    <span class="meta-dot">·</span>
                    <span>{{ cat.gender === 'MALE' ? '♂' : '♀' }}</span>
                    <span v-if="cat.sterilized" class="meta-dot">·</span>
                    <span v-if="cat.sterilized" class="meta-sterilized">已绝育</span>
                  </div>
                  <div class="card-stats">
                    <span>❤️ {{ cat.likeCount || 0 }}</span>
                    <span>👁 {{ cat.followCount || 0 }}</span>
                    <span>⭐ {{ cat.avgRating || '0' }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Load More -->
        <div v-if="hasMore && cats.length > 0" class="load-more">
          <button class="btn-pill" @click="loadCats">
            加载更多
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
/* --- Hero --- */
.hero-section {
  padding: var(--section-py) 0 3rem;
  position: relative;
  overflow: hidden;
}

.hero-section::before {
  content: '';
  position: absolute;
  top: -40%;
  left: 30%;
  width: 600px;
  height: 600px;
  background: radial-gradient(circle, rgba(245, 158, 11, 0.08) 0%, transparent 70%);
  pointer-events: none;
  z-index: 0;
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 720px;
}

.hero-title {
  font-size: clamp(2.5rem, 6vw, 4.5rem);
  font-weight: 800;
  letter-spacing: -0.03em;
  line-height: 1.05;
  margin-top: 1rem;
}

.hero-title .highlight {
  background: linear-gradient(135deg, var(--accent), #d97706);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-sub {
  font-size: 1.125rem;
  color: var(--text-secondary);
  max-width: 65ch;
  margin-top: 1rem;
  line-height: 1.7;
}

/* --- Tabs --- */
.tabs-section {
  padding-bottom: 2rem;
}

.tabs-wrapper {
  display: flex;
  gap: 0.5rem;
  overflow-x: auto;
  padding-bottom: 0.5rem;
  -webkit-overflow-scrolling: touch;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.625rem 1.25rem;
  border-radius: 999px;
  border: 1px solid var(--border-subtle);
  background: transparent;
  color: var(--text-secondary);
  font-family: var(--font-body);
  font-size: 0.875rem;
  font-weight: 500;
  cursor: pointer;
  white-space: nowrap;
  transition: all 0.4s var(--ease-smooth);
}

.tab-btn:hover {
  border-color: var(--border-hover);
  color: var(--text-primary);
  background: rgba(255, 255, 255, 0.04);
}

.tab-btn.active {
  background: rgba(255, 255, 255, 0.08);
  border-color: var(--border-hover);
  color: var(--text-primary);
}

.tab-icon {
  font-size: 1rem;
}

.tab-count {
  margin-top: 0.75rem;
  font-size: 0.8125rem;
  color: var(--text-tertiary);
}

/* --- Bento Grid --- */
.grid-section {
  padding-bottom: var(--section-py);
}

.bento-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
}

/* Cell size patterns - cycles every 6 cards */
.cell-1 {
  grid-column: span 2;
  grid-row: span 2;
}
.cell-2 {
  grid-column: span 2;
  grid-row: span 1;
}
.cell-3 {
  grid-column: span 1;
  grid-row: span 1;
}
.cell-4 {
  grid-column: span 1;
  grid-row: span 2;
}
.cell-5 {
  grid-column: span 2;
  grid-row: span 1;
}
.cell-6 {
  grid-column: span 2;
  grid-row: span 1;
}

/* --- Cat Card --- */
.cat-card {
  cursor: pointer;
  border-radius: var(--radius-lg);
  overflow: hidden;
  position: relative;
  transition: all 0.6s var(--ease-spring);
}

.cat-card:hover {
  transform: scale(0.98);
}

.card-bg {
  width: 100%;
  height: 100%;
  min-height: 160px;
  position: relative;
  border-radius: var(--radius-lg);
  overflow: hidden;
  background: var(--bg-card);
  display: flex;
  align-items: flex-end;
}

/* Gradient backgrounds as placeholder for cat photos */
.card-bg::before {
  content: '';
  position: absolute;
  inset: 0;
  opacity: 0.6;
  transition: opacity 0.5s ease;
}

.cat-card:hover .card-bg::before {
  opacity: 0.8;
}

/* Different color gradients for each cell */
.cat-card:nth-child(6n+1) .card-bg::before {
  background: linear-gradient(135deg, #e8f4f8, #d4e6f1);
}
.cat-card:nth-child(6n+2) .card-bg::before {
  background: linear-gradient(135deg, #fce4ec, #f8bbd0);
}
.cat-card:nth-child(6n+3) .card-bg::before {
  background: linear-gradient(135deg, #e8f5e9, #c8e6c9);
}
.cat-card:nth-child(6n+4) .card-bg::before {
  background: linear-gradient(135deg, #fff3e0, #ffe0b2);
}
.cat-card:nth-child(6n+5) .card-bg::before {
  background: linear-gradient(135deg, #ede7f6, #d1c4e9);
}
.cat-card:nth-child(6n+6) .card-bg::before {
  background: linear-gradient(135deg, #fce4ec, #f1f8e9);
}

.card-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to top, rgba(0,0,0,0.45) 0%, rgba(0,0,0,0.1) 50%, transparent 100%);
  z-index: 1;
}

.card-content {
  position: relative;
  z-index: 2;
  padding: 1.5rem;
  width: 100%;
}

.card-info {
  margin-top: 0.75rem;
}

.card-name {
  font-size: 1.25rem;
  font-weight: 700;
  letter-spacing: -0.02em;
  color: #fff;
}

.card-nickname {
  font-size: 0.8125rem;
  color: rgba(255,255,255,0.7);
  margin-top: 0.125rem;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.75rem;
  color: rgba(255,255,255,0.7);
  margin-top: 0.375rem;
}

.meta-dot {
  color: rgba(255,255,255,0.4);
}

.meta-sterilized {
  color: var(--accent);
  font-weight: 500;
}

.card-stats {
  display: flex;
  gap: 0.75rem;
  margin-top: 0.5rem;
  font-size: 0.6875rem;
  color: rgba(255,255,255,0.6);
}

/* --- Double-Bezel for larger cells --- */
.cell-1 .card-bg {
  border: 1px solid rgba(0,0,0,0.04);
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
}

/* --- Loading Skeleton --- */
.loading-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
}

.skeleton-card {
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  min-height: 200px;
  animation: pulse 1.5s ease-in-out infinite;
}

.skeleton-1 { grid-column: span 2; grid-row: span 2; }
.skeleton-2 { grid-column: span 2; grid-row: span 1; }
.skeleton-3 { grid-column: span 1; grid-row: span 1; }
.skeleton-4 { grid-column: span 1; grid-row: span 1; }
.skeleton-5 { grid-column: span 2; grid-row: span 1; }
.skeleton-6 { grid-column: span 2; grid-row: span 1; }

@keyframes pulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 0.6; }
}

/* --- Empty State --- */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 4rem 2rem;
  text-align: center;
}

.empty-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.empty-text {
  color: var(--text-tertiary);
  font-size: 0.9375rem;
}

/* --- Load More --- */
.load-more {
  display: flex;
  justify-content: center;
  margin-top: 2.5rem;
}

/* --- Responsive --- */
@media (max-width: 1024px) {
  .bento-grid,
  .loading-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .cell-1 { grid-column: span 2; grid-row: span 1; }
  .cell-4 { grid-column: span 1; grid-row: span 1; }
}

@media (max-width: 640px) {
  .bento-grid,
  .loading-grid {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }

  .cell-1,
  .cell-2,
  .cell-3,
  .cell-4,
  .cell-5,
  .cell-6 {
    grid-column: span 1;
    grid-row: span 1;
  }

  .card-bg {
    min-height: 140px;
  }

  .card-content {
    padding: 1rem;
  }

  .tabs-wrapper {
    gap: 0.375rem;
  }

  .tab-btn {
    padding: 0.5rem 1rem;
    font-size: 0.8125rem;
  }
}
</style>