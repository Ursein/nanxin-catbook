<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { catApi } from '@/api'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const form = ref({ name: '', nickname: '', gender: 'MALE', colourTags: '', personalityTags: '', locationArea: '', sterilized: false, status: 'ACTIVE' })
const genders = ['MALE', 'FEMALE', 'UNKNOWN']
const statuses = ['ACTIVE', 'SEEKING_ADOPT', 'MISSING', 'DECEASED']

onMounted(async () => {
  try {
    const res = await catApi.detail(route.params.id)
    const d = res.data
    form.value = {
      name: d.name || '', nickname: d.nickname || '', gender: d.gender || 'MALE',
      colourTags: d.colourTags || '', personalityTags: d.personalityTags || '',
      locationArea: d.locationArea || '', sterilized: d.sterilized || false,
      status: d.status || 'ACTIVE',
    }
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
</script>

<template>
  <div class="add-page">
    <div class="container">
      <span class="eyebrow">猫咪档案</span>
      <h1 class="add-title">编辑猫咪</h1>
      <div class="add-form">
        <div class="form-row"><label class="form-label">名字 *</label><input v-model="form.name" class="form-input" /></div>
        <div class="form-row"><label class="form-label">昵称</label><input v-model="form.nickname" class="form-input" /></div>
        <div class="form-row"><label class="form-label">性别</label><select v-model="form.gender" class="form-input"><option v-for="g in genders" :key="g" :value="g">{{ g === 'MALE' ? '男孩' : g === 'FEMALE' ? '女孩' : '未知' }}</option></select></div>
        <div class="form-row"><label class="form-label">毛色标签</label><input v-model="form.colourTags" class="form-input" placeholder="橘色;白色" /></div>
        <div class="form-row"><label class="form-label">性格标签</label><input v-model="form.personalityTags" class="form-input" placeholder="亲人;可抱" /></div>
        <div class="form-row"><label class="form-label">区域</label><input v-model="form.locationArea" class="form-input" /></div>
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
.add-form { max-width: 480px; display: flex; flex-direction: column; gap: 1rem; }
.form-row { display: flex; flex-direction: column; gap: 0.375rem; }
.form-label { font-size: 0.8125rem; color: var(--text-secondary); font-weight: 500; }
.form-input { padding: 0.75rem 1rem; border-radius: var(--radius-sm); border: 1px solid var(--border-subtle); background: var(--glass-bg); color: var(--text-primary); font-family: var(--font-body); font-size: 0.9375rem; outline: none; transition: border-color 0.3s; }
.form-input:focus { border-color: var(--border-hover); }
.form-checkbox { width: 1.25rem; height: 1.25rem; accent-color: var(--accent); }
.form-btn { margin-top: 0.5rem; justify-content: center; width: 100%; }
</style>