// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** getHotKeywords GET /api/api/search/hot-keywords */
export async function getHotKeywordsUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseArrayString_>('/api/api/search/hot-keywords', {
    method: 'GET',
    ...(options || {}),
  })
}

/** searchPosts POST /api/api/search/posts */
export async function searchPostsUsingPost(
  body: API.SearchRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseIPagePostVO_>('/api/api/search/posts', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getSearchSuggestions GET /api/api/search/suggestions */
export async function getSearchSuggestionsUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getSearchSuggestionsUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseArrayString_>('/api/api/search/suggestions', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
