<template>
  <div class="wallpaper-page-enterprise">
    <!-- 简洁的页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <div class="breadcrumb-section">
          <a-breadcrumb>
            <a-breadcrumb-item>
              <router-link to="/">首页</router-link>
            </a-breadcrumb-item>
            <a-breadcrumb-item>学习壁纸</a-breadcrumb-item>
          </a-breadcrumb>
        </div>
        <div class="title-section">
          <h1 class="page-title">学习壁纸</h1>
          <div class="title-meta">
            <span class="wallpaper-count">{{ totalCount }} 张壁纸</span>
            <span class="update-time">最近更新：今天</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="page-content">
      <!-- 搜索和筛选区域 -->
      <div class="search-filter-section">
        <div class="search-container">
          <a-input-search
            v-model:value="searchState.keyword"
            :placeholder="searchPlaceholder"
            size="large"
            @search="handleSearch"
            class="enterprise-search"
          >
            <template #prefix>
              <SearchOutlined class="search-icon" />
            </template>
            <template #enterButton>
              <a-button type="primary" size="large" class="search-button">
                搜索
              </a-button>
            </template>
          </a-input-search>
        </div>

        <div class="filter-container">
          <div class="filter-tabs">
            <a-tabs 
              v-model:activeKey="filterState.category" 
              @change="handleCategoryChange"
              type="line"
              class="category-tabs"
            >
              <a-tab-pane key="" tab="全部"></a-tab-pane>
              <a-tab-pane key="学习励志" tab="学习励志"></a-tab-pane>
              <a-tab-pane key="奋斗拼搏" tab="奋斗拼搏"></a-tab-pane>
              <a-tab-pane key="青春梦想" tab="青春梦想"></a-tab-pane>
              <a-tab-pane key="书籍文字" tab="书籍文字"></a-tab-pane>
            </a-tabs>
          </div>
          
          <div class="sort-section">
            <span class="sort-label">排序：</span>
            <a-select
              v-model:value="filterState.sortBy"
              @change="handleSortChange"
              class="sort-select"
              size="small"
              style="width: 120px"
            >
              <a-select-option value="createTime">最新</a-select-option>
              <a-select-option value="likeCount">最热</a-select-option>
              <a-select-option value="downloadCount">下载最多</a-select-option>
            </a-select>
          </div>
        </div>
      </div>

      <!-- 壁纸网格 -->
      <div class="wallpaper-grid-section">
        <VirtualMasonryGrid
          :items="wallpaperList.map(convertToGridItem)"
          :categories="categoryOptions"
          :has-more="hasMore"
          :loading="loading"
          :columns="gridColumns"
          :gap="24"
          search-placeholder="搜索壁纸..."
          empty-description="暂无壁纸数据，试试其他搜索条件吧"
          @search="handleSearch"
          @category-change="handleCategoryChange"
          @sort-change="handleSortChange"
          @load-more="loadMore"
          @refresh="refreshData"
          @item-click="handleWallpaperClick"
        >
          <template #item="{ item }">
            <WallpaperCard
              :wallpaper="item"
              @download="handleDownload"
              @like="handleLike"
              @click="handleWallpaperClick"
            />
          </template>
        </VirtualMasonryGrid>
      </div>
    </div>

    <!-- 壁纸详情模态框 -->
    <WallpaperDetailModal
      v-model:open="detailModal.visible"
      :wallpaper="detailModal.wallpaper"
      @download="handleDownload"
      @like="handleLike"
      @close="closeDetailModal"
    />

    <!-- 全局加载遮罩 -->
    <div v-if="globalLoading" class="global-loading">
      <div class="loading-content">
        <a-spin size="large" />
        <p class="loading-text">正在加载精美壁纸...</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { SearchOutlined } from '@ant-design/icons-vue'
import VirtualMasonryGrid from '@/components/business/VirtualMasonryGrid.vue'
import WallpaperCard from '@/components/business/WallpaperCard.vue'
import WallpaperDetailModal from '@/components/business/WallpaperDetailModal.vue'
import { listWallpapersByPageUsingPost, incrementDownloadCountUsingPost, likeWallpaperUsingPost } from '@/api/bizhiguanli'

interface WallpaperItem extends API.WallpaperVO {
  loading?: boolean
}

// 类型转换函数
const convertToGridItem = (wallpaper: WallpaperItem): any => {
  return {
    id: wallpaper.id || 0,
    name: wallpaper.name,
    url: wallpaper.url,
    thumbnailUrl: wallpaper.thumbnailUrl,
    category: wallpaper.category,
    tags: wallpaper.tags,
    likeCount: wallpaper.likeCount,
    downloadCount: wallpaper.downloadCount,
    width: wallpaper.width,
    height: wallpaper.height,
    loading: wallpaper.loading,
    ...wallpaper
  }
}

interface SearchState {
  keyword: string
  history: string[]
}

interface FilterState {
  category: string
  sortBy: string
}

interface DetailModal {
  visible: boolean
  wallpaper: WallpaperItem | null
}

const wallpaperList = ref<WallpaperItem[]>([])
const loading = ref(false)
const globalLoading = ref(false)
const hasMore = ref(true)
const totalCount = ref(0)
const totalDownloads = ref(0)

const searchState = reactive<SearchState>({
  keyword: '',
  history: JSON.parse(localStorage.getItem('wallpaper-search-history') || '[]'),
})

const filterState = reactive<FilterState>({
  category: '',
  sortBy: 'createTime',
})

const detailModal = reactive<DetailModal>({
  visible: false,
  wallpaper: null,
})

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 0,
})

const searchPlaceholder = computed(() => {
  const suggestions = ['学习励志', '奋斗拼搏', '青春梦想', '书籍阅读']
  const randomSuggestion = suggestions[Math.floor(Math.random() * suggestions.length)]
  return `搜索壁纸，如：${randomSuggestion}`
})

const categoryOptions = computed(() => [
  { label: '全部', value: '' },
  { label: '学习励志', value: '学习励志' },
  { label: '奋斗拼搏', value: '奋斗拼搏' },
  { label: '青春梦想', value: '青春梦想' },
  { label: '书籍文字', value: '书籍文字' },
])

const gridColumns = computed(() => ({
  xs: 1,
  sm: 2,
  md: 3,
  lg: 4,
  xl: 5,
}))

const loadWallpapers = async (reset = false) => {
  if (loading.value) return
  
  loading.value = true
  
  try {
    if (reset) {
      pagination.current = 1
      wallpaperList.value = []
    }
    
    const queryRequest = {
      current: pagination.current,
      pageSize: pagination.pageSize,
      name: searchState.keyword || undefined,
      category: filterState.category || undefined,
      status: 0,
    }
    
    const response = await listWallpapersByPageUsingPost(queryRequest)
    
    if (response.data?.code === 0 && response.data.data) {
      const pageData = response.data.data
      const newItems = pageData.records || []
      
      if (reset) {
        wallpaperList.value = newItems
      } else {
        wallpaperList.value.push(...newItems)
      }
      
      pagination.total = pageData.total || 0
      totalCount.value = pageData.total || 0
      hasMore.value = wallpaperList.value.length < pagination.total
      
      totalDownloads.value = wallpaperList.value.reduce((sum, item) => sum + (item.downloadCount || 0), 0)
    } else {
      message.error('加载失败：' + (response.data?.message || '未知错误'))
    }
  } catch (error) {
    console.error('加载壁纸失败：', error)
    message.error('网络错误，请稍后重试')
  } finally {
    loading.value = false
    globalLoading.value = false
  }
}

const handleSearch = (keyword?: string) => {
  const searchKeyword = keyword || searchState.keyword
  
  if (searchKeyword && searchKeyword.trim()) {
    const history = searchState.history.filter(h => h !== searchKeyword)
    history.unshift(searchKeyword)
    searchState.history = history.slice(0, 10)
    localStorage.setItem('wallpaper-search-history', JSON.stringify(searchState.history))
  }
  
  searchState.keyword = searchKeyword
  loadWallpapers(true)
}

const handleCategoryChange = (category: string) => {
  filterState.category = category
  loadWallpapers(true)
}

const handleSortChange = (sortBy: string) => {
  filterState.sortBy = sortBy
  loadWallpapers(true)
}

const loadMore = () => {
  if (hasMore.value && !loading.value) {
    pagination.current++
    loadWallpapers()
  }
}

const refreshData = () => {
  globalLoading.value = true
  loadWallpapers(true)
}

const handleWallpaperClick = (item: any) => {
  const wallpaper = wallpaperList.value.find(w => w.id === item.id)
  if (wallpaper) {
    detailModal.wallpaper = wallpaper
    detailModal.visible = true
  }
}

const handleDownload = async (wallpaper: WallpaperItem) => {
  if (!wallpaper.id || !wallpaper.url) return
  
  try {
    wallpaper.loading = true
    
    await incrementDownloadCountUsingPost({ id: wallpaper.id })
    
    const index = wallpaperList.value.findIndex(item => item.id === wallpaper.id)
    if (index !== -1) {
      wallpaperList.value[index].downloadCount = (wallpaperList.value[index].downloadCount || 0) + 1
    }
    
    const link = document.createElement('a')
    link.href = wallpaper.url
    link.download = `${wallpaper.name || 'wallpaper'}.jpg`
    link.target = '_blank'
    
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    message.success('下载成功！')
    
  } catch (error) {
    console.error('下载失败：', error)
    message.error('下载失败，请稍后重试')
  } finally {
    wallpaper.loading = false
  }
}

const handleLike = async (wallpaper: WallpaperItem) => {
  if (!wallpaper.id) return
  
  try {
    await likeWallpaperUsingPost({ id: wallpaper.id })
    
    const index = wallpaperList.value.findIndex(item => item.id === wallpaper.id)
    if (index !== -1) {
      wallpaperList.value[index].likeCount = (wallpaperList.value[index].likeCount || 0) + 1
    }
    
    if (detailModal.wallpaper?.id === wallpaper.id && detailModal.wallpaper) {
      detailModal.wallpaper.likeCount = (detailModal.wallpaper.likeCount || 0) + 1
    }
    
    message.success('点赞成功！')
    
  } catch (error) {
    console.error('点赞失败：', error)
    message.error('点赞失败，请稍后重试')
  }
}

const closeDetailModal = () => {
  detailModal.visible = false
  detailModal.wallpaper = null
}

onMounted(() => {
  globalLoading.value = true
  loadWallpapers()
})
</script>

<style scoped lang="scss">
.wallpaper-page-enterprise {
  min-height: 100vh;
  background: #fafafa;
}

.page-header {
  background: #ffffff;
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 0;
  
  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    
    .breadcrumb-section {
      margin-bottom: 16px;
    }
    
    .title-section {
      display: flex;
      justify-content: space-between;
      align-items: flex-end;
      
      .page-title {
        font-size: 28px;
        font-weight: 600;
        color: #262626;
        margin: 0;
        line-height: 1.2;
      }
      
      .title-meta {
        display: flex;
        align-items: center;
        gap: 16px;
        color: #8c8c8c;
        font-size: 14px;
        
        .wallpaper-count {
          color: #595959;
          font-weight: 500;
        }
      }
    }
  }
}

.page-content {
  background: #fafafa;
  padding: 24px 0;
}

.search-filter-section {
  background: #ffffff;
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 0;
  
  .search-container {
    max-width: 1200px;
    margin: 0 auto 20px;
    padding: 0 24px;
    
    .enterprise-search {
      max-width: 400px;
      margin: 0 auto;
      
      :deep(.ant-input-search) {
        .ant-input {
          border-radius: 6px;
          border: 1px solid #d9d9d9;
          
          &:focus,
          &:hover {
            border-color: #40a9ff;
          }
        }
        
        .ant-input-search-button {
          border-radius: 0 6px 6px 0;
        }
      }
      
      .search-icon {
        color: #bfbfbf;
      }
    }
  }
  
  .filter-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .filter-tabs {
      flex: 1;
      
      .category-tabs {
        :deep(.ant-tabs-nav) {
          margin-bottom: 0;
          
          .ant-tabs-tab {
            padding: 8px 16px;
            font-size: 14px;
            
            &.ant-tabs-tab-active {
              font-weight: 500;
            }
          }
          
          .ant-tabs-ink-bar {
            background: #1890ff;
          }
        }
      }
    }
    
    .sort-section {
      display: flex;
      align-items: center;
      gap: 8px;
      
      .sort-label {
        font-size: 14px;
        color: #8c8c8c;
      }
      
      .sort-select {
        :deep(.ant-select-selector) {
          border-radius: 4px;
        }
      }
    }
  }
}

.wallpaper-grid-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
  background: #fafafa;
}

.global-loading {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  
  .loading-content {
    text-align: center;
    
    .loading-text {
      margin-top: 16px;
      font-size: 18px;
      color: #666;
    }
  }
}

/* 响应式设计 */
@media (max-width: 576px) {
  .page-header {
    height: 280px;
    
    .header-content {
      padding: 0 16px;
      
      .page-title {
        font-size: 30px;
      }
      
      .page-subtitle {
        font-size: 18px;
      }
      
      .header-stats {
        gap: 24px;
        
        .stat-item .stat-number {
          font-size: 20px;
        }
      }
    }
  }
  
  .search-filter-section {
    padding: 0 16px;
    
    .filter-container {
      flex-direction: column;
      gap: 16px;
      
      .filter-group {
        width: 100%;
        
        .category-filter,
        .sort-select {
          width: 100%;
        }
      }
    }
  }
  
  .wallpaper-grid-section {
    padding: 0 16px;
  }
}

@media (max-width: 768px) {
  .search-filter-section {
    .filter-container {
      gap: 24px;
    }
  }
}

/* 简单的过渡效果 */
.wallpaper-page-enterprise {
  opacity: 1;
  transition: opacity 0.3s ease;
}
</style>