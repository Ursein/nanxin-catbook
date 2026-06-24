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
      <div class="form-header">
        <span class="eyebrow">猫咪档案</span>
        <h1 class="add-title">添加猫咪</h1>
      </div>
      <div class="add-form">
        <!-- 基本信息 -->
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
          <label class="form-label">状态</label>
          <select v-model="form.status" class="form-input">
            <option v-for="s in statuses" :key="s" :value="s">
              {{ s === 'ACTIVE' ? '在校' : s === 'SEEKING_ADOPT' ? '待领养' : s === 'MISSING' ? '失踪' : '离世' }}
            </option>
          </select>
        </div>

        <!-- 位置信息 -->
        <div class="form-row">
          <label class="form-label">区域</label>
          <select v-model="form.locationArea" class="form-input">
            <option value="">请选择</option>
            <option v-for="loc in locations" :key="loc" :value="loc">{{ loc }}</option>
          </select>
        </div>
        <div class="form-row">
          <label class="form-label">具体位置</label>
          <input v-model="form.locationDetail" class="form-input" placeholder="例如：图书馆东侧花坛" />
        </div>

        <!-- 特征信息 -->
        <div class="form-row">
          <label class="form-label">毛色标签</label>
          <input v-model="form.colourTags" class="form-input" placeholder="橘色;白色 (分号分隔)" />
        </div>
        <div class="form-row">
          <label class="form-label">性格标签</label>
          <input v-model="form.personalityTags" class="form-input" placeholder="亲人;可抱 (分号分隔)" />
        </div>
        <div class="form-row">
          <label class="form-label">出生年份</label>
          <input v-model="form.birthYear" type="number" class="form-input" placeholder="例如：2022" />
        </div>
        <div class="form-row">
          <label class="form-label">体重 (kg)</label>
          <input v-model="form.weight" type="number" step="0.1" class="form-input" placeholder="例如：4.5" />
        </div>

        <!-- 性格描述 -->
        <div class="form-row full-width">
          <label class="form-label">性格描述</label>
          <textarea v-model="form.personalityDesc" class="form-input form-textarea" placeholder="详细描述猫咪的性格特点..." rows="3" />
        </div>

        <!-- 绝育 + 提交 -->
        <div class="form-row full-width form-footer">
          <label class="form-checkbox-label">
            <input v-model="form.sterilized" type="checkbox" class="form-checkbox" />
            已绝育
          </label>
          <button class="btn-pill accent form-btn" :disabled="loading" @click="submit">
            {{ loading ? '添加中...' : '添加猫咪' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.add-page {
  padding: var(--section-py) 0;
}

.form-header {
  text-align: center;
  margin-bottom: 2rem;
}

.form-header :deep(.eyebrow) {
  background: none;
  padding: 0;
  color: var(--text-tertiary);
  font-size: 0.75rem;
  letter-spacing: 0.15em;
}

.add-title {
  font-size: clamp(2rem, 4vw, 3rem);
  font-weight: 800;
  letter-spacing: -0.03em;
  margin: 0.5rem 0 0;
}

.add-form {
  max-width: 640px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
}

.form-row {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.form-row.full-width {
  grid-column: 1 / -1;
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

.form-checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 0.9375rem;
  color: var(--text-primary);
  cursor: pointer;
}

.form-checkbox {
  width: 1.125rem;
  height: 1.125rem;
  accent-color: var(--accent);
}

.form-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-top: 0.5rem;
  border-top: 1px solid var(--border-subtle);
}

.form-btn {
  padding: 0.75rem 2rem;
}

@media (max-width: 640px) {
  .add-form {
    grid-template-columns: 1fr;
  }

  .form-footer {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }

  .form-btn {
    width: 100%;
  }
}
</style>