<template>
  <div id="searchPicturePage">
    <h2 style="margin-bottom: 16px">以图搜图</h2>
    
    <a-spin :spinning="loading">
      <h3 style="margin: 16px 0">原图</h3>
      <a-card style="width: 240px">
        <template #cover>
          <img
            style="height: 180px; object-fit: cover"
            :alt="picture.name"
            :src="picture.thumbnailUrl ?? picture.url"
          />
        </template>
        <a-card-meta :title="picture.name" />
      </a-card>
      
      <h3 style="margin: 16px 0">识图结果 (共 {{ dataList.length }} 张)</h3>
      
      <a-empty v-if="!loading && dataList.length === 0" description="暂无搜索结果" />
      
      <!-- 图片列表 -->
      <a-list
        v-if="dataList.length > 0"
        :grid="{ gutter: 16, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
        :data-source="dataList"
      >
        <template #renderItem="{ item }">
          <a-list-item style="padding: 0">
            <a :href="item.fromUrl" target="_blank">
              <a-card hoverable>
                <template #cover>
                  <img 
                    style="height: 180px; object-fit: cover" 
                    :src="item.thumbUrl"
                    :alt="item.fromUrl"
                  />
                </template>
                <a-card-meta>
                  <template #description>
                    <a-tooltip :title="item.fromUrl">
                      <div class="url-text">{{ item.fromUrl }}</div>
                    </a-tooltip>
                  </template>
                </a-card-meta>
              </a-card>
            </a>
          </a-list-item>
        </template>
      </a-list>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { searchPictureByPictureUsingPost } from '@/api/bizhiguanli'

const route = useRoute()

// 图片 id
const pictureId = computed(() => {
  return route.query?.pictureId
})

const picture = ref<API.WallpaperVO>({})
const dataList = ref<API.ImageSearchResult[]>([])
const loading = ref(false)

// 获取老数据
const getOldPicture = async () => {
  // 获取数据
  const id = route.query?.pictureId
  if (id) {
    try {
      const { getWallpaperByIdUsingGet } = await import('@/api/bizhiguanli')
      const res = await getWallpaperByIdUsingGet({
        id: id as unknown as number,
      })
      if (res.data.code === 0 && res.data.data) {
        const data = res.data.data
        picture.value = data
      }
    } catch (error) {
      console.error('获取原图失败：', error)
    }
  }
}

// 获取搜图结果
const fetchData = async () => {
  if (!pictureId.value) {
    message.error('缺少图片ID参数')
    return
  }
  
  loading.value = true
  try {
    const res = await searchPictureByPictureUsingPost({
      pictureId: pictureId.value as unknown as number,
    })
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data ?? []
      if (dataList.value.length === 0) {
        message.info('未找到相似图片')
      }
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } catch (error) {
    console.error('搜索失败：', error)
    message.error('搜索失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 页面加载时请求一次
onMounted(() => {
  getOldPicture()
  fetchData()
})
</script>

<style scoped lang="less">
#searchPicturePage {
  padding: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.url-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 12px;
  color: #666;
}

:deep(.ant-card-meta-description) {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>

