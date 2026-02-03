import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { login as loginApi } from '@/api/auth'
import router from '@/router'

const TOKEN_KEY = 'mock_ods_token'
const USER_KEY = 'mock_ods_user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(null)
  const user = ref(null)

  const isAuthenticated = computed(() => !!token.value)

  function initFromStorage() {
    const savedToken = localStorage.getItem(TOKEN_KEY)
    const savedUser = localStorage.getItem(USER_KEY)
    if (savedToken && savedUser) {
      token.value = savedToken
      user.value = JSON.parse(savedUser)
    }
  }

  async function login(userCode, password) {
    const data = await loginApi(userCode, password)
    token.value = data.token
    user.value = {
      userCode: data.userCode,
      userName: data.userName,
      branchCode: data.branchCode,
      roles: data.roles
    }
    localStorage.setItem(TOKEN_KEY, data.token)
    localStorage.setItem(USER_KEY, JSON.stringify(user.value))
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    router.push('/login')
  }

  function hasRole(role) {
    return user.value?.roles?.includes(role) ?? false
  }

  return { token, user, isAuthenticated, initFromStorage, login, logout, hasRole }
})
