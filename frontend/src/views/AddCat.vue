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

const submit = async () => {
  if (!form.value.name) return
  loading.value = true
  try {
    const res = await catApi.create(form.value)
    router.push(`/cat/${res.data.id}`)
  } catch (err) {
    alert('添加失败: ' + (err.response?.data?.message || '未知错误'))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="add-page">
    <div class="container">
      <span class="eyebrow">猫咪档案</span>
      <h1 class="add-title">添加猫咪</h1>
      <div class="add-form">
        <div class="form-row">
          <label class="form-label">名字 *</label>
          <input v-model="form.name" class="form-input" placeholder="给猫咪起个名字" />
        </div>
        <div class="form-row">
          <label class="form-label">昵称</label>
          <input v-model="form.nickname" class="form-input" placeholder="小名/外号" />
        </div>
        <div class="form-row">
          <label class="form-label">性别</label>
          <select v-model="form.gender" class="form-input">
            <option v-for="g in genders" :key="g" :value="g">{{ g === 'MALE' ? '男孩' : g === 'FEMALE' ? '女孩' : '未知' }}</option>
          </select>
        </div>
        <div class="form-row">
          <label class="form-label">毛色标签</label>
          <input v-model="form.colourTags" class="form-input" placeholder="橘色;白色 (分号分隔)" />
        </div>
        <div class="form-row">
          <label class="form-label">性格标签</label>
          <input v-model="form.personalityTags" class="form-input" placeholder="亲人;可抱 (分号分隔)" />
        </div>
        <div class="form-row">
          <label class="form-label">区域</label>
          <input v-model="form.locationArea" class="form-input" placeholder="中苑/西苑/东苑/图书馆" />
        </div>
        <div class="form-row">
          <label class="form-label">具体位置</label>
          <input v-model="form.locationDetail" class="form-input" placeholder="例如：图书馆东侧花坛" />
        </div>
        <div class="form-row">
          <label class="form-label">出生年份</label>
          <input v-model="form.birthYear" type="number" class="form-input" placeholder="例如：2022" />
        </div>
        <div class="form-row">
          <label class="form-label">体重 (kg)</label>
          <input v-model="form.weight" type="number" step="0.1" class="form-input" placeholder="例如：4.5" />
        </div>
        <div class="form-row">
          <label class="form-label">性格描述</label>
          <textarea v-model="form.personalityDesc" class="form-input form-textarea" placeholder="详细描述猫咪的性格特点..." rows="3" />
        </div>
        <div class="form-row">
          <label class="form-label">状态</label>
          <select v-model="form.status" class="form-input">
            <option v-for="s in statuses" :key="s" :value="s">
              {{ s === 'ACTIVE' ? '在校' : s === 'SEEKING_ADOPT' ? '待领养' : s === 'MISSING' ? '失踪' : '离世' }}
            </option>
          </select>
        </div>
        <div class="form-row">
          <label class="form-label">已绝育</label>
          <input v-model="form.sterilized" type="checkbox" class="form-checkbox" />
        </div>
        <button class="btn-pill accent form-btn" :disabled="loading" @click="submit">
          {{ loading ? '添加中...' : '添加猫咪' }}
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.add-page {
  padding: var(--section-py) 0;
}
.add-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 800;
  letter-spacing: -0.03em;
  margin: 0.75rem 0 2rem;
}
.add-form {
  max-width: 480px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
}
.form-row {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
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
.form-textarea {
  resize: vertical;
  min-height: 5rem;
  line-height: 1.6;
}
.form-checkbox {
  width: 1.25rem;
  height: 1.25rem;
  accent-color: var(--accent);
}
.form-btn {
  margin-top: 0.5rem;
  justify-content: center;
  width: 100%;
}
</style>