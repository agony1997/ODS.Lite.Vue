import { createRouter, createWebHistory } from 'vue-router'
import IndexView from '../views/IndexView.vue'
import PurchaseBranchView from '../views/PurchaseBranchView.vue'
import PurchaseSalesView from '../views/PurchaseSalesView.vue'
import LoginView from '../views/LoginView.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresAuth: false }
  },
  {
    path: '/',
    name: 'index',
    component: IndexView,
    meta: { requiresAuth: true }
  },
  {
    path: '/purchase-branch',
    name: 'purchase-branch',
    component: PurchaseBranchView,
    meta: { requiresAuth: true }
  },
  {
    path: '/purchase-sales',
    name: 'purchase-sales',
    component: PurchaseSalesView,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = localStorage.getItem('mock_ods_token')

  if (to.meta.requiresAuth && !token) {
    return '/login'
  }

  if (to.path === '/login' && token) {
    return '/'
  }
})

export default router
