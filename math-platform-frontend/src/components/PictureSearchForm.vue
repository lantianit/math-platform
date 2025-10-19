<template>
  <div class="picture-search-form">
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <!-- 基础搜索条件 -->
      <div class="search-group basic-search">
        <a-form-item label="关键词" name="searchText">
          <a-input
            v-model:value="searchParams.searchText"
            placeholder="从名称和简介搜索"
            allow-clear
            style="min-width: 200px"
          />
        </a-form-item>

        <a-form-item label="分类" name="category">
          <a-select
            v-model:value="searchParams.category"
            style="min-width: 150px"
            :options="categoryOptions"
            placeholder="请选择分类"
            allowClear
          />
        </a-form-item>

        <a-form-item label="标签" name="tags">
          <a-select
            v-model:value="searchParams.tags"
            style="min-width: 200px"
            :options="tagOptions"
            mode="tags"
            placeholder="请输入标签"
            allowClear
          />
        </a-form-item>

        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit" style="width: 96px">
              <SearchOutlined />
              搜索
            </a-button>
            <a-button html-type="reset" @click="doClear">重置</a-button>
            <a-button type="link" @click="toggleAdvanced">
              {{ showAdvanced ? '收起' : '展开' }}
              <template v-if="showAdvanced">
                <UpOutlined />
              </template>
              <template v-else>
                <DownOutlined />
              </template>
            </a-button>
          </a-space>
        </a-form-item>
      </div>

      <!-- 详细搜索条件 -->
      <Transition name="slide-fade">
        <div v-show="showAdvanced" class="search-group advanced-search">
          <a-divider style="margin: 12px 0" />
          
          <a-form-item label="日期范围" name="dateRange">
            <a-range-picker
              style="width: 400px"
              show-time
              v-model:value="dateRange"
              :placeholder="['编辑开始日期', '编辑结束时间']"
              format="YYYY/MM/DD HH:mm:ss"
              :presets="rangePresets"
              @change="onRangeChange"
            />
          </a-form-item>

          <a-form-item label="图片名称" name="name">
            <a-input
              v-model:value="searchParams.name"
              placeholder="请输入图片名称"
              allow-clear
              style="min-width: 200px"
            />
          </a-form-item>

          <a-form-item label="图片简介" name="introduction">
            <a-input
              v-model:value="searchParams.introduction"
              placeholder="请输入图片简介"
              allow-clear
              style="min-width: 200px"
            />
          </a-form-item>

          <a-form-item label="图片尺寸" name="picSize">
            <a-select
              v-model:value="picSizePreset"
              style="min-width: 150px"
              :options="picSizeOptions"
              placeholder="请选择尺寸"
              allowClear
              @change="onPicSizeChange"
            />
          </a-form-item>

          <a-form-item label="图片宽度" name="picWidth">
            <a-input-number
              v-model:value="searchParams.picWidth"
              placeholder="宽度"
              :min="0"
              style="width: 120px"
            />
          </a-form-item>

          <a-form-item label="图片高度" name="picHeight">
            <a-input-number
              v-model:value="searchParams.picHeight"
              placeholder="高度"
              :min="0"
              style="width: 120px"
            />
          </a-form-item>

          <a-form-item label="图片格式" name="picFormat">
            <a-select
              v-model:value="searchParams.picFormat"
              style="min-width: 120px"
              :options="formatOptions"
              placeholder="请选择格式"
              allowClear
            />
          </a-form-item>
        </div>
      </Transition>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { SearchOutlined, DownOutlined, UpOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import type { Dayjs } from 'dayjs'

// 定义组件属性
interface Props {
  onSearch?: (searchParams: API.WallpaperQueryRequest) => void
}

const props = defineProps<Props>()

// 搜索条件
const searchParams = reactive<API.WallpaperQueryRequest>({
  searchText: undefined,
  name: undefined,
  introduction: undefined,
  category: undefined,
  tags: undefined,
  picWidth: undefined,
  picHeight: undefined,
  picFormat: undefined,
  startEditTime: undefined,
  endEditTime: undefined,
})

// 是否展示高级搜索
const showAdvanced = ref(false)

// 日期范围
const dateRange = ref<[Dayjs, Dayjs] | undefined>(undefined)

// 图片尺寸预设
const picSizePreset = ref<string | undefined>(undefined)

// 分类选项
const categoryOptions = ref<{ value: string; label: string }[]>([
  { value: '学习励志', label: '学习励志' },
  { value: '奋斗拼搏', label: '奋斗拼搏' },
  { value: '青春梦想', label: '青春梦想' },
  { value: '书籍文字', label: '书籍文字' },
])

// 标签选项
const tagOptions = ref<{ value: string; label: string }[]>([])

// 图片格式选项
const formatOptions = ref([
  { value: 'jpg', label: 'JPG' },
  { value: 'jpeg', label: 'JPEG' },
  { value: 'png', label: 'PNG' },
  { value: 'gif', label: 'GIF' },
  { value: 'webp', label: 'WEBP' },
  { value: 'svg', label: 'SVG' },
])

// 图片尺寸预设选项
const picSizeOptions = ref([
  { value: 'small', label: '小 (最大800x600)' },
  { value: 'medium', label: '中 (最大1920x1080)' },
  { value: 'large', label: '大 (最大2560x1440)' },
  { value: 'xlarge', label: '特大 (2560x1440以上)' },
])

// 日期范围预设
const rangePresets = ref([
  { label: '过去 7 天', value: [dayjs().add(-7, 'd'), dayjs()] },
  { label: '过去 14 天', value: [dayjs().add(-14, 'd'), dayjs()] },
  { label: '过去 30 天', value: [dayjs().add(-30, 'd'), dayjs()] },
  { label: '过去 90 天', value: [dayjs().add(-90, 'd'), dayjs()] },
])

/**
 * 日期范围更改时触发
 */
const onRangeChange = (dates: [Dayjs, Dayjs] | null) => {
  if (!dates || dates.length < 2) {
    searchParams.startEditTime = undefined
    searchParams.endEditTime = undefined
  } else {
    searchParams.startEditTime = dates[0].toDate() as any
    searchParams.endEditTime = dates[1].toDate() as any
  }
}

/**
 * 图片尺寸预设更改时触发
 */
const onPicSizeChange = (value: string) => {
  if (!value) {
    searchParams.picWidth = undefined
    searchParams.picHeight = undefined
    return
  }

  // 根据预设值设置宽高的最大值
  // 这里使用小于等于的逻辑，需要在后端修改为范围查询
  switch (value) {
    case 'small':
      searchParams.picWidth = 800
      searchParams.picHeight = 600
      break
    case 'medium':
      searchParams.picWidth = 1920
      searchParams.picHeight = 1080
      break
    case 'large':
      searchParams.picWidth = 2560
      searchParams.picHeight = 1440
      break
    case 'xlarge':
      // 特大尺寸：不设置上限，只要大于 2560x1440
      searchParams.picWidth = 2560
      searchParams.picHeight = 1440
      break
  }
}

/**
 * 切换高级搜索
 */
const toggleAdvanced = () => {
  showAdvanced.value = !showAdvanced.value
}

/**
 * 执行搜索
 */
const doSearch = () => {
  props.onSearch?.(searchParams)
}

/**
 * 清空搜索条件
 */
const doClear = () => {
  // 重置所有搜索参数
  Object.keys(searchParams).forEach((key) => {
    searchParams[key] = undefined
  })
  dateRange.value = undefined
  picSizePreset.value = undefined
  props.onSearch?.(searchParams)
}

/**
 * 获取标签选项（可以从后端API获取）
 */
const loadTagOptions = async () => {
  // 这里可以调用后端API获取标签列表
  // 目前使用静态数据
  tagOptions.value = [
    { value: '励志', label: '励志' },
    { value: '学习', label: '学习' },
    { value: '奋斗', label: '奋斗' },
    { value: '梦想', label: '梦想' },
    { value: '读书', label: '读书' },
    { value: '文字', label: '文字' },
  ]
}

onMounted(() => {
  loadTagOptions()
})
</script>

<style scoped lang="scss">
.picture-search-form {
  background: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);

  .search-group {
    width: 100%;

    :deep(.ant-form-item) {
      margin-top: 16px;
      margin-bottom: 0;
    }
  }

  .basic-search {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  .advanced-search {
    :deep(.ant-form-item) {
      margin-top: 16px;
    }
  }

  :deep(.ant-form-inline) {
    .ant-form-item {
      margin-right: 16px;
    }
  }
}

/* 过渡动画 */
.slide-fade-enter-active {
  transition: all 0.3s ease-out;
}

.slide-fade-leave-active {
  transition: all 0.3s cubic-bezier(1, 0.5, 0.8, 1);
}

.slide-fade-enter-from,
.slide-fade-leave-to {
  transform: translateY(-20px);
  opacity: 0;
}
</style>

