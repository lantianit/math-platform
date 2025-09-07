<template>
  <div class="search-box" :class="{ expanded: expanded, focused: focused }" @click.stop>
    <a-auto-complete
      v-model:value="searchText"
      :options="suggestionOptions"
      :placeholder="placeholder"
      :size="size"
      :disabled="disabled"
      :open="showSuggestionPanel && suggestionOptions.length > 0"
      @search="handleSearch"
      @select="handleSelect"
      @focus="handleFocus"
      @blur="handleBlur"
      @keydown.enter="handleEnter"
      class="search-input"
    >
      <template #option="{ value, label, type }">
        <div class="suggestion-item" :class="`suggestion-${type}`">
          <SearchOutlined v-if="type === 'suggestion'" class="suggestion-icon" />
          <ClockCircleOutlined v-else-if="type === 'history'" class="suggestion-icon" />
          <FireOutlined v-else-if="type === 'hot'" class="suggestion-icon" />
          <span class="suggestion-text">{{ label }}</span>
          <a-button
            v-if="type === 'history'"
            type="text"
            size="small"
            @click.stop="handleRemoveHistory(value)"
            class="remove-history-btn"
          >
            <CloseOutlined />
          </a-button>
        </div>
      </template>
      
      <template #prefix>
        <SearchOutlined class="search-prefix-icon" />
      </template>
      
      <template #suffix>
        <div class="search-suffix">
          <a-button
            v-if="searchText"
            type="text"
            size="small"
            @click.stop="handleClear"
            class="clear-btn"
            title="清空搜索"
          >
            <CloseOutlined />
          </a-button>
          <a-button
            type="primary"
            :size="size"
            :loading="loading"
            @click.stop="handleSubmit"
            class="search-btn"
          >
            搜索
          </a-button>
        </div>
      </template>
    </a-auto-complete>

    <!-- 搜索建议面板 -->
    <div
      v-if="showSuggestionPanel && hasAnyContent"
      class="suggestion-panel"
      @click.stop
    >
      <!-- 搜索历史 -->
      <div v-if="hasSearchHistory && !searchText" class="suggestion-section">
        <div class="suggestion-header">
          <span class="suggestion-title">搜索历史</span>
          <a-button
            type="text"
            size="small"
            @click="handleClearHistory"
            class="clear-all-btn"
          >
            清空
          </a-button>
        </div>
        <div class="history-tags">
          <a-tag
            v-for="item in searchHistory.slice(0, 8)"
            :key="item"
            @click="handleHistoryClick(item)"
            closable
            @close="handleRemoveHistory(item)"
            class="history-tag"
          >
            {{ item }}
          </a-tag>
        </div>
      </div>

      <!-- 热门搜索 -->
      <div v-if="hasHotKeywords && !searchText" class="suggestion-section">
        <div class="suggestion-header">
          <span class="suggestion-title">热门搜索</span>
        </div>
        <div class="hot-tags">
          <a-tag
            v-for="(item, index) in hotKeywords.slice(0, 10)"
            :key="item"
            :color="getHotTagColor(index)"
            @click="handleHotKeywordClick(item)"
            class="hot-tag"
          >
            <span class="hot-rank">{{ index + 1 }}</span>
            {{ item }}
          </a-tag>
        </div>
      </div>

      <!-- 搜索建议 -->
      <div v-if="searchText" class="suggestion-section">
        <div class="suggestion-header">
          <span class="suggestion-title">搜索建议</span>
        </div>
        <div v-if="hasSuggestions" class="suggestion-list">
          <div
            v-for="suggestion in searchSuggestions.slice(0, 8)"
            :key="suggestion"
            @click="handleSuggestionClick(suggestion)"
            class="suggestion-list-item"
          >
            <SearchOutlined class="suggestion-icon" />
            <span class="suggestion-text" v-html="highlightKeyword(suggestion)"></span>
          </div>
        </div>
        <div v-else class="suggestion-loading">
          <div class="loading-item">
            <SearchOutlined class="suggestion-icon" />
            <span class="suggestion-text">正在搜索 "{{ searchText }}"...</span>
          </div>
        </div>
      </div>

      <!-- 空状态已移除，通过v-if条件控制面板显示 -->
    </div>

    <!-- 移动端搜索遮罩已移除 -->
  </div>
</template>

<script setup lang="ts">
import { computed, ref, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import {
  SearchOutlined,
  CloseOutlined,
  ClockCircleOutlined,
  FireOutlined
} from '@ant-design/icons-vue'
import { useSearchStore } from '@/stores/useSearchStore'
import { useUIStore } from '@/stores/useUIStore'

// 定义组件属性
interface Props {
  placeholder?: string
  size?: 'small' | 'middle' | 'large'
  disabled?: boolean
  expanded?: boolean
  loading?: boolean
  autoFocus?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '搜索帖子、用户、话题...',
  size: 'middle',
  disabled: false,
  expanded: false,
  loading: false,
  autoFocus: false
})

// 定义事件
const emit = defineEmits<{
  search: [keyword: string]
  focus: []
  blur: []
  clear: []
}>()

const router = useRouter()
const searchStore = useSearchStore()
const uiStore = useUIStore()

// 响应式状态
const searchText = ref('')
const focused = ref(false)
const showSuggestionPanel = ref(false)

// 计算属性
const isMobile = computed(() => uiStore.isMobile)
const searchHistory = computed(() => searchStore.searchHistory)
const hotKeywords = computed(() => searchStore.hotKeywords)
const searchSuggestions = computed(() => searchStore.searchSuggestions)

const hasSearchHistory = computed(() => searchHistory.value.length > 0)
const hasHotKeywords = computed(() => hotKeywords.value.length > 0)
const hasSuggestions = computed(() => {
  const result = searchSuggestions.value.length > 0
  console.log('hasSuggestions:', result, 'suggestions:', searchSuggestions.value)
  return result
})

const hasAnyContent = computed(() => {
  // 有输入时：显示搜索建议
  if (searchText.value.trim()) {
    const result = hasSuggestions.value || searchText.value.length > 0 // 即使没有建议也显示面板
    console.log('hasAnyContent (with input):', result, 'hasSuggestions:', hasSuggestions.value, 'searchText:', searchText.value)
    return result
  }
  // 无输入时：显示历史搜索和热门搜索
  const result = hasSearchHistory.value || hasHotKeywords.value
  console.log('hasAnyContent (no input):', result, 'hasHistory:', hasSearchHistory.value, 'hasHotKeywords:', hasHotKeywords.value)
  return result
})

const suggestionOptions = computed(() => {
  const options: Array<{ value: string; label: string; type: string }> = []
  
  // 有输入内容时，显示搜索建议
  if (searchText.value && hasSuggestions.value) {
    searchSuggestions.value.forEach(suggestion => {
      options.push({
        value: suggestion,
        label: suggestion,
        type: 'suggestion'
      })
    })
  }
  
  console.log('suggestionOptions computed:', options.length, options)
  return options
})

// 方法
const handleSearch = (value: string) => {
  console.log('handleSearch called with:', value)
  searchStore.handleSearchInputChange(value)
}

const handleSelect = (value: string) => {
  searchText.value = value
  handleSubmit()
}

const handleFocus = () => {
  focused.value = true
  showSuggestionPanel.value = true
  uiStore.setSearchFocus(true)
  emit('focus')
  
  // 加载热门关键词
  if (hotKeywords.value.length === 0) {
    searchStore.loadHotKeywords()
  }
}

const handleBlur = () => {
  // 延迟隐藏，以便点击建议项
  setTimeout(() => {
    focused.value = false
    showSuggestionPanel.value = false
    uiStore.setSearchFocus(false)
    emit('blur')
  }, 200)
}

const handleEnter = () => {
  if (searchText.value.trim()) {
    handleSubmit()
  }
}

const handleSubmit = (event?: Event) => {
  // 阻止事件冒泡
  if (event) {
    event.preventDefault()
    event.stopPropagation()
  }
  
  if (!searchText.value.trim()) return
  
  const keyword = searchText.value.trim()
  emit('search', keyword)
  
  // 执行搜索
  searchStore.performSearch(keyword)
  
  // 跳转到搜索结果页
  router.push({
    path: '/search',
    query: { q: keyword }
  })
  
  // 隐藏建议面板
  showSuggestionPanel.value = false
  focused.value = false
}

const handleClear = (event?: Event) => {
  // 阻止事件冒泡
  if (event) {
    event.preventDefault()
    event.stopPropagation()
  }
  
  searchText.value = ''
  searchStore.clearSearchResults()
  emit('clear')
  
  // 重新聚焦到输入框
  nextTick(() => {
    const input = document.querySelector('.search-input input') as HTMLInputElement
    input?.focus()
  })
}

const handleRemoveHistory = (item: string) => {
  searchStore.removeSearchHistory(item)
}

const handleClearHistory = () => {
  searchStore.clearSearchHistory()
}

const handleHistoryClick = (item: string) => {
  searchText.value = item
  handleSubmit()
}

const handleHotKeywordClick = (item: string) => {
  searchText.value = item
  handleSubmit()
}

const handleSuggestionClick = (suggestion: string) => {
  searchText.value = suggestion
  handleSubmit()
}

// 遮罩点击处理函数已移除

const getHotTagColor = (index: number) => {
  if (index < 3) return 'red'
  if (index < 6) return 'orange'
  return 'blue'
}

const highlightKeyword = (text: string) => {
  if (!searchText.value) return text
  const keyword = searchText.value.trim()
  const regex = new RegExp(`(${keyword})`, 'gi')
  return text.replace(regex, '<strong style="color: #1890ff;">$1</strong>')
}

// 监听搜索文本变化
watch(searchText, (newValue) => {
  console.log('searchText changed to:', newValue)
  searchStore.handleSearchInputChange(newValue)
  // 有输入时确保面板打开
  if (newValue && newValue.trim().length > 0) {
    showSuggestionPanel.value = true
  }
})

// 监听热门和建议数据变化，确保面板在有内容时展开
watch([hotKeywords, searchSuggestions], () => {
  if (focused.value || (searchText.value && searchText.value.trim().length > 0)) {
    if (hasAnyContent.value) {
      showSuggestionPanel.value = true
    }
  }
})

// 自动聚焦
if (props.autoFocus) {
  nextTick(() => {
    const input = document.querySelector('.search-input input') as HTMLInputElement
    input?.focus()
  })
}
</script>

<style scoped lang="scss">
.search-box {
  position: relative;
  width: 100%;
  max-width: 500px;

  .search-input {
    width: 100%;

    :deep(.ant-select-selector) {
      border-radius: 24px;
      padding-left: 16px;
      padding-right: 16px;
      transition: all 0.3s ease;

      &:hover {
        box-shadow: 0 2px 8px rgba(24, 144, 255, 0.2);
      }
    }

    :deep(.ant-select-focused .ant-select-selector) {
      border-color: #1890ff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
    }
    
    .search-prefix-icon {
      color: #bfbfbf;
    }
  }

  .search-suffix {
    display: flex;
    align-items: center;
    gap: 8px;

    .clear-btn {
      color: #bfbfbf;
      min-width: 24px;
      height: 24px;
      padding: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 50%;
      transition: all 0.2s ease;
      
      &:hover {
        color: #ff4d4f;
        background-color: #fff2f0;
        border-color: #ffccc7;
      }
      
      &:active {
        background-color: #ffe7e6;
      }
      
      &:focus {
        outline: none;
        box-shadow: 0 0 0 2px rgba(255, 77, 79, 0.2);
      }
    }

    .search-btn {
      border-radius: 16px;
      min-width: 60px;
      font-size: 13px;
    }
  }

  &.focused {
    .search-input :deep(.ant-select-selector) {
      border-color: #1890ff;
      box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
    }
  }

  &.expanded {
    max-width: 600px;
  }
}

.suggestion-panel {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border-radius: 12px;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  border: 1px solid #f0f0f0;
  z-index: 1000;
  max-height: 400px;
  overflow-y: auto;
  margin-top: 4px;

  .suggestion-section {
    padding: 16px;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    .suggestion-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      .suggestion-title {
        font-size: 13px;
        color: #8c8c8c;
        font-weight: 500;
      }

      .clear-all-btn {
        font-size: 12px;
        color: #8c8c8c;
        padding: 0;
        height: auto;
      }
    }

    .history-tags, .hot-tags {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;

      .history-tag, .hot-tag {
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          transform: translateY(-1px);
        }
      }

      .hot-tag {
        .hot-rank {
          font-size: 10px;
          margin-right: 2px;
        }
      }
    }

    .suggestion-list {
      .suggestion-list-item {
        display: flex;
        align-items: center;
        padding: 8px 12px;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          background: #f5f5f5;
        }

        .suggestion-icon {
          color: #bfbfbf;
          margin-right: 8px;
          font-size: 14px;
        }

        .suggestion-text {
          flex: 1;
          font-size: 14px;
          color: #262626;
        }
      }
    }
    
    .suggestion-loading {
      .loading-item {
        display: flex;
        align-items: center;
        padding: 8px 12px;
        border-radius: 6px;
        
        .suggestion-icon {
          color: #bfbfbf;
          margin-right: 8px;
          font-size: 14px;
        }
        
        .suggestion-text {
          flex: 1;
          font-size: 14px;
          color: #8c8c8c;
          font-style: italic;
        }
      }
    }
  }

  .suggestion-empty {
    padding: 20px;
    text-align: center;

    .empty-small {
      :deep(.ant-empty-description) {
        font-size: 12px;
        color: #bfbfbf;
      }
    }
  }
}

.suggestion-item {
  display: flex;
  align-items: center;
  width: 100%;

  .suggestion-icon {
    color: #bfbfbf;
    margin-right: 8px;
    font-size: 14px;
  }

  .suggestion-text {
    flex: 1;
  }

  .remove-history-btn {
    opacity: 0;
    transition: opacity 0.2s ease;
    padding: 0;
    width: 20px;
    height: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  &:hover .remove-history-btn {
    opacity: 1;
  }
}

// 搜索遮罩样式已移除

// 响应式样式
@media (max-width: 768px) {
  .search-box {
    max-width: 100%;

    &.focused {
      // 移动端聚焦时保持正常位置，不使用fixed定位
      position: relative;
      z-index: 1001;
    }

    .search-input :deep(.ant-select-selector) {
      border-radius: 20px;
      padding-left: 12px;
      padding-right: 12px;
    }

    .search-input-inner {
      border-radius: 20px;

      .search-suffix {
        .clear-btn {
          min-width: 22px;
          height: 22px;
          font-size: 12px;
        }
        
        .search-btn {
          min-width: 50px;
          font-size: 12px;
        }
      }
    }
  }

  .suggestion-panel {
    border-radius: 8px;
    max-height: 60vh;

    .suggestion-section {
      padding: 12px;

      .suggestion-header .suggestion-title {
        font-size: 12px;
      }

      .history-tags, .hot-tags {
        gap: 6px;
      }

      .suggestion-list .suggestion-list-item {
        padding: 10px 8px;

        .suggestion-text {
          font-size: 13px;
        }
      }
    }
  }
}

// 暗色主题已移除，统一使用白色模式
</style>
