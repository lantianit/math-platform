<template>
  <div class="note-space-detail-page">
    <a-spin :spinning="loading">
      <a-card v-if="spaceInfo" class="space-header">
        <a-page-header
          :title="spaceInfo.spaceName"
          :sub-title="getLevelText(spaceInfo.spaceLevel)"
          @back="goBack"
        >
          <template #extra>
            <a-space>
              <a-button type="primary" @click="goToCreateNote">
                <template #icon><PlusOutlined /></template>
                创建笔记
              </a-button>
              <a-button @click="editSpace">
                <template #icon><EditOutlined /></template>
                编辑空间
              </a-button>
            </a-space>
          </template>
          <a-descriptions size="small" :column="2">
            <a-descriptions-item label="笔记数量">
              {{ spaceInfo.totalNoteCount }} / {{ spaceInfo.maxNoteCount }}
              <a-progress
                :percent="getUsagePercent(spaceInfo.totalNoteCount, spaceInfo.maxNoteCount)"
                :status="getProgressStatus(spaceInfo.totalNoteCount, spaceInfo.maxNoteCount)"
                size="small"
                style="margin-left: 8px; max-width: 200px"
              />
            </a-descriptions-item>
            <a-descriptions-item label="存储空间">
              {{ formatSize(spaceInfo.totalSize) }} / {{ formatSize(spaceInfo.maxSize) }}
              <a-progress
                :percent="getUsagePercent(spaceInfo.totalSize, spaceInfo.maxSize)"
                :status="getProgressStatus(spaceInfo.totalSize, spaceInfo.maxSize)"
                size="small"
                style="margin-left: 8px; max-width: 200px"
              />
            </a-descriptions-item>
          </a-descriptions>
        </a-page-header>
      </a-card>

      <a-card class="note-list-card" title="笔记列表">
        <template #extra>
          <a-input-search
            v-model:value="searchText"
            placeholder="搜索笔记标题"
            style="width: 200px"
            @search="loadNoteList"
          />
        </template>

        <a-list
          :data-source="noteList"
          :pagination="{
            current: current,
            pageSize: pageSize,
            total: total,
            showSizeChanger: false,
            onChange: onPageChange,
          }"
        >
          <template #renderItem="{ item }">
            <a-list-item>
              <template #actions>
                <a @click="viewNote(item)">查看</a>
                <a @click="editNote(item)">编辑</a>
                <a @click="deleteNote(item)" style="color: red">删除</a>
              </template>
              <a-list-item-meta>
                <template #title>
                  <a @click="viewNote(item)">{{ item.title }}</a>
                </template>
                <template #description>
                  <div>
                    <a-tag>{{ item.category }}</a-tag>
                    <span style="margin-left: 8px; color: #999">
                      创建时间：{{ dayjs(item.createTime).format('YYYY-MM-DD HH:mm') }}
                    </span>
                    <span style="margin-left: 8px; color: #999">
                      浏览：{{ item.viewCount }} | 点赞：{{ item.likeCount }}
                    </span>
                  </div>
                </template>
              </a-list-item-meta>
            </a-list-item>
          </template>
        </a-list>

        <div v-if="noteList.length === 0 && !loading" class="empty-state">
          <a-empty description="暂无笔记">
            <a-button type="primary" @click="goToCreateNote">创建第一篇笔记</a-button>
          </a-empty>
        </div>
      </a-card>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import { PlusOutlined, EditOutlined } from '@ant-design/icons-vue'
import dayjs from 'dayjs'
import { getNoteSpaceVoByIdUsingGet } from '@/api/noteSpaceController'
import { listNoteVoByPageUsingPost, deleteNoteUsingPost } from '@/api/noteController'
import { NOTE_SPACE_LEVEL_MAP, formatSize } from '@/constants/noteSpace'

const router = useRouter()
const route = useRoute()

const spaceId = Number(route.params.id)

const loading = ref(false)
const spaceInfo = ref<API.NoteSpaceVO>()
const noteList = ref<API.NoteVO[]>([])
const searchText = ref('')
const current = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 加载空间信息
const loadSpaceInfo = async () => {
  try {
    const res = await getNoteSpaceVoByIdUsingGet({ id: spaceId })
    if (res.data.code === 0) {
      spaceInfo.value = res.data.data
    }
  } catch (error) {
    message.error('加载失败')
  }
}

// 加载笔记列表
const loadNoteList = async () => {
  loading.value = true
  try {
    const res = await listNoteVoByPageUsingPost({
      spaceId: spaceId,
      title: searchText.value,
      current: current.value,
      pageSize: pageSize.value,
    })
    if (res.data.code === 0 && res.data.data) {
      noteList.value = res.data.data.records || []
      total.value = res.data.data.total || 0
    }
  } catch (error) {
    message.error('加载失败')
  } finally {
    loading.value = false
  }
}

// 获取级别文本
const getLevelText = (level?: number) => {
  return NOTE_SPACE_LEVEL_MAP[level || 0]
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

// 返回
const goBack = () => {
  router.back()
}

// 编辑空间
const editSpace = () => {
  router.push(`/note/space/edit/${spaceId}`)
}

// 创建笔记
const goToCreateNote = () => {
  router.push(`/note/add?spaceId=${spaceId}`)
}

// 查看笔记
const viewNote = (note: API.NoteVO) => {
  router.push(`/note/${note.id}`)
}

// 编辑笔记
const editNote = (note: API.NoteVO) => {
  router.push(`/note/edit/${note.id}`)
}

// 删除笔记
const deleteNote = (note: API.NoteVO) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除笔记"${note.title}"吗？`,
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        const res = await deleteNoteUsingPost({ id: note.id })
        if (res.data.code === 0) {
          message.success('删除成功')
          loadNoteList()
          loadSpaceInfo()
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
const onPageChange = (page: number) => {
  current.value = page
  loadNoteList()
}

onMounted(() => {
  loadSpaceInfo()
  loadNoteList()
})
</script>

<style scoped lang="scss">
.note-space-detail-page {
  .space-header {
    margin-bottom: 16px;
  }

  .note-list-card {
    min-height: 400px;
  }

  .empty-state {
    padding: 40px 0;
    text-align: center;
  }
}
</style>

