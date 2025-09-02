declare namespace API {
  type BaseResponseBoolean_ = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseComment_ = {
    code?: number
    data?: Comment
    message?: string
  }

  type BaseResponseIPageComment_ = {
    code?: number
    data?: IPageComment_
    message?: string
  }

  type BaseResponseIPagePost_ = {
    code?: number
    data?: IPagePost_
    message?: string
  }

  type BaseResponseListComment_ = {
    code?: number
    data?: Comment[]
    message?: string
  }

  type BaseResponseListLong_ = {
    code?: number
    data?: number[]
    message?: string
  }

  type BaseResponseListPost_ = {
    code?: number
    data?: Post[]
    message?: string
  }

  type BaseResponseLoginUserVO_ = {
    code?: number
    data?: LoginUserVO
    message?: string
  }

  type BaseResponseLong_ = {
    code?: number
    data?: number
    message?: string
  }

  type BaseResponsePageUserVO_ = {
    code?: number
    data?: PageUserVO_
    message?: string
  }

  type BaseResponsePost_ = {
    code?: number
    data?: Post
    message?: string
  }

  type BaseResponseUser_ = {
    code?: number
    data?: User
    message?: string
  }

  type BaseResponseUserVO_ = {
    code?: number
    data?: UserVO
    message?: string
  }

  type Comment = {
    content?: string
    createTime?: string
    deleted?: boolean
    id?: number
    isDelete?: number
    likeCount?: number
    parentId?: number
    postId?: number
    reply?: boolean
    rootCommentId?: number
    status?: number
    updateTime?: string
    userId?: number
  }

  type CommentAddRequest = {
    content?: string
    postId?: number
  }

  type CommentDeleteRequest = {
    id?: number
  }

  type CommentLikeRequest = {
    commentId?: number
  }

  type CommentQueryRequest = {
    current?: number
    pageSize?: number
    postId?: number
  }

  type CommentReplyRequest = {
    content?: string
    parentId?: number
    postId?: number
  }

  type CommentUpdateRequest = {
    content?: string
    id?: number
  }

  type DeleteRequest = {
    id?: number
  }

  type getCommentByIdUsingGETParams = {
    /** id */
    id: number
  }

  type getCommentCountByPostIdUsingGETParams = {
    /** postId */
    postId: number
  }

  type getFollowerCountByUserIdUsingGETParams = {
    /** userId */
    userId: number
  }

  type getFollowingCountByUserIdUsingGETParams = {
    /** userId */
    userId: number
  }

  type getHotPostsUsingGETParams = {
    /** limit */
    limit?: number
  }

  type getLatestPostsUsingGETParams = {
    /** limit */
    limit?: number
  }

  type getPostByIdUsingGETParams = {
    /** id */
    id: number
  }

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type IPageComment_ = {
    current?: number
    pages?: number
    records?: Comment[]
    size?: number
    total?: number
  }

  type IPagePost_ = {
    current?: number
    pages?: number
    records?: Post[]
    size?: number
    total?: number
  }

  type isPostFavouritedByUserUsingGETParams = {
    /** postId */
    postId: number
  }

  type isPostLikedByUserUsingGETParams = {
    /** postId */
    postId: number
  }

  type isUserFollowedByUserUsingGETParams = {
    /** followingId */
    followingId: number
  }

  type listRepliesByParentIdUsingGETParams = {
    /** parentId */
    parentId: number
  }

  type LoginUserVO = {
    createTime?: string
    editTime?: string
    id?: number
    updateTime?: string
    userAccount?: string
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type PageUserVO_ = {
    current?: number
    pages?: number
    records?: UserVO[]
    size?: number
    total?: number
  }

  type Post = {
    category?: string
    commentCount?: number
    content?: string
    createTime?: string
    deleted?: boolean
    favouriteCount?: number
    id?: number
    images?: string
    isDelete?: number
    likeCount?: number
    status?: number
    tags?: string
    title?: string
    updateTime?: string
    userId?: number
    viewCount?: number
  }

  type PostAddRequest = {
    category?: string
    content?: string
    images?: string
    tags?: string
    title?: string
  }

  type PostDeleteRequest = {
    id?: number
  }

  type PostFavouriteRequest = {
    postId?: number
  }

  type PostFavouriteRequest1 = {
    postId?: number
  }

  type PostLikeRequest = {
    postId?: number
  }

  type PostLikeRequest1 = {
    postId?: number
  }

  type PostQueryRequest = {
    category?: string
    current?: number
    pageSize?: number
    searchText?: string
    sortField?: string
    sortOrder?: string
  }

  type PostUpdateRequest = {
    category?: string
    content?: string
    id?: number
    images?: string
    tags?: string
    title?: string
  }

  type User = {
    admin?: boolean
    createTime?: string
    editTime?: string
    id?: number
    isDelete?: number
    updateTime?: string
    userAccount?: string
    userAvatar?: string
    userName?: string
    userPassword?: string
    userProfile?: string
    userRole?: string
  }

  type UserAddRequest = {
    userAccount?: string
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type UserFollowRequest = {
    followingId?: number
  }

  type UserLoginRequest = {
    userAccount?: string
    userPassword?: string
  }

  type UserQueryRequest = {
    current?: number
    id?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    userAccount?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type UserRegisterRequest = {
    checkPassword?: string
    userAccount?: string
    userPassword?: string
  }

  type UserUpdateRequest = {
    id?: number
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }

  type UserVO = {
    createTime?: string
    id?: number
    userAccount?: string
    userAvatar?: string
    userName?: string
    userProfile?: string
    userRole?: string
  }
}
