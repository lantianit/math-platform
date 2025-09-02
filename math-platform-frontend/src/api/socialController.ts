// @ts-ignore
/* eslint-disable */
import request from '@/request'

/** togglePostFavourite POST /api/api/social/post/favourite */
export async function togglePostFavouriteUsingPost1(
  body: API.PostFavouriteRequest1,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/api/social/post/favourite', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getFavouritePostIds GET /api/api/social/post/favourited */
export async function getFavouritePostIdsUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseListLong_>('/api/api/social/post/favourited', {
    method: 'GET',
    ...(options || {}),
  })
}

/** isPostFavouritedByUser GET /api/api/social/post/isFavourited */
export async function isPostFavouritedByUserUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.isPostFavouritedByUserUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/api/social/post/isFavourited', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** isPostLikedByUser GET /api/api/social/post/isLiked */
export async function isPostLikedByUserUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.isPostLikedByUserUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/api/social/post/isLiked', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}

/** togglePostLike POST /api/api/social/post/like */
export async function togglePostLikeUsingPost1(
  body: API.PostLikeRequest1,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/api/social/post/like', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getLikedPostIds GET /api/api/social/post/liked */
export async function getLikedPostIdsUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseListLong_>('/api/api/social/post/liked', {
    method: 'GET',
    ...(options || {}),
  })
}

/** getFollowerCountByUserId GET /api/api/social/user/${param0}/followers/count */
export async function getFollowerCountByUserIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getFollowerCountByUserIdUsingGETParams,
  options?: { [key: string]: any }
) {
  const { userId: param0, ...queryParams } = params
  return request<API.BaseResponseLong_>(`/api/api/social/user/${param0}/followers/count`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** getFollowingCountByUserId GET /api/api/social/user/${param0}/following/count */
export async function getFollowingCountByUserIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.getFollowingCountByUserIdUsingGETParams,
  options?: { [key: string]: any }
) {
  const { userId: param0, ...queryParams } = params
  return request<API.BaseResponseLong_>(`/api/api/social/user/${param0}/following/count`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  })
}

/** toggleUserFollow POST /api/api/social/user/follow */
export async function toggleUserFollowUsingPost(
  body: API.UserFollowRequest,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/api/social/user/follow', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  })
}

/** getFollowerIds GET /api/api/social/user/followers */
export async function getFollowerIdsUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseListLong_>('/api/api/social/user/followers', {
    method: 'GET',
    ...(options || {}),
  })
}

/** getFollowerCount GET /api/api/social/user/followers/count */
export async function getFollowerCountUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseLong_>('/api/api/social/user/followers/count', {
    method: 'GET',
    ...(options || {}),
  })
}

/** getFollowingIds GET /api/api/social/user/following */
export async function getFollowingIdsUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseListLong_>('/api/api/social/user/following', {
    method: 'GET',
    ...(options || {}),
  })
}

/** getFollowingCount GET /api/api/social/user/following/count */
export async function getFollowingCountUsingGet(options?: { [key: string]: any }) {
  return request<API.BaseResponseLong_>('/api/api/social/user/following/count', {
    method: 'GET',
    ...(options || {}),
  })
}

/** isUserFollowedByUser GET /api/api/social/user/isFollowed */
export async function isUserFollowedByUserUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.isUserFollowedByUserUsingGETParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseBoolean_>('/api/api/social/user/isFollowed', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
