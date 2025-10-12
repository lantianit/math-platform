<template>
  <div class="wallpaper-page">
    <!-- 头部搜索区域 -->
    <div class="search-header">
      <div class="search-container">
        <h1 class="page-title">学习加油壁纸</h1>
        <p class="page-subtitle">精选励志学习壁纸，为你的学习之路加油打气</p>
        
        <div class="search-form">
          <a-input-search
            v-model:value="searchKeyword"
            placeholder="搜索壁纸..."
            size="large"
            @search="handleSearch"
            style="max-width: 400px"
          >
            <template #enterButton>
              <a-button type="primary" size="large">
                <SearchOutlined />
                搜索
              </a-button>
            </template>
          </a-input-search>
        </div>

        <!-- 分类筛选 -->
        <div class="category-filter">
          <a-radio-group v-model:value="selectedCategory" @change="handleCategoryChange">
            <a-radio-button value="">全部</a-radio-button>
            <a-radio-button value="学习励志">学习励志</a-radio-button>
            <a-radio-button value="奋斗拼搏">奋斗拼搏</a-radio-button>
            <a-radio-button value="青春梦想">青春梦想</a-radio-button>
            <a-radio-button value="书籍文字">书籍文字</a-radio-button>
          </a-radio-group>
        </div>
      </div>
    </div>

    <!-- 壁纸瀑布流展示区域 -->
    <div class="wallpaper-content">
      <div class="wallpaper-grid" ref="gridRef">
        <div
          v-for="wallpaper in wallpaperList"
          :key="wallpaper.id"
          class="wallpaper-item"
          @click="handleWallpaperClick(wallpaper)"
        >
          <div class="wallpaper-card">
            <div class="image-container">
              <img
                :src="wallpaper.thumbnailUrl || wallpaper.url"
                :alt="wallpaper.name"
                class="wallpaper-image"
                loading="lazy"
                @error="handleImageError"
              />
              <div class="image-overlay">
                <div class="overlay-actions">
                  <a-button type="primary" ghost size="small" @click.stop="handleDownload(wallpaper)">
                    <DownloadOutlined />
                    下载
                  </a-button>
                  <a-button type="primary" ghost size="small" @click.stop="handleLike(wallpaper)">
                    <HeartOutlined />
                    {{ wallpaper.likeCount || 0 }}
                  </a-button>
                </div>
              </div>
            </div>
            <div class="wallpaper-info">
              <h3 class="wallpaper-name">{{ wallpaper.name }}</h3>
              <div class="wallpaper-meta">
                <a-tag v-if="wallpaper.category" color="blue">{{ wallpaper.category }}</a-tag>
                <span class="download-count">
                  <DownloadOutlined />
                  {{ wallpaper.downloadCount || 0 }}
                </span>
              </div>
              <div v-if="wallpaper.tags && wallpaper.tags.length" class="wallpaper-tags">
                <a-tag v-for="tag in wallpaper.tags.slice(0, 3)" :key="tag" size="small">
                  {{ tag }}
                </a-tag>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 加载更多 -->
      <div class="load-more" v-if="hasMore">
        <a-button
          type="primary"
          size="large"
          :loading="loading"
          @click="loadMore"
          ghost
        >
          加载更多
        </a-button>
      </div>

      <!-- 空状态 -->
      <div v-if="!loading && wallpaperList.length === 0" class="empty-state">
        <a-empty description="暂无壁纸数据">
          <template #image>
            <PictureOutlined style="font-size: 64px; color: #ccc;" />
          </template>
        </a-empty>
      </div>
    </div>

    <!-- 壁纸详情模态框 -->
    <a-modal
      v-model:open="detailModalVisible"
      :title="currentWallpaper?.name"
      width="80%"
      :footer="null"
      centered
    >
      <div v-if="currentWallpaper" class="wallpaper-detail">
        <div class="detail-image">
          <img
            :src="currentWallpaper.url"
            :alt="currentWallpaper.name"
            style="max-width: 100%; height: auto; border-radius: 8px;"
          />
        </div>
        <div class="detail-info">
          <h3>{{ currentWallpaper.name }}</h3>
          <p v-if="currentWallpaper.description">{{ currentWallpaper.description }}</p>
          <div class="detail-meta">
            <a-descriptions :column="2" size="small">
              <a-descriptions-item label="分类">{{ currentWallpaper.category }}</a-descriptions-item>
              <a-descriptions-item label="尺寸">
                {{ currentWallpaper.width }}×{{ currentWallpaper.height }}
              </a-descriptions-item>
              <a-descriptions-item label="下载次数">{{ currentWallpaper.downloadCount }}</a-descriptions-item>
              <a-descriptions-item label="点赞数">{{ currentWallpaper.likeCount }}</a-descriptions-item>
            </a-descriptions>
          </div>
          <div class="detail-actions">
            <a-button type="primary" size="large" @click="handleDownload(currentWallpaper)">
              <DownloadOutlined />
              下载壁纸
            </a-button>
            <a-button size="large" @click="handleLike(currentWallpaper)">
              <HeartOutlined />
              点赞 ({{ currentWallpaper.likeCount || 0 }})
            </a-button>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { message } from 'ant-design-vue'
import {
  SearchOutlined,
  DownloadOutlined,
  HeartOutlined,
  PictureOutlined
} from '@ant-design/icons-vue'
import { listWallpapersByPageUsingPost, incrementDownloadCountUsingPost, likeWallpaperUsingPost } from '@/api/bizhiguanli'

// 响应式数据
const wallpaperList = ref<API.WallpaperVO[]>([])
const loading = ref(false)
const hasMore = ref(true)
const searchKeyword = ref('')
const selectedCategory = ref('')
const detailModalVisible = ref(false)
const currentWallpaper = ref<API.WallpaperVO | null>(null)
const gridRef = ref()

// 分页参数
const pagination = reactive({
  current: 1,
  pageSize: 20,
  total: 0
})

// 页面挂载时加载数据
onMounted(() => {
  loadWallpapers()
})

// 加载壁纸数据
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
      status: 0 // 只查询正常状态的壁纸
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

// 搜索处理
const handleSearch = () => {
  loadWallpapers(true)
}

// 分类切换处理
const handleCategoryChange = () => {
  loadWallpapers(true)
}

// 加载更多
const loadMore = () => {
  pagination.current++
  loadWallpapers()
}

// 壁纸点击处理
const handleWallpaperClick = (wallpaper: API.WallpaperVO) => {
  currentWallpaper.value = wallpaper
  detailModalVisible.value = true
}

// 下载处理
const handleDownload = async (wallpaper: API.WallpaperVO) => {
  try {
    // 增加下载次数
    await incrementDownloadCountUsingPost({ id: wallpaper.id! })
    
    // 更新本地数据
    const index = wallpaperList.value.findIndex(item => item.id === wallpaper.id)
    if (index !== -1) {
      wallpaperList.value[index].downloadCount = (wallpaperList.value[index].downloadCount || 0) + 1
    }
    
    // 触发下载
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

// 点赞处理
const handleLike = async (wallpaper: API.WallpaperVO) => {
  try {
    await likeWallpaperUsingPost({ id: wallpaper.id! })
    
    // 更新本地数据
    const index = wallpaperList.value.findIndex(item => item.id === wallpaper.id)
    if (index !== -1) {
      wallpaperList.value[index].likeCount = (wallpaperList.value[index].likeCount || 0) + 1
    }
    
    // 更新详情模态框数据
    if (currentWallpaper.value?.id === wallpaper.id && currentWallpaper.value) {
      currentWallpaper.value.likeCount = (currentWallpaper.value.likeCount || 0) + 1
    }
    
    message.success('点赞成功')
  } catch (error) {
    console.error('点赞失败：', error)
    message.error('点赞失败，请稍后重试')
  }
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const img = event.target as HTMLImageElement
  img.src = 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjAwIiBoZWlnaHQ9IjIwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjZjVmNWY1Ii8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCwgc2Fucy1zZXJpZiIgZm9udC1zaXplPSIxNCIgZmlsbD0iIzk5OTk5OSIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuWbvueJh+WKoOi9veWksei0pTwvdGV4dD48L3N2Zz4='
}
</script>

<style scoped lang="less">
.wallpaper-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.search-header {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  padding: 40px 0;
  text-align: center;
  box-shadow: 0 2px 20px rgba(0, 0, 0, 0.1);

  .search-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
  }

  .page-title {
    font-size: 36px;
    font-weight: bold;
    margin-bottom: 8px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }

  .page-subtitle {
    font-size: 16px;
    color: #666;
    margin-bottom: 32px;
  }

  .search-form {
    margin-bottom: 24px;
  }

  .category-filter {
    .ant-radio-group {
      .ant-radio-button-wrapper {
        border-radius: 20px;
        margin: 0 4px;
        border: 1px solid #d9d9d9;
        
        &.ant-radio-button-wrapper-checked {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          border-color: #667eea;
        }
      }
    }
  }
}

.wallpaper-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
}

.wallpaper-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
  margin-bottom: 40px;
}

.wallpaper-item {
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);

    .image-overlay {
      opacity: 1;
    }
  }
}

.wallpaper-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
}

.image-container {
  position: relative;
  overflow: hidden;

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
}

.wallpaper-info {
  padding: 16px;

  .wallpaper-name {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 8px;
    color: #333;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .wallpaper-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 8px;

    .download-count {
      color: #666;
      font-size: 12px;
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }

  .wallpaper-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
  }
}

.load-more {
  text-align: center;
  margin: 40px 0;
}

.empty-state {
  text-align: center;
  padding: 80px 0;
}

.wallpaper-detail {
  .detail-image {
    text-align: center;
    margin-bottom: 24px;
  }

  .detail-info {
    h3 {
      font-size: 24px;
      margin-bottom: 16px;
    }

    p {
      color: #666;
      margin-bottom: 16px;
    }

    .detail-meta {
      margin-bottom: 24px;
    }

    .detail-actions {
      display: flex;
      gap: 12px;
    }
  }
}

@media (max-width: 768px) {
  .wallpaper-grid {
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 16px;
  }

  .search-header {
    padding: 24px 0;

    .page-title {
      font-size: 28px;
    }

    .category-filter {
      .ant-radio-group {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        gap: 8px;
      }
    }
  }
}
</style>
