<template>
  <div class="drawer-mask" v-if="visible" @click.self="close">
    <div class="drawer">
      <div class="drawer-header">
        <h3>评论 ({{ totalComments }})</h3>
        <span class="close-btn" @click="close">✕</span>
      </div>

      <!-- 评论输入框 -->
      <div class="comment-input-wrapper">
        <textarea
          v-model="commentContent"
          :placeholder="replyToId ? '回复评论...' : '写下你的评论...'"
          rows="3"
        ></textarea>
        <div class="comment-actions">
          <span v-if="replyToId" class="cancel-reply" @click="cancelReply">取消回复</span>
          <button @click="submitComment" :disabled="!commentContent.trim()">发表评论</button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comments-list" v-if="commentsTree.length">
        <div v-for="comment in commentsTree" :key="comment.id" class="comment-item level-1">
          <div class="comment-main">
            <img class="avatar" :src="defaultAvatar" />
            <div class="comment-content">
              <div class="comment-header">
                <span class="author">{{ comment.userId }}</span>
                <span class="time">{{ formatTime(comment.createTime) }}</span>
              </div>
              <div class="text">{{ comment.content }}</div>
              <div class="comment-footer">
                <span class="reply-btn" @click="setReplyTo(comment.id)">回复</span>
              </div>
            </div>
          </div>
          <!-- 二级评论 -->
          <div v-if="comment.children && comment.children.length" class="replies">
            <div v-for="reply in comment.children" :key="reply.id" class="comment-item level-2">
              <div class="comment-main">
                <img class="avatar" :src="defaultAvatar" />
                <div class="comment-content">
                  <div class="comment-header">
                    <span class="author">{{ reply.userId }}</span>
                    <span class="time">{{ formatTime(reply.createTime) }}</span>
                  </div>
                  <div class="text">{{ reply.content }}</div>
                  <div class="comment-footer">
                    <span class="reply-btn" @click="setReplyTo(reply.id)">回复</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="no-comments">暂无评论，快来抢沙发吧~</div>

      <!-- 分页 -->
      <div class="pagination" v-if="totalPages > 1">
        <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">上一页</button>
        <span>{{ currentPage }} / {{ totalPages }}</span>
        <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">下一页</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { createComment, getArticleComments } from '../api/article'
import { inject } from 'vue'

const props = defineProps({
  articleId: {
    type: String,
    required: true
  },
  visible: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['update:visible', 'update:total'])

const globalState = inject('globalState')
const openLogin = inject('openLogin')
const defaultAvatar = '/avatar/default-avatar.png'

// 评论数据
const commentContent = ref('')
const replyToId = ref(null)
const commentsTree = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalPages = ref(1)
const totalComments = ref(0)

// 监听 visible 变化，打开时加载数据
watch(() => props.visible, (newVal) => {
  if (newVal) {
    if (commentsTree.value.length === 0) {
      loadComments()
    } else {
      // 即使有缓存，也把当前总数发送给父组件，确保按钮上的数字正确
      emit('update:total', totalComments.value)
    }
  }
})

// 加载评论
async function loadComments() {
  try {
    const res = await getArticleComments(props.articleId, currentPage.value, pageSize.value)
    const records = res.data.records || []
    totalComments.value = res.data.total || 0
    totalPages.value = res.data.pages || 1
    // 通知父组件更新总数
    emit('update:total', totalComments.value)

    // 构建两级评论树
    const commentMap = new Map()
    records.forEach(comment => {
      comment.children = []
      commentMap.set(comment.id, comment)
    })
    const roots = []
    records.forEach(comment => {
      if (comment.parentId) {
        const parent = commentMap.get(comment.parentId)
        if (parent) {
          parent.children.push(comment)
        } else {
          roots.push(comment)
        }
      } else {
        roots.push(comment)
      }
    })
    commentsTree.value = roots
  } catch (err) {
    console.error('加载评论失败', err)
  }
}

// 提交评论
async function submitComment() {
  if (!commentContent.value.trim()) return
  if (!globalState.token) {
    openLogin('login')
    return
  }
  try {
    await createComment({
      articleId: props.articleId,
      content: commentContent.value,
      parentId: replyToId.value
    })
    commentContent.value = ''
    replyToId.value = null
    currentPage.value = 1
    await loadComments()
  } catch (err) {
    alert(err.response?.data?.message || err.message || '评论失败')
  }
}

// 回复
function setReplyTo(commentId) {
  replyToId.value = commentId
}

function cancelReply() {
  replyToId.value = null
  commentContent.value = ''
}

// 分页
function changePage(page) {
  currentPage.value = page
  loadComments()
}

// 关闭抽屉
function close() {
  emit('update:visible', false)
}

// 格式化时间
function formatTime(timestamp) {
  const date = new Date(timestamp)
  return `${date.getFullYear()}-${date.getMonth()+1}-${date.getDate()}`
}
</script>

<style scoped>
.drawer-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.4);
  z-index: 10000;
  display: flex;
  justify-content: flex-end;
}
.drawer {
  width: 400px;
  height: 100%;
  background-color: white;
  box-shadow: -2px 0 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideIn 0.3s ease;
}
@keyframes slideIn {
  from { transform: translateX(100%); }
  to { transform: translateX(0); }
}
.drawer-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #eee;
}
.drawer-header h3 {
  margin: 0;
  font-size: 18px;
}
.close-btn {
  font-size: 20px;
  cursor: pointer;
  color: #999;
}
.close-btn:hover {
  color: #333;
}
.comment-input-wrapper {
  padding: 20px;
  border-bottom: 1px solid #f0f0f0;
}
.comment-input-wrapper textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  font-size: 14px;
  box-sizing: border-box;
}
.comment-input-wrapper textarea:focus {
  border-color: #1e80ff;
  outline: none;
}
.comment-actions {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-top: 10px;
  gap: 10px;
}
.comment-actions button {
  padding: 8px 20px;
  background: #1e80ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.comment-actions button:disabled {
  background: #ccc;
  cursor: not-allowed;
}
.cancel-reply {
  color: #999;
  cursor: pointer;
  font-size: 14px;
}
.cancel-reply:hover {
  color: #666;
}
.comments-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}
.comment-item {
  margin-bottom: 20px;
}
.comment-item.level-2 {
  margin-left: 40px;
  margin-top: 10px;
  padding-left: 15px;
  border-left: 2px solid #f0f0f0;
}
.comment-main {
  display: flex;
  gap: 12px;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}
.comment-content {
  flex: 1;
}
.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 5px;
}
.author {
  font-weight: 600;
  font-size: 14px;
}
.time {
  color: #999;
  font-size: 12px;
}
.text {
  font-size: 14px;
  line-height: 1.6;
  color: #333;
}
.comment-footer {
  margin-top: 8px;
}
.reply-btn {
  color: #999;
  font-size: 12px;
  cursor: pointer;
}
.reply-btn:hover {
  color: #1e80ff;
}
.replies {
  margin-top: 15px;
}
.no-comments {
  text-align: center;
  color: #999;
  padding: 40px 0;
}
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  padding: 20px;
  border-top: 1px solid #f0f0f0;
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