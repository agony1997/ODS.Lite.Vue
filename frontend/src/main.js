import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { Quasar, Notify, Dialog } from 'quasar'
import router from './router'
import App from './App.vue'
import { useAuthStore } from './stores/auth'

import '@quasar/extras/material-icons/material-icons.css'
import 'quasar/src/css/index.sass'

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(Quasar, {
  plugins: { Notify, Dialog },
  config: {
    notify: {
      position: 'top',
      timeout: 3000,
      actions: [{ icon: 'close', color: 'white' }]
    }
  }
})
app.use(router)

const authStore = useAuthStore()
authStore.initFromStorage()

app.config.errorHandler = (err, instance, info) => {
  console.error('[Vue Error]', info, err)
  Notify.create({ type: 'negative', message: err?.message || '發生未預期的錯誤' })
}

window.addEventListener('unhandledrejection', (event) => {
  console.error('[Unhandled Promise]', event.reason)
  if (event.reason?.message === '未授權，請重新登入') return
  Notify.create({ type: 'negative', message: event.reason?.message || '發生未預期的錯誤' })
})

app.mount('#app')
