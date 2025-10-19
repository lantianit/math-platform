<template>
  <a-modal
    v-model:open="visible"
    :title="wallpaper?.name"
    :width="modalWidth"
    :centered="true"
    :destroyOnClose="true"
    @cancel="handleClose"
  >
    <div v-if="wallpaper" class="wallpaper-detail">
      <!-- 图片展示区域 -->
      <div class="image-container">
        <img
          :src="wallpaper.url"
          :alt="wallpaper.name"
          class="detail-image"
          @load="handleImageLoad"
          @error="handleImageError"
        />
        
        <!-- 图片加载状态 -->
        <div v-if="imageLoading" class="image-loading">
          <a-spin size="large" />
          <p class="loading-text">正在加载高清图片...</p>
        </div>
      </div>

      <!-- 详情信息区域 -->
      <div class="detail-info">
        <!-- 基本信息 -->
        <div class="info-section">
          <h3 class="section-title">基本信息</h3>
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">分类</span>
              <a-tag :color="getCategoryColor(wallpaper.category)">
                {{ wallpaper.category }}
              </a-tag>
            </div>
            <div class="info-item">
              <span class="info-label">尺寸</span>
              <span class="info-value">{{ wallpaper.width }}×{{ wallpaper.height }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">大小</span>
              <span class="info-value">{{ formatFileSize(wallpaper.fileSize) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">下载</span>
              <span class="info-value">{{ wallpaper.downloadCount || 0 }} 次</span>
            </div>
            <div class="info-item">
              <span class="info-label">点赞</span>
              <span class="info-value">{{ wallpaper.likeCount || 0 }} 次</span>
            </div>
          </div>
        </div>

        <!-- 标签区域 -->
        <div v-if="wallpaper.tags && wallpaper.tags.length" class="tags-section">
          <h3 class="section-title">相关标签</h3>
          <div class="tags-list">
            <a-tag
              v-for="tag in wallpaper.tags"
              :key="tag"
              class="detail-tag"
              @click="handleTagClick(tag)"
            >
              {{ tag }}
            </a-tag>
          </div>
        </div>

        <!-- 描述区域 -->
        <div v-if="wallpaper.description" class="description-section">
          <h3 class="section-title">描述</h3>
          <p class="description-text">{{ wallpaper.description }}</p>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="action-footer">
        <a-button
          type="primary"
          size="large"
          @click="handleDownload"
          :loading="downloadLoading"
          class="primary-action"
        >
          <DownloadOutlined />
          下载壁纸
        </a-button>
        <a-button
          size="large"
          @click="handleLike"
          :loading="likeLoading"
          :class="{ 'liked': isLiked }"
          class="secondary-action"
        >
          <HeartFilled v-if="isLiked" />
          <HeartOutlined v-else />
          {{ isLiked ? '已点赞' : '点赞' }}
        </a-button>
        <a-button @click="handleShare" class="share-btn">
          <ShareAltOutlined />
          分享
        </a-button>
      </div>
    </template>
  </a-modal>

  <!-- 分享弹窗 -->
  <ShareModal 
    ref="shareModalRef" 
    :title="props.wallpaper?.name || '分享壁纸'" 
    :link="shareLink" 
  />
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { message } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  HeartOutlined,
  HeartFilled,
  DownloadOutlined,
  ShareAltOutlined
} from '@ant-design/icons-vue'
import ShareModal from '@/components/ShareModal.vue'

interface Props {
  open: boolean
  wallpaper: API.WallpaperVO | null
}

interface Emits {
  (e: 'update:open', value: boolean): void
  (e: 'download', wallpaper: API.WallpaperVO): void
  (e: 'like', wallpaper: API.WallpaperVO): void
  (e: 'close'): void
}

const props = defineProps<Props>()
const emit = defineEmits<Emits>()

const visible = computed({
  get: () => props.open,
  set: (value) => emit('update:open', value)
})

const imageLoading = ref(true)
const downloadLoading = ref(false)
const likeLoading = ref(false)
const isLiked = ref(false)

// 分享相关
const shareModalRef = ref()
const shareLink = ref<string>('')

const modalWidth = computed(() => {
  if (!props.wallpaper?.width || !props.wallpaper?.height) return '80%'
  
  const aspectRatio = props.wallpaper.width / props.wallpaper.height
  const maxWidth = Math.min(window.innerWidth * 0.9, 1200)
  const maxHeight = window.innerHeight * 0.8
  
  if (aspectRatio > 1) {
    return Math.min(maxWidth, maxHeight * aspectRatio) + 'px'
  } else {
    return Math.min(maxWidth, 800) + 'px'
  }
})

const getCategoryColor = (category?: string) => {
  const colorMap: Record<string, string> = {
    '学习励志': 'blue',
    '奋斗拼搏': 'red',
    '青春梦想': 'purple',
    '书籍文字': 'green',
  }
  return colorMap[category || ''] || 'default'
}

const formatFileSize = (size?: number) => {
  if (!size) return '未知'
  
  const units = ['B', 'KB', 'MB', 'GB']
  let index = 0
  let value = size
  
  while (value >= 1024 && index < units.length - 1) {
    value /= 1024
    index++
  }
  
  return `${value.toFixed(1)} ${units[index]}`
}

const handleClose = () => {
  visible.value = false
  emit('close')
}

const handleImageLoad = () => {
  imageLoading.value = false
}

const handleImageError = () => {
  imageLoading.value = false
  message.error('图片加载失败')
}

const handleDownload = () => {
  if (!props.wallpaper) return
  
  downloadLoading.value = true
  emit('download', props.wallpaper)
  
  setTimeout(() => {
    downloadLoading.value = false
  }, 1000)
}

const handleLike = () => {
  if (!props.wallpaper) return
  
  likeLoading.value = true
  emit('like', props.wallpaper)
  isLiked.value = !isLiked.value
  
  setTimeout(() => {
    likeLoading.value = false
  }, 500)
}

const handleShare = () => {
  if (!props.wallpaper) return
  
  // 生成分享链接
  shareLink.value = `${window.location.origin}/wallpaper/${props.wallpaper.id}`
  
  // 打开分享弹窗
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}

const handleTagClick = (tag: string) => {
  emit('update:open', false)
}

watch(() => props.wallpaper, (newWallpaper) => {
  if (newWallpaper) {
    imageLoading.value = true
    isLiked.value = false
  }
})
</script>

<style scoped lang="scss">
.wallpaper-detail {
  display: flex;
  flex-direction: column;
  max-height: 80vh;
}

.image-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  min-height: 300px;
  margin-bottom: 24px;
  border-radius: 8px;
  overflow: hidden;
  
  .detail-image {
    max-width: 100%;
    max-height: 50vh;
    object-fit: contain;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }
  
  .image-loading {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    text-align: center;
    
    .loading-text {
      margin-top: 16px;
      color: #999;
      font-size: 16px;
    }
  }
}

.detail-info {
  .info-section {
    margin-bottom: 24px;
    
    .section-title {
      font-size: 18px;
      font-weight: 600;
      color: #333;
      margin: 0 0 16px 0;
    }
    
    .info-list {
      display: flex;
      flex-direction: column;
      gap: 12px;
      
      .info-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        
        .info-label {
          font-size: 14px;
          color: #666;
          font-weight: 500;
        }
        
        .info-value {
          font-size: 14px;
          color: #333;
        }
      }
    }
  }
  
  .tags-section {
    margin-bottom: 24px;
    
    .section-title {
      font-size: 18px;
      font-weight: 600;
      color: #333;
      margin: 0 0 16px 0;
    }
    
    .tags-list {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      
      .detail-tag {
        cursor: pointer;
        border-radius: 6px;
        font-weight: 500;
        transition: all 0.2s ease;
        
        &:hover {
          background: #1890ff;
          color: white;
          transform: translateY(-1px);
        }
      }
    }
  }
  
  .description-section {
    .section-title {
      font-size: 18px;
      font-weight: 600;
      color: #333;
      margin: 0 0 16px 0;
    }
    
    .description-text {
      font-size: 16px;
      color: #595959;
      line-height: 1.6;
      margin: 0;
    }
  }
}

.action-footer {
  display: flex;
  gap: 12px;
  padding: 16px 0 0;
  border-top: 1px solid #f0f0f0;
  
  .primary-action {
    min-width: 120px;
    height: 40px;
    border-radius: 8px;
    font-weight: 600;
    background: #1890ff;
    border: none;
    
    &:hover {
      background: #40a9ff;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(24, 144, 255, 0.3);
    }
  }
  
  .secondary-action {
    min-width: 100px;
    height: 40px;
    border-radius: 8px;
    font-weight: 500;
    border: 1px solid #d9d9d9;
    
    &:hover {
      border-color: #40a9ff;
      color: #1890ff;
      background: #f6ffed;
    }
    
    &.liked {
      background: #ff4d4f;
      border-color: #ff4d4f;
      color: white;
      
      &:hover {
        background: #dc2626;
      }
    }
  }
  
  .share-btn {
    height: 40px;
    border-radius: 8px;
    border: 1px solid #d9d9d9;
    background: white;
    
    &:hover {
      border-color: #40a9ff;
      color: #1890ff;
      background: #f6ffed;
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .detail-info {
    .info-section .info-list .info-item {
      flex-direction: column;
      align-items: flex-start;
      gap: 4px;
    }
  }
  
  .action-footer {
    flex-direction: column;
    gap: 8px;
    
    .primary-action,
    .secondary-action,
    .share-btn {
      width: 100%;
    }
  }
}
</style>