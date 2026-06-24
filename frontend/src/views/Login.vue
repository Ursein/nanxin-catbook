<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '@/api'

const router = useRouter()
const username = ref('')
const password = ref('')
const error = ref('')
const loading = ref(false)

const login = async () => {
  error.value = ''
  if (!username.value || !password.value) {
    error.value = 'Please enter username and password'
    return
  }
  loading.value = true
  try {
    const res = await authApi.login(username.value, password.value)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('user', JSON.stringify(res.data.user))
    router.push('/')
  } catch (err) {
    error.value = err.response?.data?.message || 'Login failed'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="auth-page">
    <div class="auth-card">
      <span class="eyebrow">NanXin CatBook</span>
      <h1 class="auth-title">Login</h1>
      <div class="auth-form">
        <input v-model="username" type="text" class="auth-input" placeholder="Username" @keyup.enter="login" />
        <input v-model="password" type="password" class="auth-input" placeholder="Password" @keyup.enter="login" />
        <p v-if="error" class="auth-error">{{ error }}</p>
        <button class="btn-pill accent auth-btn" :disabled="loading" @click="login">
          {{ loading ? 'Logging in...' : 'Login' }}
        </button>
        <p class="auth-switch">
          No account? <router-link to="/register" class="auth-link">Register</router-link>
        </p>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100dvh - 16rem);
  padding: 2rem;
}

.auth-card {
  width: 100%;
  max-width: 400px;
  padding: 2.5rem 2rem;
  background: var(--glass-bg);
  border: 1px solid var(--glass-border);
  border-radius: var(--radius-lg);
}

.auth-title {
  font-size: 2rem;
  font-weight: 800;
  letter-spacing: -0.03em;
  margin: 0.75rem 0 1.5rem;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.auth-input {
  padding: 0.75rem 1rem;
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-subtle);
  background: var(--bg-card);
  color: var(--text-primary);
  font-family: var(--font-body);
  font-size: 0.9375rem;
  outline: none;
  transition: border-color 0.3s;
}
.auth-input:focus {
  border-color: var(--border-hover);
}
.auth-input::placeholder {
  color: var(--text-tertiary);
}

.auth-error {
  font-size: 0.8125rem;
  color: #ef4444;
}

.auth-btn {
  margin-top: 0.5rem;
  justify-content: center;
  width: 100%;
}

.auth-switch {
  text-align: center;
  font-size: 0.8125rem;
  color: var(--text-tertiary);
  margin-top: 0.5rem;
}

.auth-link {
  color: var(--accent);
  font-weight: 500;
}
</style>