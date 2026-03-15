import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Publish from '../views/Publish.vue'
import ArticleDetail from '../views/ArticleDetail.vue'
import Profile from '../views/Profile.vue'
import Search from '../views/Search.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: Home
    },
    {
      path: '/publish',
      component: Publish,
      meta: { requiresAuth: true }  // 需要登录
    },
    {
      path: '/article/:id',
      component: ArticleDetail
    },
    {
      path: '/profile',
      component: Profile,
      meta: { requiresAuth: true }
    },
    {
      path: '/search',
      component: Search
    }
  ]
})

// 全局路由守卫
router.beforeEach((to, from) => {
  if (to.meta.requiresAuth && !localStorage.getItem('token')) {
    // 触发登录弹窗（异步操作，不影响导航返回值）
    if (window.openLogin) window.openLogin('login')
    // 返回 false 表示取消当前导航
    return false
  }
  // 不返回任何值（或返回 true）表示允许导航
  // 也可以返回 { path: '/login' } 来重定向
  return true
})

export default router