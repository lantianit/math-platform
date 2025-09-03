<template>
  <div class="search-page">
    <!-- 搜索头部 -->
    <div class="search-header">
      <div class="search-container">
      </div>
    </div>

    <!-- 搜索结果内容 -->
    <div class="search-content">
      <div class="search-container">
        <!-- 搜索状态信息 -->
        <div v-if="searchKeyword" class="search-info">
          <div class="search-stats">
            搜索 "<span class="search-keyword">{{ searchKeyword }}</span>" 
            共找到 <span class="search-count">{{ searchStore.searchTotal }}</span> 个结果
          </div>
          
          <!-- 搜索筛选 -->
          <div class="search-filters">
            <a-space>
              <a-select
                v-model:value="searchFilters.category"
                placeholder="选择分类"
                style="width: 120px"
                allow-clear
                @change="handleFilterChange"
              >
                <a-select-option value="">全部分类</a-select-option>
                <a-select-option value="微积分">微积分</a-select-option>
                <a-select-option value="导数">导数</a-select-option>
                <a-select-option value="积分">积分</a-select-option>
                <a-select-option value="数学分析">数学分析</a-select-option>
                <a-select-option value="开源项目">开源项目</a-select-option>
              </a-select>
              
              <a-select
                v-model:value="searchFilters.sortField"
                placeholder="排序方式"
                style="width: 120px"
                @change="handleFilterChange"
              >
                <a-select-option value="">默认排序</a-select-option>
                <a-select-option value="createTime">最新发布</a-select-option>
                <a-select-option value="likeCount">最多点赞</a-select-option>
                <a-select-option value="viewCount">最多查看</a-select-option>
                <a-select-option value="commentCount">最多评论</a-select-option>
              </a-select>
            </a-space>
          </div>
        </div>

        <!-- 搜索结果列表 -->
        <div class="search-results">
          <!-- 加载状态 -->
          <div v-if="searchStore.searchLoading && searchStore.searchResults.length === 0" class="loading-container">
            <a-spin size="large" />
            <div class="loading-text">正在搜索中...</div>
          </div>

          <!-- 搜索结果 -->
          <div v-else-if="searchStore.hasSearchResults" class="results-list">
            <PostList 
              :posts="searchStore.searchResults"
              :loading="searchStore.searchLoading"
              :has-more="searchStore.searchHasMore"
              @load-more="handleLoadMore"
              show-load-more
            />
          </div>

          <!-- 空结果 -->
          <div v-else-if="searchKeyword && !searchStore.searchLoading" class="empty-results">
            <a-empty
              description="未找到相关内容"
              :image="a.PRESENTED_IMAGE_SIMPLE"
            >
              <template #description>
                <div class="empty-description">
                  <div>未找到与 "<span class="search-keyword">{{ searchKeyword }}</span>" 相关的内容</div>
                  <div class="empty-suggestions">
                    <div>建议：</div>
                    <ul>
                      <li>检查搜索关键词是否正确</li>
                      <li>尝试使用更简单的关键词</li>
                      <li>尝试使用相关的同义词</li>
                    </ul>
                  </div>
                </div>
              </template>
              <a-button type="primary" @click="handleClearSearch">
                清空搜索
              </a-button>
            </a-empty>
          </div>

          <!-- 默认状态（无搜索关键词） -->
          <div v-else class="search-default">
            <a-empty
              description="输入关键词开始搜索"
              :image="a.PRESENTED_IMAGE_SIMPLE"
            >
              <template #description>
                <div class="default-description">
                  <div>输入关键词搜索帖子、用户或话题</div>
                  
                  <!-- 热门搜索 -->
                  <div v-if="searchStore.hasHotKeywords" class="hot-keywords-section">
                    <div class="section-title">热门搜索</div>
                    <div class="hot-keywords">
                      <a-tag
                        v-for="(keyword, index) in searchStore.hotKeywords.slice(0, 8)"
                        :key="keyword"
                        :color="getHotTagColor(index)"
                        @click="handleHotKeywordClick(keyword)"
                        class="hot-keyword-tag"
                      >
                        <span class="hot-rank">{{ index + 1 }}</span>
                        {{ keyword }}
                      </a-tag>
                    </div>
                  </div>

                  <!-- 搜索历史 -->
                  <div v-if="searchStore.hasSearchHistory" class="search-history-section">
                    <div class="section-title">
                      搜索历史
                      <a-button
                        type="text"
                        size="small"
                        @click="handleClearHistory"
                        class="clear-history-btn"
                      >
                        清空
                      </a-button>
                    </div>
                    <div class="search-history">
                      <a-tag
                        v-for="keyword in searchStore.searchHistory.slice(0, 6)"
                        :key="keyword"
                        @click="handleHistoryClick(keyword)"
                        closable
                        @close="handleRemoveHistory(keyword)"
                        class="history-tag"
                      >
                        {{ keyword }}
                      </a-tag>
                    </div>
                  </div>
                </div>
              </template>
            </a-empty>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Empty } from 'ant-design-vue'
import { useSearchStore } from '@/stores/useSearchStore'
import SearchBox from '@/components/SearchBox.vue'
import PostList from '@/components/PostList.vue'

const route = useRoute()
const router = useRouter()
const searchStore = useSearchStore()

// 响应式数据
const searchKeyword = ref('')
const searchFilters = ref({
  category: '',
  sortField: '',
  sortOrder: 'desc'
})

// 计算属性
const a = computed(() => Empty)

// 方法
const handleSearch = (keyword: string) => {
  if (!keyword.trim()) return
  
  searchKeyword.value = keyword.trim()
  
  // 更新 URL
  router.replace({
    path: '/search',
    query: { 
      q: keyword.trim(),
      category: searchFilters.value.category || undefined,
      sortField: searchFilters.value.sortField || undefined
    }
  })
  
  // 执行搜索
  performSearch()
}

const performSearch = () => {
  if (!searchKeyword.value.trim()) return
  
  searchStore.searchPosts(searchKeyword.value, 1, true)
}

const handleFilterChange = () => {
  if (!searchKeyword.value.trim()) return
  
  // 更新 URL
  router.replace({
    path: '/search',
    query: { 
      q: searchKeyword.value,
      category: searchFilters.value.category || undefined,
      sortField: searchFilters.value.sortField || undefined
    }
  })
  
  // 重新搜索
  performSearch()
}

const handleLoadMore = () => {
  searchStore.loadMoreSearchResults()
}

const handleClearSearch = () => {
  searchKeyword.value = ''
  searchStore.clearSearchResults()
  router.replace({ path: '/search' })
}

const handleHotKeywordClick = (keyword: string) => {
  searchKeyword.value = keyword
  handleSearch(keyword)
}

const handleHistoryClick = (keyword: string) => {
  searchKeyword.value = keyword
  handleSearch(keyword)
}

const handleRemoveHistory = (keyword: string) => {
  searchStore.removeSearchHistory(keyword)
}

const handleClearHistory = () => {
  searchStore.clearSearchHistory()
}

const getHotTagColor = (index: number) => {
  if (index < 3) return 'red'
  if (index < 6) return 'orange'
  return 'blue'
}

// 初始化搜索
const initializeSearch = () => {
  const query = route.query.q as string
  const category = route.query.category as string
  const sortField = route.query.sortField as string
  
  if (query) {
    searchKeyword.value = query
    searchFilters.value.category = category || ''
    searchFilters.value.sortField = sortField || ''
    performSearch()
  }
  
  // 加载热门关键词
  if (searchStore.hotKeywords.length === 0) {
    searchStore.loadHotKeywords()
  }
}

// 监听路由变化
watch(() => route.query, (newQuery) => {
  const query = newQuery.q as string
  if (query !== searchKeyword.value) {
    initializeSearch()
  }
}, { immediate: true })

// 生命周期
onMounted(() => {
  initializeSearch()
})
</script>

<style scoped lang="scss">
.search-page {
  min-height: 100vh;
  background: #fafafa;
}

.search-header {
  background: white;
  border-bottom: 1px solid #f0f0f0;
  padding: 20px 0;
  
  .search-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    
    .search-header-box {
      max-width: 600px;
      margin: 0 auto;
    }
  }
}

.search-content {
  padding: 24px 0;
  
  .search-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
  }
}

.search-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px 0;
  border-bottom: 1px solid #f0f0f0;
  
  .search-stats {
    font-size: 14px;
    color: #666;
    
    .search-keyword {
      color: #1890ff;
      font-weight: 500;
    }
    
    .search-count {
      color: #1890ff;
      font-weight: 500;
    }
  }
  
  .search-filters {
    .ant-space {
      gap: 12px;
    }
  }
}

.search-results {
  .loading-container {
    text-align: center;
    padding: 60px 0;
    
    .loading-text {
      margin-top: 16px;
      color: #666;
      font-size: 14px;
    }
  }
  
  .results-list {
    background: white;
    border-radius: 8px;
    overflow: hidden;
  }
  
  .empty-results {
    background: white;
    border-radius: 8px;
    padding: 60px 24px;
    text-align: center;
    
    .empty-description {
      .search-keyword {
        color: #1890ff;
        font-weight: 500;
      }
      
      .empty-suggestions {
        margin-top: 16px;
        text-align: left;
        display: inline-block;
        
        ul {
          margin: 8px 0 0 0;
          padding-left: 16px;
          
          li {
            margin: 4px 0;
            color: #666;
            font-size: 13px;
          }
        }
      }
    }
  }
  
  .search-default {
    background: white;
    border-radius: 8px;
    padding: 60px 24px;
    text-align: center;
    
    .default-description {
      .hot-keywords-section,
      .search-history-section {
        margin-top: 32px;
        text-align: left;
        display: inline-block;
        min-width: 400px;
        
        .section-title {
          font-size: 14px;
          font-weight: 500;
          color: #262626;
          margin-bottom: 12px;
          display: flex;
          justify-content: space-between;
          align-items: center;
          
          .clear-history-btn {
            font-size: 12px;
            color: #8c8c8c;
            padding: 0;
            height: auto;
          }
        }
        
        .hot-keywords,
        .search-history {
          display: flex;
          flex-wrap: wrap;
          gap: 8px;
          
          .hot-keyword-tag,
          .history-tag {
            cursor: pointer;
            transition: all 0.2s ease;
            
            &:hover {
              transform: translateY(-1px);
            }
          }
          
          .hot-keyword-tag {
            .hot-rank {
              font-size: 10px;
              margin-right: 2px;
            }
          }
        }
      }
    }
  }
}

// 响应式样式
@media (max-width: 768px) {
  .search-header {
    padding: 16px 0;
    
    .search-container {
      padding: 0 16px;
    }
  }
  
  .search-content {
    padding: 16px 0;
    
    .search-container {
      padding: 0 16px;
    }
  }
  
  .search-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
    
    .search-filters {
      width: 100%;
      
      .ant-space {
        width: 100%;
        justify-content: flex-start;
      }
    }
  }
  
  .search-default {
    .default-description {
      .hot-keywords-section,
      .search-history-section {
        min-width: auto;
        width: 100%;
      }
    }
  }
}
</style>
