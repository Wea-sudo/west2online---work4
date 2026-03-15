<template>
  <div class="card">
    <h3>{{ article.title }}</h3>
    <p>{{ article.description }}</p>
    <div class="actions">
      <button @click="like" :disabled="liking">👍点赞 {{ article.thumbCount || 0 }}</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { likeArticle } from "../api/article"

const props = defineProps({
  article: Object
})

const liking = ref(false)

const globalState = inject('globalState')
const openLogin = inject('openLogin')
const like = async () => {
    if (!globalState.token) {
    openLogin('login')
    return
  }
  if (liking.value) return
  liking.value = true
  try {
    await likeArticle(props.article.id)
    // 乐观更新：本地增加点赞数（可选）
    props.article.thumbCount = (props.article.thumbCount || 0) + 1
    alert('点赞成功')
  } catch (err) {
    alert(err.response?.data?.message || '点赞失败')
  } finally {
    liking.value = false
  }
}
</script>

<style>
.card {
  border: 1px solid #eee;
  padding: 20px;
  margin-bottom: 15px;
  border-radius: 6px;
}
.actions button {
  background: #1e80ff;
  color: white;
  border: none;
  padding: 5px 15px;
  border-radius: 4px;
  cursor: pointer;
}
.actions button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>

