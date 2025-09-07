<template>
  <div class="notifications-page">
    <a-card title="我的通知" :extra="extra">
      <template #extra>
        <a-space>
          <a-badge :count="unreadCount">
            <span>未读</span>
          </a-badge>
          <a-button size="small" @click="markAllRead" :loading="marking">全部已读</a-button>
        </a-space>
      </template>

      <a-list :data-source="list" :loading="loading" :renderItem="renderItem" />
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { h, onMounted, ref } from 'vue'
import { listNotificationsUsingGet, getUnreadCountUsingGet, markAllReadUsingPost } from '@/api/notifyController'

const list = ref<any[]>([])
const loading = ref(false)
const unreadCount = ref(0)
const marking = ref(false)

const load = async () => {
  loading.value = true
  try {
    const [l, c] = await Promise.all([
      listNotificationsUsingGet({ limit: 100 }),
      getUnreadCountUsingGet(),
    ])
    if (l?.data?.code === 0) list.value = l.data.data || []
    if (c?.data?.code === 0) unreadCount.value = Number(c.data.data || 0)
  } finally {
    loading.value = false
  }
}

const markAllRead = async () => {
  marking.value = true
  try {
    const res = await markAllReadUsingPost()
    if (res?.data?.code === 0) {
      unreadCount.value = 0
    }
  } finally {
    marking.value = false
  }
}

const renderItem = (item: any) => {
  return h(
    'a-list-item',
    {},
    {
      default: () => [
        h('div', { class: 'notify-item' }, [
          h('div', { class: 'notify-title' }, `${item.type || '通知'}`),
          h('div', { class: 'notify-content' }, item.content),
          h('div', { class: 'notify-time' }, new Date(item.createTime).toLocaleString()),
        ]),
      ],
    },
  )
}

onMounted(() => {
  load()
})
</script>

<style scoped>
.notifications-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
}
.notify-item { padding: 8px 0; }
.notify-title { font-weight: 600; }
.notify-content { margin-top: 4px; }
.notify-time { color: #8c8c8c; font-size: 12px; margin-top: 4px; }
</style>


