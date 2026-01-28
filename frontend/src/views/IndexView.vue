<template>
  <q-page class="q-pa-md">
    <div class="text-h4 q-mb-md">歡迎，{{ authStore.user?.userName }}</div>

    <div class="row q-col-gutter-md">
      <!-- 用戶資訊卡片 -->
      <div class="col-12 col-md-6">
        <q-card>
          <q-card-section>
            <div class="text-h6">用戶資訊</div>
          </q-card-section>
          <q-card-section>
            <div class="q-mb-sm">
              <strong>帳號：</strong>{{ authStore.user?.userId }}
            </div>
            <div class="q-mb-sm">
              <strong>營業所代碼：</strong>{{ authStore.user?.branchCode }}
            </div>
            <div>
              <strong>角色：</strong>
              <q-badge
                v-for="role in authStore.user?.roles"
                :key="role"
                color="primary"
                class="q-mr-xs"
              >
                {{ role }}
              </q-badge>
            </div>
          </q-card-section>
        </q-card>
      </div>

      <!-- 快捷操作卡片 -->
      <div class="col-12 col-md-6">
        <q-card>
          <q-card-section>
            <div class="text-h6">快捷操作</div>
          </q-card-section>
          <q-card-section>
            <div class="q-gutter-sm">
              <q-btn
                v-if="hasAnyRole(['SALES', 'LEADER', 'ADMIN'])"
                color="primary"
                icon="shopping_cart"
                label="前往訂貨"
                to="/purchase-sales"
              />
              <q-btn
                v-if="hasAnyRole(['LEADER', 'ADMIN'])"
                color="secondary"
                icon="shopping_cart"
                label="前往訂貨彙總"
                to="/purchase-branch"
              />
              <q-btn
                v-if="hasAnyRole(['ADMIN'])"
                color="accent"
                icon="people"
                label="前往使用者管理"
                disable
              />
            </div>
          </q-card-section>
        </q-card>
      </div>
    </div>
  </q-page>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()

function hasAnyRole(roles) {
  return roles.some(role => authStore.hasRole(role))
}
</script>
