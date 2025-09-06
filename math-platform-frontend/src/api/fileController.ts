// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** upload POST /api/api/file/upload */
export async function uploadUsingPost(form: FormData, options?: { [key: string]: any }) {
  return request<API.BaseResponseListString_>('/api/api/file/upload', {
    method: 'POST',
    headers: {
      // 让浏览器自动设置 multipart 边界
    },
    data: form,
    ...(options || {}),
  })
}


