<template>
  <div id="homePage">
    <!-- 顶部横幅区域 -->
    <div v-if="showWelcomeBanner" class="welcome-banner">
      <div class="banner-content">
        <div class="banner-text">
          <h1>数学论坛</h1>
          <p class="banner-subtitle">探索数学之美，分享学习心得</p>
        </div>
        <div v-if="!isLoggedIn" class="banner-actions">
          <a-button type="primary" size="large" @click="goToLogin">
            立即加入
          </a-button>
          <a-button size="large" @click="goToRegister">
            注册账号
          </a-button>
        </div>
        <a-button
          type="text"
          size="small"
          @click="hideBanner"
          class="banner-close"
        >
          <CloseOutlined />
        </a-button>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <a-row :gutter="[24, 24]" class="content-row">
        <!-- 左侧边栏 -->
        <a-col v-bind="uiStore.getSidebarGrid('left')" class="left-sidebar">
          <div class="sidebar-container">
            <!-- 分类导航 -->
            <CategoryNav
              :selected-category="currentCategory"
              @category-change="handleCategoryChange"
              class="category-nav"
            />

            <!-- 热门话题 -->
            <a-card title="热门话题" size="small" class="hot-topics-card">
              <div class="topic-list">
                <div
                  v-for="topic in hotTopics"
                  :key="topic.id"
                  @click="handleTopicClick(topic)"
                  class="topic-item"
                >
                  <span class="topic-name">{{ topic.name }}</span>
                  <span class="topic-count">{{ formatCount(topic.postCount) }}</span>
                </div>
              </div>
              <a-button type="text" size="small" block class="more-topics-btn">
                查看更多话题
              </a-button>
            </a-card>

            <!-- 用户收藏夹（仅登录用户） -->
            <a-card
              v-if="isLoggedIn"
              title="我的收藏夹"
              size="small"
              class="favorites-card"
            >
              <div class="favorites-list">
                <div
                  v-for="folder in favoriteFolders"
                  :key="folder.id"
                  @click="handleFolderClick(folder)"
                  class="folder-item"
                >
                  <FolderOutlined class="folder-icon" />
                  <span class="folder-name">{{ folder.name }}</span>
                  <span class="folder-count">({{ folder.postCount }})</span>
                </div>
              </div>
              <a-button type="dashed" size="small" block class="create-folder-btn">
                <PlusOutlined />
                新建收藏夹
              </a-button>
            </a-card>
          </div>
        </a-col>

        <!-- 中间主区域 -->
        <a-col v-bind="uiStore.getResponsiveGrid()" class="main-area">
          <div class="main-container">
            <!-- 快速发布区域（仅登录用户） -->
            <a-card v-if="isLoggedIn" class="quick-post-card">
              <div class="quick-post-content">
                <a-avatar :src="loginUserStore.loginUser.userAvatar" class="user-avatar">
                  {{ loginUserStore.loginUser.userName?.charAt(0) }}
                </a-avatar>
                <a-input
                  placeholder="分享你的数学见解..."
                  class="quick-post-input"
                  @click="handleQuickPost"
                  readonly
                />
                <a-button type="primary" @click="handleQuickPost" class="post-btn">
                  发布
                </a-button>
              </div>
            </a-card>

            <!-- 信息流标签 -->
            <FeedTabs
              :default-tab="currentFeedType"
              :show-badges="true"
              :refreshing="postStore.loading"
              @change="handleFeedTypeChange"
              @refresh="handleRefresh"
              @filter="handleFilter"
              class="feed-tabs"
            />

            <!-- 帖子列表 -->
            <PostList
              :posts="postStore.posts"
              :loading="postStore.loading"
              :has-more="postStore.hasMore"
              :infinite-scroll="true"
              @load-more="handleLoadMore"
              @refresh="handleRefresh"
              @post-click="handlePostClick"
              @user-click="handleUserClick"
              @tag-click="handleTagClick"
              class="post-list"
            />
          </div>
        </a-col>

        <!-- 右侧边栏 -->
        <a-col v-bind="uiStore.getSidebarGrid('right')" class="right-sidebar">
          <div class="sidebar-container">
            <!-- 热搜关键词 -->
            <a-card title="热搜榜" size="small" class="hot-search-card">
              <div class="hot-search-list">
                <div
                  v-for="(keyword, index) in hotKeywords"
                  :key="keyword"
                  @click="handleHotKeywordClick(keyword)"
                  class="hot-search-item"
                >
                  <span
                    class="search-rank"
                    :class="{ hot: index < 3 }"
                  >
                    {{ index + 1 }}
                  </span>
                  <span class="search-keyword">{{ keyword }}</span>
                  <FireOutlined v-if="index < 3" class="hot-icon" />
                </div>
              </div>
            </a-card>

            <!-- 活跃用户 -->
            <a-card title="活跃用户" size="small" class="active-users-card">
              <div class="user-list">
                <div
                  v-for="user in activeUsers"
                  :key="user.id"
                  @click="handleUserClick(user.id)"
                  class="user-item"
                >
                  <a-avatar :src="user.userAvatar" :size="32">
                    {{ user.userName?.charAt(0) }}
                  </a-avatar>
                  <div class="user-info">
                    <div class="user-name">{{ user.userName }}</div>
                    <div class="user-stats">
                      {{ formatCount(user.postCount) }}帖 · {{ formatCount(user.likeCount) }}赞
                    </div>
                  </div>
                  <a-button
                    v-if="user.id !== loginUserStore.loginUser.id"
                    type="text"
                    size="small"
                    class="follow-btn"
                  >
                    关注
                  </a-button>
                </div>
              </div>
            </a-card>

            <!-- 统计数据 -->
            <a-card title="论坛统计" size="small" class="stats-card">
              <div class="stats-grid">
                <div class="stat-item">
                  <div class="stat-value">{{ formatCount(forumStats.totalPosts) }}</div>
                  <div class="stat-label">总帖子数</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ formatCount(forumStats.totalUsers) }}</div>
                  <div class="stat-label">注册用户</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ formatCount(forumStats.todayPosts) }}</div>
                  <div class="stat-label">今日发帖</div>
                </div>
                <div class="stat-item">
                  <div class="stat-value">{{ formatCount(forumStats.onlineUsers) }}</div>
                  <div class="stat-label">在线用户</div>
                </div>
              </div>
            </a-card>
          </div>
        </a-col>
      </a-row>
    </div>

    <!-- 移动端底部导航已移除 -->
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { useLocalStorage } from '@vueuse/core'
import {
  CloseOutlined,
  FolderOutlined,
  PlusOutlined,
  FireOutlined
} from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { usePostStore } from '@/stores/usePostStore'
import { useSearchStore } from '@/stores/useSearchStore'
import { useUIStore } from '@/stores/useUIStore'
import CategoryNav from '@/components/CategoryNav.vue'
import FeedTabs from '@/components/FeedTabs.vue'
import PostList from '@/components/PostList.vue'

const router = useRouter()
const loginUserStore = useLoginUserStore()
const postStore = usePostStore()
const searchStore = useSearchStore()
const uiStore = useUIStore()

// 响应式状态
const showWelcomeBanner = useLocalStorage('show-welcome-banner', true)
const currentCategory = ref('all')
const currentFeedType = ref<'recommend' | 'latest' | 'hot' | 'following'>('recommend')

// 模拟数据（实际项目中应从API获取）
const hotTopics = ref([
  { id: 1, name: '微积分', postCount: 1234 },
  { id: 2, name: '线性代数', postCount: 987 },
  { id: 3, name: '概率论', postCount: 756 },
  { id: 4, name: '数值分析', postCount: 543 },
  { id: 5, name: '抽象代数', postCount: 321 }
])

const favoriteFolders = ref([
  { id: 1, name: '数学基础', postCount: 15 },
  { id: 2, name: '算法题目', postCount: 23 },
  { id: 3, name: '学习资料', postCount: 8 }
])

const activeUsers = ref([
  {
    id: 1,
    userName: '数学达人',
    userAvatar: '',
    postCount: 156,
    likeCount: 2341
  },
  {
    id: 2,
    userName: '算法专家',
    userAvatar: '',
    postCount: 89,
    likeCount: 1567
  },
  {
    id: 3,
    userName: '微积分王者',
    userAvatar: '',
    postCount: 134,
    likeCount: 1890
  }
])

const forumStats = ref({
  totalPosts: 12345,
  totalUsers: 5678,
  todayPosts: 89,
  onlineUsers: 234
})

// 计算属性
const isLoggedIn = computed(() => !!loginUserStore.loginUser.id)
const hotKeywords = computed(() => searchStore.hotKeywords)

// 方法
const formatCount = (count: number) => {
  if (count < 1000) return count.toString()
  if (count < 10000) return `${(count / 1000).toFixed(1)}k`
  return `${(count / 10000).toFixed(1)}w`
}

const goToLogin = () => {
  router.push('/user/login')
}

const goToRegister = () => {
  router.push('/user/register')
}

const hideBanner = () => {
  showWelcomeBanner.value = false
}

const handleCategoryChange = (category: string) => {
  currentCategory.value = category
  // 根据分类加载帖子
  loadPostsByCategory(category)
}

const handleFeedTypeChange = (feedType: string) => {
  currentFeedType.value = feedType as any
  // 加载对应类型的信息流
  postStore.loadFeedPosts(feedType as any, true)
}

const handleRefresh = () => {
  postStore.loadFeedPosts(currentFeedType.value, true)
}

const handleLoadMore = () => {
  postStore.loadFeedPosts(currentFeedType.value, false)
}

const handleFilter = (filterType: string) => {
  message.info(`筛选类型: ${filterType}`)
  // 实现筛选逻辑
}

const handleQuickPost = () => {
  router.push('/post/create')
}

const handlePostClick = (post: API.PostVO | API.Post) => {
  router.push(`/post/${post.id}`)
}

const handleUserClick = (userId: number) => {
  router.push(`/user/${userId}`)
}

const handleTagClick = (tag: string) => {
  router.push(`/search?tag=${encodeURIComponent(tag)}`)
}

const handleTopicClick = (topic: any) => {
  router.push(`/topic/${topic.id}`)
}

const handleFolderClick = (folder: any) => {
  router.push(`/favorites/${folder.id}`)
}

const handleHotKeywordClick = (keyword: string) => {
  router.push(`/search?q=${encodeURIComponent(keyword)}`)
}

const loadPostsByCategory = async (category: string) => {
  try {
    if (category === 'all') {
      await postStore.loadFeedPosts(currentFeedType.value, true)
    } else {
      // 根据分类加载帖子的逻辑
      message.info(`加载分类: ${category}`)
    }
  } catch (error) {
    console.error('加载帖子失败:', error)
    message.error('加载失败，请稍后重试')
  }
}

// 初始化数据
const initializeData = async () => {
  try {
    // 获取用户登录状态
    await loginUserStore.fetchLoginUser()
    
    // 加载热门关键词
    await searchStore.loadHotKeywords()
    
    // 加载用户交互状态
    if (isLoggedIn.value) {
      await postStore.loadUserInteractionStatus()
    }
    
    // 加载默认信息流
    await postStore.loadFeedPosts(currentFeedType.value, true)
  } catch (error) {
    console.error('初始化数据失败:', error)
  }
}

// 组件挂载时初始化
onMounted(() => {
  initializeData()
})
</script>

<style scoped lang="scss">
#homePage {
  min-height: calc(100vh - 64px);
  background: #fafafa !important;
  /* 强制白色背景，覆盖暗色主题 */
}

// 顶部横幅
.welcome-banner {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  margin-bottom: 24px;
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.1"/><circle cx="10" cy="60" r="0.5" fill="white" opacity="0.1"/><circle cx="90" cy="40" r="0.5" fill="white" opacity="0.1"/></pattern></defs><rect width="100%" height="100%" fill="url(%23grain)"/></svg>');
    pointer-events: none;
  }

  .banner-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 32px 24px;
    position: relative;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .banner-text {
      flex: 1;

      h1 {
        font-size: 2.5rem;
        font-weight: 700;
        margin-bottom: 8px;
        text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      }

      .banner-subtitle {
        font-size: 1.1rem;
        opacity: 0.9;
        margin: 0;
      }
    }

    .banner-actions {
      display: flex;
      gap: 12px;

      .ant-btn {
        height: 40px;
        padding: 0 24px;
        font-weight: 500;
        border-radius: 20px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

        &:not(.ant-btn-primary) {
          background: rgba(255, 255, 255, 0.1);
          border-color: rgba(255, 255, 255, 0.3);
          color: white;

          &:hover {
            background: rgba(255, 255, 255, 0.2);
            border-color: rgba(255, 255, 255, 0.5);
          }
        }
      }
    }

    .banner-close {
      position: absolute;
      top: 16px;
      right: 16px;
      color: rgba(255, 255, 255, 0.7);

      &:hover {
        color: white;
        background: rgba(255, 255, 255, 0.1);
      }
    }
  }
}

// 主要内容区域
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px 24px;

  .content-row {
    align-items: flex-start;
  }
}

// 侧边栏通用样式
.sidebar-container {
  position: sticky;
  top: 88px;
  
  .ant-card {
    margin-bottom: 16px;
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    &:last-child {
      margin-bottom: 0;
    }

    :deep(.ant-card-head) {
      border-bottom: 1px solid #f0f0f0;
      min-height: 48px;
      padding: 0 16px;

      .ant-card-head-title {
        font-size: 14px;
        font-weight: 600;
        color: #262626;
      }
    }

    :deep(.ant-card-body) {
      padding: 16px;
    }
  }
}

// 左侧边栏
.left-sidebar {
  .category-nav {
    margin-bottom: 16px;
  }

  .hot-topics-card {
    .topic-list {
      .topic-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 8px 12px;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          background: #f5f5f5;
        }

        .topic-name {
          font-size: 14px;
          color: #262626;
          font-weight: 500;
        }

        .topic-count {
          font-size: 12px;
          color: #8c8c8c;
          background: #f0f0f0;
          padding: 2px 6px;
          border-radius: 10px;
          min-width: 24px;
          text-align: center;
        }
      }
    }

    .more-topics-btn {
      margin-top: 12px;
      color: #1890ff;
      font-size: 13px;
    }
  }

  .favorites-card {
    .favorites-list {
      .folder-item {
        display: flex;
        align-items: center;
        padding: 8px 12px;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          background: #f5f5f5;
        }

        .folder-icon {
          color: #1890ff;
          margin-right: 8px;
          font-size: 14px;
        }

        .folder-name {
          flex: 1;
          font-size: 14px;
          color: #262626;
        }

        .folder-count {
          font-size: 12px;
          color: #8c8c8c;
        }
      }
    }

    .create-folder-btn {
      margin-top: 12px;
      height: 32px;
      font-size: 13px;
      color: #8c8c8c;

      &:hover {
        color: #1890ff;
        border-color: #1890ff;
      }
    }
  }
}

// 主区域
.main-area {
  .main-container {
    .quick-post-card {
      margin-bottom: 16px;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

      .quick-post-content {
        display: flex;
        align-items: center;
        gap: 12px;

        .user-avatar {
          flex-shrink: 0;
        }

        .quick-post-input {
          flex: 1;
          border-radius: 20px;
          cursor: pointer;

          :deep(.ant-input) {
            background: #fafafa;
            border: 1px solid #f0f0f0;

            &:hover {
              border-color: #d9d9d9;
            }
          }
        }

        .post-btn {
          border-radius: 16px;
          min-width: 80px;
        }
      }
    }

    .feed-tabs {
      margin-bottom: 16px;
      background: white;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
    }
  }
}

// 右侧边栏
.right-sidebar {
  .hot-search-card {
    .hot-search-list {
      .hot-search-item {
        display: flex;
        align-items: center;
        padding: 8px 12px;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          background: #f5f5f5;
        }

        .search-rank {
          width: 20px;
          height: 20px;
          background: #f0f0f0;
          color: #8c8c8c;
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 11px;
          font-weight: 500;
          margin-right: 8px;

          &.hot {
            background: linear-gradient(135deg, #ff6b6b, #ff8e53);
            color: white;
          }
        }

        .search-keyword {
          flex: 1;
          font-size: 14px;
          color: #262626;
        }

        .hot-icon {
          color: #ff4d4f;
          font-size: 12px;
        }
      }
    }
  }

  .active-users-card {
    .user-list {
      .user-item {
        display: flex;
        align-items: center;
        padding: 8px 12px;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          background: #f5f5f5;
        }

        .user-info {
          flex: 1;
          margin-left: 8px;

          .user-name {
            font-size: 14px;
            color: #262626;
            font-weight: 500;
          }

          .user-stats {
            font-size: 12px;
            color: #8c8c8c;
            margin-top: 2px;
          }
        }

        .follow-btn {
          color: #1890ff;
          font-size: 12px;
          padding: 0 8px;
          height: 24px;
          border-radius: 12px;

          &:hover {
            background: rgba(24, 144, 255, 0.1);
          }
        }
      }
    }
  }

  .stats-card {
    .stats-grid {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;

      .stat-item {
        text-align: center;

        .stat-value {
          font-size: 18px;
          font-weight: 600;
          color: #1890ff;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 12px;
          color: #8c8c8c;
        }
      }
    }
  }
}

// 移动端底部导航已移除

// 响应式样式
@media (max-width: 768px) {
  #homePage {
    background: white;
  }

  .welcome-banner {
    margin: 0 0 16px;

    .banner-content {
      padding: 20px 16px;
      flex-direction: column;
      text-align: center;
      gap: 16px;

      .banner-text h1 {
        font-size: 1.8rem;
      }

      .banner-actions {
        width: 100%;
        justify-content: center;

        .ant-btn {
          flex: 1;
          max-width: 120px;
        }
      }
    }
  }

  .main-content {
    padding: 0 12px 20px;

    .content-row {
      margin: 0;
    }

    .sidebar-container {
      position: static;

      .ant-card {
        margin-bottom: 12px;

        :deep(.ant-card-head) {
          padding: 0 12px;
          min-height: 40px;
        }

        :deep(.ant-card-body) {
          padding: 12px;
        }
      }
    }
  }

  .main-area .main-container {
    .quick-post-card {
      margin-bottom: 12px;

      .quick-post-content {
        gap: 8px;

        .post-btn {
          min-width: 60px;
          font-size: 13px;
        }
      }
    }

    .feed-tabs {
      margin-bottom: 12px;
    }
  }

  .right-sidebar .stats-card .stats-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 8px;

    .stat-item {
      .stat-value {
        font-size: 14px;
      }

      .stat-label {
        font-size: 10px;
      }
    }
  }
}

// 暗色主题支持（已禁用）
/*
@media (prefers-color-scheme: dark) {
  #homePage {
    background: #141414;
  }

  .main-content .sidebar-container .ant-card {
    background: #1f1f1f;
    border-color: #303030;

    :deep(.ant-card-head) {
      background: #1f1f1f;
      border-color: #303030;

      .ant-card-head-title {
        color: #d9d9d9;
      }
    }
  }

  // 各种卡片的暗色主题样式
  .left-sidebar {
    .hot-topics-card .topic-list .topic-item {
      &:hover {
        background: #262626;
      }

      .topic-name {
        color: #d9d9d9;
      }

      .topic-count {
        background: #303030;
        color: #999;
      }
    }

    .favorites-card .favorites-list .folder-item {
      &:hover {
        background: #262626;
      }

      .folder-name {
        color: #d9d9d9;
      }
    }
  }

  .right-sidebar {
    .hot-search-item {
      &:hover {
        background: #262626;
      }

      .search-keyword {
        color: #d9d9d9;
      }
    }

    .user-item {
      &:hover {
        background: #262626;
      }

      .user-name {
        color: #d9d9d9;
      }
    }
  }
}
*/

// 动画效果
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.main-content {
  animation: slideInUp 0.6s ease-out;
}

.sidebar-container .ant-card {
  animation: slideInUp 0.6s ease-out;
  animation-fill-mode: both;

  &:nth-child(1) { animation-delay: 0.1s; }
  &:nth-child(2) { animation-delay: 0.2s; }
  &:nth-child(3) { animation-delay: 0.3s; }
}
</style>
