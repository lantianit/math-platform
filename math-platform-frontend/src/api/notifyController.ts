// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** listNotifications GET /api/api/notify/list */
export async function listNotificationsUsingGet(
  params?: { limit?: number },
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseListNotification_>('/api/api/notify/list', {
    method: 'GET',
    params,
    ...(options || {}),
  })
}

/** unreadCount GET /api/api/notify/unread/count */
export async function getUnreadCountUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseLong_>('/api/api/notify/unread/count', {
    method: 'GET',
    ...(options || {}),
  })
}

/** markAllRead POST /api/api/notify/markAllRead */
export async function markAllReadUsingPost(options?: { [key: string]: any }) {
  return request<API.BaseResponseBoolean_>('/api/api/notify/markAllRead', {
    method: 'POST',
    ...(options || {}),
  })
}


