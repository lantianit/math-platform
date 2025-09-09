// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** list GET /api/notify/list */
export async function listUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.listUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListNotification_>('/api/notify/list', {
    method: 'GET',
    params: {
      // limit has a default value: 50
      limit: '50',
      ...params,
    },
    ...(options || {}),
  })
}

/** markAllRead POST /api/notify/markAllRead */
export async function markAllReadUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean_>('/api/notify/markAllRead', {
    method: 'POST',
    ...(options || {}),
  })
}

/** unreadCount GET /api/notify/unread/count */
export async function unreadCountUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseLong_>('/api/notify/unread/count', {
    method: 'GET',
    ...(options || {}),
  })
}
