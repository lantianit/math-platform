// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** getPostsByCategory POST /api/api/feed/category/${param0} */
export async function getPostsByCategoryUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getPostsByCategoryUsingPOSTParams,
  body: API.PageRequest,
  options?: { [key: string]: any }
) {
  const { category: param0, ...queryParams } = params
  return request<API.BaseResponseIPagePostVO_>(`/api/api/feed/category/${param0}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    params: { ...queryParams },
    data: body,
    ...(options || {}),
  })
}

/** getFollowingPosts POST /api/api/feed/following */
export async function getFollowingPostsUsingPost(
  body: API.PageRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseIPagePostVO_>('/api/api/feed/following', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getHotPosts POST /api/api/feed/hot */
export async function getHotPostsUsingPost(
  body: API.PageRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseIPagePostVO_>('/api/api/feed/hot', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getLatestPosts POST /api/api/feed/latest */
export async function getLatestPostsUsingPost(
  body: API.PageRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseIPagePostVO_>('/api/api/feed/latest', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getRecommendPosts POST /api/api/feed/recommend */
export async function getRecommendPostsUsingPost(
  body: API.PageRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseIPagePostVO_>('/api/api/feed/recommend', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
