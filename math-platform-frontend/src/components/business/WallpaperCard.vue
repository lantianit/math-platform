<template>
  <div
    class="wallpaper-card"
    :class="{ 'loading': wallpaper.loading }"
    @click="handleCardClick"
  >
    <!-- 图片区域 -->
    <div class="image-section">
      <img
        :src="wallpaper.thumbnailUrl || wallpaper.url"
        :alt="wallpaper.name"
        class="wallpaper-image"
        @error="handleImageError"
      />
      
      <div class="image-overlay">
        <div class="image-actions">
          <a-button
            type="primary"
            ghost
            size="small"
            @click.stop="handleQuickDownload"
            class="action-btn"
          >
            <DownloadOutlined />
          </a-button>
          <a-button
            type="primary"
            ghost
            size="small"
            @click.stop="handleQuickLike"
            class="action-btn"
            :class="{ 'liked': isLiked }"
          >
            <HeartFilled v-if="isLiked" />
            <HeartOutlined v-else />
          </a-button>
          <a-button
            type="primary"
            ghost
            size="small"
            @click.stop="handlePreview"
            class="action-btn"
          >
            <EyeOutlined />
          </a-button>
        </div>
      </div>
      
      <!-- 质量标识 -->
      <div v-if="qualityBadge" class="quality-badge">
        <a-tag :color="qualityBadge.color" class="quality-tag">
          {{ qualityBadge.text }}
        </a-tag>
      </div>
    </div>

    <!-- 内容区域 -->
    <div class="content-section">
      <!-- 标题和描述 -->
      <div class="title-section">
        <h3 class="wallpaper-title" :title="wallpaper.name">
          {{ wallpaper.name }}
        </h3>
        <p v-if="wallpaper.description" class="wallpaper-description">
          {{ wallpaper.description }}
        </p>
      </div>

      <!-- 元数据 -->
      <div class="meta-section">
        <div class="meta-primary">
          <a-tag
            v-if="wallpaper.category"
            :color="getCategoryColor(wallpaper.category)"
            class="category-tag"
          >
            {{ wallpaper.category }}
          </a-tag>
          <span class="resolution" v-if="wallpaper.width && wallpaper.height">
            {{ wallpaper.width }}×{{ wallpaper.height }}
          </span>
        </div>
        
        <div class="meta-stats">
          <div class="stat-item">
            <HeartOutlined :class="{ 'text-red-500': isLiked }" />
            <span>{{ formatNumber(wallpaper.likeCount) }}</span>
          </div>
          <div class="stat-item">
            <DownloadOutlined />
            <span>{{ formatNumber(wallpaper.downloadCount) }}</span>
          </div>
        </div>
      </div>

      <!-- 标签 -->
      <div v-if="wallpaper.tags && wallpaper.tags.length" class="tags-section">
        <a-tag
          v-for="tag in displayTags"
          :key="tag"
          size="small"
          class="wallpaper-tag"
        >
          {{ tag }}
        </a-tag>
        <a-tag
          v-if="wallpaper.tags.length > maxDisplayTags"
          size="small"
          class="more-tags"
        >
          +{{ wallpaper.tags.length - maxDisplayTags }}
        </a-tag>
      </div>

      <!-- 操作按钮 -->
      <div class="actions-section">
        <a-button
          type="primary"
          @click.stop="handleDownload"
          :loading="downloadLoading"
          class="primary-action"
        >
          <DownloadOutlined />
          下载
        </a-button>
        <a-button
          @click.stop="handleLike"
          :loading="likeLoading"
          :class="{ 'liked': isLiked }"
          class="secondary-action"
        >
          <HeartFilled v-if="isLiked" />
          <HeartOutlined v-else />
          {{ isLiked ? '已赞' : '点赞' }}
        </a-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import {
  DownloadOutlined,
  HeartOutlined,
  HeartFilled,
  EyeOutlined
} from '@ant-design/icons-vue'

interface Props {
  wallpaper: API.WallpaperVO & { loading?: boolean }
  maxDisplayTags?: number
  showQuality?: boolean
}

interface Emits {
  (e: 'click', wallpaper: API.WallpaperVO): void
  (e: 'download', wallpaper: API.WallpaperVO): void
  (e: 'like', wallpaper: API.WallpaperVO): void
  (e: 'preview', wallpaper: API.WallpaperVO): void
}

const props = withDefaults(defineProps<Props>(), {
  maxDisplayTags: 3,
  showQuality: true,
})

const emit = defineEmits<Emits>()

const downloadLoading = ref(false)
const likeLoading = ref(false)
const isLiked = ref(false)

const displayTags = computed(() => {
  if (!props.wallpaper.tags) return []
  return props.wallpaper.tags.slice(0, props.maxDisplayTags)
})

const qualityBadge = computed(() => {
  if (!props.showQuality || !props.wallpaper.width || !props.wallpaper.height) return null
  
  const pixels = props.wallpaper.width * props.wallpaper.height
  
  if (pixels >= 3840 * 2160) {
    return { text: '4K', color: 'gold' }
  } else if (pixels >= 1920 * 1080) {
    return { text: 'HD', color: 'blue' }
  } else if (pixels >= 1280 * 720) {
    return { text: 'SD', color: 'green' }
  }
  
  return null
})

const getCategoryColor = (category: string) => {
  const colorMap: Record<string, string> = {
    '学习励志': 'blue',
    '奋斗拼搏': 'red',
    '青春梦想': 'purple',
    '书籍文字': 'green',
  }
  return colorMap[category] || 'default'
}

const formatNumber = (num?: number) => {
  if (!num) return '0'
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

const handleCardClick = () => {
  emit('click', props.wallpaper)
}

const handleDownload = async () => {
  downloadLoading.value = true
  try {
    emit('download', props.wallpaper)
  } finally {
    setTimeout(() => {
      downloadLoading.value = false
    }, 1000)
  }
}

const handleLike = async () => {
  likeLoading.value = true
  try {
    emit('like', props.wallpaper)
    isLiked.value = !isLiked.value
  } finally {
    setTimeout(() => {
      likeLoading.value = false
    }, 500)
  }
}

const handleQuickDownload = () => {
  handleDownload()
}

const handleQuickLike = () => {
  handleLike()
}

const handlePreview = () => {
  emit('preview', props.wallpaper)
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OTk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuWbvueJh+WKoOi9veWksei0pTwvdGV4dD48L3N2Zz4='
}
</script>

<style scoped lang="scss">
.wallpaper-card {
  height: fit-content;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #f0f0f0;
  transition: all 0.2s ease;
  cursor: pointer;
  
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transform: translateY(-2px);
  }
  
  &.loading {
    pointer-events: none;
    opacity: 0.7;
  }
}

.image-section {
  position: relative;
  
  .wallpaper-image {
    width: 100%;
    height: 200px;
    object-fit: cover;
    transition: transform 0.3s ease;
  }
  
  .image-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: linear-gradient(to bottom, transparent 0%, rgba(0,0,0,0.7) 100%);
    display: flex;
    align-items: flex-end;
    justify-content: center;
    opacity: 0;
    transition: opacity 0.2s ease;
    padding: 16px;
    
    .image-actions {
      display: flex;
      gap: 8px;
      
      .action-btn {
        width: 32px;
        height: 32px;
        border-radius: 4px;
        background: rgba(255, 255, 255, 0.9);
        border: none;
        color: #595959;
        transition: all 0.2s ease;
        
        &:hover {
          background: white;
          color: #1890ff;
        }
        
        &.liked {
          background: #ff4d4f;
          color: white;
          
          &:hover {
            background: #f5222d;
          }
        }
      }
    }
  }
  
  &:hover .image-overlay {
    opacity: 1;
  }
  
  .quality-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    
    .quality-tag {
      border-radius: 6px;
      font-weight: 600;
      font-size: 11px;
      border: none;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
  }
}

.content-section {
  padding: 16px;
}

.title-section {
  margin-bottom: 12px;
  
  .wallpaper-title {
    font-size: 16px;
    font-weight: 600;
    color: #333;
    margin: 0 0 8px 0;
    line-height: 1.3;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
  
  .wallpaper-description {
    font-size: 14px;
    color: #666;
    margin: 0;
    line-height: 1.4;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
  }
}

.meta-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  
  .meta-primary {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .category-tag {
      font-size: 11px;
      font-weight: 500;
      border-radius: 6px;
    }
    
    .resolution {
      font-size: 11px;
      color: #999;
      background: #f5f5f5;
      padding: 2px 6px;
      border-radius: 4px;
    }
  }
  
  .meta-stats {
    display: flex;
    gap: 12px;
    
    .stat-item {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 12px;
      color: #999;
      
      .anticon {
        font-size: 14px;
      }
    }
  }
}

.tags-section {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 16px;
  
  .wallpaper-tag {
    font-size: 11px;
    border-radius: 4px;
    background: #f5f5f5;
    border: 1px solid #e5e5e5;
    color: #666;
    
    &:hover {
      background: #e6f7ff;
      border-color: #91d5ff;
      color: #1890ff;
    }
  }
  
  .more-tags {
    background: #f0f0f0;
    color: #999;
    font-weight: 500;
  }
}

.actions-section {
  display: flex;
  gap: 8px;
  
  .primary-action {
    flex: 1;
    height: 32px;
    border-radius: 4px;
    font-weight: 400;
    font-size: 14px;
    background: #1890ff;
    border: none;
    
    &:hover {
      background: #40a9ff;
    }
  }
  
  .secondary-action {
    flex: 1;
    height: 32px;
    border-radius: 4px;
    font-weight: 400;
    font-size: 14px;
    border: 1px solid #d9d9d9;
    background: white;
    color: #595959;
    
    &:hover {
      border-color: #40a9ff;
      color: #1890ff;
    }
    
    &.liked {
      background: #ff4d4f;
      border-color: #ff4d4f;
      color: white;
      
      &:hover {
        background: #f5222d;
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 576px) {
  .wallpaper-card {
    .content-section {
      padding: 12px;
    }
    
    .meta-section {
      flex-direction: column;
      align-items: flex-start;
      gap: 8px;
    }
    
    .actions-section {
      .primary-action,
      .secondary-action {
        height: 32px;
        font-size: 14px;
      }
    }
  }
}

/* 移除过度动画 */

/* 加载状态 */
.wallpaper-card.loading {
  .image-section {
    .wallpaper-image {
      background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
      background-size: 200px 100%;
      animation: shimmer 1.5s ease-in-out infinite;
    }
  }
}

@keyframes shimmer {
  0% {
    background-position: -200px 0;
  }
  100% {
    background-position: 200px 0;
  }
}
</style>