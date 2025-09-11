<template>
  <div id="avatarReviewPage">
    <a-space direction="vertical" style="width: 100%">
      <a-form layout="inline">
        <a-form-item label="审核状态">
          <a-select v-model:value="statusFilter" style="width: 160px" allow-clear placeholder="全部">
            <a-select-option :value="0">待审核</a-select-option>
            <a-select-option :value="1">通过</a-select-option>
            <a-select-option :value="2">驳回</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="doSearch">查询</a-button>
        </a-form-item>
      </a-form>

      <a-table
        :loading="loading"
        :columns="columns"
        :data-source="dataList"
        :pagination="pagination"
        row-key="id"
        @change="onTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.dataIndex === 'thumbnail'">
            <a-image :src="record.thumbnailUrl || record.url" :width="96" />
          </template>
          <template v-else-if="column.dataIndex === 'status'">
            <a-tag :color="statusColor(record.status)">{{ statusText(record.status) }}</a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="primary" :disabled="record.status !== 0" @click="handleApprove(record)">通过</a-button>
              <a-button danger :disabled="record.status !== 0" @click="openReject(record)">驳回</a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-space>

    <a-modal v-model:open="rejectModal.open" title="填写驳回原因" @ok="confirmReject" @cancel="closeReject">
      <a-textarea v-model:value="rejectModal.reason" :rows="4" placeholder="可选，填写驳回原因" />
    </a-modal>
  </div>
  
</template>

<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { listAvatarReviewsUsingGet, approveAvatarUsingPost, rejectAvatarUsingPost } from '@/api/userController.ts'

interface AvatarReview {
  id: number
  userId: number
  url?: string
  thumbnailUrl?: string
  status: number
  reason?: string
  reviewerId?: number
  submittedAt?: string
  reviewedAt?: string
  size?: number
  width?: number
  height?: number
  format?: string
  machineScore?: number
}

const loading = ref(false)
const dataList = ref<AvatarReview[]>([])
const total = ref(0)
const statusFilter = ref<number | undefined>(0)

const query = reactive({
  current: 1,
  pageSize: 10,
})

const columns = [
  { title: 'ID', dataIndex: 'id' },
  { title: '用户ID', dataIndex: 'userId' },
  { title: '缩略图', dataIndex: 'thumbnail' },
  { title: '状态', dataIndex: 'status' },
  { title: '尺寸', dataIndex: 'sizeDesc' },
  { title: '提交时间', dataIndex: 'submittedAt' },
  { title: '审核时间', dataIndex: 'reviewedAt' },
  { title: '审核人', dataIndex: 'reviewerId' },
  { title: '原因', dataIndex: 'reason' },
  { title: '操作', key: 'action' },
]

const pagination = computed(() => ({
  current: query.current,
  pageSize: query.pageSize,
  total: total.value,
  showSizeChanger: true,
  showTotal: (t: number) => `共 ${t} 条`,
}))

const statusText = (s?: number) => {
  if (s === 0) return '待审核'
  if (s === 1) return '通过'
  if (s === 2) return '驳回'
  return '未知'
}

const statusColor = (s?: number) => {
  if (s === 0) return 'orange'
  if (s === 1) return 'green'
  if (s === 2) return 'red'
  return 'default'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res: any = await listAvatarReviewsUsingGet({
      status: statusFilter.value,
      current: query.current,
      pageSize: query.pageSize,
    })
    if (res?.data?.code === 0 && res?.data?.data) {
      const page = res.data.data
      const records: any[] = page.records || []
      dataList.value = records.map((r) => ({
        id: r.id,
        userId: r.userId,
        url: r.url,
        thumbnailUrl: r.thumbnailUrl,
        status: r.status,
        reason: r.reason,
        reviewerId: r.reviewerId,
        submittedAt: r.submittedAt,
        reviewedAt: r.reviewedAt,
        size: r.size,
        width: r.width,
        height: r.height,
        format: r.format,
        machineScore: r.machineScore,
        // 展示字段
        sizeDesc: r.width && r.height ? `${r.width}x${r.height} ${r.format || ''}` : (r.format || ''),
      })) as any
      total.value = page.total || 0
    } else {
      message.error('获取数据失败：' + (res?.data?.message || ''))
    }
  } catch (e: any) {
    message.error('获取数据异常')
  } finally {
    loading.value = false
  }
}

const doSearch = () => {
  query.current = 1
  fetchData()
}

const onTableChange = (page: any) => {
  query.current = page.current
  query.pageSize = page.pageSize
  fetchData()
}

const handleApprove = async (record: AvatarReview) => {
  const id = Number((record as any)?.id)
  if (!Number.isFinite(id) || id <= 0) {
    message.error('数据异常：记录ID无效')
    return
  }
  const res: any = await approveAvatarUsingPost(id)
  if (res?.data?.code === 0) {
    message.success('已通过')
    fetchData()
  } else {
    message.error('操作失败：' + (res?.data?.message || ''))
  }
}

const rejectModal = reactive({ open: false, id: 0 as number, reason: '' })
const openReject = (record: AvatarReview) => {
  rejectModal.open = true
  rejectModal.id = record.id
  rejectModal.reason = ''
}
const closeReject = () => {
  rejectModal.open = false
}
const confirmReject = async () => {
  const id = Number(rejectModal.id)
  if (!Number.isFinite(id) || id <= 0) {
    message.error('数据异常：记录ID无效')
    return
  }
  const res: any = await rejectAvatarUsingPost(id, { reason: rejectModal.reason })
  if (res?.data?.code === 0) {
    message.success('已驳回')
    closeReject()
    fetchData()
  } else {
    message.error('操作失败：' + (res?.data?.message || ''))
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
#avatarReviewPage {
  padding: 12px;
}
</style>


