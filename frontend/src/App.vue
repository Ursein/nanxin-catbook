<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const isMenuOpen = ref(false)
const isScrolled = ref(false)
const user = ref(null)

const navLinks = [
  { name: '猫咪', path: '/' },
  { name: '搜索', path: '/search' },
  { name: '关于', path: '/about' },
]

const checkUser = () => {
  const stored = localStorage.getItem('user')
  if (stored) {
    try { user.value = JSON.parse(stored) } catch {}
  } else {
    user.value = null
  }
}

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  user.value = null
  router.push('/')
}

const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
}

const navigate = (path) => {
  isMenuOpen.value = false
  router.push(path)
}

onMounted(() => {
  checkUser()
  watch(route, checkUser)
  // IntersectionObserver for fade-up animations
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('visible')
        }
      })
    },
    { threshold: 0.1 }
  )

  document.querySelectorAll('.fade-up').forEach((el) => observer.observe(el))
})
</script>

<template>
  <div class="app">
    <!-- Floating Glass Nav -->
    <nav class="nav-fluid" :class="{ 'nav-scrolled': isScrolled }">
      <div class="nav-inner">
        <router-link to="/" class="nav-brand">
          <span class="brand-icon">🐱</span>
          <span class="brand-text">南信猫友记</span>
        </router-link>

        <div class="nav-links-desktop">
          <router-link
            v-for="link in navLinks"
            :key="link.path"
            :to="link.path"
            class="nav-link"
            :class="{ active: route.path === link.path }"
          >
            {{ link.name }}
          </router-link>
        </div>

        <div class="nav-actions">
          <template v-if="user">
            <router-link to="/my" class="nav-link" :class="{ active: route.path === '/my' }">我的</router-link>
            <button class="nav-logout" @click="logout">退出</button>
          </template>
          <router-link v-else to="/login" class="nav-link">登录</router-link>
        </div>

        <button class="hamburger" @click="toggleMenu" aria-label="菜单">
          <span class="hamburger-line" :class="{ open: isMenuOpen }" />
          <span class="hamburger-line" :class="{ open: isMenuOpen }" />
        </button>
      </div>
    </nav>

    <!-- Mobile Menu Overlay -->
    <transition name="menu">
      <div v-if="isMenuOpen" class="menu-overlay" @click="toggleMenu">
        <div class="menu-content" @click.stop>
          <div class="menu-links">
            <button
              v-for="link in navLinks"
              :key="link.path"
              class="menu-link"
              :class="{ active: route.path === link.path }"
              @click="navigate(link.path)"
            >
              {{ link.name }}
            </button>
            <template v-if="user">
              <button class="menu-link" :class="{ active: route.path === '/my' }" @click="navigate('/my')">
                我的
              </button>
              <button class="menu-link menu-logout" @click="logout">
                退出登录
              </button>
            </template>
            <template v-else>
              <button class="menu-link" :class="{ active: route.path === '/login' }" @click="navigate('/login')">
                登录
              </button>
              <button class="menu-link" :class="{ active: route.path === '/register' }" @click="navigate('/register')">
                注册
              </button>
            </template>
          </div>
        </div>
      </div>
    </transition>

    <!-- Main Content -->
    <main class="main-content">
      <router-view :key="$route.fullPath" v-slot="{ Component }">
        <transition name="page" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </main>

    <!-- Footer -->
    <footer class="footer">
      <div class="container">
        <div class="footer-inner">
          <p class="footer-text">南信猫友记 &copy; 2025</p>
          <p class="footer-sub">南京信息工程大学 &middot; 软件工程课程项目</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.app {
  min-height: 100dvh;
  display: flex;
  flex-direction: column;
}

/* --- Navigation --- */
.nav-fluid {
  position: fixed;
  top: 1.5rem;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;
  width: auto;
  min-width: 320px;
  max-width: 90vw;
  background: rgba(255, 255, 255, 0.7);
  backdrop-filter: blur(24px);
  -webkit-backdrop-filter: blur(24px);
  border: 1px solid rgba(0, 0, 0, 0.06);
  border-radius: 999px;
  padding: 0.5rem 0.75rem;
  transition: all 0.5s cubic-bezier(0.32, 0.72, 0, 1);
}

.nav-scrolled {
  background: rgba(255, 255, 255, 0.92);
  border-color: rgba(0, 0, 0, 0.1);
}

.nav-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.375rem 1rem 0.375rem 0.75rem;
  border-radius: 999px;
  transition: background 0.3s ease;
}
.nav-brand:hover {
  background: rgba(0, 0, 0, 0.03);
}

.brand-icon {
  font-size: 1.25rem;
}

.brand-text {
  font-family: var(--font-display);
  font-size: 0.95rem;
  font-weight: 700;
  letter-spacing: -0.02em;
}

.nav-links-desktop {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.nav-link {
  padding: 0.5rem 1rem;
  border-radius: 999px;
  font-size: 0.875rem;
  font-weight: 500;
  color: var(--text-secondary);
  transition: all 0.3s var(--ease-smooth);
}
.nav-link:hover {
  color: var(--text-primary);
  background: rgba(0, 0, 0, 0.04);
}
.nav-link.active {
  color: var(--text-primary);
  background: rgba(0, 0, 0, 0.06);
}

/* --- Nav Actions --- */
.nav-actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.nav-login {
  padding: 0.5rem 1rem;
  border-radius: 999px;
  font-size: 0.8125rem;
  font-weight: 500;
  color: var(--text-secondary);
  transition: all 0.3s var(--ease-smooth);
}
.nav-login:hover {
  color: var(--text-primary);
  background: rgba(0, 0, 0, 0.04);
}

.nav-user {
  font-size: 0.8125rem;
  color: var(--text-secondary);
}

.nav-logout {
  padding: 0.375rem 0.875rem;
  border-radius: 999px;
  font-size: 0.75rem;
  background: none;
  border: 1px solid var(--border-subtle);
  color: var(--text-tertiary);
  cursor: pointer;
  transition: all 0.3s;
}
.nav-logout:hover {
  border-color: var(--border-hover);
  color: var(--text-primary);
}

/* --- Hamburger --- */
.hamburger {
  display: none;
  flex-direction: column;
  gap: 5px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem;
  z-index: 10;
}

.hamburger-line {
  display: block;
  width: 20px;
  height: 2px;
  background: var(--text-primary);
  border-radius: 2px;
  transition: all 0.4s var(--ease-spring);
  transform-origin: center;
}

.hamburger-line.open:nth-child(1) {
  transform: rotate(45deg) translate(2.5px, 2.5px);
}
.hamburger-line.open:nth-child(2) {
  transform: rotate(-45deg) translate(2.5px, -2.5px);
}

/* --- Mobile Menu Overlay --- */
.menu-overlay {
  position: fixed;
  inset: 0;
  z-index: 99;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(32px);
  -webkit-backdrop-filter: blur(32px);
  display: flex;
  align-items: center;
  justify-content: center;
}

.menu-content {
  padding: 2rem;
}

.menu-links {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.menu-link {
  padding: 1rem 2.5rem;
  border-radius: 999px;
  font-family: var(--font-display);
  font-size: 1.5rem;
  font-weight: 600;
  background: none;
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.3s ease;
}
.menu-link:hover,
.menu-link.active {
  color: var(--text-primary);
  background: rgba(0, 0, 0, 0.06);
}

.menu-logout {
  color: var(--text-tertiary) !important;
  font-size: 1rem !important;
  margin-top: 0.5rem;
}

/* Menu Transition */
.menu-enter-active,
.menu-leave-active {
  transition: all 0.5s var(--ease-spring);
}
.menu-enter-from,
.menu-leave-to {
  opacity: 0;
}

/* --- Main Content --- */
.main-content {
  flex: 1;
  padding-top: 6rem;
}

/* Page Transition */
.page-enter-active,
.page-leave-active {
  transition: all 0.4s var(--ease-smooth);
}
.page-enter-from {
  opacity: 0;
  transform: translateY(1rem);
}
.page-leave-to {
  opacity: 0;
  transform: translateY(-1rem);
}

/* --- Footer --- */
.footer {
  border-top: 1px solid var(--border-subtle);
  padding: 2.5rem 0;
  margin-top: 4rem;
}

.footer-inner {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
  text-align: center;
}

.footer-text {
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.footer-sub {
  font-size: 0.75rem;
  color: var(--text-tertiary);
}

/* --- Responsive --- */
@media (max-width: 768px) {
  .nav-fluid {
    top: 0.75rem;
    min-width: unset;
    width: calc(100vw - 1.5rem);
    padding: 0.4rem 0.5rem;
  }

  .nav-links-desktop {
    display: none;
  }

  .nav-actions {
    display: none;
  }

  .hamburger {
    display: flex;
  }

  .main-content {
    padding-top: 5rem;
  }
}
</style>