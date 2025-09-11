declare namespace API {
  type BaseResponseArrayString_ = {
    code?: number
    data?: string[]
    message?: string
  }

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

  type BaseResponseIPagePostVO_ = {
    code?: number
    data?: IPagePostVO_
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

  type BaseResponseListNotification_ = {
    code?: number
    data?: Notification[]
    message?: string
  }

  type BaseResponseListPost_ = {
    code?: number
    data?: Post[]
    message?: string
  }

  type BaseResponseListString_ = {
    code?: number
    data?: string[]
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

  type BaseResponsePostVO_ = {
    code?: number
    data?: PostVO
    message?: string
  }

  type BaseResponseString_ = {
    code?: number
    data?: string
    message?: string
  }

  type BaseResponseUploadPictureResult_ = {
    code?: number
    data?: UploadPictureResult
    message?: string
  }

  type BaseResponseUser_ = {
    code?: number
    data?: User
    message?: string
  }

  type BaseResponseUserPostStatsVO_ = {
    code?: number
    data?: UserPostStatsVO
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
    replyCount?: number
    replyToId?: number
    rootCommentId?: number
    rootId?: number
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

  type getPostsByCategoryUsingPOSTParams = {
    /** category */
    category: string
  }

  type getSearchSuggestionsUsingGETParams = {
    /** keyword */
    keyword: string
  }

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type getUserPostStatsUsingGETParams = {
    /** userId */
    userId: number
  }

  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type getUserVOPublicUsingGETParams = {
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

  type IPagePostVO_ = {
    current?: number
    pages?: number
    records?: PostVO[]
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

  type listUsingGETParams = {
    /** limit */
    limit?: number
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

  type Notification = {
    content?: string
    createTime?: string
    extra?: string
    id?: number
    readFlag?: number
    receiverId?: number
    type?: string
  }

  type PageRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
  }

  type PageUserVO_ = {
    current?: number
    pages?: number
    records?: UserVO[]
    size?: number
    total?: number
  }

  type Post = {
    auditRemark?: string
    auditStatus?: number
    auditTime?: string
    auditUserId?: number
    category?: string
    commentCount?: number
    content?: string
    contentSummary?: string
    createTime?: string
    deleted?: boolean
    favouriteCount?: number
    hotScore?: number
    id?: number
    images?: string
    isDelete?: number
    isHot?: number
    isTop?: number
    likeCount?: number
    publishTime?: string
    qualityScore?: number
    shareCount?: number
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

  type PostVO = {
    category?: string
    commentCount?: number
    content?: string
    createTime?: string
    favouriteCount?: number
    id?: number
    images?: string[]
    isFavourited?: boolean
    isLiked?: boolean
    likeCount?: number
    tags?: string[]
    title?: string
    updateTime?: string
    userAvatar?: string
    userId?: number
    userName?: string
    viewCount?: number
  }

  type SearchRequest = {
    category?: string
    current?: number
    keyword?: string
    pageSize?: number
    sortField?: string
    sortOrder?: string
  }

  type UploadPictureResult = {
    picColor?: string
    picFormat?: string
    picHeight?: number
    picName?: string
    picScale?: number
    picSize?: number
    picWidth?: number
    thumbnailUrl?: string
    url?: string
  }

  type User = {
    admin?: boolean
    birthday?: string
    createTime?: string
    editTime?: string
    email?: string
    followerCount?: number
    followingCount?: number
    gender?: number
    id?: number
    isDelete?: number
    lastLoginIp?: string
    lastLoginTime?: string
    likeCount?: number
    location?: string
    phone?: string
    postCount?: number
    status?: number
    updateTime?: string
    userAccount?: string
    userAvatar?: string
    userName?: string
    userPassword?: string
    userProfile?: string
    userRole?: string
    website?: string
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

  type UserPostQueryRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    userId?: number
  }

  type UserPostStatsVO = {
    favouriteTotal?: number
    likeTotal?: number
    postCount?: number
    viewTotal?: number
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
