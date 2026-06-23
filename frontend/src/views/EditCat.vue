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
    // 加载已有照片
    const photoRes = await photoApi.list(route.params.id)
    photos.value = photoRes.data || []
  } catch (err) {
    alert('加载失败')
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
    alert('保存失败')
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
    alert('上传失败')
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
      <span class="eyebrow">猫咪档案</span>
      <h1 class="add-title">编辑猫咪</h1>

      <!-- 照片管理 -->
      <section class="photo-manage">
        <h2 class="section-label">照片管理</h2>
        <div class="photo-grid">
          <div
            v-for="photo in photos"
            :key="photo.id"
            class="photo-item"
            :class="{ 'is-cover': form.coverPhotoId === photo.id }"
          >
            <img :src="photo.url" class="photo-thumb" alt="猫咪照片" />
            <div class="photo-overlay">
              <button
                v-if="form.coverPhotoId !== photo.id"
                class="photo-action"
                @click="setCover(photo.id)"
              >设为封面</button>
              <span v-else class="photo-action active">⭐ 封面</span>
            </div>
          </div>
        </div>
        <input
          ref="fileInput" type="file" accept="image/*" style="display:none"
          @change="handleFileUpload"
        />
        <button class="btn-pill" :disabled="uploading" @click="triggerUpload" style="margin-top:0.75rem">
          {{ uploading ? '上传中...' : '📸 上传新照片' }}
        </button>
      </section>

      <div class="add-form">
        <div class="form-row"><label class="form-label">名字 *</label><input v-model="form.name" class="form-input" /></div>
        <div class="form-row"><label class="form-label">昵称</label><input v-model="form.nickname" class="form-input" /></div>
        <div class="form-row"><label class="form-label">性别</label><select v-model="form.gender" class="form-input"><option v-for="g in genders" :key="g" :value="g">{{ g === 'MALE' ? '男孩' : g === 'FEMALE' ? '女孩' : '未知' }}</option></select></div>
        <div class="form-row"><label class="form-label">毛色标签</label><input v-model="form.colourTags" class="form-input" placeholder="橘色;白色" /></div>
        <div class="form-row"><label class="form-label">性格标签</label><input v-model="form.personalityTags" class="form-input" placeholder="亲人;可抱" /></div>
        <div class="form-row"><label class="form-label">区域</label><input v-model="form.locationArea" class="form-input" /></div>
        <div class="form-row"><label class="form-label">具体位置</label><input v-model="form.locationDetail" class="form-input" placeholder="例如：图书馆东侧花坛" /></div>
        <div class="form-row"><label class="form-label">出生年份</label><input v-model="form.birthYear" type="number" class="form-input" placeholder="例如：2022" /></div>
        <div class="form-row"><label class="form-label">体重 (kg)</label><input v-model="form.weight" type="number" step="0.1" class="form-input" placeholder="例如：4.5" /></div>
        <div class="form-row"><label class="form-label">性格描述</label><textarea v-model="form.personalityDesc" class="form-input form-textarea" placeholder="详细描述猫咪的性格特点..." rows="3" /></div>
        <div class="form-row"><label class="form-label">状态</label><select v-model="form.status" class="form-input"><option v-for="s in statuses" :key="s" :value="s">{{ s === 'ACTIVE' ? '在校' : s === 'SEEKING_ADOPT' ? '待领养' : s === 'MISSING' ? '失踪' : '离世' }}</option></select></div>
        <div class="form-row"><label class="form-label">已绝育</label><input v-model="form.sterilized" type="checkbox" class="form-checkbox" /></div>
        <button class="btn-pill accent form-btn" :disabled="loading" @click="submit">{{ loading ? '保存中...' : '保存' }}</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.add-page { padding: var(--section-py) 0; }
.add-title { font-size: clamp(2rem, 4vw, 3rem); font-weight: 800; letter-spacing: -0.03em; margin: 0.75rem 0 2rem; }

/* Photo Management */
.photo-manage { margin-bottom: 2rem; }
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

.add-form { max-width: 480px; display: flex; flex-direction: column; gap: 1rem; }
.form-row { display: flex; flex-direction: column; gap: 0.375rem; }
.form-label { font-size: 0.8125rem; color: var(--text-secondary); font-weight: 500; }
.form-input { padding: 0.75rem 1rem; border-radius: var(--radius-sm); border: 1px solid var(--border-subtle); background: var(--glass-bg); color: var(--text-primary); font-family: var(--font-body); font-size: 0.9375rem; outline: none; transition: border-color 0.3s; }
.form-input:focus { border-color: var(--border-hover); }
.form-input::placeholder { color: var(--text-tertiary); }
.form-textarea { resize: vertical; min-height: 5rem; line-height: 1.6; }
.form-checkbox { width: 1.25rem; height: 1.25rem; accent-color: var(--accent); }
.form-btn { margin-top: 0.5rem; justify-content: center; width: 100%; }
</style>