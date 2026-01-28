<template>
  <div class="fullscreen bg-grey-2 flex flex-center">
    <q-card class="login-card q-pa-lg" style="width: 400px; max-width: 90vw;">
      <q-card-section class="text-center q-pb-none">
        <div class="text-h5 text-weight-bold text-primary">Mock ODS VUE</div>
        <div class="text-subtitle2 text-grey-7 q-mt-sm">請登入以繼續</div>
      </q-card-section>

      <q-card-section>
        <q-form @submit.prevent="handleLogin" class="q-gutter-md">
          <q-input
            v-model="userId"
            label="使用者帳號"
            outlined
            :rules="[val => !!val || '請輸入帳號']"
          >
            <template #prepend>
              <q-icon name="person" />
            </template>
          </q-input>

          <q-input
            v-model="password"
            label="密碼"
            outlined
            :type="showPassword ? 'text' : 'password'"
            :rules="[val => !!val || '請輸入密碼']"
          >
            <template #prepend>
              <q-icon name="lock" />
            </template>
            <template #append>
              <q-icon
                :name="showPassword ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="showPassword = !showPassword"
              />
            </template>
          </q-input>

          <q-btn
            type="submit"
            label="登入"
            color="primary"
            class="full-width"
            size="lg"
            :loading="loading"
          />
        </q-form>
      </q-card-section>

      <q-banner v-if="errorMessage" class="bg-negative text-white q-mx-md q-mb-md" rounded>
        {{ errorMessage }}
      </q-banner>
    </q-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const userId = ref('')
const password = ref('')
const showPassword = ref(false)
const loading = ref(false)
const errorMessage = ref('')

onMounted(() => {
  if (authStore.isAuthenticated) {
    router.replace('/')
  }
})

async function handleLogin() {
  errorMessage.value = ''
  loading.value = true
  try {
    await authStore.login(userId.value, password.value)
    router.replace('/')
  } catch (e) {
    errorMessage.value = e.message || '登入失敗，請檢查帳號密碼'
  } finally {
    loading.value = false
  }
}
</script>
