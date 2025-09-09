// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** addComment POST /api/comment/add */
export async function addCommentUsingPost(
  body: API.CommentAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/comment/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getCommentCountByPostId GET /api/comment/count */
export async function getCommentCountByPostIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getCommentCountByPostIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/comment/count', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** deleteComment POST /api/comment/delete */
export async function deleteCommentUsingPost(
  body: API.CommentDeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/comment/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getCommentById GET /api/comment/get */
export async function getCommentByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getCommentByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseComment_>('/api/comment/get', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** toggleCommentLike POST /api/comment/like */
export async function toggleCommentLikeUsingPost(
  body: API.CommentLikeRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/comment/like', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** listCommentsByPostId POST /api/comment/list */
export async function listCommentsByPostIdUsingPost(
  body: API.CommentQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseIPageComment_>('/api/comment/list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** listRepliesByParentId GET /api/comment/replies */
export async function listRepliesByParentIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listRepliesByParentIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListComment_>('/api/comment/replies', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** addReplyComment POST /api/comment/reply */
export async function addReplyCommentUsingPost(
  body: API.CommentReplyRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/comment/reply', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** updateComment POST /api/comment/update */
export async function updateCommentUsingPost(
  body: API.CommentUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/comment/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
