import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import VueKeycloak from '@dsb-norge/vue-keycloak-js'

import App from './App.vue'
import { createAppRouter } from '@/router'

const app = createApp(App)

console.log(import.meta.env.VITE_KEYCLOAK_BASE_URL)

app.use(VueKeycloak, {
  config: {
    url: import.meta.env.VITE_KEYCLOAK_BASE_URL,
    realm: import.meta.env.VITE_KEYCLOAK_REALM,
    clientId: import.meta.env.VITE_KEYCLOAK_CLIENT_ID,
  },
  init: {
    onLoad: 'login-required',
    checkLoginIframe: false,
  },
  onReady: () => {
    app.use(createPinia())
    app.use(createAppRouter())
    app.mount('#app')
  },
})
