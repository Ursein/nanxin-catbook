<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { catApi } from '@/api'

const router = useRouter()
const keyword = ref('')
const activeStatus = ref('')
const activeLocation = ref('')
const cats = ref([])
const loading = ref(false)
const SIZE = 50

const statuses = [
  { key: '', label: '全部' },
  { key: 'ACTIVE', label: '在校' },
  { key: 'SEEKING_ADOPT', label: '待领养' },
  { key: 'MISSING', label: '失踪' },
  { key: 'DECEASED', label: '喵星' },
]

const locations = [
  { key: '', label: '全部区域' },
  { key: '中苑', label: '中苑' },
  { key: '西苑', label: '西苑' },
  { key: '东苑', label: '东苑' },
]

let debounceTimer = null

const loadCats = async () => {
  loading.value = true
  try {
    const params = { size: SIZE }
    if (keyword.value.trim()) params.keyword = keyword.value.trim()
    if (activeStatus.value) params.status = activeStatus.value
    if (activeLocation.value) params.location = activeLocation.value
    const res = await catApi.list(params)
    cats.value = res.data.content
  } catch (err) {
    console.error('Search failed:', err)
  } finally {
    loading.value = false
  }
}

const onInput = () => {
  clearTimeout(debounceTimer)
  debounceTimer = setTimeout(loadCats, 300)
}

const switchStatus = (key) => {
  activeStatus.value = key
  loadCats()
}

const switchLocation = (key) => {
  activeLocation.value = key
  loadCats()
}

const goToDetail = (id) => router.push(`/cat/${id}`)

const statusLabel = (s) => {
  const map = { ACTIVE: '在校', SEEKING_ADOPT: '待领养', MISSING: '失踪', DECEASED: '离世' }
  return map[s] || s
}

onMounted(() => {
  loadCats()
})
</script>

<template>
  <div class="search-page">
    <section class="search-hero">
      <div class="container">
        <span class="eyebrow">找一只猫</span>
        <h1 class="search-title">搜索猫咪</h1>
        <div class="search-bar">
          <input
            v-model="keyword"
            type="text"
            class="search-input"
            placeholder="输入猫咪名字、毛色..."
            @input="onInput"
          />
          <span v-if="keyword" class="search-clear" @click="keyword = ''; loadCats()">✕</span>
        </div>

        <!-- Status Tabs -->
        <div class="filter-row">
          <div class="filter-group">
            <span class="filter-label">状态</span>
            <div class="filter-chips">
              <button
                v-for="s in statuses"
                :key="s.key"
                class="chip"
                :class="{ active: activeStatus === s.key }"
                @click="switchStatus(s.key)"
              >{{ s.label }}</button>
            </div>
          </div>

          <!-- Location Chips -->
          <div class="filter-group">
            <span class="filter-label">区域</span>
            <div class="filter-chips">
              <button
                v-for="loc in locations"
                :key="loc.key"
                class="chip"
                :class="{ active: activeLocation === loc.key }"
                @click="switchLocation(loc.key)"
              >{{ loc.label }}</button>
            </div>
          </div>
        </div>
      </div>
    </section>

    <section class="search-results">
      <div class="container">
        <div v-if="loading" class="empty-state">
          <p class="empty-text">加载中...</p>
        </div>

        <div v-else-if="cats.length === 0" class="empty-state">
          <span class="empty-icon">🐱</span>
          <p class="empty-text">没有找到匹配的猫咪</p>
        </div>

        <div v-else class="results-grid">
          <button
            v-for="cat in cats"
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
                <span class="status-tag" :class="cat.status?.toLowerCase()">{{ statusLabel(cat.status) }}</span>
                <span v-if="cat.locationArea">· {{ cat.locationArea }}</span>
              </p>
              <p v-if="cat.colourTags" class="cat-card-colour">{{ cat.colourTags }}</p>
            </div>
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.search-page {
  padding: var(--section-py) 0;
}

.search-hero {
  margin-bottom: 2rem;
}

.search-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 800;
  letter-spacing: -0.03em;
  margin: 0.75rem 0 1.5rem;
}

.search-bar {
  position: relative;
  max-width: 480px;
}

.search-input {
  width: 100%;
  padding: 0.75rem 2.5rem 0.75rem 1.25rem;
  border-radius: 999px;
  border: 1px solid var(--border-subtle);
  background: var(--glass-bg);
  color: var(--text-primary);
  font-family: var(--font-body);
  font-size: 0.9375rem;
  outline: none;
  transition: border-color 0.3s ease;
}
.search-input:focus {
  border-color: var(--border-hover);
}
.search-input::placeholder {
  color: var(--text-tertiary);
}

.search-clear {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  font-size: 0.875rem;
  color: var(--text-tertiary);
  cursor: pointer;
  padding: 0.25rem;
  line-height: 1;
}
.search-clear:hover {
  color: var(--text-primary);
}

/* Filter Row */
.filter-row {
  display: flex;
  flex-wrap: wrap;
  gap: 1.5rem;
  margin-top: 1.25rem;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.filter-label {
  font-size: 0.8125rem;
  font-weight: 600;
  color: var(--text-secondary);
  flex-shrink: 0;
}

.filter-chips {
  display: flex;
  gap: 0.375rem;
  flex-wrap: wrap;
}

.chip {
  padding: 0.375rem 0.875rem;
  border-radius: 999px;
  font-size: 0.8125rem;
  font-weight: 500;
  background: var(--glass-bg);
  border: 1px solid var(--border-subtle);
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s var(--ease-smooth);
}
.chip:hover {
  border-color: var(--border-hover);
  color: var(--text-primary);
}
.chip.active {
  background: var(--text-primary);
  color: #fff;
  border-color: var(--text-primary);
}

/* Results Grid */
.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 1rem;
}

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
  height: 160px;
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

.cat-card-colour {
  font-size: 0.75rem;
  color: var(--text-tertiary);
  margin-top: 0.125rem;
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

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 4rem 2rem;
  text-align: center;
  gap: 0.5rem;
}
.empty-icon {
  font-size: 3rem;
  opacity: 0.5;
}
.empty-text {
  color: var(--text-tertiary);
  font-size: 0.9375rem;
}

@media (max-width: 768px) {
  .filter-row {
    flex-direction: column;
    gap: 0.75rem;
  }
  .results-grid {
    grid-template-columns: 1fr;
  }
}
</style>