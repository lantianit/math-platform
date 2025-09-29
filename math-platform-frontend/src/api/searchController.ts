// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** refreshCache POST /api/search/cache/refresh */
export async function refreshCacheUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseObject_>('/api/search/cache/refresh', {
    method: 'POST',
    ...(options || {}),
  })
}

/** getCacheStats GET /api/search/cache/stats */
export async function getCacheStatsUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseObject_>('/api/search/cache/stats', {
    method: 'GET',
    ...(options || {}),
  })
}

/** testSerialization GET /api/search/cache/test */
export async function testSerializationUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseObject_>('/api/search/cache/test', {
    method: 'GET',
    ...(options || {}),
  })
}

/** getHotKeywords GET /api/search/hot-keywords */
export async function getHotKeywordsUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseArrayString_>('/api/search/hot-keywords', {
    method: 'GET',
    ...(options || {}),
  })
}

/** searchPosts POST /api/search/posts */
export async function searchPostsUsingPost(
  body: API.SearchRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseIPagePostVO_>('/api/search/posts', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getSearchSuggestions GET /api/search/suggestions */
export async function getSearchSuggestionsUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getSearchSuggestionsUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseArrayString_>('/api/search/suggestions', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
