<template>
  <div class="search-page">
    <Navbar />

    <div class="container">
      <div class="search-header">
        <h2>搜索结果: "{{ keyword }}"</h2>
        <div class="tabs">
          <span :class="{ active: sortType === 'latest' }" @click="switchSort('latest')">最新</span>
          <span :class="{ active: sortType === 'hot' }" @click="switchSort('hot')">热门</span>
        </div>
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="articles.length === 0" class="empty">没有找到相关文章</div>
      <div v-else class="article-list">
        <div
          v-for="item in articles"
          :key="item.id"
          class="article"
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

      <!-- 分页控件（可选） -->
      <div class="pagination" v-if="total > pageSize">
        <button @click="changePage(current - 1)" :disabled="current === 1">上一页</button>
        <span>{{ current }} / {{ totalPages }}</span>
        <button @click="changePage(current + 1)" :disabled="current === totalPages">下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import Navbar from '../components/Navbar.vue'
import { searchArticlesByTime, searchArticlesByThumb } from '../api/article'

const route = useRoute()
const router = useRouter()

const keyword = ref('')
const articles = ref([])
const sortType = ref('latest') // latest 或 hot
const loading = ref(false)

// 分页相关
const current = ref(1)
const pageSize = ref(10)
const total = ref(0)
const totalPages = ref(0)

// 监听路由参数变化
watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword) {
    keyword.value = newKeyword
    current.value = 1
    loadSearch()
  }
}, { immediate: true })

// 切换排序
function switchSort(type) {
  sortType.value = type
  current.value = 1
  loadSearch()
}

// 加载搜索结果
async function loadSearch() {
  if (!keyword.value) return
  loading.value = true
  try {
    let res
    if (sortType.value === 'latest') {
      res = await searchArticlesByTime(keyword.value, current.value, pageSize.value)
    } else {
      res = await searchArticlesByThumb(keyword.value, current.value, pageSize.value)
    }
    // 假设返回格式 { code, data: { records, total, pages } }
    articles.value = res.data.records || []
    total.value = res.data.total || 0
    totalPages.value = res.data.pages || 0
  } catch (err) {
    console.error('搜索失败', err)
    articles.value = []
  } finally {
    loading.value = false
  }
}

// 分页跳转
function changePage(page) {
  current.value = page
  loadSearch()
}

// 跳转文章详情
function goDetail(id) {
  router.push(`/article/${id}`)
}
</script>

<style scoped>
.search-page {
  background: #f4f5f5;
  min-height: 100vh;
}
.container {
  width: 1200px;
  margin: 80px auto 20px;
}
.search-header {
  background: white;
  padding: 20px;
  margin-bottom: 20px;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.tabs span {
  margin-left: 20px;
  cursor: pointer;
  padding: 5px 10px;
}
.tabs span.active {
  color: #1e80ff;
  border-bottom: 2px solid #1e80ff;
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

.desc {
  color: #666;
  font-size: 14px;
}
.meta {
  font-size: 12px;
  color: #999;
}
.loading, .empty {
  text-align: center;
  padding: 100px;
  background: white;
  border-radius: 4px;
  color: #999;
}
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
}
.pagination button {
  padding: 5px 15px;
  background: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}
.pagination button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>