<template>
  <div class="container">
    <Navbar />

    <div class="main">
      <!-- 左侧分类（保持不变） -->
      <div class="left">
        <div class="menu-item">综合</div>
        <div class="menu-item">后端</div>
        <div class="menu-item">前端</div>
        <div class="menu-item">Android</div>
        <div class="menu-item">iOS</div>
        <div class="menu-item">人工智能</div>
        <div class="menu-item">开发工具</div>
      </div>

      <!-- 中间文章 -->
      <div class="center">
        <div class="tabs">
          <span :class="{ active: sortType === 'recommend' }" @click="switchSort('recommend')">推荐</span>
          <span :class="{ active: sortType === 'latest' }" @click="switchSort('latest')">最新</span>
        </div>

        <div
          class="article"
          v-for="item in articles"
          :key="item.id"
          @click="goDetail(item.id)"
        >
          <div class="content">
            <h3>{{ item.title }}</h3>
            <p class="desc">{{ item.description }}</p>
            <div class="meta">
              {{ item.authorId }} · {{ item.viewCount }} 阅读 · {{ item.thumbCount }} 点赞
            </div>
          </div>
         
          
        </div>
      </div>

      <!-- 右侧保持不变 -->
      <div class="right">
        <div class="card">
          <h3>文章榜</h3>
          <ol>
            <li v-for="rank in ranks" :key="rank">{{ rank }}</li>
          </ol>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import Navbar from "../components/Navbar.vue"
import { getArticlesByTime, getArticlesByThumb } from "../api/article"

const router = useRouter()
const articles = ref([])
const sortType = ref('recommend') // 'recommend' 或 'latest'

const ranks = ref([
  "SpringBoot项目实战",
  "Vue3开发指南",
  "MyBatisPlus最佳实践",
  "Java面试题合集",
  "Redis使用详解"
])

function goDetail(id) {
  router.push("/article/" + id)
}

async function loadArticles() {
  try {
    let res
    if (sortType.value === 'latest') {
      res = await getArticlesByTime(1, 10)
    } else {
      // 推荐目前先用点赞数排序（可根据需求调整）
      res = await getArticlesByThumb(1, 10)
    }
    // 假设返回格式 { code, data: { records } }
    articles.value = res.data.records
  } catch (err) {
    console.error('加载文章失败', err)
  }
}

function switchSort(type) {
  sortType.value = type
  loadArticles()
}

onMounted(() => {
  loadArticles()
})
</script>




<style scoped>

.tabs span.active {
  color: #1e80ff;
  font-weight: bold;
}

.container{
background:#f4f5f5;
min-height:100vh;
}



.logo{
font-weight:bold;
font-size:20px;
color:#1e80ff;
}

.menu span{
margin:0 15px;
cursor:pointer;
}

.user{
display:flex;
align-items:center;
}

.publish{
background:#1e80ff;
color:white;
border:none;
padding:8px 16px;
border-radius:4px;
margin-right:20px;
}

.avatar{
width:36px;
height:36px;
border-radius:50%;
}

/* 主体 */

.main{
width:1200px;
margin:auto;
display:flex;
margin-top:20px;
}

/* 左侧 */

.left{
width:200px;
}

.menu-item{
padding:10px;
cursor:pointer;
}

.menu-item:hover{
background:#eaf2ff;
}

/* 中间 */

.center{
flex:1;
margin:0 20px;
}

.tabs{
background:white;
padding:10px;
margin-bottom:10px;
}

.tabs span{
margin-right:20px;
cursor:pointer;
}

.tabs .active{
color:#1e80ff;
}

.article {
  background: white;
  padding: 15px;
  margin-bottom: 10px;
  border-radius: 4px;
  cursor: pointer;
}
.article:hover {
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
/* 删除 .cover 样式 */



.desc{
color:#666;
font-size:14px;
}

.meta{
font-size:12px;
color:#999;
}

/* 右侧 */

.right{
width:260px;
}

.card{
background:white;
padding:15px;
border-radius:4px;
}

.card li{
margin:10px 0;
}

</style>