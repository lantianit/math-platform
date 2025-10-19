<template>
  <div class="wallpaper-manage-page">
    <!-- 操作栏 -->
    <div class="action-bar">
      <a-space>
        <a-button type="primary" @click="goToBatchCrawl">
          <PlusOutlined />
          批量抓取壁纸
        </a-button>
        <a-button 
          type="primary" 
          :icon="h(EditOutlined)" 
          @click="doBatchEdit"
          :disabled="selectedRowKeys.length === 0"
        >
          批量编辑 ({{ selectedRowKeys.length }})
        </a-button>
        <a-button @click="refreshData">
          <ReloadOutlined />
          刷新
        </a-button>
      </a-space>
    </div>

    <!-- 搜索筛选 -->
    <PictureSearchForm :onSearch="handleSearch" />

    <!-- 壁纸列表 -->
    <a-card title="壁纸管理" class="table-card">
      <template #extra>
        <a-space>
          <span>共 {{ pagination.total }} 条数据</span>
        </a-space>
      </template>

      <a-table
        :columns="columns"
        :data-source="wallpaperList"
        :pagination="paginationConfig"
        :loading="loading"
        :row-selection="rowSelection"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'image'">
            <img
              :src="record.thumbnailUrl || record.url"
              :alt="record.name"
              class="table-image"
              @click="previewImage(record)"
            />
          </template>
          
          <template v-if="column.key === 'name'">
            <div class="name-cell">
              <div class="name-text">{{ record.name }}</div>
              <div v-if="record.description" class="description-text">
                {{ record.description }}
              </div>
            </div>
          </template>

          <template v-if="column.key === 'category'">
            <a-tag color="blue">{{ record.category }}</a-tag>
          </template>

          <template v-if="column.key === 'tags'">
            <div class="tags-cell">
              <a-tag
                v-for="tag in record.tags?.slice(0, 2)"
                :key="tag"
                size="small"
              >
                {{ tag }}
              </a-tag>
              <a-tag v-if="record.tags && record.tags.length > 2" size="small">
                +{{ record.tags.length - 2 }}
              </a-tag>
            </div>
          </template>

          <template v-if="column.key === 'stats'">
            <div class="stats-cell">
              <div>
                <DownloadOutlined />
                {{ record.downloadCount || 0 }}
              </div>
              <div>
                <HeartOutlined />
                {{ record.likeCount || 0 }}
              </div>
            </div>
          </template>

          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 0 ? 'green' : 'red'">
              {{ record.status === 0 ? '正常' : '隐藏' }}
            </a-tag>
          </template>

          <template v-if="column.key === 'createTime'">
            {{ formatTime(record.createTime) }}
          </template>

          <template v-if="column.key === 'action'">
            <a-space>
              <a-button
                type="link"
                size="small"
                @click="searchImage(record)"
              >
                <SearchOutlined />
                搜索
              </a-button>
              <a-button
                type="link"
                size="small"
                @click="toggleStatus(record)"
              >
                {{ record.status === 0 ? '隐藏' : '显示' }}
              </a-button>
              <a-button
                type="link"
                size="small"
                @click="viewDetail(record)"
              >
                查看
              </a-button>
              <a-popconfirm
                title="确定要删除这张壁纸吗？"
                @confirm="deleteWallpaper(record)"
              >
                <a-button
                  type="link"
                  size="small"
                  danger
                >
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <!-- 图片预览模态框 -->
    <a-modal
      v-model:open="previewVisible"
      :title="previewWallpaper?.name"
      :footer="null"
      width="80%"
      centered
    >
      <div v-if="previewWallpaper" class="preview-content">
        <img
          :src="previewWallpaper.url"
          :alt="previewWallpaper.name"
          style="max-width: 100%; height: auto;"
        />
      </div>
    </a-modal>

    <!-- 详情模态框 -->
    <a-modal
      v-model:open="detailVisible"
      :title="detailWallpaper?.name"
      :footer="null"
      width="800px"
    >
      <div v-if="detailWallpaper" class="detail-content">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="ID">{{ detailWallpaper.id }}</a-descriptions-item>
          <a-descriptions-item label="名称">{{ detailWallpaper.name }}</a-descriptions-item>
          <a-descriptions-item label="分类">{{ detailWallpaper.category }}</a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="detailWallpaper.status === 0 ? 'green' : 'red'">
              {{ detailWallpaper.status === 0 ? '正常' : '隐藏' }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="尺寸">
            {{ detailWallpaper.width }}×{{ detailWallpaper.height }}
          </a-descriptions-item>
          <a-descriptions-item label="文件大小">
            {{ formatFileSize(detailWallpaper.fileSize) }}
          </a-descriptions-item>
          <a-descriptions-item label="下载次数">{{ detailWallpaper.downloadCount || 0 }}</a-descriptions-item>
          <a-descriptions-item label="点赞数">{{ detailWallpaper.likeCount || 0 }}</a-descriptions-item>
          <a-descriptions-item label="创建时间" :span="2">
            {{ formatTime(detailWallpaper.createTime!) }}
          </a-descriptions-item>
          <a-descriptions-item label="描述" :span="2">
            {{ detailWallpaper.description || '暂无描述' }}
          </a-descriptions-item>
          <a-descriptions-item label="标签" :span="2">
            <a-tag v-for="tag in detailWallpaper.tags" :key="tag">{{ tag }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="图片URL" :span="2">
            <a :href="detailWallpaper.url" target="_blank">{{ detailWallpaper.url }}</a>
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </a-modal>

    <!-- 批量编辑弹窗 -->
    <BatchEditWallpaperModal
      ref="batchEditWallpaperModalRef"
      :wallpaperList="selectedWallpapers"
      :onSuccess="onBatchEditSuccess"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, h, computed } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'
import {
  PlusOutlined,
  ReloadOutlined,
  SearchOutlined,
  DownloadOutlined,
  HeartOutlined,
  EditOutlined
} from '@ant-design/icons-vue'
import { listAllWallpapersByPageUsingPost, deleteWallpaperUsingPost } from '@/api/bizhiguanli'
import PictureSearchForm from '@/components/PictureSearchForm.vue'
import BatchEditWallpaperModal from '@/components/BatchEditWallpaperModal.vue'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const wallpaperList = ref<API.WallpaperVO[]>([])
const previewVisible = ref(false)
const detailVisible = ref(false)
const previewWallpaper = ref<API.WallpaperVO | null>(null)
const detailWallpaper = ref<API.WallpaperVO | null>(null)

// 批量编辑相关
const selectedRowKeys = ref<number[]>([])
const batchEditWallpaperModalRef = ref()

// 搜索表单
const searchForm = ref<API.WallpaperQueryRequest>({
  current: 1,
  pageSize: 10,
})

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0
})

const paginationConfig = {
  current: pagination.current,
  pageSize: pagination.pageSize,
  total: pagination.total,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number, range: [number, number]) => 
    `第 ${range[0]}-${range[1]} 条，共 ${total} 条`
}

// 表格行选择配置
const rowSelection = {
  selectedRowKeys: selectedRowKeys,
  onChange: (keys: number[]) => {
    selectedRowKeys.value = keys
  },
  getCheckboxProps: (record: API.WallpaperVO) => ({
    name: record.name,
  }),
}

// 选中的壁纸列表
const selectedWallpapers = computed(() => {
  return wallpaperList.value.filter(wallpaper => 
    selectedRowKeys.value.includes(wallpaper.id!)
  )
})

// 表格列配置
const columns = [
  {
    title: '图片',
    key: 'image',
    width: 100,
    align: 'center'
  },
  {
    title: '名称',
    key: 'name',
    width: 200
  },
  {
    title: '分类',
    key: 'category',
    width: 100
  },
  {
    title: '标签',
    key: 'tags',
    width: 150
  },
  {
    title: '统计',
    key: 'stats',
    width: 100,
    align: 'center'
  },
  {
    title: '状态',
    key: 'status',
    width: 80,
    align: 'center'
  },
  {
    title: '创建时间',
    key: 'createTime',
    width: 150
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    align: 'center'
  }
]

// 页面加载
onMounted(() => {
  loadWallpapers()
})

// 加载壁纸数据
const loadWallpapers = async () => {
  loading.value = true
  
  try {
    const queryRequest = {
      ...searchForm.value,
      current: pagination.current,
      pageSize: pagination.pageSize,
    }
    
    const response = await listAllWallpapersByPageUsingPost(queryRequest)
    
    if (response.data?.code === 0 && response.data.data) {
      const pageData = response.data.data
      wallpaperList.value = pageData.records || []
      pagination.total = pageData.total || 0
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

// 搜索
const handleSearch = (newSearchParams: API.WallpaperQueryRequest) => {
  searchForm.value = {
    ...newSearchParams,
    current: 1,
    pageSize: pagination.pageSize,
  }
  pagination.current = 1
  loadWallpapers()
}

// 表格变化处理
const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadWallpapers()
}

// 刷新数据
const refreshData = () => {
  loadWallpapers()
}

// 跳转到批量抓取页面
const goToBatchCrawl = () => {
  router.push('/admin/wallpaper/batch-crawl')
}

// 以图搜图 - 直接打开百度识图页面
const searchImage = (wallpaper: API.WallpaperVO) => {
  if (!wallpaper.url) {
    message.error('图片地址无效')
    return
  }
  // 构建百度识图 URL
  const baiduImageSearchUrl = `https://graph.baidu.com/details?isfromtusoupc=1&tn=pc&carousel=0&promotion_name=pc_image_shituindex&extUiData%5bisLogoShow%5d=1&image=${encodeURIComponent(wallpaper.url)}`
  window.open(baiduImageSearchUrl, '_blank')
}

// 预览图片
const previewImage = (wallpaper: API.WallpaperVO) => {
  previewWallpaper.value = wallpaper
  previewVisible.value = true
}

// 查看详情
const viewDetail = (wallpaper: API.WallpaperVO) => {
  detailWallpaper.value = wallpaper
  detailVisible.value = true
}

// 切换状态
const toggleStatus = async (wallpaper: API.WallpaperVO) => {
  try {
    // 这里应该调用更新状态的API
    // 暂时只是前端更新
    const index = wallpaperList.value.findIndex(item => item.id === wallpaper.id)
    if (index !== -1) {
      wallpaperList.value[index].status = wallpaper.status === 0 ? 1 : 0
    }
    message.success('状态更新成功')
  } catch (error) {
    message.error('状态更新失败')
  }
}

// 删除壁纸
const deleteWallpaper = async (wallpaper: API.WallpaperVO) => {
  try {
    const response = await deleteWallpaperUsingPost({ id: wallpaper.id })
    
    if (response.data?.code === 0) {
      // 从列表中移除
      const index = wallpaperList.value.findIndex(item => item.id === wallpaper.id)
      if (index !== -1) {
        wallpaperList.value.splice(index, 1)
        pagination.total--
      }
      message.success('删除成功')
    } else {
      message.error('删除失败：' + (response.data?.message || '未知错误'))
    }
  } catch (error) {
    console.error('删除壁纸失败：', error)
    message.error('删除失败，请稍后重试')
  }
}

// 格式化时间
const formatTime = (time: string) => {
  return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
}

// 格式化文件大小
const formatFileSize = (size: number | undefined) => {
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

// 批量编辑相关函数
const doBatchEdit = () => {
  if (selectedRowKeys.value.length === 0) {
    message.warning('请先选择要编辑的壁纸')
    return
  }
  
  if (batchEditWallpaperModalRef.value) {
    batchEditWallpaperModalRef.value.openModal()
  }
}

// 批量编辑成功后，刷新数据
const onBatchEditSuccess = () => {
  selectedRowKeys.value = []
  loadWallpapers()
}
</script>

<style scoped lang="less">
.wallpaper-manage-page {
  padding: 24px;
}

.action-bar {
  margin-bottom: 16px;
}


.table-card {
  .table-image {
    width: 60px;
    height: 40px;
    object-fit: cover;
    border-radius: 4px;
    cursor: pointer;
    transition: transform 0.2s;

    &:hover {
      transform: scale(1.1);
    }
  }

  .name-cell {
    .name-text {
      font-weight: 500;
      margin-bottom: 4px;
    }

    .description-text {
      color: #666;
      font-size: 12px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .tags-cell {
    display: flex;
    flex-wrap: wrap;
    gap: 4px;
  }

  .stats-cell {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    font-size: 12px;

    > div {
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
}

.preview-content {
  text-align: center;
}

.detail-content {
  max-height: 600px;
  overflow-y: auto;
}
</style>
