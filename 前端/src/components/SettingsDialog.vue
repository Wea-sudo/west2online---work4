<template>
  <div class="mask">
    <div class="dialog">

      <h2>账号设置</h2>

      <div class="menu">
        <button @click="tab='info'">修改信息</button>
        <button @click="tab='password'">修改密码</button>
        <button class="logout" @click="logout">退出登录</button>
      </div>

      <!-- 修改用户信息 -->
      <div v-if="tab==='info'" class="form">

        <input v-model="name" placeholder="用户名"/>

        <!-- 头像上传 -->
        <div class="avatar-upload">

          <img
            v-if="avatarPreview || avatarUrl"
            :src="avatarPreview || avatarUrl"
            class="preview"
          />

          <input
            type="file"
            ref="fileInput"
            accept="image/*"
            @change="handleFileChange"
          />

        </div>

        <button @click="updateInfo" :disabled="uploading">
          {{ uploading ? '保存中...' : '保存' }}
        </button>

      </div>

      <!-- 修改密码 -->
      <div v-if="tab==='password'" class="form">

        <input type="password" v-model="oldPassword" placeholder="旧密码"/>
        <input type="password" v-model="newPassword" placeholder="新密码"/>
        <input type="password" v-model="confirmPassword" placeholder="确认密码"/>

        <button @click="updatePassword">
          修改密码
        </button>

      </div>

      <span class="close" @click="$emit('close')">✕</span>

    </div>
  </div>
</template>

<script setup>
import { ref, inject, onMounted } from "vue"
import request from "../api/request"
import { useRouter } from "vue-router"

const router = useRouter()

const globalState = inject("globalState")
const setUser = inject("setUser")

const fileInput = ref(null)

const tab = ref("info")

const name = ref("")
const avatarUrl = ref("")
const avatarPreview = ref("")

const uploading = ref(false)

const oldPassword = ref("")
const newPassword = ref("")
const confirmPassword = ref("")

// 初始化用户信息
onMounted(() => {
  if (globalState.user) {
    name.value = globalState.user.name
    avatarUrl.value = globalState.user.avatarUrl || '/avatar/default-avatar.png'
    avatarPreview.value = globalState.user.avatarUrl || '/avatar/default-avatar.png'
  }
})

// 选择头像
function handleFileChange() {

  const file = fileInput.value.files[0]
  
  if (file) {

    avatarPreview.value = URL.createObjectURL(file)
    alert("avatarPreview.value:",avatarPreview.value)
  }

}

// 更新用户信息
async function updateInfo() {
  const file = fileInput.value?.files?.[0]
  try {
    uploading.value = true
    let finalAvatarUrl = avatarUrl.value
    if (file) {
      const formData = new FormData()
      formData.append("file", file)
      const uploadRes = await request.post("/upload/avatar", formData)
      if (uploadRes.code !== 200) {
        throw new Error(uploadRes.msg)
      }
      finalAvatarUrl = uploadRes.data
    }
    const res = await request.put("/users/info", {
      name: name.value,
      avatarUrl: finalAvatarUrl
    })
    if (res.code !== 200) {
      throw new Error(res.msg)
    }
    const updatedUser = { ...globalState.user, name: name.value, avatarUrl: finalAvatarUrl }
    setUser(updatedUser)
    avatarUrl.value = finalAvatarUrl
    avatarPreview.value = finalAvatarUrl
    alert("更新成功")
  } catch (err) {
    // 如果是 token 过期错误，拦截器已经弹登录框，这里不再重复提示
    // 但需要判断错误消息，避免覆盖
    if (err.message?.includes('Token已过期') || err.message?.includes('未登录')) {
      // 已经弹框，这里什么都不做
      return
    }
    console.error(err)
    alert(err.message || "更新失败")
  } finally {
    uploading.value = false
  }
}

// 修改密码
async function updatePassword() {

  if (newPassword.value !== confirmPassword.value) {

    alert("两次密码不一致")
    return

  }

  try {

    const res = await request.put("/users/password", {

      oldPassword: oldPassword.value,
      newPassword: newPassword.value,
      confirmPassword: confirmPassword.value

    })

    if (res.code !== 200) {

      throw new Error(res.msg)

    }

    alert("密码修改成功")

    oldPassword.value = ""
    newPassword.value = ""
    confirmPassword.value = ""

  } catch (err) {

    alert(err.message || "修改失败")

  }

}

// 退出登录
function logout() {

  localStorage.removeItem("token")
  localStorage.removeItem("user")

  globalState.token = ""
  setUser(null)

  router.replace("/")

}
</script>

<style scoped>

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
  width:360px;
  border-radius:8px;
  position:relative;
}

.menu{
  display:flex;
  gap:10px;
  margin-bottom:20px;
}

.form input{
  width:100%;
  padding:10px;
  margin-bottom:10px;
  border:1px solid #ddd;
  border-radius:4px;
}

.form button{
  width:100%;
  padding:10px;
  background:#1e80ff;
  color:white;
  border:none;
  border-radius:4px;
  cursor:pointer;
}

.avatar-upload{
  margin-bottom:10px;
}

.preview{
  width:80px;
  height:80px;
  border-radius:50%;
  object-fit:cover;
  margin-bottom:10px;
}

.logout{
  background:#ff4d4f;
  color:white;
  border:none;
  padding:6px 12px;
  border-radius:4px;
  cursor:pointer;
}

.close{
  position:absolute;
  top:10px;
  right:10px;
  cursor:pointer;
}

</style>