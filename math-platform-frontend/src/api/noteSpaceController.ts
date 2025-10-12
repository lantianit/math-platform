// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** 创建笔记空间 POST /api/note_space/add */
export async function addNoteSpaceUsingPost(
  body: API.NoteSpaceAddRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseLong_>('/api/note_space/add', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 删除笔记空间 POST /api/note_space/delete */
export async function deleteNoteSpaceUsingPost(
  body: API.DeleteRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/note_space/delete', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 编辑笔记空间 POST /api/note_space/edit */
export async function editNoteSpaceUsingPost(
  body: API.NoteSpaceEditRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/note_space/edit', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 根据id获取笔记空间 GET /api/note_space/get/vo */
export async function getNoteSpaceVoByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getNoteSpaceVOByIdUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseNoteSpaceVO_>('/api/note_space/get/vo', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** 获取空间级别列表 GET /api/note_space/list/level */
export async function listNoteSpaceLevelUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseListNoteSpaceLevel_>('/api/note_space/list/level', {
    method: 'GET',
    ...(options || {}),
  })
}

/** 分页获取笔记空间列表 POST /api/note_space/list/page */
export async function listNoteSpaceByPageUsingPost(
  body: API.NoteSpaceQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageNoteSpace_>('/api/note_space/list/page', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 分页获取笔记空间列表 POST /api/note_space/list/page/vo */
export async function listNoteSpaceVoByPageUsingPost(
  body: API.NoteSpaceQueryRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponsePageNoteSpaceVO_>('/api/note_space/list/page/vo', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** 更新笔记空间 POST /api/note_space/update */
export async function updateNoteSpaceUsingPost(
  body: API.NoteSpaceUpdateRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/note_space/update', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}
