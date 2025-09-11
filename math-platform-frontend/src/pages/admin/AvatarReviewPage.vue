<template>
  <div class="avatar-review-page">
    <a-card title="头像审核">
      <a-space style="margin-bottom:12px">
        <a-select v-model:value="status" style="width:160px" :options="statusOptions" />
        <a-button type="primary" :loading="loading" @click="load">刷新</a-button>
      </a-space>
      <a-table :data-source="list" :columns="columns" row-key="id" :pagination="pagination" @change="onTableChange">
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'preview'">
            <a-image :width="64" :src="record.thumbnailUrl || record.url" />
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="primary" size="small" @click="approve(record)">通过</a-button>
              <a-popconfirm title="确认驳回？" @confirm="() => reject(record)">
                <a-button danger size="small">驳回</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
  <a-modal v-model:open="rejectOpen" title="填写驳回原因" @ok="doReject">
    <a-input v-model:value="rejectReason" placeholder="请填写驳回原因" />
  </a-modal>
  
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { message } from 'ant-design-vue'
import * as userController from '@/api/userController'

const list = ref<any[]>([])
const loading = ref(false)
const status = ref<number | undefined>(0)
const statusOptions = [
  { value: undefined, label: '全部' },
  { value: 0, label: '待提交/已提交' },
  { value: 5, label: '已通过' },
  { value: 6, label: '已驳回' },
]

const pagination = ref({ current: 1, pageSize: 10, total: 0 })

const columns = [
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '用户ID', dataIndex: 'userId', key: 'userId' },
  { title: '预览', key: 'preview' },
  { title: '状态', dataIndex: 'status', key: 'status' },
  { title: '提交时间', dataIndex: 'submittedAt', key: 'submittedAt' },
  { title: '操作', key: 'action' },
]

const load = async () => {
  loading.value = true
  try {
    const res = await userController.listAvatarReviewsUsingGet({
      status: status.value,
      current: pagination.value.current,
      pageSize: pagination.value.pageSize,
    })
    if (res?.data?.code === 0) {
      const data = (res.data.data as any)
      list.value = data?.records || []
      pagination.value.total = data?.total || 0
    }
  } finally {
    loading.value = false
  }
}

const onTableChange = (p: any) => {
  pagination.value.current = p.current
  pagination.value.pageSize = p.pageSize
  load()
}

const rejectOpen = ref(false)
const rejectId = ref<number | null>(null)
const rejectReason = ref('')

const approve = async (record: any) => {
  if (!record.id) {
    message.error('记录ID不能为空')
    return
  }
  const res = await userController.approveAvatarUsingPost({ id: record.id })
  if (res?.data?.code === 0) {
    message.success('已通过')
    load()
  } else {
    message.error(res?.data?.message || '操作失败')
  }
}

const reject = (record: any) => {
  if (!record.id) {
    message.error('记录ID不能为空')
    return
  }
  rejectId.value = record.id
  rejectReason.value = ''
  rejectOpen.value = true
}

const doReject = async () => {
  if (!rejectId.value) {
    message.error('记录ID不能为空')
    return
  }
  const res = await userController.rejectAvatarUsingPost({ 
    id: rejectId.value, 
    reason: rejectReason.value 
  })
  if (res?.data?.code === 0) {
    message.success('已驳回')
    rejectOpen.value = false
    load()
  } else {
    message.error(res?.data?.message || '操作失败')
  }
}

onMounted(() => {
  load()
})
</script>

<style scoped>
.avatar-review-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 16px;
}
</style>


