// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 创建笔记 POST /api/note/add */
export async function addNoteUsingPost(body: API.NoteAddRequest, options?: { [key: string]: any }) {
  return request<API.BaseResponseLong_>('/api/note/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 删除笔记 POST /api/note/delete */
export async function deleteNoteUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/note/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 编辑笔记 POST /api/note/edit */
export async function editNoteUsingPost(
  body: API.NoteEditRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/note/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 根据id获取笔记 GET /api/note/get/vo */
export async function getNoteVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getNoteVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseNoteVO_>('/api/note/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 分页获取笔记列表 POST /api/note/list/page */
export async function listNoteByPageUsingPost(
  body: API.NoteQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageNote_>('/api/note/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 分页获取笔记列表 POST /api/note/list/page/vo */
export async function listNoteVoByPageUsingPost(
  body: API.NoteQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageNoteVO_>('/api/note/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
