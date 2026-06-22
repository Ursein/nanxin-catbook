<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { catApi } from '@/api'

const router = useRouter()
const query = ref('')
const results = ref([])
const searched = ref(false)
const searching = ref(false)

const doSearch = async () => {
  if (!query.value.trim()) return
  searched.value = true
  searching.value = true
  try {
    const res = await catApi.list({ keyword: query.value })
    results.value = res.data.content
  } catch (err) {
    console.error('Search failed:', err)
  } finally {
    searching.value = false
  }
}

const goToDetail = (id) => router.push(`/cat/${id}`)
</script>

<template>
  <div class="search-page">
    <section class="search-hero">
      <div class="container">
        <span class="eyebrow">找一只猫</span>
        <h1 class="search-title">搜索猫咪</h1>
        <div class="search-bar">
          <input
            v-model="query"
            type="text"
            class="search-input"
            placeholder="输入猫咪名字、毛色或位置..."
            @keyup.enter="doSearch"
          />
          <button class="btn-pill accent" @click="doSearch">搜索</button>
        </div>
      </div>
    </section>

    <section class="search-results">
      <div class="container">
        <div v-if="searched && !searching && results.length === 0" class="empty-state">
          <p class="empty-text">没有找到匹配的猫咪</p>
        </div>

        <div v-if="searching" class="empty-state">
          <p class="empty-text">搜索中...</p>
        </div>

        <div v-if="results.length > 0" class="results-grid">
          <button
            v-for="cat in results"
            :key="cat.id"
            class="result-card"
            @click="goToDetail(cat.id)"
          >
            <div class="result-placeholder" />
            <div class="result-info">
              <h3 class="result-name">{{ cat.name }}</h3>
              <p class="result-meta">{{ cat.locationArea }} · {{ cat.colourTags }}</p>
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
  display: flex;
  gap: 0.75rem;
  max-width: 560px;
}

.search-input {
  flex: 1;
  padding: 0.75rem 1.25rem;
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

.results-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}

.result-card {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-md);
  cursor: pointer;
  transition: all 0.3s var(--ease-smooth);
  text-align: left;
  color: var(--text-primary);
  width: 100%;
}
.result-card:hover {
  background: var(--bg-card);
  border-color: var(--border-hover);
}

.result-placeholder {
  width: 4rem;
  height: 4rem;
  border-radius: var(--radius-sm);
  background: var(--bg-card-hover);
  flex-shrink: 0;
}

.result-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
  justify-content: center;
}

.result-name {
  font-size: 1rem;
  font-weight: 600;
}

.result-meta {
  font-size: 0.8125rem;
  color: var(--text-tertiary);
}

.empty-state {
  padding: 4rem 2rem;
  text-align: center;
}

.empty-text {
  color: var(--text-tertiary);
}
</style>