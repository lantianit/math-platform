<template>
  <div class="post-list">
    <!-- 加载骨架屏 -->
    <div v-if="loading && posts.length === 0" class="skeleton-container">
      <a-card v-for="i in skeletonCount" :key="i" class="skeleton-card">
        <a-skeleton active :paragraph="{ rows: 3 }" />
      </a-card>
    </div>

    <!-- 帖子列表 -->
    <div v-else-if="posts.length > 0" class="posts-container">
      <PostCard
        v-for="post in posts"
        :key="post.id"
        :post="post"
        :compact="compact"
        :show-actions="showActions"
        @click="handlePostClick"
        @user-click="handleUserClick"
        @tag-click="handleTagClick"
        @share="handleShare"
        @report="handleReport"
      />

      <!-- 加载更多 -->
      <div v-if="hasMore" class="load-more-container">
        <a-spin v-if="loading" size="large">
          <div class="loading-text">加载中...</div>
        </a-spin>
        <a-button
          v-else-if="showLoadMoreButton"
          type="primary"
          ghost
          size="large"
          @click="handleLoadMore"
          class="load-more-button"
        >
          加载更多
        </a-button>
      </div>

      <!-- 没有更多数据 -->
      <div v-else-if="showNoMoreTip" class="no-more-container">
        <a-divider>
          <span class="no-more-text">没有更多内容了</span>
        </a-divider>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else class="empty-container">
      <a-empty
        :image="emptyImage"
        :description="emptyDescription"
        class="empty-state"
      >
        <template v-if="showRefreshButton" #extra>
          <a-button type="primary" @click="handleRefresh">
            刷新
          </a-button>
        </template>
      </a-empty>
    </div>

    <!-- 回到顶部 -->
    <a-back-top
      v-if="showBackTop"
      :visibility-height="300"
      :style="{ right: '50px', bottom: '50px' }"
    >
      <div class="back-top-button">
        <UpOutlined />
      </div>
    </a-back-top>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onUnmounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { UpOutlined } from '@ant-design/icons-vue'
import { useIntersectionObserver } from '@vueuse/core'
import PostCard from './PostCard.vue'

// 定义组件属性
interface Props {
  posts: API.PostVO[] | API.Post[]
  loading?: boolean
  hasMore?: boolean
  compact?: boolean
  showActions?: boolean
  showLoadMoreButton?: boolean
  showNoMoreTip?: boolean
  showRefreshButton?: boolean
  showBackTop?: boolean
  emptyImage?: string
  emptyDescription?: string
  skeletonCount?: number
  infiniteScroll?: boolean
  scrollContainer?: string | HTMLElement
}

const props = withDefaults(defineProps<Props>(), {
  loading: false,
  hasMore: true,
  compact: false,
  showActions: true,
  showLoadMoreButton: true,
  showNoMoreTip: true,
  showRefreshButton: true,
  showBackTop: true,
  emptyDescription: '暂无帖子',
  skeletonCount: 5,
  infiniteScroll: true,
  scrollContainer: 'window'
})

// 定义事件
const emit = defineEmits<{
  loadMore: []
  refresh: []
  postClick: [post: API.PostVO | API.Post]
  userClick: [userId: number]
  tagClick: [tag: string]
  share: [post: API.PostVO | API.Post]
  report: [post: API.PostVO | API.Post]
}>()

const router = useRouter()

// 响应式状态
const loadMoreTarget = ref<HTMLElement>()
const isIntersecting = ref(false)

// 无限滚动设置
const { stop } = useIntersectionObserver(
  loadMoreTarget,
  ([{ isIntersecting: intersecting }]) => {
    isIntersecting.value = intersecting
    if (intersecting && props.infiniteScroll && props.hasMore && !props.loading) {
      handleLoadMore()
    }
  },
  {
    rootMargin: '100px',
    threshold: 0.1
  }
)

// 计算属性
const shouldShowLoadMore = computed(() => {
  return props.hasMore && props.posts.length > 0
})

// 事件处理
const handleLoadMore = () => {
  emit('loadMore')
}

const handleRefresh = () => {
  emit('refresh')
}

const handlePostClick = (post: API.PostVO | API.Post) => {
  emit('postClick', post)
}

const handleUserClick = (userId: number) => {
  emit('userClick', userId)
}

const handleTagClick = (tag: string) => {
  emit('tagClick', tag)
}

const handleShare = (post: API.PostVO | API.Post) => {
  emit('share', post)
}

const handleReport = (post: API.PostVO | API.Post) => {
  emit('report', post)
}

// 监听滚动位置（用于显示回到顶部按钮）
const scrollY = ref(0)
const updateScrollY = () => {
  scrollY.value = window.scrollY
}

onMounted(() => {
  if (props.showBackTop) {
    window.addEventListener('scroll', updateScrollY, { passive: true })
  }
})

onUnmounted(() => {
  stop()
  if (props.showBackTop) {
    window.removeEventListener('scroll', updateScrollY)
  }
})

// 监听props变化
watch(
  () => props.infiniteScroll,
  (newValue) => {
    if (!newValue) {
      stop()
    }
  }
)
</script>

<style scoped lang="scss">
.post-list {
  width: 100%;
  min-height: 400px;
}

.skeleton-container {
  .skeleton-card {
    margin-bottom: 16px;
    border-radius: 12px;
  }
}

.posts-container {
  width: 100%;
}

.load-more-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 24px 0;
  margin-top: 16px;

  .loading-text {
    margin-top: 8px;
    color: #666;
    font-size: 14px;
  }

  .load-more-button {
    min-width: 120px;
    height: 40px;
    border-radius: 20px;
    font-weight: 500;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
    }
  }
}

.no-more-container {
  margin: 32px 0 16px;

  .no-more-text {
    color: #8c8c8c;
    font-size: 13px;
  }

  :deep(.ant-divider-inner-text) {
    padding: 0 16px;
  }
}

.empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  padding: 40px 20px;

  .empty-state {
    :deep(.ant-empty-description) {
      color: #8c8c8c;
      margin-top: 16px;
    }
  }
}

.back-top-button {
  width: 40px;
  height: 40px;
  background: #1890ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.3);

  &:hover {
    background: #40a9ff;
    transform: scale(1.1);
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.5);
  }
}

// 紧凑模式样式
.compact {
  .skeleton-card {
    margin-bottom: 8px;
  }

  .load-more-container {
    padding: 16px 0;
    margin-top: 8px;
  }

  .no-more-container {
    margin: 20px 0 8px;
  }
}

// 响应式样式
@media (max-width: 768px) {
  .post-list {
    min-height: 300px;
  }

  .load-more-container {
    padding: 16px 0;

    .load-more-button {
      min-width: 100px;
      height: 36px;
      font-size: 14px;
    }
  }

  .empty-container {
    min-height: 250px;
    padding: 30px 15px;
  }

  .back-top-button {
    width: 36px;
    height: 36px;
    font-size: 14px;
  }

  :deep(.ant-back-top) {
    right: 20px !important;
    bottom: 20px !important;
  }
}

// 暗色主题已移除，统一使用白色模式

// 加载动画
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.posts-container {
  animation: fadeIn 0.5s ease-out;
}

// 无限滚动触发器（隐藏元素）
.load-more-trigger {
  height: 1px;
  width: 100%;
  visibility: hidden;
}
</style>
