<template>
  <q-layout view="hHh lpR fFf">
    <q-header elevated class="bg-primary text-white" v-if="authStore.isAuthenticated">
      <q-toolbar>
        <q-btn flat dense round icon="menu" @click="drawerOpen = !drawerOpen" />
        <q-toolbar-title>Mock ODS VUE</q-toolbar-title>
        <span class="q-mr-sm">{{ authStore.user?.userName }}</span>
        <q-btn flat icon="logout" label="登出" @click="authStore.logout()" />
      </q-toolbar>
    </q-header>

    <q-drawer
      v-if="authStore.isAuthenticated"
      v-model="drawerOpen"
      show-if-above
      bordered
      :mini="miniState"
      @mouseover="miniState = false"
      @mouseout="miniState = true"
      :width="250"
      :mini-width="60"
    >
      <q-list>
        <!-- 首頁 -->
        <q-item clickable v-ripple :to="'/'" exact :active="route.path === '/'">
          <q-item-section avatar>
            <q-icon name="home" />
          </q-item-section>
          <q-item-section>首頁</q-item-section>
        </q-item>

        <!-- 訂貨作業 -->
        <q-item-label header v-if="!miniState && hasAnyRole(['SALES', 'LEADER', 'ADMIN'])">
          訂貨作業
        </q-item-label>

        <q-item
          v-if="hasAnyRole(['SALES', 'LEADER', 'ADMIN'])"
          clickable v-ripple
          :to="'/purchase-sales'"
          :active="route.path === '/purchase-sales'"
        >
          <q-item-section avatar>
            <q-icon name="shopping_cart" />
          </q-item-section>
          <q-item-section>業務員訂貨</q-item-section>
        </q-item>

        <q-item
          v-if="hasAnyRole(['LEADER', 'ADMIN'])"
          clickable v-ripple
          :to="'/purchase-branch'"
          :active="route.path === '/purchase-branch'"
        >
          <q-item-section avatar>
            <q-icon name="shopping_cart" />
          </q-item-section>
          <q-item-section>營業所訂貨彙總</q-item-section>
        </q-item>

        <q-separator spaced />

        <!-- Phase 2+ 預留（disabled） -->
        <q-item-label header v-if="!miniState">未來功能</q-item-label>

        <q-item disable v-if="hasAnyRole(['WAREHOUSE', 'ADMIN'])">
          <q-item-section avatar>
            <q-icon name="local_shipping" />
          </q-item-section>
          <q-item-section>收貨作業</q-item-section>
        </q-item>

        <q-item disable v-if="hasAnyRole(['WAREHOUSE', 'ADMIN'])">
          <q-item-section avatar>
            <q-icon name="inventory" />
          </q-item-section>
          <q-item-section>配貨作業</q-item-section>
        </q-item>

        <q-item disable v-if="hasAnyRole(['SALES', 'ADMIN'])">
          <q-item-section avatar>
            <q-icon name="delivery_dining" />
          </q-item-section>
          <q-item-section>送貨作業</q-item-section>
        </q-item>

        <q-item disable v-if="hasAnyRole(['SALES', 'ADMIN'])">
          <q-item-section avatar>
            <q-icon name="receipt_long" />
          </q-item-section>
          <q-item-section>結帳作業</q-item-section>
        </q-item>

        <!-- 系統管理 -->
        <template v-if="hasAnyRole(['ADMIN'])">
          <q-separator spaced />
          <q-item-label header v-if="!miniState">系統管理</q-item-label>

          <q-item disable>
            <q-item-section avatar>
              <q-icon name="people" />
            </q-item-section>
            <q-item-section>使用者管理</q-item-section>
          </q-item>

          <q-item disable>
            <q-item-section avatar>
              <q-icon name="admin_panel_settings" />
            </q-item-section>
            <q-item-section>角色管理</q-item-section>
          </q-item>
        </template>
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const route = useRoute()
const drawerOpen = ref(false)
const miniState = ref(true)

function hasAnyRole(roles) {
  return roles.some(role => authStore.hasRole(role))
}
</script>

<style>
</style>
