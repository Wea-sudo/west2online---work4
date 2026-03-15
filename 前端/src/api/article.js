import request from "./request"




export function getArticles(){
    return request.get("/articles")
}

// 按时间排序（最新）
export function getArticlesByTime(page = 1, pageSize = 10) {
    return request.get("/articles/time", { params: { page, pageSize } })
}

// 按点赞数排序（最多赞）
export function getArticlesByThumb(page = 1, pageSize = 10) {
    return request.get("/articles/thumb", { params: { page, pageSize } })
}

// 获取指定作者的文章，按时间排序
export function getArticlesByAuthorByTime(authorId, page = 1, pageSize = 10) {
    return request.get(`/articles/author/${authorId}/time`, { params: { page, pageSize } })
}

// 获取指定作者的文章，按点赞数排序
export function getArticlesByAuthorByThumb(authorId, page = 1, pageSize = 10) {
    return request.get(`/articles/author/${authorId}/thumb`, { params: { page, pageSize } })
}


export function createArticle(data){
    return request.post("/articles/create",data)
}


// 按时间搜索（最新）
export function searchArticlesByTime(keyword, page = 1, pageSize = 10) {
    return request.get("/articles/search/time", { params: { keyword, page, pageSize } })
}

// 按点赞数搜索（热门）
export function searchArticlesByThumb(keyword, page = 1, pageSize = 10) {
    return request.get("/articles/search/thumb", { params: { keyword, page, pageSize } })
}

// 点赞
export function likeArticle(id) {
    return request.post(`/articles/${id}/thumb`)
}

// 取消点赞
export function unlikeArticle(id) {
    return request.delete(`/articles/${id}/thumb`)
}

// 收藏
export function collectArticle(id) {
    return request.post(`/articles/${id}/collect`)
}

// 取消收藏
export function uncollectArticle(id) {
    return request.delete(`/articles/${id}/collect`)
}

// 创建评论
export function createComment(data) {
    return request.post('/comments/create', data)
}

// 获取文章评论分页
export function getArticleComments(articleId, page = 1, pageSize = 10) {
    return request.get(`/comments/article/${articleId}`, { params: { page, pageSize } })
}