import axios from 'axios'

const request = axios.create({ baseURL: 'http://localhost:8080', timeout: 5000 })

// 请求拦截器：添加 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) config.headers.Authorization = token
  return config
})

// 统一处理 token 过期
function handleTokenExpired() {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  if (window.openLogin) {
    window.openLogin('login')
  } else {
    alert('登录已过期，请重新登录')
    window.location.href = '/'
  }
}

// 响应拦截器
request.interceptors.response.use(
  response => {
    // 业务 code 非 200 时视为错误
    if (response.data.code && response.data.code !== 200) {
      // 如果是 token 过期（假设业务 code 为 401），则触发登录
      if (response.data.code === 401) {
        handleTokenExpired()
      }
      // 将错误传递给业务层 catch
      return Promise.reject(new Error(response.data.msg || '业务错误'))
    }
    return response.data
  },
  error => {
    if (error.response?.status === 401) {
      handleTokenExpired()
    }
    return Promise.reject(error)
  }
)

export default request