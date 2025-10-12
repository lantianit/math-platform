<template>
  <div class="my-notes-page">
    <a-card title="我的笔记" class="page-header">
      <template #extra>
        <a-space>
          <a-button type="primary" @click="goToCreateSpace">
            <template #icon><PlusOutlined /></template>
            创建笔记空间
          </a-button>
          <a-button @click="goToPublicNotes">
            <template #icon><FileTextOutlined /></template>
            查看公共笔记
          </a-button>
        </a-space>
      </template>
      <a-typography-paragraph type="secondary">
        管理您的私人笔记空间，支持分级存储和无限扩展
      </a-typography-paragraph>
    </a-card>

    <a-spin :spinning="loading" tip="加载中...">
      <div class="space-list">
        <a-row :gutter="[16, 16]">
          <a-col
            v-for="space in spaceList"
            :key="space.id"
            :xs="24"
            :sm="12"
            :md="8"
            :lg="6"
          >
            <a-card hoverable class="space-card" @click="goToSpaceDetail(space.id)">
              <template #cover>
                <div class="space-cover">
                  <div class="level-badge" :class="`level-${space.spaceLevel}`">
                    {{ getLevelText(space.spaceLevel) }}
                  </div>
                </div>
              </template>
              <a-card-meta :title="space.spaceName">
                <template #description>
                  <div class="space-info">
                    <p>笔记数：{{ space.totalNoteCount }} / {{ space.maxNoteCount }}</p>
                    <a-progress
                      :percent="getUsagePercent(space.totalNoteCount, space.maxNoteCount)"
                      :status="getProgressStatus(space.totalNoteCount, space.maxNoteCount)"
                      size="small"
                    />
                    <p style="margin-top: 8px">
                      存储：{{ formatSize(space.totalSize) }} / {{ formatSize(space.maxSize) }}
                    </p>
                    <a-progress
                      :percent="getUsagePercent(space.totalSize, space.maxSize)"
                      :status="getProgressStatus(space.totalSize, space.maxSize)"
                      size="small"
                    />
                  </div>
                </template>
              </a-card-meta>
              <template #actions>
                <EditOutlined key="edit" @click.stop="editSpace(space)" />
                <DeleteOutlined key="delete" @click.stop="deleteSpace(space)" />
              </template>
            </a-card>
          </a-col>
        </a-row>

        <div v-if="spaceList.length === 0 && !loading" class="empty-state">
          <a-empty description="还没有笔记空间">
            <a-button type="primary" @click="goToCreateSpace">立即创建</a-button>
          </a-empty>
        </div>
      </div>
    </a-spin>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { message, Modal } from 'ant-design-vue'
import {
  PlusOutlined,
  FileTextOutlined,
  EditOutlined,
  DeleteOutlined,
} from '@ant-design/icons-vue'
import {
  listNoteSpaceVoByPageUsingPost,
  deleteNoteSpaceUsingPost,
} from '@/api/noteSpaceController'
import { NOTE_SPACE_LEVEL_MAP, formatSize } from '@/constants/noteSpace'
import { useLoginUserStore } from '@/stores/useLoginUserStore'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const loading = ref(false)
const spaceList = ref<API.NoteSpaceVO[]>([])

// 获取笔记空间列表
const loadSpaceList = async () => {
  loading.value = true
  try {
    const res = await listNoteSpaceVoByPageUsingPost({
      userId: loginUserStore.loginUser?.id,
      current: 1,
      pageSize: 20,
    })
    if (res.data.code === 0) {
      spaceList.value = res.data.data?.records || []
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

// 跳转到创建空间
const goToCreateSpace = () => {
  router.push('/note/space/add')
}

// 跳转到公共笔记
const goToPublicNotes = () => {
  router.push('/note/public')
}

// 跳转到空间详情
const goToSpaceDetail = (spaceId?: number) => {
  router.push(`/note/space/${spaceId}`)
}

// 编辑空间
const editSpace = (space: API.NoteSpaceVO) => {
  router.push(`/note/space/edit/${space.id}`)
}

// 删除空间
const deleteSpace = (space: API.NoteSpaceVO) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除笔记空间"${space.spaceName}"吗？空间内的所有笔记也将被删除！`,
    okText: '确认',
    cancelText: '取消',
    onOk: async () => {
      try {
        const res = await deleteNoteSpaceUsingPost({ id: space.id })
        if (res.data.code === 0) {
          message.success('删除成功')
          loadSpaceList()
        } else {
          message.error(res.data.message || '删除失败')
        }
      } catch (error) {
        message.error('删除失败')
      }
    },
  })
}

onMounted(() => {
  loadSpaceList()
})
</script>

<style scoped lang="scss">
.my-notes-page {
  .page-header {
    margin-bottom: 24px;
  }

  .space-list {
    min-height: 400px;
  }

  .space-card {
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
    }

    .space-cover {
      height: 120px;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      position: relative;

      .level-badge {
        position: absolute;
        top: 12px;
        right: 12px;
        padding: 4px 12px;
        border-radius: 12px;
        color: white;
        font-size: 12px;
        font-weight: bold;

        &.level-0 {
          background: rgba(255, 255, 255, 0.3);
        }

        &.level-1 {
          background: rgba(255, 215, 0, 0.8);
        }

        &.level-2 {
          background: rgba(255, 69, 0, 0.8);
        }
      }
    }

    .space-info {
      p {
        margin: 4px 0;
        font-size: 12px;
      }
    }
  }

  .empty-state {
    margin-top: 80px;
    text-align: center;
  }
}
</style>

