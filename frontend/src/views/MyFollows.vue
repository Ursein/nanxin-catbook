<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { catApi, followApi } from '@/api'

const router = useRouter()
const cats = ref([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    router.push('/login')
    return
  }
  try {
    const idsRes = await followApi.myFollows()
    const ids = idsRes.data
    if (ids.length === 0) {
      loading.value = false
      return
    }
    const details = await Promise.all(
      ids.map(id => catApi.detail(id).then(r => r.data))
    )
    cats.value = details
  } catch (err) {
    error.value = 'Failed to load'
  } finally {
    loading.value = false
  }
})

const goToDetail = (id) => router.push(`/cat/${id}`)
</script>

<template>
  <div class="follows-page">
    <section class="follows-hero">
      <div class="container">
        <span class="eyebrow">Following</span>
        <h1 class="follows-title">Cats I Follow</h1>
      </div>
    </section>

    <section class="follows-content">
      <div class="container">
        <div v-if="loading" class="empty-state">
          <p class="empty-text">Loading...</p>
        </div>

        <div v-else-if="error" class="empty-state">
          <p class="empty-text">{{ error }}</p>
        </div>

        <div v-else-if="cats.length === 0" class="empty-state">
          <span class="empty-icon">🐱</span>
          <p class="empty-text">Not following any cats yet</p>
          <button class="btn-pill accent" @click="router.push('/')">Discover cats</button>
        </div>

        <div v-else class="follows-grid">
          <button
            v-for="cat in cats"
            :key="cat.id"
            class="follow-card"
            @click="goToDetail(cat.id)"
          >
            <div class="follow-avatar" />
            <div class="follow-info">
              <h3 class="follow-name">{{ cat.name }}</h3>
              <p v-if="cat.nickname" class="follow-nickname">{{ cat.nickname }}</p>
              <p class="follow-meta">{{ cat.locationArea }} · {{ cat.colourTags }}</p>
            </div>
          </button>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.follows-page {
  padding: var(--section-py) 0;
}

.follows-hero {
  margin-bottom: 2rem;
}

.follows-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 800;
  letter-spacing: -0.03em;
  margin: 0.75rem 0 1.5rem;
}

.follows-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1rem;
}

.follow-card {
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
</style>