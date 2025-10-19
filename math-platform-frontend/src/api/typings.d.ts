declare namespace API {
  type approveAvatarUsingPOSTParams = {
    /** id */
    id: number
  }

  type AvatarReviewSubmitRequest = {
    etag?: string
    format?: string
    height?: number
    objectKey?: string
    sha256?: string
    size?: number
    thumbnailKey?: string
    thumbnailUrl?: string
    url?: string
    width?: number
  }

  type AvatarUrlUploadRequest = {
    url?: string
  }

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

  type BaseResponseCompressionSummaryStats_ = {
    code?: number
    data?: CompressionSummaryStats
    message?: string
  }

  type BaseResponseInt_ = {
    code?: number
    data?: number
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

  type BaseResponseListNoteSpaceLevel_ = {
    code?: number
    data?: NoteSpaceLevel[]
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

  type BaseResponseNoteSpaceVO_ = {
    code?: number
    data?: NoteSpaceVO
    message?: string
  }

  type BaseResponseNoteVO_ = {
    code?: number
    data?: NoteVO
    message?: string
  }

  type BaseResponseObject_ = {
    code?: number
    data?: Record<string, any>
    message?: string
  }

  type BaseResponsePageNote_ = {
    code?: number
    data?: PageNote_
    message?: string
  }

  type BaseResponsePageNoteSpace_ = {
    code?: number
    data?: PageNoteSpace_
    message?: string
  }

  type BaseResponsePageNoteSpaceVO_ = {
    code?: number
    data?: PageNoteSpaceVO_
    message?: string
  }

  type BaseResponsePageNoteVO_ = {
    code?: number
    data?: PageNoteVO_
    message?: string
  }

  type BaseResponsePageUserAvatarReview_ = {
    code?: number
    data?: PageUserAvatarReview_
    message?: string
  }

  type BaseResponsePageUserVO_ = {
    code?: number
    data?: PageUserVO_
    message?: string
  }

  type BaseResponsePageWallpaperVO_ = {
    code?: number
    data?: PageWallpaperVO_
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

  type BaseResponseWallpaperVO_ = {
    code?: number
    data?: WallpaperVO
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

  type CompressionSummaryStats = {
    averageCompressionRatio?: number
    averageProcessingTime?: number
    compressionSuccessRate?: number
    formattedTotalSpaceSaved?: string
    totalCompressions?: number
    totalProcessingTime?: number
    totalSpaceSaved?: number
    totalUploads?: number
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

  type getNoteSpaceVOByIdUsingGETParams = {
    /** id */
    id?: number
  }

  type getNoteVOByIdUsingGETParams = {
    /** id */
    id?: number
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

  type getWallpaperByIdUsingGETParams = {
    /** id */
    id: number
  }

  type incrementDownloadCountUsingPOSTParams = {
    /** id */
    id: number
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

  type likeWallpaperUsingPOSTParams = {
    /** id */
    id: number
  }

  type listAvatarReviewsUsingGETParams = {
    /** current */
    current?: number
    /** pageSize */
    pageSize?: number
    /** status */
    status?: number
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

  type Note = {
    attachmentSize?: number
    attachments?: string
    auditStatus?: number
    auditTime?: string
    auditUserId?: number
    category?: string
    content?: string
    contentSummary?: string
    createTime?: string
    editTime?: string
    favouriteCount?: number
    id?: number
    isDelete?: number
    likeCount?: number
    publishTime?: string
    spaceId?: number
    status?: number
    tags?: string
    title?: string
    updateTime?: string
    userId?: number
    viewCount?: number
  }

  type NoteAddRequest = {
    attachments?: string
    category?: string
    content?: string
    spaceId?: number
    status?: number
    tags?: string
    title?: string
  }

  type NoteEditRequest = {
    attachments?: string
    category?: string
    content?: string
    id?: number
    status?: number
    tags?: string
    title?: string
  }

  type NoteQueryRequest = {
    category?: string
    current?: number
    id?: number
    pageSize?: number
    searchText?: string
    sortField?: string
    sortOrder?: string
    spaceId?: number
    status?: number
    title?: string
    userId?: number
  }

  type NoteSpace = {
    createTime?: string
    editTime?: string
    id?: number
    isDelete?: number
    maxNoteCount?: number
    maxSize?: number
    spaceLevel?: number
    spaceName?: string
    totalNoteCount?: number
    totalSize?: number
    updateTime?: string
    userId?: number
  }

  type NoteSpaceAddRequest = {
    spaceLevel?: number
    spaceName?: string
  }

  type NoteSpaceEditRequest = {
    id?: number
    spaceName?: string
  }

  type NoteSpaceLevel = {
    maxCount?: number
    maxSize?: number
    text?: string
    value?: number
  }

  type NoteSpaceQueryRequest = {
    current?: number
    id?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    spaceLevel?: number
    spaceName?: string
    userId?: number
  }

  type NoteSpaceUpdateRequest = {
    id?: number
    maxNoteCount?: number
    maxSize?: number
    spaceLevel?: number
    spaceName?: string
  }

  type NoteSpaceVO = {
    createTime?: string
    editTime?: string
    id?: number
    maxNoteCount?: number
    maxSize?: number
    spaceLevel?: number
    spaceName?: string
    totalNoteCount?: number
    totalSize?: number
    updateTime?: string
    user?: UserVO
    userId?: number
  }

  type NoteVO = {
    attachmentSize?: number
    attachments?: string
    auditStatus?: number
    category?: string
    content?: string
    contentSummary?: string
    createTime?: string
    editTime?: string
    favouriteCount?: number
    id?: number
    likeCount?: number
    publishTime?: string
    spaceId?: number
    status?: number
    tags?: string
    title?: string
    updateTime?: string
    user?: UserVO
    userId?: number
    viewCount?: number
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

  type PageNote_ = {
    current?: number
    pages?: number
    records?: Note[]
    size?: number
    total?: number
  }

  type PageNoteSpace_ = {
    current?: number
    pages?: number
    records?: NoteSpace[]
    size?: number
    total?: number
  }

  type PageNoteSpaceVO_ = {
    current?: number
    pages?: number
    records?: NoteSpaceVO[]
    size?: number
    total?: number
  }

  type PageNoteVO_ = {
    current?: number
    pages?: number
    records?: NoteVO[]
    size?: number
    total?: number
  }

  type PageRequest = {
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
  }

  type PageUserAvatarReview_ = {
    current?: number
    pages?: number
    records?: UserAvatarReview[]
    size?: number
    total?: number
  }

  type PageUserVO_ = {
    current?: number
    pages?: number
    records?: UserVO[]
    size?: number
    total?: number
  }

  type PageWallpaperVO_ = {
    current?: number
    pages?: number
    records?: WallpaperVO[]
    size?: number
    total?: number
  }

  type PictureCompressionStats = {
    compressedFormat?: string
    compressedSize?: number
    compressionRatio?: number
    formattedCompressedSize?: string
    formattedCompressionRatio?: string
    formattedOriginalSize?: string
    formattedSavedSpace?: string
    formattedThumbnailSize?: string
    hasThumbnail?: boolean
    originalFormat?: string
    originalSize?: number
    processingTime?: number
    savedSpace?: number
    thumbnailSize?: number
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

  type rejectAvatarUsingPOSTParams = {
    /** id */
    id: number
    /** reason */
    reason?: string
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
    compressionStats?: PictureCompressionStats
    etag?: string
    objectKey?: string
    picColor?: string
    picFormat?: string
    picHeight?: number
    picName?: string
    picScale?: number
    picSize?: number
    picWidth?: number
    sha256?: string
    thumbnailKey?: string
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
    userAvatarVersion?: number
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

  type UserAvatarReview = {
    autoReviewedAt?: string
    bucket?: string
    etag?: string
    format?: string
    height?: number
    id?: number
    lastError?: string
    machineScore?: number
    objectKey?: string
    publishedAt?: string
    reason?: string
    reviewedAt?: string
    reviewerId?: number
    riskLabels?: string
    sha256?: string
    size?: number
    status?: number
    submittedAt?: string
    thumbnailKey?: string
    thumbnailUrl?: string
    url?: string
    userId?: number
    width?: number
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

  type WallpaperBatchCrawlRequest = {
    /** Category */
    category?: string
    /** Crawl count */
    count?: number
    /** Name prefix */
    namePrefix?: string
    /** Search text */
    searchText: string
  }

  type WallpaperQueryRequest = {
    /** Search keyword */
    searchText?: string
    /** Wallpaper name */
    name?: string
    /** Introduction/Description */
    introduction?: string
    /** Category */
    category?: string
    /** Tag */
    tag?: string
    /** Tags list */
    tags?: string[]
    /** Picture width */
    picWidth?: number
    /** Picture height */
    picHeight?: number
    /** Picture format */
    picFormat?: string
    /** Start edit time */
    startEditTime?: string
    /** End edit time */
    endEditTime?: string
    /** User ID */
    userId?: number
    /** Status */
    status?: number
    current?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
  }

  type WallpaperBatchEditRequest = {
    /** 壁纸 id 列表 */
    wallpaperIdList?: number[]
    /** 分类 */
    category?: string
    /** 标签 */
    tags?: string[]
    /** 命名规则 */
    nameRule?: string
  }

  type WallpaperVO = {
    /** Category */
    category?: string
    /** Create time */
    createTime?: string
    /** Description */
    description?: string
    /** Download count */
    downloadCount?: number
    /** File size */
    fileSize?: number
    /** Height */
    height?: number
    /** Wallpaper ID */
    id?: number
    /** Like count */
    likeCount?: number
    /** Wallpaper name */
    name?: string
    /** Picture color */
    picColor?: string
    /** Status */
    status?: number
    /** Tags */
    tags?: string[]
    /** Thumbnail URL */
    thumbnailUrl?: string
    /** Wallpaper URL */
    url?: string
    /** User ID */
    userId?: number
    /** Width */
    width?: number
  }

  type SearchPictureByPictureRequest = {
    /** 图片 id */
    pictureId?: number
  }

  type ImageSearchResult = {
    /** 缩略图地址 */
    thumbUrl?: string
    /** 来源地址 */
    fromUrl?: string
  }

  type BaseResponseListImageSearchResult_ = {
    code?: number
    data?: ImageSearchResult[]
    message?: string
  }

  type SearchWallpaperByColorRequest = {
    /** 目标颜色（十六进制） */
    picColor?: string
    /** 返回数量 */
    limit?: number
  }

  type BaseResponseListWallpaperVO_ = {
    code?: number
    data?: WallpaperVO[]
    message?: string
  }
}
