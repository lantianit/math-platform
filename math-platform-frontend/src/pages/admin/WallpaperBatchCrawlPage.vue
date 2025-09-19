<template>
  <div class="wallpaper-batch-crawl-page">
    <a-card title="批量抓取学习励志壁纸" class="crawl-card">
      <template #extra>
        <a-button type="link" @click="goToWallpaperManage">
          <SettingOutlined />
          管理壁纸
        </a-button>
      </template>

      <a-alert
        message="注意事项"
        description="此功能仅供管理员使用，用于批量抓取网络上的学习励志图片。请确保抓取的内容符合版权要求，仅用于学习交流目的。"
        type="warning"
        show-icon
        style="margin-bottom: 24px"
      />

      <a-form
        :model="formData"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 20 }"
        @finish="handleSubmit"
      >
        <a-form-item
          label="搜索关键词"
          name="searchText"
          :rules="[{ required: true, message: '请输入搜索关键词' }]"
        >
          <a-input
            v-model:value="formData.searchText"
            placeholder="例如: 学习励志、奋斗拼搏、青春梦想"
            size="large"
          />
          <div class="form-tip">
            推荐关键词：学习励志、奋斗拼搏、青春梦想、书籍阅读、考试加油等
          </div>
        </a-form-item>

        <a-form-item
          label="抓取数量"
          name="count"
          :rules="[
            { required: true, message: '请输入抓取数量' },
            { type: 'number', min: 1, max: 30, message: '数量范围为1-30' }
          ]"
        >
          <a-input-number
            v-model:value="formData.count"
            :min="1"
            :max="30"
            placeholder="请输入抓取数量"
            size="large"
            style="width: 200px"
          />
          <div class="form-tip">
            建议每次抓取10-20张，避免过多请求被限制
          </div>
        </a-form-item>

        <a-form-item label="名称前缀" name="namePrefix">
          <a-input
            v-model:value="formData.namePrefix"
            placeholder="壁纸名称前缀，留空则使用搜索关键词"
            size="large"
          />
          <div class="form-tip">
            生成的壁纸名称格式：前缀 + 序号，如"学习励志1"、"学习励志2"
          </div>
        </a-form-item>

        <a-form-item label="壁纸分类" name="category">
          <a-select
            v-model:value="formData.category"
            placeholder="选择壁纸分类"
            size="large"
            style="width: 200px"
          >
            <a-select-option value="学习励志">学习励志</a-select-option>
            <a-select-option value="奋斗拼搏">奋斗拼搏</a-select-option>
            <a-select-option value="青春梦想">青春梦想</a-select-option>
            <a-select-option value="书籍文字">书籍文字</a-select-option>
          </a-select>
          <div class="form-tip">
            选择合适的分类便于用户筛选查找
          </div>
        </a-form-item>

        <a-form-item :wrapper-col="{ span: 20, offset: 4 }">
          <a-button
            type="primary"
            html-type="submit"
            size="large"
            :loading="loading"
            style="width: 200px"
          >
            <CloudDownloadOutlined />
            开始抓取
          </a-button>
        </a-form-item>
      </a-form>

      <!-- 抓取进度 -->
      <div v-if="crawlResult" class="crawl-result">
        <a-divider />
        <h3>抓取结果</h3>
        <a-result
          :status="crawlResult.success ? 'success' : 'error'"
          :title="crawlResult.title"
          :sub-title="crawlResult.description"
        >
          <template #extra>
            <a-button type="primary" @click="goToWallpaperList">
              查看壁纸
            </a-button>
            <a-button @click="resetForm">
              继续抓取
            </a-button>
          </template>
        </a-result>
      </div>
    </a-card>

    <!-- 最近抓取记录 -->
    <a-card title="最近抓取的壁纸" style="margin-top: 24px">
      <div class="recent-wallpapers">
        <div v-if="recentWallpapers.length === 0" class="empty-recent">
          <a-empty description="暂无最近抓取的壁纸" />
        </div>
        <div v-else class="wallpaper-preview-grid">
          <div
            v-for="wallpaper in recentWallpapers"
            :key="wallpaper.id"
            class="wallpaper-preview-item"
          >
            <img
              :src="wallpaper.thumbnailUrl || wallpaper.url"
              :alt="wallpaper.name"
              class="preview-image"
            />
            <div class="preview-info">
              <div class="preview-name">{{ wallpaper.name }}</div>
              <div class="preview-category">{{ wallpaper.category }}</div>
            </div>
          </div>
        </div>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import {
  SettingOutlined,
  CloudDownloadOutlined
} from '@ant-design/icons-vue'
import { batchCrawlWallpapersUsingPost, listAllWallpapersByPageUsingPost } from '@/api/wallpaperController'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const crawlResult = ref<{
  success: boolean
  title: string
  description: string
} | null>(null)

const recentWallpapers = ref<API.WallpaperVO[]>([])

// 表单数据
const formData = reactive<API.WallpaperBatchCrawlRequest>({
  searchText: '',
  count: 10,
  namePrefix: '',
  category: '学习励志'
})

// 页面加载时获取最近的壁纸
onMounted(() => {
  loadRecentWallpapers()
})

// 加载最近的壁纸
const loadRecentWallpapers = async () => {
  try {
    const response = await listAllWallpapersByPageUsingPost({
      current: 1,
      pageSize: 8,
      status: 0
    })
    
    if (response.data?.code === 0 && response.data.data) {
      recentWallpapers.value = response.data.data.records || []
    }
  } catch (error) {
    console.error('加载最近壁纸失败：', error)
  }
}

// 提交表单
const handleSubmit = async () => {
  loading.value = true
  crawlResult.value = null
  
  try {
    const response = await batchCrawlWallpapersUsingPost(formData)
    
    if (response.data?.code === 0) {
      const count = response.data.data || 0
      crawlResult.value = {
        success: true,
        title: '抓取成功！',
        description: `成功抓取并创建了 ${count} 张壁纸`
      }
      
      // 刷新最近壁纸列表
      setTimeout(() => {
        loadRecentWallpapers()
      }, 1000)
      
      message.success(`成功创建 ${count} 张壁纸`)
    } else {
      crawlResult.value = {
        success: false,
        title: '抓取失败',
        description: response.data?.message || '未知错误'
      }
      message.error('抓取失败：' + (response.data?.message || '未知错误'))
    }
  } catch (error) {
    console.error('抓取失败：', error)
    crawlResult.value = {
      success: false,
      title: '抓取失败',
      description: '网络错误或服务异常'
    }
    message.error('抓取失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 重置表单
const resetForm = () => {
  crawlResult.value = null
  formData.searchText = ''
  formData.count = 10
  formData.namePrefix = ''
  formData.category = '学习励志'
}

// 跳转到壁纸列表
const goToWallpaperList = () => {
  router.push('/wallpaper')
}

// 跳转到壁纸管理
const goToWallpaperManage = () => {
  router.push('/admin/wallpaper')
}
</script>

<style scoped lang="less">
.wallpaper-batch-crawl-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 24px;
}

.crawl-card {
  .ant-card-head {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    
    .ant-card-head-title {
      color: white;
      font-weight: 600;
    }
  }
}

.form-tip {
  color: #666;
  font-size: 12px;
  margin-top: 4px;
  line-height: 1.4;
}

.crawl-result {
  margin-top: 24px;
  
  h3 {
    color: #333;
    margin-bottom: 16px;
  }
}

.recent-wallpapers {
  .empty-recent {
    text-align: center;
    padding: 40px 0;
  }
}

.wallpaper-preview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.wallpaper-preview-item {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }

  .preview-image {
    width: 100%;
    height: 120px;
    object-fit: cover;
  }

  .preview-info {
    padding: 12px;

    .preview-name {
      font-weight: 500;
      margin-bottom: 4px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .preview-category {
      color: #666;
      font-size: 12px;
    }
  }
}

@media (max-width: 768px) {
  .wallpaper-batch-crawl-page {
    padding: 16px;
  }

  .wallpaper-preview-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 12px;
  }
}
</style>
