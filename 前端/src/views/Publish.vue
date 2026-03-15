<template>
  <div class="publish-page">
    <Navbar />

    <div class="publish-container">
      <div class="editor-card">
        <!-- 标题输入 -->
        <input
          v-model="title"
          class="title-input"
          placeholder="输入文章标题..."
        />

        <!-- 工具栏（模拟富文本） -->
        <div class="toolbar">
          <span class="tool-btn">B</span>
          <span class="tool-btn">I</span>
          <span class="tool-btn">U</span>
          <span class="tool-btn">H<sub>1</sub></span>
          <span class="tool-btn">H<sub>2</sub></span>
          <span class="tool-btn">“</span>
          <span class="tool-btn">【】</span>
          <span class="tool-btn">《》</span>
          <span class="tool-btn">&lt;/&gt;</span>
          <span class="tool-btn">{ }</span>
          <span class="tool-btn">=</span>
          <span class="tool-btn">三</span>
          <span class="tool-btn">品</span>
        </div>

        <!-- 正文编辑区（文本域） -->
        <textarea
          v-model="content"
          class="content-input"
          placeholder="写点什么..."
        ></textarea>

        <!-- 底部操作栏 -->
        <div class="footer">
          <span class="auto-save">⏺ 文章自动保存至草稿箱</span>
          <div class="actions">
            <button class="draft-btn" @click="saveDraft">草稿箱</button>
            <button class="publish-btn" @click="publish">发布</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue"
import { createArticle } from "../api/article"
import Navbar from "../components/Navbar.vue"

const title = ref("")
const content = ref("")
// 简介字段可根据需要保留，此处暂时隐藏，如需使用可取消注释

const publish = async () => {
  // 发布时调用接口（假设后端不需要简介字段，若需要可添加）
  await createArticle({
    title: title.value,
    content: content.value
  })
  alert("发布成功")
}

const saveDraft = () => {
  // 保存草稿逻辑（需后端支持）
  alert("已保存至草稿箱")
}
</script>

<style scoped>
.publish-page {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.publish-container {
  max-width: 900px;
  margin: 80px auto 40px;
  padding: 0 20px;
}

.editor-card {
  background: white;
  border-radius: 12px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
  padding: 30px;
}

.title-input {
  width: 100%;
  font-size: 28px;
  font-weight: 600;
  border: none;
  outline: none;
  padding: 10px 0;
  margin-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
  transition: border-color 0.2s;
}

.title-input:focus {
  border-bottom-color: #1e80ff;
}

.title-input::placeholder {
  color: #ccc;
  font-weight: 400;
}

.toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 15px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 20px;
}

.tool-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 36px;
  height: 36px;
  border-radius: 6px;
  background: #f8f9fa;
  color: #333;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.tool-btn:hover {
  background: #e9ecef;
  border-color: #ddd;
}

.content-input {
  width: 100%;
  min-height: 300px;
  padding: 15px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  font-size: 16px;
  line-height: 1.8;
  resize: vertical;
  outline: none;
  transition: border-color 0.2s;
}

.content-input:focus {
  border-color: #1e80ff;
}

.footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 25px;
}

.auto-save {
  color: #888;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.actions {
  display: flex;
  gap: 12px;
}

.draft-btn {
  padding: 8px 20px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.draft-btn:hover {
  background: #f5f5f5;
  border-color: #ccc;
}

.publish-btn {
  padding: 8px 24px;
  border: none;
  background: #1e80ff;
  color: white;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}

.publish-btn:hover {
  background: #0a5fd0;
}
</style>