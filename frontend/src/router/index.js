import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
  },
  {
    path: '/cat/:id',
    name: 'CatDetail',
    component: () => import('@/views/CatDetail.vue'),
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('@/views/Search.vue'),
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
  },
  {
    path: '/add',
    name: 'AddCat',
    component: () => import('@/views/AddCat.vue'),
  },
  {
    path: '/edit/:id',
    name: 'EditCat',
    component: () => import('@/views/EditCat.vue'),
  },
  {
    path: '/my',
    name: 'MyProfile',
    component: () => import('@/views/MyProfile.vue'),
  },
  {
    path: '/my-follows',
    name: 'MyFollows',
    component: () => import('@/views/MyFollows.vue'),
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

export default router