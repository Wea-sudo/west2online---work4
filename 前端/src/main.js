// main.js
import { createApp, reactive, provide } from 'vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)

// 全局响应式状态
const globalState = reactive({
  token: localStorage.getItem('token') || '',
  user: JSON.parse(localStorage.getItem('user') || 'null'),  // 新增
  showLogin: false,
  loginMode: 'login'
})

// 提供全局方法
function setToken(token) {
  globalState.token = token
  if (token) {
    localStorage.setItem('token', token)
  } else {
    localStorage.removeItem('token')
    globalState.user = null
  }
}

function setUser(user) {
  globalState.user = user
  if (user) {
    localStorage.setItem('user', JSON.stringify(user))
  } else {
    localStorage.removeItem('user')
  }
}

function openLogin(mode = 'login') {
  globalState.loginMode = mode
  globalState.showLogin = true
}

function closeLogin() {
  globalState.showLogin = false
}

app.provide('globalState', globalState)
app.provide('setToken', setToken)
app.provide('setUser', setUser)
app.provide('openLogin', openLogin)
app.provide('closeLogin', closeLogin)

app.use(router).mount('#app')

// main.js 末尾
window.openLogin = openLogin