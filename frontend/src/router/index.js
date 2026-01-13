import { createRouter, createWebHistory } from 'vue-router'
import IndexView from '../views/IndexView.vue'
import PurchaseBranchView from '../views/PurchaseBranchView.vue'
import PurchaseSalesView from '../views/PurchaseSalesView.vue'

const routes = [
  {
    path: '/',
    name: 'index',
    component: IndexView
  },
  {
    path: '/purchase-branch',
    name: 'purchase-branch',
    component: PurchaseBranchView
  },
  {
    path: '/purchase-sales',
    name: 'purchase-sales',
    component: PurchaseSalesView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
