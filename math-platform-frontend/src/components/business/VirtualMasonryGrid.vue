<template>
  <div class="virtual-masonry-grid" ref="containerRef">
    <!-- 搜索和筛选头部 -->
    <div class="grid-header">
      <div class="search-section">
        <a-input-search
          v-model:value="searchKeyword"
          :placeholder="searchPlaceholder"
          size="large"
          @search="handleSearch"
          class="search-input"
        >
          <template #enterButton>
            <a-button type="primary" size="large">
              <SearchOutlined />
            </a-button>
          </template>
        </a-input-search>
      </div>
      
      <div class="filter-section">
        <a-space wrap>
          <a-select
            v-model:value="selectedCategory"
            placeholder="选择分类"
            style="min-width: 120px"
            allowClear
            @change="handleCategoryChange"
          >
            <a-select-option
              v-for="category in categories"
              :key="category.value"
              :value="category.value"
            >
              {{ category.label }}
            </a-select-option>
          </a-select>
          
          <a-select
            v-model:value="sortBy"
            placeholder="排序方式"
            style="min-width: 120px"
            @change="handleSortChange"
          >
            <a-select-option value="createTime">最新</a-select-option>
            <a-select-option value="likeCount">最热</a-select-option>
            <a-select-option value="downloadCount">下载量</a-select-option>
          </a-select>
          
          <a-button @click="handleRefresh" :loading="refreshing">
            <ReloadOutlined />
            刷新
          </a-button>
        </a-space>
      </div>
    </div>

    <!-- 瀑布流网格 -->
    <div class="grid-container" :style="gridContainerStyles">
      <div
        v-for="(item, index) in visibleItems"
        :key="item.id"
        :class="getItemClasses(item, index)"
        :style="getItemStyles(item, index)"
        @click="handleItemClick(item)"
      >
        <slot name="item" :item="item" :index="index">
          <!-- 默认卡片内容 -->
          <div class="grid-item-card">
            <div class="item-image-container">
              <img
                :src="item.thumbnailUrl || item.url"
                :alt="item.name"
                class="item-image"
                loading="lazy"
                @error="handleImageError"
              />
              <div class="image-overlay">
                <div class="overlay-actions">
                  <slot name="item-overlay" :item="item">
                    <a-space>
                      <a-button type="primary" ghost size="small">
                        <EyeOutlined />
                      </a-button>
                      <a-button type="primary" ghost size="small">
                        <DownloadOutlined />
                      </a-button>
                    </a-space>
                  </slot>
                </div>
              </div>
            </div>
            
            <div class="item-content">
              <h3 class="item-title">{{ item.name }}</h3>
              <div class="item-meta">
                <a-tag v-if="item.category" :color="getCategoryColor(item.category)">
                  {{ item.category }}
                </a-tag>
                <span class="item-stats">
                  <HeartOutlined />
                  {{ item.likeCount || 0 }}
                  <DownloadOutlined />
                  {{ item.downloadCount || 0 }}
                </span>
              </div>
              <div v-if="item.tags && item.tags.length" class="item-tags">
                <a-tag
                  v-for="tag in item.tags.slice(0, 3)"
                  :key="tag"
                  size="small"
                  class="item-tag"
                >
                  {{ tag }}
                </a-tag>
              </div>
            </div>
          </div>
        </slot>
      </div>
    </div>

    <!-- 加载更多 -->
    <div v-if="hasMore" class="load-more-section">
      <a-button
        type="primary"
        size="large"
        :loading="loading"
        @click="loadMore"
        class="load-more-btn"
      >
        {{ loading ? '加载中...' : '加载更多' }}
      </a-button>
    </div>

    <!-- 空状态 -->
    <div v-if="!loading && items.length === 0" class="empty-state">
      <a-empty :description="emptyDescription">
        <template #image>
          <PictureOutlined class="empty-icon" />
        </template>
      </a-empty>
    </div>

    <!-- 回到顶部 -->
    <a-back-top :visibility-height="300" class="back-to-top">
      <div class="back-to-top-content">
        <UpOutlined />
      </div>
    </a-back-top>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useWindowSize, useElementSize } from '@vueuse/core'
import {
  SearchOutlined,
  ReloadOutlined,
  EyeOutlined,
  DownloadOutlined,
  HeartOutlined,
  PictureOutlined,
  UpOutlined
} from '@ant-design/icons-vue'

interface GridItem {
  id?: number | string
  name?: string
  url?: string
  thumbnailUrl?: string
  category?: string
  tags?: string[]
  likeCount?: number
  downloadCount?: number
  width?: number
  height?: number
  loading?: boolean
  [key: string]: any
}

interface Category {
  label: string
  value: string
  color?: string
}

interface Props {
  items: GridItem[]
  categories?: Category[]
  columns?: {
    xs: number
    sm: number
    md: number
    lg: number
    xl: number
  }
  gap?: number
  hasMore?: boolean
  loading?: boolean
  searchPlaceholder?: string
  emptyDescription?: string
  virtual?: boolean
}

interface Emits {
  (e: 'search', keyword: string): void
  (e: 'category-change', category: string): void
  (e: 'sort-change', sortBy: string): void
  (e: 'load-more'): void
  (e: 'refresh'): void
  (e: 'item-click', item: GridItem): void
}

const props = withDefaults(defineProps<Props>(), {
  categories: () => [],
  columns: () => ({
    xs: 1,
    sm: 2,
    md: 3,
    lg: 4,
    xl: 5,
  }),
  gap: 16,
  hasMore: false,
  loading: false,
  searchPlaceholder: '搜索...',
  emptyDescription: '暂无数据',
  virtual: true,
})

const emit = defineEmits<Emits>()

const containerRef = ref<HTMLElement>()
const searchKeyword = ref('')
const selectedCategory = ref<string>()
const sortBy = ref('createTime')
const refreshing = ref(false)

const { width: windowWidth } = useWindowSize()
const { width: containerWidth } = useElementSize(containerRef)

const currentColumns = computed(() => {
  const width = containerWidth.value || windowWidth.value
  if (width < 576) return props.columns.xs
  if (width < 768) return props.columns.sm
  if (width < 992) return props.columns.md
  if (width < 1200) return props.columns.lg
  return props.columns.xl
})

const gridContainerStyles = computed(() => ({
  display: 'grid',
  gridTemplateColumns: `repeat(${currentColumns.value}, 1fr)`,
  gap: `${props.gap}px`,
  width: '100%',
}))

const visibleItems = computed(() => {
  if (!props.virtual) return props.items
  return props.items
})

const getItemStyles = (item: GridItem, index: number) => {
  const styles: any = {}
  
  if (item.height && item.width) {
    const aspectRatio = item.height / item.width
    const estimatedHeight = 200 * aspectRatio + 80
    const gridRows = Math.ceil(estimatedHeight / (props.gap + 10))
    styles.gridRowEnd = `span ${gridRows}`
  }
  
  return styles
}

const getItemClasses = (item: GridItem, index: number) => [
  'grid-item',
  {
    'grid-item--featured': item.featured,
  }
]

const getCategoryColor = (category: string) => {
  const categoryConfig = props.categories.find(c => c.value === category)
  return categoryConfig?.color || 'blue'
}

const handleSearch = (keyword: string) => {
  emit('search', keyword)
}

const handleCategoryChange = (category: string) => {
  emit('category-change', category)
}

const handleSortChange = (sort: string) => {
  emit('sort-change', sort)
}

const loadMore = () => {
  if (!props.loading && props.hasMore) {
    emit('load-more')
  }
}

const handleRefresh = async () => {
  refreshing.value = true
  emit('refresh')
  setTimeout(() => {
    refreshing.value = false
  }, 1000)
}

const handleItemClick = (item: GridItem) => {
  emit('item-click', item)
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OTk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuWbvueJh+WKoOi9veWksei0pTwvdGV4dD48L3N2Zz4='
}

let observer: IntersectionObserver | null = null

onMounted(() => {
  const loadMoreBtn = document.querySelector('.load-more-btn')
  if (loadMoreBtn) {
    observer = new IntersectionObserver(
      (entries) => {
        if (entries[0].isIntersecting && props.hasMore && !props.loading) {
          loadMore()
        }
      },
      { threshold: 0.1 }
    )
    observer.observe(loadMoreBtn)
  }
})

onUnmounted(() => {
  if (observer) {
    observer.disconnect()
  }
})
</script>

<style scoped lang="scss">
.virtual-masonry-grid {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0;
}

.grid-header {
  margin-bottom: 32px;
  
  .search-section {
    margin-bottom: 16px;
    
    .search-input {
      max-width: 500px;
      margin: 0 auto;
      display: block;
    }
  }
  
  .filter-section {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 12px;
  }
}

.grid-container {
  margin-bottom: 24px;
  padding: 0 24px;
}

.grid-item {
  break-inside: avoid;
  margin-bottom: 20px;
  
  .grid-item-card {
    height: fit-content;
    background: white;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border: 1px solid #f0f0f0;
    transition: all 0.2s ease;
    cursor: pointer;
    
    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    }
  }
  
  .item-image-container {
    position: relative;
    overflow: hidden;
    
    .item-image {
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
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s ease;
      
      .overlay-actions {
        display: flex;
        gap: 12px;
      }
    }
    
    &:hover .image-overlay {
      opacity: 1;
    }
  }
  
  .item-content {
    padding: 16px;
    
    .item-title {
      font-size: 16px;
      font-weight: 600;
      margin: 0 0 8px 0;
      color: #333;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .item-meta {
      display: flex;
      align-items: center;
      justify-content: space-between;
      margin-bottom: 8px;
      
      .item-stats {
        color: #666;
        font-size: 12px;
        display: flex;
        align-items: center;
        gap: 4px;
      }
    }
    
    .item-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 4px;
      
      .item-tag {
        font-size: 11px;
      }
    }
  }
}

.load-more-section {
  text-align: center;
  margin: 40px 0;
  
  .load-more-btn {
    min-width: 160px;
    height: 48px;
    border-radius: 24px;
    font-weight: 500;
  }
}

.empty-state {
  text-align: center;
  padding: 80px 0;
  
  .empty-icon {
    font-size: 4rem;
    color: #ccc;
    margin-bottom: 16px;
  }
}

.back-to-top {
  .back-to-top-content {
    width: 48px;
    height: 48px;
    background: #1890ff;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    font-size: 18px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transition: all 0.3s ease;
    
    &:hover {
      background: #40a9ff;
      transform: scale(1.1);
    }
  }
}

/* 响应式设计 */
@media (max-width: 576px) {
  .virtual-masonry-grid {
    padding: 16px;
  }
  
  .grid-header {
    .search-section .search-input {
      max-width: 100%;
    }
    
    .filter-section {
      justify-content: flex-start;
    }
  }
  
  .grid-container {
    grid-template-columns: 1fr !important;
  }
}

@media (max-width: 768px) {
  .grid-header {
    .filter-section {
      flex-direction: column;
      align-items: stretch;
      
      .ant-space {
        width: 100%;
        justify-content: space-between;
      }
    }
  }
}
</style>