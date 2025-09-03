import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { message } from 'ant-design-vue'
import * as postController from '@/api/postController'
import * as feedController from '@/api/feedController'
import * as socialController from '@/api/socialController'

/**
 * 帖子状态管理
 */
export const usePostStore = defineStore('post', () => {
  // 帖子列表
  const posts = ref<API.PostVO[]>([])
  const loading = ref(false)
  const hasMore = ref(true)
  const currentPage = ref(1)
  const pageSize = ref(10)
  
  // 当前信息流类型
  const feedType = ref<'recommend' | 'latest' | 'hot' | 'following'>('recommend')
  
  // 用户交互状态
  const likedPostIds = ref<Set<number>>(new Set())
  const favouritedPostIds = ref<Set<number>>(new Set())
  
  // 搜索相关
  const searchKeyword = ref('')
  const searchResults = ref<API.PostVO[]>([])
  const searchLoading = ref(false)

  /**
   * 获取信息流帖子
   */
  const loadFeedPosts = async (type?: typeof feedType.value, reset = false) => {
    if (loading.value) return
    
    loading.value = true
    
    try {
      if (reset) {
        currentPage.value = 1
        posts.value = []
        hasMore.value = true
      }
      
      if (type) {
        feedType.value = type
      }
      
      const pageRequest = {
        current: currentPage.value,
        pageSize: pageSize.value
      }
      
      let response
      
      switch (feedType.value) {
        case 'recommend':
          response = await feedController.getRecommendPostsUsingPost(pageRequest)
          break
        case 'latest':
          response = await feedController.getLatestPostsUsingPost(pageRequest)
          break
        case 'hot':
          response = await feedController.getHotPostsUsingPost(pageRequest)
          break
        case 'following':
          response = await feedController.getFollowingPostsUsingPost(pageRequest)
          break
      }
      
      if (response?.data?.code === 0 && response.data.data) {
        const newPosts = response.data.data.records || []
        
        if (reset) {
          posts.value = newPosts
        } else {
          posts.value.push(...newPosts)
        }
        
        // 更新分页信息
        hasMore.value = currentPage.value < (response.data.data.pages || 1)
        if (hasMore.value) {
          currentPage.value++
        }
      }
    } catch (error) {
      console.error('加载帖子失败:', error)
      message.error('加载帖子失败，请稍后重试')
    } finally {
      loading.value = false
    }
  }

  /**
   * 获取热门帖子
   */
  const loadHotPosts = async (limit = 10) => {
    try {
      const response = await postController.getHotPostsUsingGet({ limit })
      if (response?.data?.code === 0 && response.data.data) {
        return response.data.data
      }
    } catch (error) {
      console.error('加载热门帖子失败:', error)
    }
    return []
  }

  /**
   * 获取最新帖子
   */
  const loadLatestPosts = async (limit = 10) => {
    try {
      const response = await postController.getLatestPostsUsingGet({ limit })
      if (response?.data?.code === 0 && response.data.data) {
        return response.data.data
      }
    } catch (error) {
      console.error('加载最新帖子失败:', error)
    }
    return []
  }

  /**
   * 获取帖子详情
   */
  const getPostById = async (id: number) => {
    try {
      const response = await postController.getPostByIdUsingGet({ id })
      if (response?.data?.code === 0 && response.data.data) {
        return response.data.data
      }
    } catch (error) {
      console.error('获取帖子详情失败:', error)
      message.error('获取帖子详情失败')
    }
    return null
  }

  /**
   * 点赞/取消点赞帖子
   */
  const togglePostLike = async (postId: number) => {
    try {
      // 乐观更新UI
      const isCurrentlyLiked = likedPostIds.value.has(postId)
      if (isCurrentlyLiked) {
        likedPostIds.value.delete(postId)
      } else {
        likedPostIds.value.add(postId)
      }
      
      // 更新帖子的点赞数
      const post = posts.value.find(p => p.id === postId)
      if (post) {
        post.likeCount = (post.likeCount || 0) + (isCurrentlyLiked ? -1 : 1)
      }
      
      const response = await postController.togglePostLikeUsingPost({ postId })
      
      if (response?.data?.code !== 0) {
        // 请求失败，回滚UI状态
        if (isCurrentlyLiked) {
          likedPostIds.value.add(postId)
        } else {
          likedPostIds.value.delete(postId)
        }
        
        if (post) {
          post.likeCount = (post.likeCount || 0) + (isCurrentlyLiked ? 1 : -1)
        }
        
        message.error('操作失败，请稍后重试')
      }
    } catch (error) {
      console.error('点赞操作失败:', error)
      message.error('操作失败，请稍后重试')
    }
  }

  /**
   * 收藏/取消收藏帖子
   */
  const togglePostFavourite = async (postId: number) => {
    try {
      // 乐观更新UI
      const isCurrentlyFavourited = favouritedPostIds.value.has(postId)
      if (isCurrentlyFavourited) {
        favouritedPostIds.value.delete(postId)
      } else {
        favouritedPostIds.value.add(postId)
      }
      
      // 更新帖子的收藏数
      const post = posts.value.find(p => p.id === postId)
      if (post) {
        post.favouriteCount = (post.favouriteCount || 0) + (isCurrentlyFavourited ? -1 : 1)
      }
      
      const response = await postController.togglePostFavouriteUsingPost({ postId })
      
      if (response?.data?.code !== 0) {
        // 请求失败，回滚UI状态
        if (isCurrentlyFavourited) {
          favouritedPostIds.value.add(postId)
        } else {
          favouritedPostIds.value.delete(postId)
        }
        
        if (post) {
          post.favouriteCount = (post.favouriteCount || 0) + (isCurrentlyFavourited ? 1 : -1)
        }
        
        message.error('操作失败，请稍后重试')
      }
    } catch (error) {
      console.error('收藏操作失败:', error)
      message.error('操作失败，请稍后重试')
    }
  }

  /**
   * 加载用户的点赞和收藏状态
   */
  const loadUserInteractionStatus = async () => {
    try {
      // 加载点赞的帖子ID列表
      const likedResponse = await socialController.getLikedPostIdsUsingGet()
      if (likedResponse?.data?.code === 0 && likedResponse.data.data) {
        likedPostIds.value = new Set(likedResponse.data.data)
      }
      
      // 加载收藏的帖子ID列表
      const favouritedResponse = await socialController.getFavouritePostIdsUsingGet()
      if (favouritedResponse?.data?.code === 0 && favouritedResponse.data.data) {
        favouritedPostIds.value = new Set(favouritedResponse.data.data)
      }
    } catch (error) {
      console.error('加载用户交互状态失败:', error)
    }
  }

  /**
   * 搜索帖子 (暂时注释掉，等待searchController实现)
   */
  const searchPosts = async (keyword: string, page = 1) => {
    if (!keyword.trim()) return
    
    searchLoading.value = true
    searchKeyword.value = keyword
    
    try {
      // TODO: 实现搜索API调用
      // const response = await searchController.searchPostsUsingPost({
      //   keyword: keyword.trim(),
      //   current: page,
      //   pageSize: pageSize.value
      // })
      
      // 临时返回空结果
      if (page === 1) {
        searchResults.value = []
      }
      
      message.info('搜索功能开发中...')
    } catch (error) {
      console.error('搜索失败:', error)
      message.error('搜索失败，请稍后重试')
    } finally {
      searchLoading.value = false
    }
  }

  // 计算属性
  const postsCount = computed(() => posts.value.length)
  const hasLikedPost = (postId: number) => likedPostIds.value.has(postId)
  const hasFavouritedPost = (postId: number) => favouritedPostIds.value.has(postId)

  // 重置状态
  const reset = () => {
    posts.value = []
    currentPage.value = 1
    hasMore.value = true
    loading.value = false
    searchResults.value = []
    searchKeyword.value = ''
    searchLoading.value = false
  }

  return {
    // 状态
    posts,
    loading,
    hasMore,
    currentPage,
    pageSize,
    feedType,
    likedPostIds,
    favouritedPostIds,
    searchKeyword,
    searchResults,
    searchLoading,
    
    // 方法
    loadFeedPosts,
    loadHotPosts,
    loadLatestPosts,
    getPostById,
    togglePostLike,
    togglePostFavourite,
    loadUserInteractionStatus,
    searchPosts,
    
    // 计算属性
    postsCount,
    hasLikedPost,
    hasFavouritedPost,
    
    // 工具方法
    reset
  }
})
