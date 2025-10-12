<template>
  <div class="wallpaper-page">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="container">
        <a-breadcrumb class="breadcrumb">
          <a-breadcrumb-item>
            <router-link to="/">首页</router-link>
          </a-breadcrumb-item>
          <a-breadcrumb-item>学习壁纸</a-breadcrumb-item>
        </a-breadcrumb>
        
        <div class="header-main">
          <h1 class="page-title">学习壁纸</h1>
          <p class="page-desc">收录优质学习励志壁纸，助力学习生活</p>
        </div>
      </div>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="container">
        <div class="toolbar-left">
          <a-input-search
            v-model:value="searchKeyword"
            placeholder="搜索壁纸..."
            style="width: 300px"
            @search="handleSearch"
          />
        </div>
        
        <div class="toolbar-right">
          <a-tabs 
            v-model:activeKey="selectedCategory" 
            @change="handleCategoryChange"
            type="card"
            size="small"
          >
            <a-tab-pane key="" tab="全部" />
            <a-tab-pane key="学习励志" tab="学习励志" />
            <a-tab-pane key="奋斗拼搏" tab="奋斗拼搏" />
            <a-tab-pane key="青春梦想" tab="青春梦想" />
            <a-tab-pane key="书籍文字" tab="书籍文字" />
          </a-tabs>
          
          <a-select
            v-model:value="sortBy"
            style="width: 100px; margin-left: 16px"
            size="small"
            @change="handleSortChange"
          >
            <a-select-option value="createTime">最新</a-select-option>
            <a-select-option value="likeCount">最热</a-select-option>
            <a-select-option value="downloadCount">下载</a-select-option>
          </a-select>
        </div>
      </div>
    </div>

    <!-- 壁纸网格 -->
    <div class="content">
      <div class="container">
        <div class="wallpaper-grid">
          <div
            v-for="wallpaper in wallpaperList"
            :key="wallpaper.id"
            class="wallpaper-item"
            @click="handleWallpaperClick(wallpaper)"
          >
            <div class="wallpaper-card">
              <div class="image-wrapper">
                <img
                  :src="wallpaper.thumbnailUrl || wallpaper.url"
                  :alt="wallpaper.name"
                  class="wallpaper-image"
                  loading="lazy"
                  @error="handleImageError"
                />
                <div class="image-actions">
                  <a-button 
                    type="text" 
                    size="small" 
                    @click.stop="handleDownload(wallpaper)"
                    class="action-btn"
                  >
                    <DownloadOutlined />
                  </a-button>
                  <a-button 
                    type="text" 
                    size="small" 
                    @click.stop="handleLike(wallpaper)"
                    class="action-btn"
                  >
                    <HeartOutlined />
                    {{ wallpaper.likeCount || 0 }}
                  </a-button>
                </div>
              </div>
              
              <div class="card-content">
                <h3 class="wallpaper-title">{{ wallpaper.name }}</h3>
                <div class="wallpaper-meta">
                  <a-tag v-if="wallpaper.category" size="small">
                    {{ wallpaper.category }}
                  </a-tag>
                  <span class="download-count">
                    {{ wallpaper.downloadCount || 0 }} 下载
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载更多 -->
        <div v-if="hasMore" class="load-more">
          <a-button 
            @click="loadMore" 
            :loading="loading"
            size="large"
          >
            {{ loading ? '加载中...' : '加载更多' }}
          </a-button>
        </div>

        <!-- 空状态 -->
        <div v-if="!loading && wallpaperList.length === 0" class="empty-state">
          <a-empty description="暂无壁纸">
            <a-button type="primary" @click="refreshData">刷新页面</a-button>
          </a-empty>
        </div>
      </div>
    </div>

    <!-- 详情模态框 -->
    <a-modal
      v-model:open="detailVisible"
      :title="currentWallpaper?.name"
      width="80%"
      :footer="null"
    >
      <div v-if="currentWallpaper" class="wallpaper-detail">
        <div class="detail-image">
          <img 
            :src="currentWallpaper.url" 
            :alt="currentWallpaper.name"
            style="max-width: 100%; height: auto;"
          />
        </div>
        <div class="detail-info">
          <a-descriptions :column="2" size="small">
            <a-descriptions-item label="分类">{{ currentWallpaper.category }}</a-descriptions-item>
            <a-descriptions-item label="尺寸">
              {{ currentWallpaper.width }}×{{ currentWallpaper.height }}
            </a-descriptions-item>
            <a-descriptions-item label="下载次数">{{ currentWallpaper.downloadCount }}</a-descriptions-item>
            <a-descriptions-item label="点赞数">{{ currentWallpaper.likeCount }}</a-descriptions-item>
          </a-descriptions>
          
          <div class="detail-actions" style="margin-top: 16px;">
            <a-button type="primary" @click="handleDownload(currentWallpaper)">
              <DownloadOutlined />
              下载壁纸
            </a-button>
            <a-button @click="handleLike(currentWallpaper)" style="margin-left: 8px;">
              <HeartOutlined />
              点赞
            </a-button>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  DownloadOutlined,
  HeartOutlined
} from '@ant-design/icons-vue'
import { listWallpapersByPageUsingPost, incrementDownloadCountUsingPost, likeWallpaperUsingPost } from '@/api/bizhiguanli'

const wallpaperList = ref<API.WallpaperVO[]>([])
const loading = ref(false)
const hasMore = ref(true)
const searchKeyword = ref('')
const selectedCategory = ref('')
const sortBy = ref('createTime')
const detailVisible = ref(false)
const currentWallpaper = ref<API.WallpaperVO | null>(null)

const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 0
})

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
      name: searchKeyword.value || undefined,
      category: selectedCategory.value || undefined,
      status: 0
    }
    
    const response = await listWallpapersByPageUsingPost(queryRequest)
    
    if (response.data?.code === 0 && response.data.data) {
      const pageData = response.data.data
      
      if (reset) {
        wallpaperList.value = pageData.records || []
      } else {
        wallpaperList.value.push(...(pageData.records || []))
      }
      
      pagination.total = pageData.total || 0
      hasMore.value = wallpaperList.value.length < pagination.total
    } else {
      message.error('加载失败：' + (response.data?.message || '未知错误'))
    }
  } catch (error) {
    console.error('加载壁纸失败：', error)
    message.error('加载失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  loadWallpapers(true)
}

const handleCategoryChange = () => {
  loadWallpapers(true)
}

const handleSortChange = () => {
  loadWallpapers(true)
}

const loadMore = () => {
  pagination.current++
  loadWallpapers()
}

const refreshData = () => {
  loadWallpapers(true)
}

const handleWallpaperClick = (wallpaper: API.WallpaperVO) => {
  currentWallpaper.value = wallpaper
  detailVisible.value = true
}

const handleDownload = async (wallpaper: API.WallpaperVO) => {
  try {
    await incrementDownloadCountUsingPost({ id: wallpaper.id! })
    
    const index = wallpaperList.value.findIndex(item => item.id === wallpaper.id)
    if (index !== -1) {
      wallpaperList.value[index].downloadCount = (wallpaperList.value[index].downloadCount || 0) + 1
    }
    
    const link = document.createElement('a')
    link.href = wallpaper.url!
    link.download = wallpaper.name + '.jpg'
    link.target = '_blank'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    message.success('下载成功')
  } catch (error) {
    console.error('下载失败：', error)
    message.error('下载失败，请稍后重试')
  }
}

const handleLike = async (wallpaper: API.WallpaperVO) => {
  try {
    await likeWallpaperUsingPost({ id: wallpaper.id! })
    
    const index = wallpaperList.value.findIndex(item => item.id === wallpaper.id)
    if (index !== -1) {
      wallpaperList.value[index].likeCount = (wallpaperList.value[index].likeCount || 0) + 1
    }
    
    if (currentWallpaper.value?.id === wallpaper.id && currentWallpaper.value) {
      currentWallpaper.value.likeCount = (currentWallpaper.value.likeCount || 0) + 1
    }
    
    message.success('点赞成功')
  } catch (error) {
    console.error('点赞失败：', error)
    message.error('点赞失败，请稍后重试')
  }
}

const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OTk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuWbvueJh+WKoOi9veWksei0pTwvdGV4dD48L3N2Zz4='
}

onMounted(() => {
  loadWallpapers()
})
</script>

<style scoped lang="less">
.wallpaper-page {
  min-height: 100vh;
  background: #f5f5f5;
}

.page-header {
  background: white;
  border-bottom: 1px solid #e8e8e8;
  padding: 16px 0;
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }
  
  .breadcrumb {
    margin-bottom: 12px;
  }
  
  .header-main {
    .page-title {
      font-size: 24px;
      font-weight: 600;
      color: #262626;
      margin: 0 0 4px 0;
    }
    
    .page-desc {
      color: #8c8c8c;
      font-size: 14px;
      margin: 0;
    }
  }
}

.toolbar {
  background: white;
  border-bottom: 1px solid #f0f0f0;
  padding: 16px 0;
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .toolbar-left {
    flex-shrink: 0;
  }
  
  .toolbar-right {
    display: flex;
    align-items: center;
    
    :deep(.ant-tabs) {
      .ant-tabs-nav {
        margin-bottom: 0;
      }
      
      .ant-tabs-tab {
        background: #fafafa;
        border: 1px solid #e8e8e8;
        border-radius: 4px;
        margin-right: 4px;
        
        &.ant-tabs-tab-active {
          background: #1890ff;
          border-color: #1890ff;
          color: white;
        }
      }
    }
  }
}

.content {
  padding: 20px 0;
  
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }
}

.wallpaper-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

.wallpaper-item {
  cursor: pointer;
}

.wallpaper-card {
  background: white;
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border: 1px solid #f0f0f0;
  transition: all 0.2s ease;
  
  &:hover {
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
    transform: translateY(-1px);
    
    .image-actions {
      opacity: 1;
    }
  }
}

.image-wrapper {
  position: relative;
  
  .wallpaper-image {
    width: 100%;
    height: 200px;
    object-fit: cover;
    display: block;
  }
  
  .image-actions {
    position: absolute;
    top: 8px;
    right: 8px;
    display: flex;
    gap: 4px;
    opacity: 0;
    transition: opacity 0.2s ease;
    
    .action-btn {
      background: rgba(255, 255, 255, 0.9);
      border: none;
      border-radius: 4px;
      color: #595959;
      
      &:hover {
        background: white;
        color: #1890ff;
      }
    }
  }
}

.card-content {
  padding: 12px;
  
  .wallpaper-title {
    font-size: 14px;
    font-weight: 500;
    color: #262626;
    margin: 0 0 8px 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
  
  .wallpaper-meta {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .download-count {
      font-size: 12px;
      color: #8c8c8c;
    }
  }
}

.load-more {
  text-align: center;
  margin: 40px 0;
}

.empty-state {
  text-align: center;
  padding: 60px 0;
}

.wallpaper-detail {
  .detail-image {
    text-align: center;
    margin-bottom: 20px;
    
    img {
      max-width: 100%;
      border-radius: 4px;
    }
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .toolbar {
    .container {
      flex-direction: column;
      gap: 16px;
      align-items: stretch;
    }
    
    .toolbar-left {
      order: 2;
    }
    
    .toolbar-right {
      order: 1;
      justify-content: space-between;
    }
  }
  
  .wallpaper-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }
  
  .container {
    padding: 0 16px !important;
  }
}

@media (max-width: 480px) {
  .wallpaper-grid {
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 12px;
  }
  
  .wallpaper-card .image-wrapper .wallpaper-image {
    height: 160px;
  }
}
</style>
