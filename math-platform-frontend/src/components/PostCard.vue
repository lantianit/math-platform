<template>
  <a-card 
    class="post-card" 
    :hoverable="true"
    :loading="loading"
    @click="handleCardClick"
  >
    <!-- 帖子头部 -->
    <template #title>
      <a-row justify="space-between" align="middle">
        <a-col>
          <a-space align="center">
            <a-avatar 
              :src="(post as any).userAvatar || (post as any).user?.userAvatar" 
              :size="32"
              @click.stop="handleUserClick"
              style="cursor: pointer;"
            >
              {{ ((post as any).userName || (post as any).user?.userName || '匿名')?.charAt(0) }}
            </a-avatar>
            <div>
              <div 
                class="user-name" 
                @click.stop="handleUserClick"
              >
                {{ (post as any).userName || (post as any).user?.userName || '匿名用户' }}
              </div>
              <div class="post-meta">
                <a-tag 
                  :color="getCategoryColor(post.category)" 
                  size="small"
                >
                  {{ getCategoryName(post.category) }}
                </a-tag>
                <span class="post-time">{{ formatTime(post.createTime || '') }}</span>
              </div>
            </div>
          </a-space>
        </a-col>
        <a-col>
          <a-dropdown :trigger="['click']" @click.stop>
            <a-button type="text" size="small">
              <MoreOutlined />
            </a-button>
            <template #overlay>
              <a-menu>
                <a-menu-item key="share" @click="handleShare">
                  <ShareAltOutlined />
                  分享
                </a-menu-item>
                <a-menu-item key="report" @click="handleReport">
                  <ExclamationCircleOutlined />
                  举报
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </a-col>
      </a-row>
    </template>
    
    <!-- 帖子内容 -->
    <div class="post-content" @click="handleContentClick">
      <a-typography-title 
        :level="4" 
        class="post-title"
        :ellipsis="{ rows: 2, tooltip: true }"
      >
        {{ post.title }}
      </a-typography-title>
      
      <a-typography-paragraph 
        class="post-summary"
        :ellipsis="{ rows: 3, expandable: false }"
        v-if="(post as any).contentSummary || (post as any).content"
      >
        {{ (post as any).contentSummary || (post as any).content?.substring(0, 200) || '暂无内容' }}
      </a-typography-paragraph>
      
      <!-- 帖子图片（优化：列表使用缩略图，点击查看原图） -->
      <div v-if="postImages.length > 0" class="post-images">
        <a-image-preview-group>
          <div class="image-grid" :class="`grid-${Math.min(postImages.length, 3)}`">
            <a-image
              v-for="(image, index) in postImages.slice(0, 9)"
              :key="index"
              :src="getThumbnailUrl(image)"
              :preview="{ src: image }"
              class="post-image"
              loading="lazy"
              @click.stop
            />
            <div 
              v-if="postImages.length > 9" 
              class="more-images-overlay"
              @click.stop="handleShowAllImages"
            >
              +{{ postImages.length - 9 }}
            </div>
          </div>
        </a-image-preview-group>
      </div>
      
      <!-- 帖子标签 -->
      <div v-if="postTags.length > 0" class="post-tags">
        <a-tag 
          v-for="tag in postTags" 
          :key="tag" 
          color="blue"
          size="small"
          @click.stop="handleTagClick(tag)"
        >
          {{ tag }}
        </a-tag>
      </div>
    </div>
    
    <!-- 帖子操作栏 -->
    <template #actions>
      <a-tooltip :title="isLiked ? '取消点赞' : '点赞'">
        <a-button 
          type="text" 
          size="small"
          :loading="likeLoading"
          @click.stop="handleToggleLike"
          class="action-button"
          :class="{ 'action-active': isLiked }"
        >
          <template #icon>
            <LikeOutlined v-if="!isLiked" />
            <LikeFilled v-else />
          </template>
          {{ formatCount(post.likeCount) }}
        </a-button>
      </a-tooltip>
      
      <a-tooltip :title="isFavourited ? '取消收藏' : '收藏'">
        <a-button 
          type="text" 
          size="small"
          :loading="favouriteLoading"
          @click.stop="handleToggleFavourite"
          class="action-button"
          :class="{ 'action-active': isFavourited }"
        >
          <template #icon>
            <StarOutlined v-if="!isFavourited" />
            <StarFilled v-else />
          </template>
          {{ formatCount(post.favouriteCount) }}
        </a-button>
      </a-tooltip>
      
      <a-tooltip title="评论">
        <a-button 
          type="text" 
          size="small"
          @click.stop="handleCommentClick"
          class="action-button"
        >
          <template #icon>
            <CommentOutlined />
          </template>
          {{ formatCount(post.commentCount) }}
        </a-button>
      </a-tooltip>
      
      <a-tooltip title="浏览量">
        <a-button 
          type="text" 
          size="small"
          class="action-button view-count"
        >
          <template #icon>
            <EyeOutlined />
          </template>
          {{ formatCount(post.viewCount) }}
        </a-button>
      </a-tooltip>
    </template>
  </a-card>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'
import {
  LikeOutlined,
  LikeFilled,
  StarOutlined,
  StarFilled,
  CommentOutlined,
  EyeOutlined,
  ShareAltOutlined,
  ExclamationCircleOutlined,
  MoreOutlined
} from '@ant-design/icons-vue'
import { usePostStore } from '@/stores/usePostStore'
import { getCategoryColor, getCategoryName } from '@/constants/category'

// 配置dayjs
dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

// 定义组件属性
interface Props {
  post: API.PostVO | API.Post
  loading?: boolean
  showActions?: boolean
  compact?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  showActions: true,
  compact: false
})

// 定义事件
const emit = defineEmits<{
  click: [post: API.PostVO | API.Post]
  userClick: [userId: number]
  tagClick: [tag: string]
  share: [post: API.PostVO | API.Post]
  report: [post: API.PostVO | API.Post]
}>()

const router = useRouter()
const postStore = usePostStore()

// 响应式状态
const likeLoading = ref(false)
const favouriteLoading = ref(false)

// 计算属性
const postImages = computed(() => {
  const images: any = (props.post as any).images
  if (!images) return []
  if (Array.isArray(images)) return images
  try {
    const arr = JSON.parse(images as string)
    return Array.isArray(arr) ? arr : String(images).split(',').filter(Boolean)
  } catch {
    return String(images).split(',').filter(Boolean)
  }
})

const postTags = computed(() => {
  const tags: any = (props.post as any).tags
  if (!tags) return []
  if (Array.isArray(tags)) return tags
  try {
    const arr = JSON.parse(tags as string)
    return Array.isArray(arr) ? arr : String(tags).split(',').filter(Boolean)
  } catch {
    return String(tags).split(',').filter(Boolean)
  }
})

const isLiked = computed(() => postStore.hasLikedPost(props.post.id || 0))
const isFavourited = computed(() => postStore.hasFavouritedPost(props.post.id || 0))

// 方法
const formatTime = (time: string) => {
  return dayjs(time).fromNow()
}

const formatCount = (count?: number) => {
  if (!count) return '0'
  if (count < 1000) return count.toString()
  if (count < 10000) return `${(count / 1000).toFixed(1)}k`
  return `${(count / 10000).toFixed(1)}w`
}

/**
 * 获取缩略图 URL（如果存在的话）
 * 优化策略：列表页使用缩略图加快加载速度，点击预览时查看原图
 * 
 * @param imageUrl 原图URL
 * @returns 缩略图URL（如果存在）或原图URL
 */
const getThumbnailUrl = (imageUrl: string): string => {
  if (!imageUrl) return imageUrl
  
  // 如果已经是缩略图，直接返回
  if (imageUrl.includes('_thumbnail')) {
    return imageUrl
  }
  
  // 尝试构造缩略图URL
  // 例如：photo.webp -> photo_thumbnail.webp
  const lastDotIndex = imageUrl.lastIndexOf('.')
  if (lastDotIndex > 0) {
    const thumbnailUrl = imageUrl.substring(0, lastDotIndex) + 
                         '_thumbnail' + 
                         imageUrl.substring(lastDotIndex)
    // 返回缩略图URL，如果缩略图不存在，img标签的onerror会回退到原图
    return thumbnailUrl
  }
  
  // 无法构造缩略图URL，返回原图
  return imageUrl
}

// 使用统一分类工具函数

// 使用统一分类工具函数

const handleCardClick = () => {
  emit('click', props.post)
  router.push(`/post/${props.post.id}`)
}

const handleContentClick = () => {
  handleCardClick()
}

const handleUserClick = () => {
  const userId = props.post.userId || (props.post as any).user?.id
  if (userId) {
    emit('userClick', userId)
    router.push(`/user/${userId}`)
  }
}

const handleTagClick = (tag: string) => {
  emit('tagClick', tag)
  router.push(`/search?tag=${encodeURIComponent(tag)}`)
}

const handleToggleLike = async () => {
  if (likeLoading.value) return
  
  likeLoading.value = true
  try {
    await postStore.togglePostLike(props.post.id || 0)
  } catch (error) {
    console.error('点赞操作失败:', error)
  } finally {
    likeLoading.value = false
  }
}

const handleToggleFavourite = async () => {
  if (favouriteLoading.value) return
  
  favouriteLoading.value = true
  try {
    await postStore.togglePostFavourite(props.post.id || 0)
  } catch (error) {
    console.error('收藏操作失败:', error)
  } finally {
    favouriteLoading.value = false
  }
}

const handleCommentClick = () => {
  router.push(`/post/${props.post.id}#comments`)
}

const handleShare = () => {
  emit('share', props.post)
  // 复制链接到剪贴板
  const url = `${window.location.origin}/post/${props.post.id}`
  navigator.clipboard.writeText(url).then(() => {
    message.success('链接已复制到剪贴板')
  }).catch(() => {
    message.error('复制失败')
  })
}

const handleReport = () => {
  emit('report', props.post)
  message.info('举报功能开发中...')
}

const handleShowAllImages = () => {
  // 显示所有图片的预览
  message.info('查看更多图片功能开发中...')
}
</script>

<style scoped lang="scss">
.post-card {
  margin-bottom: 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transform: translateY(-2px);
  }

  :deep(.ant-card-head) {
    border-bottom: 1px solid #f0f0f0;
    min-height: 60px;
  }

  :deep(.ant-card-body) {
    padding: 16px 20px;
  }

  :deep(.ant-card-actions) {
    background: #fafafa;
    border-top: 1px solid #f0f0f0;
    
    li {
      margin: 8px 0;
    }
  }
}

.user-name {
  font-weight: 500;
  color: #262626;
  cursor: pointer;
  font-size: 14px;
  
  &:hover {
    color: #1890ff;
  }
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 2px;
}

.post-time {
  font-size: 12px;
  color: #8c8c8c;
}

.post-content {
  .post-title {
    margin-bottom: 8px !important;
    color: #262626;
    font-weight: 500;
    line-height: 1.4;
    cursor: pointer;

    &:hover {
      color: #1890ff;
    }
  }

  .post-summary {
    color: #595959;
    margin-bottom: 12px !important;
    line-height: 1.6;
  }
}

.post-images {
  margin: 12px 0;

  .image-grid {
    display: grid;
    gap: 8px;
    border-radius: 8px;
    overflow: hidden;
    position: relative;

    &.grid-1 {
      grid-template-columns: 1fr;
      max-width: 400px;
    }

    &.grid-2 {
      grid-template-columns: 1fr 1fr;
      max-width: 400px;
    }

    &.grid-3 {
      grid-template-columns: repeat(3, 1fr);
      max-width: 300px;
    }

    .post-image {
      width: 100%;
      height: 120px;
      object-fit: cover;
      border-radius: 6px;
      cursor: pointer;
    }

    .more-images-overlay {
      position: absolute;
      top: 0;
      right: 0;
      width: 100%;
      height: 100%;
      background: rgba(0, 0, 0, 0.6);
      color: white;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
      font-weight: 500;
      cursor: pointer;
      border-radius: 6px;
    }
  }
}

.post-tags {
  margin-top: 12px;
  
  .ant-tag {
    margin-bottom: 4px;
    cursor: pointer;
    
    &:hover {
      opacity: 0.8;
    }
  }
}

.action-button {
  color: #8c8c8c;
  transition: all 0.2s ease;
  
  &:hover {
    color: #1890ff;
  }
  
  &.action-active {
    color: #1890ff;
  }
  
  &.view-count {
    cursor: default;
    
    &:hover {
      color: #8c8c8c;
    }
  }

  .anticon {
    margin-right: 4px;
  }
}

// 紧凑模式样式
.compact {
  .post-card {
    margin-bottom: 8px;
    
    :deep(.ant-card-body) {
      padding: 12px 16px;
    }
    
    .post-title {
      font-size: 14px;
    }
    
    .post-summary {
      font-size: 13px;
    }
  }
}

// 响应式样式
@media (max-width: 768px) {
  .post-card {
    margin-bottom: 12px;
    border-radius: 8px;
    
    :deep(.ant-card-head) {
      padding: 0 16px;
      min-height: 50px;
    }
    
    :deep(.ant-card-body) {
      padding: 12px 16px;
    }
    
    .post-images .image-grid {
      max-width: 100%;
      
      .post-image {
        height: 100px;
      }
    }
  }
  
  .user-name {
    font-size: 13px;
  }
  
  .post-title {
    font-size: 15px !important;
  }
  
  .post-summary {
    font-size: 13px;
  }
}
</style>
