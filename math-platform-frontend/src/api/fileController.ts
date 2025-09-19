// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** upload POST /api/file/upload */
export async function uploadUsingPost(
  body: {
    /** file */
    file: any[]
  },
  options?: { [key: string]: any }
) {
  const formData = new FormData()

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele]

    if (item !== undefined && item !== null) {
      if (typeof item === 'object' && !(item instanceof File)) {
        if (item instanceof Array) {
          item.forEach((f) => formData.append(ele, f || ''))
        } else {
          formData.append(ele, JSON.stringify(item))
        }
      } else {
        formData.append(ele, item)
      }
    }
  })

  return request<API.BaseResponseListString_>('/api/file/upload', {
    method: 'POST',
    data: formData,
    requestType: 'form',
    ...(options || {}),
  })
}

/** uploadAvatarPending POST /api/file/upload/avatar/pending */
export async function uploadAvatarPendingUsingPost(
  body: {},
  file?: File,
  options?: { [key: string]: any }
) {
  const formData = new FormData()

  if (file) {
    formData.append('file', file)
  }

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele]

    if (item !== undefined && item !== null) {
      if (typeof item === 'object' && !(item instanceof File)) {
        if (item instanceof Array) {
          item.forEach((f) => formData.append(ele, f || ''))
        } else {
          formData.append(ele, JSON.stringify(item))
        }
      } else {
        formData.append(ele, item)
      }
    }
  })

  return request<API.BaseResponseUploadPictureResult_>('/api/file/upload/avatar/pending', {
    method: 'POST',
    data: formData,
    requestType: 'form',
    ...(options || {}),
  })
}

/** uploadAvatarPendingByUrl POST /api/file/upload/avatar/pendingByUrl */
export async function uploadAvatarPendingByUrlUsingPost(
  body: API.AvatarUrlUploadRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseUploadPictureResult_>('/api/file/upload/avatar/pendingByUrl', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** uploadToCos POST /api/file/upload/cos */
export async function uploadToCosUsingPost(
  body: {},
  file?: File,
  options?: { [key: string]: any }
) {
  const formData = new FormData()

  if (file) {
    formData.append('file', file)
  }

  Object.keys(body).forEach((ele) => {
    const item = (body as any)[ele]

    if (item !== undefined && item !== null) {
      if (typeof item === 'object' && !(item instanceof File)) {
        if (item instanceof Array) {
          item.forEach((f) => formData.append(ele, f || ''))
        } else {
          formData.append(ele, JSON.stringify(item))
        }
      } else {
        formData.append(ele, item)
      }
    }
  })

  return request<API.BaseResponseUploadPictureResult_>('/api/file/upload/cos', {
    method: 'POST',
    data: formData,
    requestType: 'form',
    ...(options || {}),
  })
}
