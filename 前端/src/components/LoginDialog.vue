<template>
  <div class="mask">
    <div class="dialog">
      <h2 v-if="mode==='login'">登录</h2>
      <h2 v-else>注册</h2>

      <input v-model="name" placeholder="用户名"/>
      <input v-model="password" type="password" placeholder="密码"/>

      <input v-if="mode==='register'" v-model="confirmPassword" type="password" placeholder="确认密码"/>

      <button @click="submit">{{ mode==='login' ? "登录" : "注册" }}</button>

      <p v-if="mode==='login'">
        没有账号？
        <span class="link" @click="switchMode">去注册</span>
      </p>
      <p v-else>
        已有账号？
        <span class="link" @click="switchMode">去登录</span>
      </p>

      <span class="close" @click="$emit('close')">✕</span>
    </div>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import request from '../api/request'

const emit = defineEmits(['close', 'login-success'])
const props = defineProps({ mode: String })

const globalState = inject('globalState')
const setToken = inject('setToken')
const setUser = inject('setUser')
const closeLogin = inject('closeLogin')

const name = ref('')
const password = ref('')
const confirmPassword = ref('')

function switchMode() {
  globalState.loginMode = globalState.loginMode === 'login' ? 'register' : 'login'
}

async function submit() {
  if (props.mode === 'login') {
    try {
      const res = await request.post("/users/login", {
        name: name.value,
        password: password.value
      })

      if (res.code !== 200) {
        alert(res.msg)
        return
      }

      // 假设后端返回的数据格式为 { token, id, name, avatarUrl }
      const { token, id, name: userName, avatarUrl } = res.data
      const user = { 
        id, 
        name: userName, 
        avatarUrl: avatarUrl || '/avatar/default-avatar.png'  // 为空时使用默认头像
      }
      localStorage.setItem("token", token)
      localStorage.setItem("user", JSON.stringify(user))
      setToken(token)
      setUser(user)

      emit("login-success", user)
      closeLogin()
    } catch (err) {
      alert('登录失败：' + (err.response?.data?.message || err.message))
    }
  } else {
    // 注册逻辑
    if (password.value !== confirmPassword.value) {
      alert('两次密码不一致')
      return
    }
    try {
      await request.post('/users/register', {
        name: name.value,
        password: password.value,
        confirmPassword: confirmPassword.value
      })
      alert('注册成功，请登录')
      globalState.loginMode = 'login'
    } catch (err) {
      alert('注册失败：' + (err.response?.data?.message || err.message))
    }
  }
}
</script>

<style scoped>
/* 样式保持不变 */
.mask{
  position:fixed;
  top:0;
  left:0;
  right:0;
  bottom:0;
  background:rgba(0,0,0,0.4);
  display:flex;
  align-items:center;
  justify-content:center;
}
.dialog{
  background:white;
  padding:30px;
  width:320px;
  border-radius:8px;
  position:relative;
}
.dialog input{
  width:100%;
  padding:10px;
  margin:10px 0;
  border:1px solid #ddd;
  border-radius:4px;
}
.dialog button{
  width:100%;
  padding:10px;
  background:#1e80ff;
  color:white;
  border:none;
  border-radius:4px;
  cursor:pointer;
}
.close{
  position:absolute;
  right:10px;
  top:10px;
  cursor:pointer;
}
.link{
  color:#1e80ff;
  cursor:pointer;
}
</style>