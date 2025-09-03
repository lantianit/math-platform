<template>
  <div class="feed-tabs">
    <a-tabs
      v-model:activeKey="activeTab"
      :type="type"
      :size="size"
      :tabPosition="tabPosition"
      :animated="animated"
      @change="handleTabChange"
      class="feed-tabs-container"
    >
      <!-- 推荐 -->
      <a-tab-pane key="recommend" class="tab-pane">
        <template #tab>
          <span class="tab-content">
            <FireOutlined class="tab-icon" />
            <span class="tab-text">推荐</span>
            <a-badge 
              v-if="showBadges && recommendCount > 0" 
              :count="recommendCount"
              :offset="[8, -2]"
              class="tab-badge"
            />
          </span>
        </template>
      </a-tab-pane>

      <!-- 最新 -->
      <a-tab-pane key="latest" class="tab-pane">
        <template #tab>
          <span class="tab-content">
            <ClockCircleOutlined class="tab-icon" />
            <span class="tab-text">最新</span>
            <a-badge 
              v-if="showBadges && latestCount > 0" 
              :count="latestCount"
              :offset="[8, -2]"
              class="tab-badge"
            />
          </span>
        </template>
      </a-tab-pane>

      <!-- 热门 -->
      <a-tab-pane key="hot" class="tab-pane">
        <template #tab>
          <span class="tab-content">
            <ThunderboltOutlined class="tab-icon" />
            <span class="tab-text">热门</span>
            <a-badge 
              v-if="showBadges && hotCount > 0" 
              :count="hotCount"
              :offset="[8, -2]"
              class="tab-badge"
            />
          </span>
        </template>
      </a-tab-pane>

      <!-- 关注 -->
      <a-tab-pane 
        v-if="showFollowing && isLoggedIn"
        key="following" 
        class="tab-pane"
      >
        <template #tab>
          <span class="tab-content">
            <HeartOutlined class="tab-icon" />
            <span class="tab-text">关注</span>
            <a-badge 
              v-if="showBadges && followingCount > 0" 
              :count="followingCount"
              :offset="[8, -2]"
              class="tab-badge"
            />
          </span>
        </template>
      </a-tab-pane>

      <!-- 自定义标签页 -->
      <a-tab-pane
        v-for="customTab in customTabs"
        :key="customTab.key"
        class="tab-pane"
      >
        <template #tab>
          <span class="tab-content">
            <component :is="customTab.icon" class="tab-icon" />
            <span class="tab-text">{{ customTab.label }}</span>
            <a-badge 
              v-if="showBadges && customTab.count && customTab.count > 0" 
              :count="customTab.count"
              :offset="[8, -2]"
              class="tab-badge"
            />
          </span>
        </template>
      </a-tab-pane>

      <!-- 标签页操作区域 -->
      <template #rightExtra v-if="showExtra">
        <div class="tab-extra">
          <!-- 刷新按钮 -->
          <a-tooltip title="刷新">
            <a-button
              type="text"
              size="small"
              :loading="refreshing"
              @click="handleRefresh"
              class="extra-btn refresh-btn"
            >
              <ReloadOutlined />
            </a-button>
          </a-tooltip>

          <!-- 筛选按钮 -->
          <a-tooltip title="筛选">
            <a-dropdown :trigger="['click']">
              <a-button
                type="text"
                size="small"
                class="extra-btn filter-btn"
              >
                <FilterOutlined />
              </a-button>
              <template #overlay>
                <a-menu @click="handleFilterClick">
                  <a-menu-item key="time">
                    <ClockCircleOutlined />
                    按时间排序
                  </a-menu-item>
                  <a-menu-item key="hot">
                    <FireOutlined />
                    按热度排序
                  </a-menu-item>
                  <a-menu-item key="like">
                    <LikeOutlined />
                    按点赞排序
                  </a-menu-item>
                  <a-menu-divider />
                  <a-menu-item key="today">
                    <CalendarOutlined />
                    今日发布
                  </a-menu-item>
                  <a-menu-item key="week">
                    <CalendarOutlined />
                    本周发布
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </a-tooltip>

          <!-- 设置按钮 -->
          <a-tooltip title="设置">
            <a-button
              type="text"
              size="small"
              @click="handleSettings"
              class="extra-btn settings-btn"
            >
              <SettingOutlined />
            </a-button>
          </a-tooltip>
        </div>
      </template>
    </a-tabs>

    <!-- 移动端底部导航 -->
    <div v-if="isMobile && showMobileNav" class="mobile-nav">
      <div
        v-for="tab in mobileTabs"
        :key="tab.key"
        @click="handleTabChange(tab.key)"
        :class="['mobile-nav-item', { active: activeTab === tab.key }]"
      >
        <component :is="tab.icon" class="mobile-nav-icon" />
        <span class="mobile-nav-text">{{ tab.label }}</span>
        <a-badge 
          v-if="showBadges && tab.count > 0" 
          :count="tab.count"
          class="mobile-nav-badge"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import {
  FireOutlined,
  ClockCircleOutlined,
  ThunderboltOutlined,
  HeartOutlined,
  ReloadOutlined,
  FilterOutlined,
  SettingOutlined,
  LikeOutlined,
  CalendarOutlined
} from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { useUIStore } from '@/stores/useUIStore'

// 自定义标签页接口
interface CustomTab {
  key: string
  label: string
  icon: any
  count?: number
}

// 定义组件属性
interface Props {
  defaultTab?: string
  type?: 'line' | 'card' | 'editable-card'
  size?: 'small' | 'default' | 'large'
  tabPosition?: 'top' | 'right' | 'bottom' | 'left'
  animated?: boolean
  showFollowing?: boolean
  showBadges?: boolean
  showExtra?: boolean
  showMobileNav?: boolean
  customTabs?: CustomTab[]
  recommendCount?: number
  latestCount?: number
  hotCount?: number
  followingCount?: number
  refreshing?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  defaultTab: 'recommend',
  type: 'line',
  size: 'default',
  tabPosition: 'top',
  animated: true,
  showFollowing: true,
  showBadges: false,
  showExtra: true,
  showMobileNav: false,
  customTabs: () => [],
  recommendCount: 0,
  latestCount: 0,
  hotCount: 0,
  followingCount: 0,
  refreshing: false
})

// 定义事件
const emit = defineEmits<{
  change: [tab: string]
  refresh: []
  filter: [type: string]
  settings: []
}>()

const loginUserStore = useLoginUserStore()
const uiStore = useUIStore()

// 响应式状态
const activeTab = ref(props.defaultTab)

// 计算属性
const isLoggedIn = computed(() => !!loginUserStore.loginUser.id)
const isMobile = computed(() => uiStore.isMobile)

const mobileTabs = computed(() => {
  const tabs = [
    {
      key: 'recommend',
      label: '推荐',
      icon: FireOutlined,
      count: props.recommendCount
    },
    {
      key: 'latest',
      label: '最新',
      icon: ClockCircleOutlined,
      count: props.latestCount
    },
    {
      key: 'hot',
      label: '热门',
      icon: ThunderboltOutlined,
      count: props.hotCount
    }
  ]

  if (props.showFollowing && isLoggedIn.value) {
    tabs.push({
      key: 'following',
      label: '关注',
      icon: HeartOutlined,
      count: props.followingCount
    })
  }

  return tabs
})

// 方法
const handleTabChange = (tab: string) => {
  activeTab.value = tab
  emit('change', tab)
}

const handleRefresh = () => {
  emit('refresh')
}

const handleFilterClick = ({ key }: { key: string }) => {
  emit('filter', key)
}

const handleSettings = () => {
  emit('settings')
}

const formatCount = (count: number) => {
  if (count < 1000) return count
  if (count < 10000) return `${(count / 1000).toFixed(1)}k`
  return `${(count / 10000).toFixed(1)}w`
}
</script>

<style scoped lang="scss">
.feed-tabs {
  .feed-tabs-container {
    :deep(.ant-tabs-bar) {
      margin-bottom: 0;
      border-bottom: 1px solid #f0f0f0;
    }

    :deep(.ant-tabs-nav) {
      &::before {
        border-bottom: 1px solid #f0f0f0;
      }
    }

    :deep(.ant-tabs-tab) {
      padding: 12px 20px;
      font-weight: 500;
      transition: all 0.3s ease;

      &:hover {
        color: #1890ff;
      }

      &.ant-tabs-tab-active {
        .tab-icon {
          color: #1890ff;
        }
      }
    }

    .tab-content {
      display: flex;
      align-items: center;
      gap: 6px;
      position: relative;

      .tab-icon {
        font-size: 14px;
        color: #8c8c8c;
        transition: color 0.3s ease;
      }

      .tab-text {
        font-size: 14px;
      }

      .tab-badge {
        position: absolute;
        top: -8px;
        right: -8px;
      }
    }

    .tab-extra {
      display: flex;
      align-items: center;
      gap: 4px;
      margin-right: 16px;

      .extra-btn {
        color: #8c8c8c;
        border: none;
        box-shadow: none;
        
        &:hover {
          color: #1890ff;
          background: rgba(24, 144, 255, 0.06);
        }

        &.refresh-btn {
          &.ant-btn-loading .anticon {
            color: #1890ff;
          }
        }
      }
    }
  }

  // 移动端底部导航
  .mobile-nav {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    background: white;
    border-top: 1px solid #f0f0f0;
    display: flex;
    z-index: 1000;
    box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.1);

    .mobile-nav-item {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 8px 4px 12px;
      cursor: pointer;
      transition: all 0.2s ease;
      position: relative;

      &:hover {
        background: rgba(24, 144, 255, 0.06);
      }

      &.active {
        .mobile-nav-icon {
          color: #1890ff;
        }

        .mobile-nav-text {
          color: #1890ff;
          font-weight: 500;
        }
      }

      .mobile-nav-icon {
        font-size: 20px;
        color: #8c8c8c;
        margin-bottom: 4px;
        transition: color 0.2s ease;
      }

      .mobile-nav-text {
        font-size: 11px;
        color: #8c8c8c;
        transition: color 0.2s ease;
      }

      .mobile-nav-badge {
        position: absolute;
        top: 2px;
        right: 50%;
        transform: translateX(12px);
      }
    }
  }
}

// 卡片类型样式
.feed-tabs :deep(.ant-tabs-card) {
  .ant-tabs-tab {
    background: #fafafa;
    border-color: #f0f0f0;
    
    &.ant-tabs-tab-active {
      background: white;
      border-bottom-color: white;
    }
  }
}

// 小尺寸样式
.feed-tabs :deep(.ant-tabs-small) {
  .ant-tabs-tab {
    padding: 8px 16px;
    
    .tab-text {
      font-size: 13px;
    }

    .tab-icon {
      font-size: 13px;
    }
  }
}

// 大尺寸样式
.feed-tabs :deep(.ant-tabs-large) {
  .ant-tabs-tab {
    padding: 16px 24px;
    
    .tab-text {
      font-size: 15px;
    }

    .tab-icon {
      font-size: 15px;
    }
  }
}

// 响应式样式
@media (max-width: 768px) {
  .feed-tabs {
    .feed-tabs-container {
      :deep(.ant-tabs-tab) {
        padding: 10px 12px;
        
        .tab-text {
          font-size: 13px;
        }

        .tab-icon {
          font-size: 13px;
        }
      }

      .tab-extra {
        margin-right: 8px;
        gap: 2px;

        .extra-btn {
          width: 28px;
          height: 28px;
          min-width: 28px;
          padding: 0;
          
          .anticon {
            font-size: 12px;
          }
        }
      }
    }

    // 为移动端底部导航预留空间
    &.with-mobile-nav {
      padding-bottom: 60px;
    }
  }
}

// 暗色主题已移除，统一使用白色模式

// 动画效果
@keyframes tabSlideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.feed-tabs .tab-content {
  animation: tabSlideIn 0.3s ease-out;
}

// 徽章动画
@keyframes badgeBounce {
  0%, 20%, 53%, 80%, 100% {
    transform: translate3d(0, 0, 0);
  }
  40%, 43% {
    transform: translate3d(0, -8px, 0);
  }
  70% {
    transform: translate3d(0, -4px, 0);
  }
  90% {
    transform: translate3d(0, -2px, 0);
  }
}

.feed-tabs :deep(.ant-badge-count) {
  animation: badgeBounce 1s ease-in-out;
}
</style>
