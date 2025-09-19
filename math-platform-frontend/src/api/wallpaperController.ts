// @ts-ignore
/* eslint-disable */
import request from '@/request';

/** 分页获取壁纸列表 POST /api/wallpaper/list/page */
export async function listWallpapersByPageUsingPost(
  body: API.WallpaperQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageWallpaperVO>('/api/wallpaper/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 根据ID获取壁纸 GET /api/wallpaper/get/${param0} */
export async function getWallpaperByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getWallpaperByIdUsingGETParams,
  options?: { [key: string]: any },
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseWallpaperVO>(`/api/wallpaper/get/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 增加下载次数 POST /api/wallpaper/download/${param0} */
export async function incrementDownloadCountUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.incrementDownloadCountUsingPOSTParams,
  options?: { [key: string]: any },
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean>(`/api/wallpaper/download/${param0}`, {
    method: 'POST',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 点赞壁纸 POST /api/wallpaper/like/${param0} */
export async function likeWallpaperUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.likeWallpaperUsingPOSTParams,
  options?: { [key: string]: any },
) {
  const { id: param0, ...queryParams } = params;
  return request<API.BaseResponseBoolean>(`/api/wallpaper/like/${param0}`, {
    method: 'POST',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 删除壁纸 POST /api/wallpaper/delete */
export async function deleteWallpaperUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseBoolean>('/api/wallpaper/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 批量爬取并创建壁纸 POST /api/wallpaper/batch/crawl */
export async function batchCrawlWallpapersUsingPost(
  body: API.WallpaperBatchCrawlRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponseInteger>('/api/wallpaper/batch/crawl', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 分页获取所有壁纸 POST /api/wallpaper/admin/list/page */
export async function listAllWallpapersByPageUsingPost(
  body: API.WallpaperQueryRequest,
  options?: { [key: string]: any },
) {
  return request<API.BaseResponsePageWallpaperVO>('/api/wallpaper/admin/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}
