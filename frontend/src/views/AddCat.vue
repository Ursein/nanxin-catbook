<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { catApi } from '@/api'

const router = useRouter()
const loading = ref(false)
const form = ref({
  name: '', nickname: '', gender: 'MALE', colourTags: '',
  personalityTags: '', personalityDesc: '', locationArea: '',
  locationDetail: '', birthYear: null, weight: null,
  sterilized: false, status: 'ACTIVE',
})

const genders = ['MALE', 'FEMALE', 'UNKNOWN']
const statuses = ['ACTIVE', 'SEEKING_ADOPT', 'MISSING', 'DECEASED']
const locations = ['中苑', '西苑', '东苑']

const locationLabels = { '中苑': 'Central', '西苑': 'West', '东苑': 'East' }

const submit = async () => {
  if (!form.value.name) return
  loading.value = true
  try {
    const res = await catApi.create(form.value)
    router.push(`/cat/${res.data.id}`)
  } catch (err) {
    alert('Failed to add: ' + (err.response?.data?.message || 'Unknown error'))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="add-page">
    <div class="container">
      <div class="form-header">
        <span class="eyebrow">Cat Profile</span>
        <h1 class="add-title">Add Cat</h1>
      </div>
      <div class="add-form">
        <div class="form-row">
          <label class="form-label">Name *</label>
          <input v-model="form.name" class="form-input" placeholder="Give the cat a name" />
        </div>
        <div class="form-row">
          <label class="form-label">Nickname</label>
          <input v-model="form.nickname" class="form-input" placeholder="A short nickname" />
        </div>
        <div class="form-row">
          <label class="form-label">Gender</label>
          <select v-model="form.gender" class="form-input">
            <option v-for="g in genders" :key="g" :value="g">{{ g === 'MALE' ? 'Male' : g === 'FEMALE' ? 'Female' : 'Unknown' }}</option>
          </select>
        </div>
        <div class="form-row">
          <label class="form-label">Status</label>
          <select v-model="form.status" class="form-input">
            <option v-for="s in statuses" :key="s" :value="s">
              {{ s === 'ACTIVE' ? 'On Campus' : s === 'SEEKING_ADOPT' ? 'Adoptable' : s === 'MISSING' ? 'Missing' : 'Deceased' }}
            </option>
          </select>
        </div>

        <div class="form-row">
          <label class="form-label">Area</label>
          <select v-model="form.locationArea" class="form-input">
            <option value="">Select</option>
            <option v-for="loc in locations" :key="loc" :value="loc">{{ locationLabels[loc] }}</option>
          </select>
        </div>
        <div class="form-row">
          <label class="form-label">Specific Location</label>
          <input v-model="form.locationDetail" class="form-input" placeholder="e.g. East of Library" />
        </div>

        <div class="form-row">
          <label class="form-label">Color Tags</label>
          <input v-model="form.colourTags" class="form-input" placeholder="Orange;White (semicolon separated)" />
        </div>
        <div class="form-row">
          <label class="form-label">Personality Tags</label>
          <input v-model="form.personalityTags" class="form-input" placeholder="Friendly;Cuddly (semicolon separated)" />
        </div>
        <div class="form-row">
          <label class="form-label">Birth Year</label>
          <input v-model="form.birthYear" type="number" class="form-input" placeholder="e.g. 2022" />
        </div>
        <div class="form-row">
          <label class="form-label">Weight (kg)</label>
          <input v-model="form.weight" type="number" step="0.1" class="form-input" placeholder="e.g. 4.5" />
        </div>

        <div class="form-row full-width">
          <label class="form-label">Personality Description</label>
          <textarea v-model="form.personalityDesc" class="form-input form-textarea" placeholder="Describe the cat's personality..." rows="3" />
        </div>

        <div class="form-row full-width form-footer">
          <label class="form-checkbox-label">
            <input v-model="form.sterilized" type="checkbox" class="form-checkbox" />
            Neutered
          </label>
          <button class="btn-pill accent form-btn" :disabled="loading" @click="submit">
            {{ loading ? 'Adding...' : 'Add Cat' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.add-page { padding: var(--section-py) 0; }
.form-header { text-align: center; margin-bottom: 2rem; }
.form-header :deep(.eyebrow) { background: none; padding: 0; color: var(--text-tertiary); font-size: 0.75rem; letter-spacing: 0.15em; }
.add-title { font-size: clamp(2rem, 4vw, 3rem); font-weight: 800; letter-spacing: -0.03em; margin: 0.5rem 0 0; }
.add-form { max-width: 640px; margin: 0 auto; display: grid; grid-template-columns: 1fr 1fr; gap: 1rem; }
.form-row { display: flex; flex-direction: column; gap: 0.375rem; }
.form-row.full-width { grid-column: 1 / -1; }
.form-label { font-size: 0.8125rem; color: var(--text-secondary); font-weight: 500; }
.form-input { padding: 0.75rem 1rem; border-radius: var(--radius-sm); border: 1px solid var(--border-subtle); background: var(--glass-bg); color: var(--text-primary); font-family: var(--font-body); font-size: 0.9375rem; outline: none; transition: border-color 0.3s; }
.form-input:focus { border-color: var(--border-hover); }
.form-input::placeholder { color: var(--text-tertiary); }
.form-textarea { resize: vertical; min-height: 5rem; line-height: 1.6; }
.form-checkbox-label { display: flex; align-items: center; gap: 0.5rem; font-size: 0.9375rem; color: var(--text-primary); cursor: pointer; }
.form-checkbox { width: 1.125rem; height: 1.125rem; accent-color: var(--accent); }
.form-footer { display: flex; align-items: center; justify-content: space-between; padding-top: 0.5rem; border-top: 1px solid var(--border-subtle); }
.form-btn { padding: 0.75rem 2rem; }
@media (max-width: 640px) { .add-form { grid-template-columns: 1fr; } .form-footer { flex-direction: column; gap: 1rem; align-items: stretch; } .form-btn { width: 100%; } }
</style>