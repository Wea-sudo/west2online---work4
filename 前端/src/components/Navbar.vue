<template>
  <div class="navbar">
    <!-- 左侧 Logo -->
    <div class="logo" @click="goHome">
      <span class="logo-text">仿稀土掘金</span>
    </div>

    <!-- 中间菜单 -->
    <div class="menu">
      <router-link to="/" class="menu-item">首页</router-link>
      <span class="menu-item">沸点</span>
      <span class="menu-item">课程</span>
      <span class="menu-item">直播</span>
      <span class="menu-item">活动</span>
    </div>

    <!-- 搜索框（占据剩余空间） -->
    <div class="search">
      <input
        type="text"
        v-model="searchKeyword"
        placeholder="搜索文章..."
        @keyup.enter="handleSearch"
      />
      <button @click="handleSearch">🔍</button>
    </div>

    <!-- 右侧用户区域 -->
    <div class="user">
      <button class="publish" @click="goPublish">创作者中心</button>
      <template v-if="!globalState.token">
        <span class="login-btn" @click="openLogin('login')">登录</span>
        <span class="register-btn" @click="openLogin('register')">注册</span>
      </template>
      <template v-else>
        <img
          class="avatar"
          :src="globalState.user?.avatarUrl || defaultAvatar"
          @click="goProfile"
        />
      </template>
    </div>
  </div>

  <!-- 登录弹窗（保持不变） -->
  <LoginDialog
    v-if="globalState.showLogin"
    :mode="globalState.loginMode"
    @close="closeLogin"
    @login-success="handleLoginSuccess"
  />
</template>

<script setup>
import { ref, inject } from 'vue'
import { useRouter } from 'vue-router'
import LoginDialog from './LoginDialog.vue'

const globalState = inject('globalState')
const openLogin = inject('openLogin')
const closeLogin = inject('closeLogin')
const setUser = inject('setUser')
const searchKeyword = ref('')
const router = useRouter()

const defaultAvatar = '/avatar/default-avatar.png'

function goProfile() {
  router.push('/profile')
}

function goHome() {
  router.push('/')
}

function goPublish() {
  if (!globalState.token) {
    openLogin('login')
  } else {
    router.push('/publish')
  }
}


function handleSearch() {
  const keyword = searchKeyword.value.trim()
  if (keyword) {
    router.push({ path: '/search', query: { keyword } })
    searchKeyword.value = '' // 可选清空
  } else {
    alert('请输入搜索关键词')
  }
}
// 登录成功后更新用户信息（由 LoginDialog 触发）
function handleLoginSuccess(userData) {
  // userData 应包含 token 和用户基本信息
  // 实际由 LoginDialog 在登录成功后调用 setToken 和 setUser
  // 这里只需关闭弹窗
  closeLogin()
}
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 9999;
  height: 60px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px; /* 左右内边距可调 */
  border-bottom: 1px solid #eee;
  box-sizing: border-box;
}

/* Logo 区域 - 固定宽度或自动 */
.logo {
  font-weight: bold;
  font-size: 24px; /* 调小一点更协调 */
  color: #1e80ff;
  cursor: pointer;
  white-space: nowrap;
  margin-right: 20px; /* 与菜单保持间距 */
}
.logo-text {
  display: inline-block;
  /* 移除 transform: translateX(450px) */
}

/* 菜单区域 - 使用 flex 自动收缩 */
.menu {
  display: flex;
  align-items: center;
  gap: 20px; /* 菜单项间距 */
  margin-right: 20px;
}
.menu-item {
  cursor: pointer;
  color: #333;
  text-decoration: none;
  white-space: nowrap;
  font-size: 16px;
}
.router-link-active {
  color: #1e80ff;
  font-weight: 600;
}

/* 搜索框 - 占据剩余空间，设置最大宽度避免过宽 */
.search {
  display: flex;
  flex: 1;           /* 占满剩余空间 */
  max-width: 400px;   /* 可自行调整 */
  margin: 0 20px;
}
.search input {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px 0 0 4px;
  outline: none;
}
.search input:focus {
  border-color: #1e80ff;
}
.search button {
  padding: 8px 12px;
  background: #1e80ff;
  color: white;
  border: none;
  border-radius: 0 4px 4px 0;
  cursor: pointer;
}

/* 用户区域 - 固定宽度或自动 */
.user {
  display: flex;
  align-items: center;
  gap: 10px;
  white-space: nowrap;
  /* 如果需要固定宽度可以设置，但不设置也能自适应 */
}
.publish {
  background: #1e80ff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  white-space: nowrap;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
  cursor: pointer;
}
.login-btn, .register-btn {
  padding: 6px 14px;
  border-radius: 6px;
  background: #eaf3ff;
  color: #1e80ff;
  font-size: 14px;
  cursor: pointer;
}
.login-btn:hover, .register-btn:hover {
  background: #d6e8ff;
}
</style>