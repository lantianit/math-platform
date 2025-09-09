<template>
  <div id="globalHeader">
    <a-row :wrap="false">
      <a-col flex="200px">
        <router-link to="/">
          <div class="title-bar">
            <img class="logo" src="../assets/logo.png" alt="logo" />
            <div class="title">数学刷题平台</div>
          </div>
        </router-link>
      </a-col>
      <a-col flex="auto">
        <a-row :gutter="16" align="middle">
          <a-col flex="200px">
            <a-menu
              v-model:selectedKeys="current"
              mode="horizontal"
              :items="items"
              @click="doMenuClick"
              class="header-menu"
            />
          </a-col>
          <a-col flex="auto">
            <SearchBox
              placeholder="搜索帖子、用户、话题..."
              size="middle"
              @search="handleSearch"
              class="header-search"
            />
          </a-col>
        </a-row>
      </a-col>
      <!-- 用户信息展示栏 -->
      <a-col flex="320px">
        <div class="user-login-status">
          <a-space v-if="loginUserStore.loginUser.id" align="center">
            <a-badge :count="unreadCount" offset="[0,8]">
              <a-button type="text" @click="goNotifications">通知</a-button>
            </a-badge>
            <a-button type="primary" @click="goCreatePost">发布</a-button>
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '无名' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item>
                    <router-link :to="`/user/${loginUserStore.loginUser.id}`">
                      <UserOutlined />
                      我的空间
                    </router-link>
                  </a-menu-item>
                  <a-menu-item>
                    <router-link to="/user/edit">资料编辑</router-link>
                  </a-menu-item>
                  <a-menu-item @click="doLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-space>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>
<script lang="ts" setup>
import { computed, ref } from 'vue'
import { LogoutOutlined, UserOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { userLogoutUsingPost } from '@/api/userController.ts'
import { menuConfig, filterMenusByPermission, convertToAntMenuItems } from '@/constants/menu.ts'
import SearchBox from './SearchBox.vue'
import { unreadCountUsingGet } from '@/api/notifyController'

const loginUserStore = useLoginUserStore()

// 根据用户权限过滤菜单项
const filteredMenus = computed(() => {
  const isAdmin = loginUserStore.loginUser?.userRole === 'admin'
  return filterMenusByPermission(menuConfig, isAdmin)
})

// 转换为 Ant Design Menu 格式
const items = computed(() => convertToAntMenuItems(filteredMenus.value))

const router = useRouter()
// 当前要高亮的菜单项
const current = ref<string[]>([])
// 监听路由变化，更新高亮菜单项
router.afterEach((to, from, next) => {
  current.value = [to.path]
})

// 路由跳转事件
const doMenuClick = ({ key }: { key: string }) => {
  router.push({
    path: key,
  })
}

// 用户注销
const doLogout = async () => {
  const res = await userLogoutUsingPost()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}

// 搜索处理
const handleSearch = (keyword: string) => {
  if (keyword.trim()) {
    router.push({
      path: '/search',
      query: { q: keyword }
    })
  }
}

// 未读通知数
const unreadCount = ref(0)
const refreshUnread = async () => {
  try {
    const res = await unreadCountUsingGet()
    if (res?.data?.code === 0) unreadCount.value = Number(res.data.data || 0)
  } catch {}
}

const goNotifications = () => {
  router.push('/notifications')
}

const goCreatePost = () => {
  router.push('/post/create')
}

refreshUnread()


</script>

<style scoped>
#globalHeader .title-bar {
  display: flex;
  align-items: center;
}

.title {
  color: black;
  font-size: 18px;
  margin-left: 16px;
}

.logo {
  height: 48px;
}

.header-menu {
  border-bottom: none !important;
}

.header-search {
  max-width: 400px;
  margin: 0 auto;
}

.user-login-status {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
}
</style>
