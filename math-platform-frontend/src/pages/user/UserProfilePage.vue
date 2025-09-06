<template>
  <div class="user-profile-page">
    <a-row :gutter="[24, 24]">
      <a-col :xs="24" :md="7">
        <a-card class="user-card" :loading="loadingUser">
          <div v-if="user">
            <div class="profile-header">
              <a-avatar :src="user.userAvatar" :size="64">{{ user.userName?.charAt(0) }}</a-avatar>
              <div class="profile-meta">
                <div class="user-name">{{ user.userName }}</div>
                <div class="user-profile">{{ user.userProfile || '这个人很神秘~' }}</div>
              </div>
            </div>
            <div class="profile-stats">
              <div class="stat-item"><div class="value">{{ followerCount }}</div><div class="label">粉丝</div></div>
              <div class="stat-item"><div class="value">{{ followingCount }}</div><div class="label">关注</div></div>
              <div class="stat-item"><div class="value">{{ postCount }}</div><div class="label">帖子</div></div>
              <div class="stat-item"><div class="value">{{ likeTotal }}</div><div class="label">获赞</div></div>
              <div class="stat-item"><div class="value">{{ favouriteTotal }}</div><div class="label">被收藏</div></div>
              <div class="stat-item"><div class="value">{{ viewTotal }}</div><div class="label">浏览</div></div>
            </div>
            <div class="profile-actions">
              <a-button type="primary" v-if="!isSelf" :loading="followLoading" @click="toggleFollow">{{ followed ? '已关注' : '关注' }}</a-button>
            </div>
          </div>
        </a-card>
      </a-col>

      <a-col :xs="24" :md="17">
        <a-card title="TA 的帖子" :loading="loadingPosts">
          <PostList
            :posts="posts"
            :loading="loadingPosts"
            :has-more="hasMore"
            :infinite-scroll="true"
            @load-more="loadMore"
            @post-click="goDetail"
          />
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PostList from '@/components/PostList.vue'
import * as userController from '@/api/userController'
import * as postController from '@/api/postController'
import * as socialController from '@/api/socialController'
import { getUserPostStatsUsingGet } from '@/api/postController'
import { useLoginUserStore } from '@/stores/useLoginUserStore'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()
const userId = Number(route.params.id)

const user = ref<API.UserVO | null>(null)
const loadingUser = ref(false)
const loadingPosts = ref(false)
const posts = ref<API.Post[]>([])
const current = ref(1)
const pageSize = ref(10)
const hasMore = ref(true)

const followerCount = ref(0)
const followingCount = ref(0)
const followed = ref(false)
const followLoading = ref(false)

const postCount = ref(0)
const likeTotal = ref(0)
const favouriteTotal = ref(0)
const viewTotal = ref(0)

const isSelf = computed(() => !!loginUserStore.loginUser?.id && loginUserStore.loginUser.id === userId)

const loadUser = async () => {
  loadingUser.value = true
  try {
    const res = await userController.getUserVoByIdUsingGet({ id: userId })
    if (res?.data?.code === 0) {
      user.value = res.data.data
    }
  } finally {
    loadingUser.value = false
  }
}

const loadFollowStats = async () => {
  try {
    const [f1, f2] = await Promise.all([
      socialController.getFollowerCountByUserIdUsingGet({ userId }),
      socialController.getFollowingCountByUserIdUsingGet({ userId }),
    ])
    if (f1?.data?.code === 0) followerCount.value = Number(f1.data.data || 0)
    if (f2?.data?.code === 0) followingCount.value = Number(f2.data.data || 0)
  } catch {}
}

const loadFollowed = async () => {
  try {
    const res = await socialController.isUserFollowedByUserUsingGet({ followingId: userId })
    if (res?.data?.code === 0) followed.value = !!res.data.data
  } catch {}
}

const toggleFollow = async () => {
  followLoading.value = true
  const old = followed.value
  followed.value = !old
  try {
    const res = await socialController.toggleUserFollowUsingPost({ followingId: userId })
    if (res?.data?.code !== 0) throw new Error('failed')
    await loadFollowStats()
  } catch {
    followed.value = old
  } finally {
    followLoading.value = false
  }
}

const loadPosts = async (reset = false) => {
  if (loadingPosts.value) return
  loadingPosts.value = true
  try {
    if (reset) {
      current.value = 1
      posts.value = []
      hasMore.value = true
    }
    const res = await postController.listUserPostsByPageUsingPost({
      userId,
      current: current.value,
      pageSize: pageSize.value,
      sortField: 'createTime',
      sortOrder: 'desc'
    })
    if (res?.data?.code === 0 && res.data.data) {
      posts.value.push(...(res.data.data.records || []))
      hasMore.value = current.value < (res.data.data.pages || 1)
      if (hasMore.value) current.value++
    }
  } finally {
    loadingPosts.value = false
  }
}

const goDetail = (post: any) => {
  router.push(`/post/${post.id}`)
}

const loadAll = async () => {
  await Promise.all([loadUser(), loadFollowStats(), loadFollowed(), loadPosts(true)])
  try {
    const res = await getUserPostStatsUsingGet({ userId })
    if (res?.data?.code === 0 && res.data.data) {
      postCount.value = Number(res.data.data.postCount || 0)
      likeTotal.value = Number(res.data.data.likeTotal || 0)
      favouriteTotal.value = Number(res.data.data.favouriteTotal || 0)
      viewTotal.value = Number(res.data.data.viewTotal || 0)
    }
  } catch {}
}

onMounted(() => {
  loadAll()
})
</script>

<style scoped lang="scss">
.user-profile-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;

  .user-card {
    border-radius: 12px;

    .profile-header {
      display: flex;
      gap: 12px;
      align-items: center;
      .profile-meta { flex: 1; }
      .user-name { font-size: 18px; font-weight: 600; }
      .user-profile { color: #8c8c8c; margin-top: 4px; }
    }

    .profile-stats {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 8px;
      margin-top: 12px;
      .stat-item { text-align: center; }
      .value { font-size: 16px; font-weight: 600; color: #1890ff; }
      .label { color: #8c8c8c; font-size: 12px; }
    }

    .profile-actions { margin-top: 12px; }
  }
}
</style>


