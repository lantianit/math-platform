import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { useLocalStorage, useDebounceFn } from '@vueuse/core'
import * as searchController from '@/api/searchController'

/**
 * 搜索状态管理
 */
export const useSearchStore = defineStore('search', () => {
  // 搜索相关状态
  const searchText = ref('')
  const searchSuggestions = ref<string[]>([])
  const hotKeywords = ref<string[]>([])
  const searchHistory = useLocalStorage<string[]>('search-history', [])
  const loading = ref(false)

  // 搜索结果
  const searchResults = ref<API.PostVO[]>([])
  const searchTotal = ref(0)
  const searchCurrentPage = ref(1)
  const searchPageSize = ref(10)
  const searchHasMore = ref(true)
  const searchLoading = ref(false)

  /**
   * 获取热门搜索关键词
   */
  const loadHotKeywords = async () => {
    try {
      const response = await searchController.getHotKeywordsUsingGet()
      if (response?.data?.code === 0 && response.data.data) {
        hotKeywords.value = response.data.data
      }
    } catch (error) {
      console.error('加载热门关键词失败:', error)
    }
  }

  /**
   * 获取搜索建议
   */
  const fetchSearchSuggestions = async (keyword: string) => {
    console.log('fetchSearchSuggestions called with:', keyword)
    if (!keyword.trim()) {
      searchSuggestions.value = []
      return
    }

    try {
      loading.value = true
      console.log('Making API call to get search suggestions...')
      const response = await searchController.getSearchSuggestionsUsingGet({
        keyword: keyword.trim()
      })

      console.log('API response:', response)
      if (response?.data?.code === 0 && response.data.data) {
        console.log('Setting search suggestions:', response.data.data)
        searchSuggestions.value = response.data.data
      } else {
        console.log('No suggestions received or API error, using mock data')
        // 临时模拟数据，用于测试UI逻辑
        searchSuggestions.value = [
          `${keyword}相关内容1`,
          `${keyword}相关内容2`,
          `${keyword}相关内容3`
        ]
      }
    } catch (error) {
      console.error('获取搜索建议失败:', error)
      searchSuggestions.value = []
    } finally {
      loading.value = false
    }
  }

  // 防抖搜索建议
  const debouncedFetchSuggestions = useDebounceFn(fetchSearchSuggestions, 300)

  /**
   * 搜索帖子
   */
  const searchPosts = async (
    keyword: string,
    page = 1,
    reset = true,
    extra?: { category?: string; sortField?: string; sortOrder?: string }
  ) => {
    if (!keyword.trim()) return

    searchLoading.value = true

    try {
      if (reset) {
        searchCurrentPage.value = 1
        searchResults.value = []
        searchHasMore.value = true
      }

      const response = await searchController.searchPostsUsingPost({
        keyword: keyword.trim(),
        current: page,
        pageSize: searchPageSize.value,
        category: extra?.category || '',
        sortField: extra?.sortField || 'createTime',
        sortOrder: extra?.sortOrder || 'desc',
      })

      if (response?.data?.code === 0 && response.data.data) {
        const newResults = response.data.data.records || []

        if (reset) {
          searchResults.value = newResults
        } else {
          searchResults.value.push(...newResults)
        }

        searchTotal.value = response.data.data.total || 0
        searchHasMore.value = searchCurrentPage.value < (response.data.data.pages || 1)

        if (searchHasMore.value) {
          searchCurrentPage.value++
        }

        // 添加到搜索历史
        addToSearchHistory(keyword.trim())
      }
    } catch (error) {
      console.error('搜索失败:', error)
    } finally {
      searchLoading.value = false
    }
  }

  /**
   * 加载更多搜索结果
   */
  const loadMoreSearchResults = async () => {
    if (!searchText.value.trim() || searchLoading.value || !searchHasMore.value) {
      return
    }

    await searchPosts(searchText.value, searchCurrentPage.value, false)
  }

  /**
   * 添加到搜索历史
   */
  const addToSearchHistory = (keyword: string) => {
    if (!keyword.trim()) return

    // 移除重复项
    const history = searchHistory.value.filter(item => item !== keyword)

    // 添加到开头
    history.unshift(keyword)

    // 限制历史记录数量
    searchHistory.value = history.slice(0, 10)
  }

  /**
   * 清除搜索历史
   */
  const clearSearchHistory = () => {
    searchHistory.value = []
  }

  /**
   * 删除单个搜索历史
   */
  const removeSearchHistory = (keyword: string) => {
    searchHistory.value = searchHistory.value.filter(item => item !== keyword)
  }

  /**
   * 执行搜索
   */
  const performSearch = async (keyword: string) => {
    if (!keyword.trim()) return

    searchText.value = keyword.trim()
    await searchPosts(keyword.trim())

    // 清空搜索建议
    searchSuggestions.value = []
  }

  /**
   * 清空搜索结果
   */
  const clearSearchResults = () => {
    searchResults.value = []
    searchText.value = ''
    searchTotal.value = 0
    searchCurrentPage.value = 1
    searchHasMore.value = true
    searchSuggestions.value = []
  }

  /**
   * 处理搜索输入变化
   */
  const handleSearchInputChange = (value: string) => {
    console.log('handleSearchInputChange called with:', value)
    searchText.value = value

    if (value.trim()) {
      console.log('Calling debouncedFetchSuggestions with:', value.trim())
      debouncedFetchSuggestions(value.trim())
    } else {
      console.log('Clearing search suggestions')
      searchSuggestions.value = []
    }
  }

  // 计算属性
  const hasSearchResults = computed(() => searchResults.value.length > 0)
  const hasSearchHistory = computed(() => searchHistory.value.length > 0)
  const hasHotKeywords = computed(() => hotKeywords.value.length > 0)
  const hasSuggestions = computed(() => searchSuggestions.value.length > 0)

  // 搜索建议选项（包含历史记录和实时建议）
  const suggestionOptions = computed(() => {
    const options: Array<{ value: string; label: string; type: 'history' | 'suggestion' | 'hot' }> = []

    // 如果有输入内容，显示搜索建议
    if (searchText.value.trim() && hasSuggestions.value) {
      searchSuggestions.value.forEach(suggestion => {
        options.push({
          value: suggestion,
          label: suggestion,
          type: 'suggestion'
        })
      })
    }

    // 如果没有输入内容，显示搜索历史
    if (!searchText.value.trim() && hasSearchHistory.value) {
      searchHistory.value.forEach(history => {
        options.push({
          value: history,
          label: history,
          type: 'history'
        })
      })
    }

    return options
  })

  return {
    // 状态
    searchText,
    searchSuggestions,
    hotKeywords,
    searchHistory,
    loading,
    searchResults,
    searchTotal,
    searchCurrentPage,
    searchPageSize,
    searchHasMore,
    searchLoading,

    // 方法
    loadHotKeywords,
    fetchSearchSuggestions,
    searchPosts,
    loadMoreSearchResults,
    addToSearchHistory,
    clearSearchHistory,
    removeSearchHistory,
    performSearch,
    clearSearchResults,
    handleSearchInputChange,

    // 计算属性
    hasSearchResults,
    hasSearchHistory,
    hasHotKeywords,
    hasSuggestions,
    suggestionOptions
  }
})
