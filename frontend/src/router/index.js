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
    meta: { requiresAuth: false, title: '登入' }
  },
  {
    path: '/',
    name: 'index',
    component: IndexView,
    meta: { requiresAuth: true, title: '首頁', icon: 'home' }
  },
  {
    path: '/purchase-sales',
    name: 'purchase-sales',
    component: PurchaseSalesView,
    meta: { requiresAuth: true, title: '業務員訂貨', icon: 'shopping_cart', roles: ['SALES', 'LEADER', 'ADMIN'] }
  },
  {
    path: '/purchase-branch',
    name: 'purchase-branch',
    component: PurchaseBranchView,
    meta: { requiresAuth: true, title: '營業所訂貨彙總', icon: 'shopping_cart', roles: ['LEADER', 'ADMIN'] }
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

  if (to.meta.roles && to.meta.roles.length > 0 && token) {
    const savedUser = localStorage.getItem('mock_ods_user')
    if (savedUser) {
      const user = JSON.parse(savedUser)
      const userRoles = user.roles || []
      const hasRole = to.meta.roles.some(role => userRoles.includes(role))
      if (!hasRole) {
        return '/'
      }
    }
  }
})

export default router
