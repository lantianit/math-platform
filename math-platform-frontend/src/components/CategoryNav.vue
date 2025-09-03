<template>
  <div class="category-nav">
    <a-card 
      :title="title" 
      :bordered="bordered"
      :size="size"
      class="category-card"
    >
      <template #extra v-if="showMore">
        <a-button 
          type="text" 
          size="small"
          @click="toggleExpanded"
          class="toggle-btn"
        >
          {{ expanded ? '收起' : '更多' }}
          <DownOutlined v-if="!expanded" />
          <UpOutlined v-else />
        </a-button>
      </template>

      <!-- 分类菜单 -->
      <a-menu
        v-model:selectedKeys="selectedKeys"
        :mode="mode"
        :theme="theme"
        :inline-collapsed="collapsed"
        @click="handleMenuClick"
        class="category-menu"
      >
        <!-- 全部分类 -->
        <a-menu-item key="all" class="menu-item-all">
          <template #icon>
            <AppstoreOutlined />
          </template>
          <span>全部</span>
          <span v-if="showCounts && allCount" class="item-count">{{ allCount }}</span>
        </a-menu-item>

        <a-menu-divider v-if="showDivider" />

        <!-- 分类列表 -->
        <a-menu-item 
          v-for="category in displayCategories" 
          :key="category.key"
          :class="['menu-item-category', { active: selectedKeys.includes(category.key) }]"
        >
          <template #icon>
            <component :is="category.icon" />
          </template>
          <span>{{ category.label }}</span>
          <span v-if="showCounts && category.count" class="item-count">
            {{ formatCount(category.count) }}
          </span>
        </a-menu-item>

        <!-- 展开更多分类 -->
        <a-menu-item 
          v-if="!expanded && hiddenCategories.length > 0"
          key="more" 
          @click="toggleExpanded" 
          class="menu-item-more"
        >
          <template #icon>
            <MoreOutlined />
          </template>
          <span>更多分类 ({{ hiddenCategories.length }})</span>
        </a-menu-item>
      </a-menu>

      <!-- 自定义分类管理 -->
      <div v-if="showCustomize" class="customize-section">
        <a-divider />
        <a-button 
          type="dashed" 
          size="small" 
          block
          @click="handleCustomize"
          class="customize-btn"
        >
          <SettingOutlined />
          自定义分类
        </a-button>
      </div>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import {
  AppstoreOutlined,
  CodeOutlined,
  QuestionCircleOutlined,
  FolderOpenOutlined,
  ShareAltOutlined,
  DownOutlined,
  UpOutlined,
  MoreOutlined,
  SettingOutlined,
  ExperimentOutlined,
  BookOutlined,
  TrophyOutlined,
  HeartOutlined
} from '@ant-design/icons-vue'

// 分类接口定义
interface Category {
  key: string
  label: string
  icon: any
  count?: number
  color?: string
  description?: string
}

// 定义组件属性
interface Props {
  title?: string
  selectedCategory?: string
  bordered?: boolean
  size?: 'small' | 'default' | 'large'
  mode?: 'vertical' | 'horizontal' | 'inline'
  theme?: 'light'
  collapsed?: boolean
  showCounts?: boolean
  showDivider?: boolean
  showMore?: boolean
  showCustomize?: boolean
  maxVisible?: number
  categories?: Category[]
  allCount?: number
}

const props = withDefaults(defineProps<Props>(), {
  title: '分类导航',
  selectedCategory: 'all',
  bordered: true,
  size: 'default',
  mode: 'inline',
  theme: 'light',
  collapsed: false,
  showCounts: true,
  showDivider: true,
  showMore: true,
  showCustomize: false,
  maxVisible: 6,
  allCount: 0
})

// 定义事件
const emit = defineEmits<{
  categoryChange: [category: string]
  customize: []
}>()

const router = useRouter()

// 响应式状态
const selectedKeys = ref<string[]>([props.selectedCategory])
const expanded = ref(false)

// 默认分类配置
const defaultCategories: Category[] = [
  {
    key: 'tech',
    label: '技术讨论',
    icon: CodeOutlined,
    color: 'blue',
    description: '技术交流与讨论'
  },
  {
    key: 'question',
    label: '问题求助',
    icon: QuestionCircleOutlined,
    color: 'orange',
    description: '问题求助与解答'
  },
  {
    key: 'project',
    label: '项目分享',
    icon: FolderOpenOutlined,
    color: 'green',
    description: '项目展示与分享'
  },
  {
    key: 'share',
    label: '经验分享',
    icon: ShareAltOutlined,
    color: 'purple',
    description: '经验心得分享'
  },
  {
    key: 'tutorial',
    label: '教程指南',
    icon: BookOutlined,
    color: 'cyan',
    description: '学习教程与指南'
  },
  {
    key: 'algorithm',
    label: '算法数学',
    icon: ExperimentOutlined,
    color: 'red',
    description: '算法与数学讨论'
  },
  {
    key: 'competition',
    label: '竞赛相关',
    icon: TrophyOutlined,
    color: 'gold',
    description: '编程竞赛讨论'
  },
  {
    key: 'life',
    label: '生活随笔',
    icon: HeartOutlined,
    color: 'pink',
    description: '生活感悟与随笔'
  }
]

// 计算属性
const categories = computed(() => props.categories || defaultCategories)

const displayCategories = computed(() => {
  if (expanded.value) {
    return categories.value
  }
  return categories.value.slice(0, props.maxVisible)
})

const hiddenCategories = computed(() => {
  return categories.value.slice(props.maxVisible)
})

// 方法
const formatCount = (count: number) => {
  if (count < 1000) return count.toString()
  if (count < 10000) return `${(count / 1000).toFixed(1)}k`
  return `${(count / 10000).toFixed(1)}w`
}

const handleMenuClick = ({ key }: { key: string }) => {
  if (key === 'more') {
    toggleExpanded()
    return
  }

  selectedKeys.value = [key]
  emit('categoryChange', key)
  
  // 路由跳转
  if (key === 'all') {
    router.push('/')
  } else {
    router.push({
      path: '/category',
      query: { type: key }
    })
  }
}

const toggleExpanded = () => {
  expanded.value = !expanded.value
}

const handleCustomize = () => {
  emit('customize')
}

// 监听选中分类变化
watch(
  () => props.selectedCategory,
  (newCategory) => {
    selectedKeys.value = [newCategory]
  }
)
</script>

<style scoped lang="scss">
.category-nav {
  .category-card {
    border-radius: 12px;
    overflow: hidden;

    :deep(.ant-card-head) {
      background: #fafafa;
      border-bottom: 1px solid #f0f0f0;
      
      .ant-card-head-title {
        font-size: 14px;
        font-weight: 500;
        color: #262626;
      }
    }

    :deep(.ant-card-body) {
      padding: 0;
    }

    .toggle-btn {
      font-size: 12px;
      color: #8c8c8c;
      
      .anticon {
        font-size: 10px;
        margin-left: 4px;
      }
    }
  }

  .category-menu {
    border: none !important;

    :deep(.ant-menu-item) {
      margin: 0;
      height: 40px;
      line-height: 40px;
      border-radius: 0;
      transition: all 0.2s ease;

      &:hover {
        background: #f5f5f5;
      }

      &.ant-menu-item-selected {
        background: #e6f7ff;
        border-right: 3px solid #1890ff;
        
        &::after {
          display: none;
        }
      }

      .ant-menu-title-content {
        display: flex;
        align-items: center;
        justify-content: space-between;
        width: 100%;
      }

      .anticon {
        font-size: 14px;
        color: #8c8c8c;
      }

      &.ant-menu-item-selected .anticon {
        color: #1890ff;
      }
    }

    .menu-item-all {
      font-weight: 500;
      
      .anticon {
        color: #1890ff;
      }
    }

    .menu-item-category {
      &.active {
        background: #e6f7ff;
        border-right: 3px solid #1890ff;
        
        .anticon {
          color: #1890ff;
        }
      }
    }

    .menu-item-more {
      color: #8c8c8c;
      font-size: 13px;
      
      &:hover {
        color: #1890ff;
      }
    }

    .item-count {
      font-size: 11px;
      color: #bfbfbf;
      background: #f5f5f5;
      padding: 1px 6px;
      border-radius: 10px;
      min-width: 18px;
      text-align: center;
    }

    :deep(.ant-menu-item-selected) .item-count {
      background: rgba(24, 144, 255, 0.1);
      color: #1890ff;
    }
  }

  .customize-section {
    padding: 12px;

    .customize-btn {
      height: 32px;
      font-size: 12px;
      color: #8c8c8c;
      border-color: #d9d9d9;
      
      &:hover {
        color: #1890ff;
        border-color: #1890ff;
      }

      .anticon {
        font-size: 12px;
        margin-right: 4px;
      }
    }
  }
}

// 水平模式样式
.category-nav :deep(.ant-menu-horizontal) {
  .ant-menu-item {
    height: 36px;
    line-height: 36px;
    margin: 0 8px;
    border-radius: 18px;
    
    &.ant-menu-item-selected {
      background: #1890ff;
      color: white;
      border-right: none;
      
      .anticon {
        color: white;
      }
      
      .item-count {
        background: rgba(255, 255, 255, 0.2);
        color: white;
      }
    }
  }
}

// 折叠模式样式
.category-nav :deep(.ant-menu-inline-collapsed) {
  .item-count {
    display: none;
  }
  
  .ant-menu-item {
    text-align: center;
    
    .ant-menu-title-content {
      justify-content: center;
    }
  }
}

// 响应式样式
@media (max-width: 768px) {
  .category-nav {
    .category-card {
      border-radius: 8px;
      
      :deep(.ant-card-head) {
        padding: 0 12px;
        min-height: 44px;
        
        .ant-card-head-title {
          font-size: 13px;
        }
      }
    }

    .category-menu {
      :deep(.ant-menu-item) {
        height: 36px;
        line-height: 36px;
        font-size: 13px;
        
        .anticon {
          font-size: 13px;
        }
      }

      .item-count {
        font-size: 10px;
        padding: 0 4px;
      }
    }

    .customize-section {
      padding: 8px;
      
      .customize-btn {
        height: 28px;
        font-size: 11px;
      }
    }
  }
}

// 暗色主题已移除，统一使用白色模式

// 动画效果
@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.category-menu :deep(.ant-menu-item) {
  animation: slideIn 0.3s ease-out;
}
</style>
