<template>
  <div>
    <Navbar />
    <div class="page">
      <!-- 左侧操作栏：仅在文章加载后显示 -->
      <div class="left-bar" v-if="article.id">
        <div class="icon-btn like" :class="{ active: liked }" @click="toggleLike">
          <Pointer />
          <span>{{ thumbCount }}</span>
        </div>
        <div class="icon-btn collect" :class="{ active: collected }" @click="toggleCollect">
          <Star />
          <span>{{ collectCount }}</span>
        </div>
        <div class="icon-btn comment" @click="toggleCommentsDrawer">
          <ChatDotRound />
          <span>{{ totalComments }}</span>
        </div>
      </div>

      <!-- 中间文章：加载状态保护 -->
      <div class="article" v-if="article.id">
        <h1 class="title">{{ article.title }}</h1>
        <div class="meta">
          作者 {{ article.authorId }} · 阅读 {{ article.viewCount }}
        </div>
        <div class="content" v-html="htmlContent"></div>
      </div>
      <div v-else class="article loading">
        加载中...
      </div>

      <!-- 右侧作者卡片 -->
      <div class="right" v-if="article.id">
        <div class="author-card">
          <h3>作者</h3>
          <p>{{ article.authorId }}</p>
        </div>
      </div>
    </div>

    <!-- 评论抽屉：仅当有 articleId 时渲染 -->
    <CommentsDrawer
      v-if="article.id"
      v-model:visible="showComments"
      :article-id="article.id"
      @update:total="totalComments = $event"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRoute } from "vue-router"
import { marked } from "marked"
import hljs from "highlight.js"
import "highlight.js/styles/github.css"
import Navbar from "../components/Navbar.vue"
import CommentsDrawer from "../components/CommentsDrawer.vue"
import { Star, Pointer, ChatDotRound } from "@element-plus/icons-vue"
import {
  likeArticle,
  unlikeArticle,
  collectArticle,
  uncollectArticle
} from "../api/article"
import { inject } from "vue"
import request from "../api/request"  // 添加这行导入

const route = useRoute()
const globalState = inject('globalState')
const openLogin = inject('openLogin')

const article = ref({})
const htmlContent = ref("")

// 点赞相关
const liked = ref(false)
const thumbCount = ref(0)

// 收藏相关
const collected = ref(false)
const collectCount = ref(0)

// 评论相关
const showComments = ref(false)
const totalComments = ref(0)

async function toggleLike() {
  if (!globalState.token) {
    openLogin('login');
    return;
  }
  const originalLiked = liked.value;
  const originalCount = thumbCount.value;
  try {
    if (liked.value) {
      await unlikeArticle(article.value.id);
      thumbCount.value -= 1;
      liked.value = false;
    } else {
      await likeArticle(article.value.id);
      thumbCount.value += 1;
      liked.value = true;
    }
  } catch (err) {
  liked.value = originalLiked;
  thumbCount.value = originalCount;
  // 如果是 token 过期，拦截器已弹框，这里不再重复 alert
  if (err.message?.includes('Token已过期') || err.message?.includes('未登录')) {
    return
  }
  const msg = err.response?.data?.message || err.message;
  alert(msg || "操作失败");
}
}

async function toggleCollect() {
  if (!globalState.token) {
    openLogin('login');
    return;
  }
  try {
    if (collected.value) {
      await uncollectArticle(article.value.id)
      collectCount.value -= 1
      collected.value = false
    } else {
      await collectArticle(article.value.id)
      collectCount.value += 1
      collected.value = true
    }
  } catch (err) {
    const msg = err.response?.data?.message || err.message
    alert(msg || "操作失败")
  }
}

function toggleCommentsDrawer() {
  showComments.value = !showComments.value
}

marked.setOptions({
  highlight(code) {
    return hljs.highlightAuto(code).value
  }
})

onMounted(async () => {
  const id = route.params.id
  try {
    const res = await request.get("/articles/" + id)  // 使用 request
    article.value = res.data

    liked.value = article.value.thumbed || false
    collected.value = article.value.collected || false
    thumbCount.value = article.value.thumbCount || 0
    collectCount.value = article.value.collectCount || 0
    totalComments.value = article.value.commentCount || 0

    htmlContent.value = marked.parse(article.value.content || "")
  } catch (err) {
    console.error('加载文章失败', err)
  }
})
</script>

<style scoped>

.like.active {
  color: #1e80ff;
}
.collect.active {
  color: #ff9800;
}

.page{
display:flex;
width:1200px;
margin:auto;
margin-top:20px;
}

/* 左侧按钮 */

.left-bar{
position:fixed;
left:120px;
top:200px;
display:flex;
flex-direction:column;
gap:20px;
}

/* 按钮 */

.icon-btn{
width:48px;
height:48px;
background:white;
border-radius:50%;
display:flex;
flex-direction:column;
align-items:center;
justify-content:center;
cursor:pointer;
box-shadow:0 2px 8px rgba(0,0,0,0.08);
transition:all .2s;
font-size:20px;
}

.icon-btn span{
font-size:12px;
margin-top:3px;
color:#666;
}

/* hover效果 */

.icon-btn:hover{
transform:scale(1.1);
box-shadow:0 4px 12px rgba(0,0,0,0.15);
}

.like:hover{
color:#1e80ff;
}

.collect:hover{
color:#ff9800;
}

.comment:hover{
color:#67c23a;
}

/* 中间文章 */

.article{
flex:1;
background:white;
padding:30px;
margin:0 20px;
}

.title{
font-size:30px;
margin-bottom:10px;
}

.meta{
color:#888;
margin-bottom:30px;
}

/* markdown */

.content{
line-height:1.8;
}

.content h1{
font-size:28px;
margin:20px 0;
}

.content h2{
font-size:22px;
margin:20px 0;
}

.content p{
margin:10px 0;
}

/* 右侧 */

.right{
width:260px;
}

.author-card{
background:white;
padding:20px;
}

</style>