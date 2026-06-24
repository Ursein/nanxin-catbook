<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { catApi, photoApi } from '@/api'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const photos = ref([])
const uploading = ref(false)
const fileInput = ref(null)
const form = ref({ name: '', nickname: '', gender: 'MALE', colourTags: '', personalityTags: '', personalityDesc: '', locationArea: '', locationDetail: '', birthYear: null, weight: null, sterilized: false, status: 'ACTIVE', coverPhotoId: null })
const genders = ['MALE', 'FEMALE', 'UNKNOWN']
const statuses = ['ACTIVE', 'SEEKING_ADOPT', 'MISSING', 'DECEASED']
const locations = ['中苑', '西苑', '东苑']
const locationLabels = { '中苑': 'Central', '西苑': 'West', '东苑': 'East' }

onMounted(async () => {
  try {
    const res = await catApi.detail(route.params.id)
    const d = res.data
    form.value = {
      name: d.name || '', nickname: d.nickname || '', gender: d.gender || 'MALE',
      colourTags: d.colourTags || '', personalityTags: d.personalityTags || '',
      personalityDesc: d.personalityDesc || '', locationArea: d.locationArea || '',
      locationDetail: d.locationDetail || '', birthYear: d.birthYear || null,
      weight: d.weight || null, sterilized: d.sterilized || false,
      status: d.status || 'ACTIVE', coverPhotoId: d.coverPhotoId || null,
    }
    const photoRes = await photoApi.list(route.params.id)
    photos.value = photoRes.data || []
  } catch (err) {
    alert('Failed to load')
    router.push('/')
  }
})

const submit = async () => {
  if (!form.value.name) return
  loading.value = true
  try {
    await catApi.update(route.params.id, form.value)
    router.push(`/cat/${route.params.id}`)
  } catch (err) {
    alert('Failed to save')
  } finally {
    loading.value = false
  }
}

const triggerUpload = () => fileInput.value?.click()
const handleFileUpload = async (e) => {
  const file = e.target.files?.[0]
  if (!file) return
  uploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const res = await photoApi.upload(route.params.id, formData)
    if (res.data) photos.value.push(res.data)
  } catch (err) {
    alert('Upload failed')
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}
const setCover = (photoId) => {
  form.value.coverPhotoId = form.value.coverPhotoId === photoId ? null : photoId
}
</script>

<template>
  <div class="add-page">
    <div class="container">
      <div class="form-header">
        <span class="eyebrow">Cat Profile</span>
        <h1 class="add-title">Edit Cat</h1>
      </div>

      <section class="photo-manage">
        <h2 class="section-label">Photos</h2>
        <div class="photo-grid">
          <div v-for="photo in photos" :key="photo.id" class="photo-item" :class="{ 'is-cover': form.coverPhotoId === photo.id }">
            <img :src="photo.url" class="photo-thumb" alt="Cat photo" />
            <div class="photo-overlay">
              <button v-if="form.coverPhotoId !== photo.id" class="photo-action" @click="setCover(photo.id)">Set Cover</button>
              <span v-else class="photo-action active">⭐ Cover</span>
            </div>
          </div>
        </div>
        <input ref="fileInput" type="file" accept="image/*" style="display:none" @change="handleFileUpload" />
        <button class="btn-pill" :disabled="uploading" @click="triggerUpload" style="margin-top:0.75rem">
          {{ uploading ? 'Uploading...' : '📸 Upload Photo' }}
        </button>
      </section>

      <div class="add-form">
        <div class="form-row"><label class="form-label">Name *</label><input v-model="form.name" class="form-input" /></div>
        <div class="form-row"><label class="form-label">Nickname</label><input v-model="form.nickname" class="form-input" /></div>
        <div class="form-row"><label class="form-label">Gender</label><select v-model="form.gender" class="form-input"><option v-for="g in genders" :key="g" :value="g">{{ g === 'MALE' ? 'Male' : g === 'FEMALE' ? 'Female' : 'Unknown' }}</option></select></div>
        <div class="form-row"><label class="form-label">Status</label><select v-model="form.status" class="form-input"><option v-for="s in statuses" :key="s" :value="s">{{ s === 'ACTIVE' ? 'On Campus' : s === 'SEEKING_ADOPT' ? 'Adoptable' : s === 'MISSING' ? 'Missing' : 'Deceased' }}</option></select></div>
        <div class="form-row"><label class="form-label">Area</label><select v-model="form.locationArea" class="form-input"><option value="">Select</option><option v-for="loc in locations" :key="loc" :value="loc">{{ locationLabels[loc] }}</option></select></div>
        <div class="form-row"><label class="form-label">Specific Location</label><input v-model="form.locationDetail" class="form-input" placeholder="e.g. East of Library" /></div>
        <div class="form-row"><label class="form-label">Color Tags</label><input v-model="form.colourTags" class="form-input" placeholder="Orange;White" /></div>
        <div class="form-row"><label class="form-label">Personality Tags</label><input v-model="form.personalityTags" class="form-input" placeholder="Friendly;Cuddly" /></div>
        <div class="form-row"><label class="form-label">Birth Year</label><input v-model="form.birthYear" type="number" class="form-input" placeholder="e.g. 2022" /></div>
        <div class="form-row"><label class="form-label">Weight (kg)</label><input v-model="form.weight" type="number" step="0.1" class="form-input" placeholder="e.g. 4.5" /></div>
        <div class="form-row full-width"><label class="form-label">Personality Description</label><textarea v-model="form.personalityDesc" class="form-input form-textarea" placeholder="Describe the cat's personality..." rows="3" /></div>
        <div class="form-row full-width form-footer">
          <label class="form-checkbox-label"><input v-model="form.sterilized" type="checkbox" class="form-checkbox" /> Neutered</label>
          <button class="btn-pill accent form-btn" :disabled="loading" @click="submit">{{ loading ? 'Saving...' : 'Save' }}</button>
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
.photo-manage { margin-bottom: 2rem; max-width: 640px; margin-left: auto; margin-right: auto; }
.section-label { font-size: 0.875rem; font-weight: 700; color: var(--text-secondary); margin-bottom: 0.75rem; }
.photo-grid { display: flex; flex-wrap: wrap; gap: 0.75rem; }
.photo-item { position: relative; width: 120px; height: 120px; border-radius: var(--radius-sm); overflow: hidden; border: 2px solid transparent; transition: all 0.3s; }
.photo-item.is-cover { border-color: var(--accent); }
.photo-thumb { width: 100%; height: 100%; object-fit: cover; display: block; }
.photo-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.4); display: flex; align-items: center; justify-content: center; opacity: 0; transition: opacity 0.3s; }
.photo-item:hover .photo-overlay { opacity: 1; }
.photo-action { padding: 0.375rem 0.75rem; border-radius: 999px; font-size: 0.75rem; font-weight: 500; border: 1px solid rgba(255,255,255,0.3); background: rgba(255,255,255,0.15); color: #fff; cursor: pointer; backdrop-filter: blur(4px); }
.photo-action:hover { background: rgba(255,255,255,0.3); }
.photo-action.active { border-color: var(--accent); background: var(--accent); color: #fff; }
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