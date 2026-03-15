<template>
  <div class="page">
    <Navbar />

    <div class="container">
      <!-- 左侧主内容 -->
      <div class="main">
        <!-- 用户信息卡片 -->
        <div class="card user-card">
          <img class="avatar" :src="user.avatar || defaultAvatar" />
          <div class="info">
            <h2 class="username">
              {{ user.name || '用户' + user.id }}
              <span class="level">JY.1</span>
            </h2>
            <div class="bio">
              <p>+ 你从事什么职业？</p>
              <p>+ 你的人生格言是什么？</p>
            </div>
          </div>
          <button v-if="isSelf" class="settings" @click="showSettings = true">设置</button>
        </div>

        <!-- 徽章 -->
        <div class="card badge-card">
          🎃 获得徽章 {{ badgeCount }}
          <span class="arrow">›</span>
        </div>

        <!-- 一级 Tab -->
        <div class="card tabs">
          <span
            v-for="tab in tabs"
            :key="tab"
            :class="['tab', { active: tab === activeTab }]"
            @click="switchTab(tab)"
          >
            {{ tab }}
          </span>
          <span class="search">🔍</span>
        </div>

        <!-- 二级 Tab（仅当一级为“文章”时显示） -->
        <div v-if="activeTab === '文章'" class="subtabs">
          <span
            v-for="sub in subTabs"
            :key="sub"
            :class="['subtab', { active: sub === activeSubTab }]"
            @click="switchSubTab(sub)"
          >
            {{ sub }}
          </span>
        </div>

        <!-- 内容区域 -->
        <div class="card content">
          <!-- 当 Tab 为“文章”时，显示文章列表 -->
          <div v-if="activeTab === '文章'">
            <div v-if="userArticles.length === 0" class="empty">
              <img src="https://picsum.photos/80/80" />
              <p>暂无文章</p>
            </div>
            <div v-else>
              <div
                v-for="article in userArticles"
                :key="article.id"
                class="article-item"
                @click="goArticleDetail(article.id)"
              >
                <ArticleCard :article="article" />
              </div>
            </div>
          </div>
          <!-- 其他 Tab 显示空状态（可后续扩展） -->
          <div v-else-if="!hasContent" class="empty">
            <img src="https://picsum.photos/80/80" />
            <p>这里什么都没有</p>
          </div>
        </div>
      </div>

      <!-- 右侧侧边栏 -->
      <div class="sidebar">
        <div class="card stats">
          <div class="stat">
            <p>关注了</p>
            <b>{{ following }}</b>
          </div>
          <div class="stat">
            <p>关注者</p>
            <b>{{ followers }}</b>
          </div>
        </div>

        <div class="card item">
          收藏集
          <span>{{ collections }}</span>
        </div>

        <div class="card item">
          关注标签
          <span>{{ tags }}</span>
        </div>

        <div class="card item">
          加入于
          <span>{{ joinDate }}</span>
        </div>
      </div>
    </div>

    <!-- 登录弹窗 -->
    <LoginDialog
      v-if="globalState.showLogin"
      :mode="globalState.loginMode"
      @close="closeLogin"
      @login-success="handleLoginSuccess"
    />

    <!-- 设置弹窗 -->
    <SettingsDialog
      v-if="showSettings"
      @close="showSettings = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, inject, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import LoginDialog from '../components/LoginDialog.vue'
import request from '../api/request'
import SettingsDialog from '../components/SettingsDialog.vue'
import ArticleCard from '../components/ArticleCard.vue'
import {
  getArticlesByAuthorByTime,
  getArticlesByAuthorByThumb
} from '../api/article'

const globalState = inject('globalState')
const setUser = inject('setUser')
const openLogin = inject('openLogin')
const closeLogin = inject('closeLogin')
const showSettings = ref(false)

const route = useRoute()
const router = useRouter()

// 用户基本信息
const user = ref({
  id: null,
  name: '',
  avatar: '',
})

// 动态统计数据
const following = ref(0)
const followers = ref(0)
const collections = ref(0)
const tags = ref(18)
const joinDate = ref('')
const badgeCount = ref(0)

// 用户文章列表
const userArticles = ref([])

// UI 状态
const tabs = ref(['动态', '文章', '专栏', '沸点', '收藏集', '关注', '作品', '赞 0'])
const activeTab = ref('文章')
const subTabs = ref(['最新', '热门'])
const activeSubTab = ref('最新')
const hasContent = ref(false)

const defaultAvatar = '/avatar/default-avatar.png'

// 当前查看的用户ID（用于加载文章）
const currentViewUserId = ref(null)

// 计算属性：当前查看的是否为本人主页
const isSelf = computed(() => {
  return globalState.user?.id === user.value.id
})

// 加载用户文章
async function loadUserArticles() {
  if (!currentViewUserId.value) return
  try {
    let res
    if (activeSubTab.value === '最新') {
      res = await getArticlesByAuthorByTime(currentViewUserId.value, 1, 10)
    } else {
      res = await getArticlesByAuthorByThumb(currentViewUserId.value, 1, 10)
    }
    // 假设接口返回格式为 { code, data: { records } }
    userArticles.value = res.data.records || []
  } catch (err) {
    console.error('加载用户文章失败', err)
    userArticles.value = []
  }
}

// 获取完整用户资料（包括统计数据）
async function fetchFullUser(targetUserId) {
  try {
    const res = await request.get(`/users/${targetUserId}`)
    const data = res.data // 返回 { id, name, avatarUrl, followingCount, followerCount, collectionCount, joinDate, badgeCount }

    user.value = {
      id: data.id,
      name: data.name,
      avatar: data.avatarUrl,
    }

    following.value = data.followingCount || 0
    followers.value = data.followerCount || 0
    collections.value = data.collectionCount || 0
    joinDate.value = data.joinDate || '未知'
    badgeCount.value = data.badgeCount || 0

    // 设置当前查看的用户 ID
    currentViewUserId.value = data.id

    // 如果当前 Tab 是“文章”，则加载文章列表
    if (activeTab.value === '文章') {
      loadUserArticles()
    }

    // 如果是自己，更新全局状态
    if (globalState.user?.id === data.id) {
      setUser({ ...globalState.user, ...data })
    }
  } catch (err) {
    console.error('获取用户信息失败', err)
    // 如果是 token 过期，拦截器已弹框，这里可以重置页面或跳转
    if (err.message?.includes('Token已过期') || err.message?.includes('未登录')) {
      // 可选：跳回首页
      router.push('/')
    }
  }
}

// 处理路由加载
function loadUserFromRoute() {
  const targetUserId = route.params.userId

  if (targetUserId) {
    // 查看其他用户：清空数据，从 API 获取
    user.value = { id: null, name: '', avatar: '' }
    currentViewUserId.value = null
    userArticles.value = []
    fetchFullUser(targetUserId)
  } else {
    // 查看自己
    if (!globalState.token) {
      openLogin('login')
      return
    }

    if (globalState.user?.id) {
      // 优先使用全局状态的用户信息作为初始值
      user.value = {
        id: globalState.user.id,
        name: globalState.user.name,
        avatar: globalState.user.avatarUrl,
      }
      currentViewUserId.value = globalState.user.id

      // 立即加载文章（如果当前 Tab 是“文章”）
      if (activeTab.value === '文章') {
        loadUserArticles()
      }

      // 异步获取完整资料（更新统计数据）
      fetchFullUser(globalState.user.id)
    } else {
      // 有 token 但 user 为空（异常情况），尝试重新登录
      openLogin('login')
    }
  }
}

// 登录成功回调
function handleLoginSuccess() {
  loadUserFromRoute()
}

// Tab 切换
function switchTab(tab) {
  activeTab.value = tab
  if (tab === '文章' && currentViewUserId.value) {
    loadUserArticles()
  } else {
    userArticles.value = [] // 清空非文章 Tab 的内容
  }
}

// 二级 Tab 切换（最新/热门）
function switchSubTab(sub) {
  activeSubTab.value = sub
  if (activeTab.value === '文章' && currentViewUserId.value) {
    loadUserArticles()
  }
}

// 跳转文章详情
function goArticleDetail(id) {
  router.push(`/article/${id}`)
}

// 监听路由参数变化
watch(() => route.params.userId, (newId, oldId) => {
  if (newId !== oldId) {
    userArticles.value = []
    loadUserFromRoute()
  }
})

onMounted(() => {
  loadUserFromRoute()
})

</script>

<style scoped>
/* 原有样式保留 */
.page{
  background:#f5f5f5;
  min-height:100vh;
}
.container{
  width:1200px;
  margin:20px auto;
  display:flex;
  gap:20px;
}
.main{
  flex:2;
}
.sidebar{
  flex:1;
}
.card{
  background:white;
  padding:20px;
  margin-bottom:15px;
  border-radius:6px;
}
.user-card{
  display:flex;
  align-items:center;
}
.avatar{
  width:80px;
  height:80px;
  border-radius:50%;
  margin-right:20px;
}
.username{
  font-size:22px;
  font-weight:bold;
}
.level{
  background:#eaf3ff;
  color:#1e80ff;
  padding:2px 8px;
  margin-left:10px;
  border-radius:4px;
  font-size:12px;
}
.bio{
  color:#666;
  font-size:14px;
  margin-top:6px;
}
.settings{
  margin-left:auto;
  border:1px solid #1e80ff;
  background:white;
  color:#1e80ff;
  padding:6px 16px;
  border-radius:4px;
  cursor:pointer;
}
.badge-card{
  display:flex;
  align-items:center;
  cursor:pointer;
}
.arrow{
  margin-left:auto;
}
.tabs{
  display:flex;
  align-items:center;
}
.tab{
  margin-right:20px;
  cursor:pointer;
  color:#666;
}
.tab.active{
  color:#1e80ff;
  border-bottom:2px solid #1e80ff;
  padding-bottom:6px;
}
.search{
  margin-left:auto;
  cursor:pointer;
}
.subtabs{
  margin-bottom:15px;
}
.subtab{
  background:#f2f3f5;
  padding:6px 12px;
  margin-right:10px;
  border-radius:4px;
  cursor:pointer;
}
.subtab.active{
  background:#1e80ff;
  color:white;
}
.empty{
  text-align:center;
  padding:60px 0;
  color:#999;
}
.stats{
  display:flex;
  justify-content:space-around;
}
.stat p{
  color:#666;
}
.item{
  display:flex;
  justify-content:space-between;
}
.loading {
  text-align: center;
  padding: 100px;
  color: #999;
}
/* 新增文章列表项的样式 */
.article-item {
  cursor: pointer;
  transition: all 0.2s;
}
.article-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
</style>