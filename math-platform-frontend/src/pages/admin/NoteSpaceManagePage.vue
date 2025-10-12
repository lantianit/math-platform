<template>
  <div class="note-space-manage-page">
    <a-card title="笔记空间管理">
      <a-form layout="inline" :model="searchParams" style="margin-bottom: 16px">
        <a-form-item label="空间名称">
          <a-input
            v-model:value="searchParams.spaceName"
            placeholder="请输入空间名称"
            style="width: 200px"
            @press-enter="loadDataList"
          />
        </a-form-item>
        <a-form-item label="用户ID">
          <a-input-number
            v-model:value="searchParams.userId"
            placeholder="请输入用户ID"
            style="width: 150px"
          />
        </a-form-item>
        <a-form-item label="空间级别">
          <a-select
            v-model:value="searchParams.spaceLevel"
            placeholder="请选择级别"
            style="width: 120px"
            allow-clear
          >
            <a-select-option :value="0">普通版</a-select-option>
            <a-select-option :value="1">专业版</a-select-option>
            <a-select-option :value="2">旗舰版</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="loadDataList">搜索</a-button>
            <a-button @click="resetSearch">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>

      <a-table
        :columns="columns"
        :data-source="dataList"
        :pagination="{
          current: current,
          pageSize: pageSize,
          total: total,
          showSizeChanger: true,
          showTotal: (total: number) => `共 ${total} 条`,
          onChange: onPageChange,
          onShowSizeChange: onPageSizeChange,
        }"
        :loading="loading"
        :scroll="{ x: 1200 }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'spaceLevel'">
            <a-tag :color="getLevelColor(record.spaceLevel)">
              {{ getLevelText(record.spaceLevel) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'usage'">
            <div>
              <div>
                笔记：{{ record.totalNoteCount }} / {{ record.maxNoteCount }}
              </div>
              <a-progress
                :percent="getUsagePercent(record.totalNoteCount, record.maxNoteCount)"
                :status="getProgressStatus(record.totalNoteCount, record.maxNoteCount)"
                size="small"
              />
              <div style="margin-top: 4px">
                存储：{{ formatSize(record.totalSize) }} / {{ formatSize(record.maxSize) }}
              </div>
              <a-progress
                :percent="getUsagePercent(record.totalSize, record.maxSize)"
                :status="getProgressStatus(record.totalSize, record.maxSize)"
                size="small"
              />
            </div>
          </template>
          <template v-else-if="column.key === 'createTime'">
            {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm') }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="viewDetail(record)">详情</a-button>
              <a-button type="link" size="small" @click="editSpace(record)">编辑</a-button>
              <a-button
                type="link"
                size="small"
                danger
                @click="deleteSpace(record)"
              >
                删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import dayjs from 'dayjs'
import {
  listNoteSpaceByPageUsingPost,
  deleteNoteSpaceUsingPost,
} from '@/api/noteSpaceController'
import { NOTE_SPACE_LEVEL_MAP, formatSize } from '@/constants/noteSpace'

const router = useRouter()

const loading = ref(false)
const dataList = ref<API.NoteSpace[]>([])
const current = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchParams = reactive({
  spaceName: '',
  userId: undefined as number | undefined,
  spaceLevel: undefined as number | undefined,
})

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id', width: 80 },
  { title: '空间名称', dataIndex: 'spaceName', key: 'spaceName', width: 150 },
  { title: '级别', key: 'spaceLevel', width: 100 },
  { title: '用户ID', dataIndex: 'userId', key: 'userId', width: 100 },
  { title: '使用情况', key: 'usage', width: 200 },
  { title: '创建时间', key: 'createTime', width: 150 },
  { title: '操作', key: 'action', width: 200, fixed: 'right' },
]

// 加载数据
const loadDataList = async () => {
  loading.value = true
  try {
    const res = await listNoteSpaceByPageUsingPost({
      ...searchParams,
      current: current.value,
      pageSize: pageSize.value,
    })
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
  } catch (error) {
    message.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchParams.spaceName = ''
  searchParams.userId = undefined
  searchParams.spaceLevel = undefined
  current.value = 1
  loadDataList()
}

// 获取级别文本
const getLevelText = (level: number) => {
  return NOTE_SPACE_LEVEL_MAP[level || 0]
}

// 获取级别颜色
const getLevelColor = (level: number) => {
  const colors = ['blue', 'gold', 'red']
  return colors[level] || 'blue'
}

// 计算使用百分比
const getUsagePercent = (current: number, max: number) => {
  if (!max) return 0
  return Math.floor((current / max) * 100)
}

// 获取进度条状态
const getProgressStatus = (current: number, max: number) => {
  const percent = getUsagePercent(current, max)
  if (percent >= 90) return 'exception'
  if (percent >= 70) return 'normal'
  return 'success'
}

// 查看详情
const viewDetail = (record: API.NoteSpace) => {
  router.push(`/note/space/${record.id}`)
}

// 编辑空间
const editSpace = (record: API.NoteSpace) => {
  router.push(`/note/space/edit/${record.id}`)
}

// 删除空间
const deleteSpace = (record: API.NoteSpace) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除笔记空间"${record.spaceName}"吗？空间内的所有笔记也将被删除！`,
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        const res = await deleteNoteSpaceUsingPost({ id: record.id })
        if (res.data.code === 0) {
          message.success('删除成功')
          loadDataList()
        } else {
          message.error(res.data.message || '删除失败')
        }
      } catch (error) {
        message.error('删除失败')
      }
    },
  })
}

// 分页变化
const onPageChange = (page: number, size: number) => {
  current.value = page
  pageSize.value = size
  loadDataList()
}

// 每页条数变化
const onPageSizeChange = (page: number, size: number) => {
  current.value = 1
  pageSize.value = size
  loadDataList()
}

onMounted(() => {
  loadDataList()
})
</script>

<style scoped lang="scss">
.note-space-manage-page {
  padding: 0;
}
</style>

