// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** listWallpapersByPage POST /api/wallpaper/list/page */
export async function listWallpapersByPageUsingPost(
  body: API.WallpaperQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageWallpaperVO_>('/api/wallpaper/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getWallpaperById GET /api/wallpaper/get/{id} */
export async function getWallpaperByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getWallpaperByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.BaseResponseWallpaperVO_>(`/api/wallpaper/get/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** incrementDownloadCount POST /api/wallpaper/download/{id} */
export async function incrementDownloadCountUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.incrementDownloadCountUsingPOSTParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.BaseResponseBoolean_>(`/api/wallpaper/download/${param0}`, {
    method: 'POST',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** likeWallpaper POST /api/wallpaper/like/{id} */
export async function likeWallpaperUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.likeWallpaperUsingPOSTParams,
  options?: { [key: string]: any }
) {
  const { id: param0, ...queryParams } = params
  return request<API.BaseResponseBoolean_>(`/api/wallpaper/like/${param0}`, {
    method: 'POST',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** deleteWallpaper POST /api/wallpaper/delete */
export async function deleteWallpaperUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/wallpaper/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** batchCrawlWallpapers POST /api/wallpaper/batch/crawl */
export async function batchCrawlWallpapersUsingPost(
  body: API.WallpaperBatchCrawlRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseInt_>('/api/wallpaper/batch/crawl', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** listAllWallpapersByPage POST /api/wallpaper/admin/list/page */
export async function listAllWallpapersByPageUsingPost(
  body: API.WallpaperQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageWallpaperVO_>('/api/wallpaper/admin/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
